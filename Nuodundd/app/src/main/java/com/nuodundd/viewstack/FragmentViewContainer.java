package com.nuodundd.viewstack;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.nuodundd.util.DeviceHelper;
import com.nuodundd.util.ScreenUtil;


/**
 * Created by nuodundd on 17/1/17.
 */

public class FragmentViewContainer extends FrameLayout {
    private static final int ANIM_DURATION = 150;
    private static final int AUTOANIM_LIMIT = (int) ScreenUtil.dip2px(150);
    private static final int AUTOANIM_SPEED = (int) ScreenUtil.dip2px(400);
    private static final int CLICK_LIMIT = (int) ScreenUtil.dip2px(15);
    private static final int INTERCEPTMINSIZE = (int) ScreenUtil.dip2px(10);
    private static final int ANIM_HIDE_SPEED = (int) ScreenUtil.dip2px(1800);
    private static final int ANIM_SHOW_SPEED = (int) ScreenUtil.dip2px(800);

    private static final int SHADOW_WIDTH = (int) ScreenUtil.dip2px(5);
    private boolean isRtl = DeviceHelper.isRtlLanguage();
    private boolean visibleChange;
    private VisibleListener visibleListener = null;
    private ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private Paint shadowPaint;
    private Paint backGroundPaint;
    private boolean touchAnim;

    public void setVisibleListener(VisibleListener visibleListener) {
        this.visibleListener = visibleListener;
    }

    public interface VisibleListener {
        void onVisibleChange(FragmentViewContainer view, boolean visible, boolean touchAnim);

        void onMove(FragmentViewContainer view);
    }

    public FragmentViewContainer(Context context) {
        super(context);
        init();
    }

    public FragmentViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FragmentViewContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public FragmentViewContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setClickable(true);
        shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        backGroundPaint = new Paint();

//        ObjectAnimator transAnim = ObjectAnimator.ofFloat(this, "childTranX", getPrefix() * ScreenUtil.getScreenWidth() / 2, 0);
//        ObjectAnimator alpha = ObjectAnimator.ofFloat(this, "childAlpha", 0, 1);
//        showAnim = new AnimatorSet();
//        showAnim.addListener(listener);
//        showAnim.setDuration(ANIM_DURATION);
//        showAnim.playTogether(transAnim, alpha);
//        showAnim.setInterpolator(new DecelerateInterpolator());
//        hideAnim = new AnimatorSet();
//        transAnim = ObjectAnimator.ofFloat(this, "childTranX", 0, getPrefix() * ScreenUtil.getScreenWidth() / 2);
//        alpha = ObjectAnimator.ofFloat(this, "childAlpha", 1, 0);
//        hideAnim.addListener(listener);
//        hideAnim.setDuration(ANIM_DURATION);
//        hideAnim.playTogether(transAnim, alpha);
    }

    private float downX;
    private float dX;
    private float lastX;
    private boolean anim;
    private boolean hide;
    private boolean canPerformClick;
    private VelocityTracker mVelocityTracker;
    private boolean canMove = true;
    private boolean moveStarted;
    private boolean disallowIntercept;
    private float onInterceptTouchEventDownX;
    private float onInterceptTouchEventDownY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (anim || !canMove || getChildCount() <= 1) {
            return super.onInterceptTouchEvent(ev);
        }
        if (disallowIntercept && MotionEvent.ACTION_DOWN != ev.getAction()) {
            return super.onInterceptTouchEvent(ev);
        } else {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    disallowIntercept = false;
                    onInterceptTouchEventDownX = ev.getX();
                    onInterceptTouchEventDownY = ev.getY();
                    onTouchEvent(ev);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dx = ev.getX() - onInterceptTouchEventDownX;
                    float dy = ev.getY() - onInterceptTouchEventDownY;
                    if (Math.abs(dx) + Math.abs(dy) > INTERCEPTMINSIZE) {
                        return Math.abs(dx) > Math.abs(dy);
                    }
                    break;
            }
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
        this.disallowIntercept = disallowIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (anim || !canMove || getChildCount() <= 1) {
            return super.onTouchEvent(event);
        }
        initVelocityTrackerIfNotExists();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                canPerformClick = true;
                resetVelocityTracker();
                mVelocityTracker.addMovement(event);
                downX = event.getX();
                lastX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                if (canPerformClick) {
                    performClick();
                }
            case MotionEvent.ACTION_CANCEL:
                if (!canPerformClick) {
                    float translateX = getChildTop().getTranslationX();
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000);
                    int initialVelocity = (int) velocityTracker.getXVelocity();
                    if (hide) {
                        if (isRtl) {
                            if (initialVelocity > AUTOANIM_SPEED || translateX > -(ScreenUtil.getScreenWidth() - AUTOANIM_LIMIT)) {
                                animHideInner(initialVelocity);
                            } else {
                                animHideInner(initialVelocity);
                            }
                        } else {
                            if (initialVelocity < -AUTOANIM_SPEED || translateX < ScreenUtil.getScreenWidth() - AUTOANIM_LIMIT) {
                                animHideInner(initialVelocity);
                            } else {
                                animHideInner(initialVelocity);
                            }
                        }
                    } else {
                        if (isRtl) {
                            if (initialVelocity < -AUTOANIM_SPEED || translateX < -AUTOANIM_LIMIT) {
                                animHideInner(initialVelocity);
                            } else {
                                animShowInner(initialVelocity);
                            }
                        } else {
                            if (initialVelocity > AUTOANIM_SPEED || translateX > AUTOANIM_LIMIT) {
                                animHideInner(initialVelocity);
                            } else {
                                animShowInner(initialVelocity);
                            }
                        }
                    }
                }
                recycleVelocityTracker();
                moveStarted = false;
                break;
            case MotionEvent.ACTION_MOVE:
                dX = event.getX() - downX;
                if (CLICK_LIMIT < Math.abs(dX)) {
                    canPerformClick = false;
                }
                if (!hide && (isRtl ? dX > 0 : dX < 0)) {
                    downX = event.getX();
                    resetVelocityTracker();
                    mVelocityTracker.addMovement(event);
                    lastX = event.getX();
                    break;
                }
                dX = event.getX() - lastX;
                lastX = event.getX();
                initVelocityTrackerIfNotExists();
                mVelocityTracker.addMovement(event);
                if (!canPerformClick) {
                    float tranX = getChildTop().getTranslationX() + dX;
                    if (isRtl ? tranX > 0 : tranX < 0) {
                        tranX = 0;
                    }
                    if (!moveStarted && visibleListener != null) {
                        visibleListener.onMove(this);
                    }
                    if (!moveStarted) {
                        showBottom();
                    }
                    moveStarted = true;
                    setMovingX(tranX);
                }
                break;
        }
        return true;
    }

    private int getPrefix() {
        return isRtl ? -1 : 1;
    }

    private void resetVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private AnimatorSet hideAnim;

    public void animHide() {
        if (hideAnim != null && hideAnim.isRunning()) {
            hideAnim.end();
            showBottom();
        }
        if (getChildCount() <= 1) {
            post(new Runnable() {
                @Override
                public void run() {
                    if (null != visibleListener) {
                        visibleListener.onVisibleChange(FragmentViewContainer.this, false, false);
                    }
                }
            });
            return;
        }
        anim = true;
        visibleChange = true;
        hide = true;
        showBottom();
        final View view = getChildTop();
        hideAnim = new AnimatorSet();
        Animator transAnim = ObjectAnimator.ofFloat(view, "translationX", 0, getPrefix() * ScreenUtil.getScreenWidth() / 2);
        Animator alpha = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
        hideAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        anim = false;
                        removeView(view);
                        if (null != visibleListener) {
                            visibleListener.onVisibleChange(FragmentViewContainer.this, false, false);
                        }
                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        hideAnim.setDuration(ANIM_DURATION);
        hideAnim.playTogether(transAnim, alpha);
        hideAnim.start();
    }

    public void animShow() {
        if (getChildCount() <= 1) {
            return;
        }
        anim = true;
        visibleChange = true;
        hide = false;
        final View top = getChildTop();
        top.setTranslationX(getPrefix() * ScreenUtil.getScreenWidth() / 2);
        top.setAlpha(0);

        ObjectAnimator transAnim = ObjectAnimator.ofFloat(top, "translationX", getPrefix() * ScreenUtil.getScreenWidth() / 2, 0);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(top, "alpha", 0, 1);
        final AnimatorSet showAnim = new AnimatorSet();
        showAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                anim = false;
                hideBottom();
                if (null != visibleListener) {
                    visibleListener.onVisibleChange(FragmentViewContainer.this, true, false);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        showAnim.setDuration(ANIM_DURATION);
        showAnim.playTogether(transAnim, alpha);
        showAnim.setInterpolator(new DecelerateInterpolator());
        showAnim.start();
        post(new Runnable() {
            @Override
            public void run() {
                showAnim.start();
            }
        });

    }

    private void animHideInner(int initialVelocity) {
        if (getChildCount() <= 1) {
            return;
        }
        anim = true;
        hide = true;
        visibleChange = true;
        touchAnim = true;
        ObjectAnimator transAnim = ObjectAnimator.ofFloat(this, "movingX", getChildTop().getTranslationX(), getPrefix() * ScreenUtil.getScreenWidth());
        if (initialVelocity < ANIM_HIDE_SPEED) {
            initialVelocity = ANIM_HIDE_SPEED;
        }
        int duration = (int) ((ScreenUtil.getScreenWidth() - Math.abs(getChildTop().getTranslationX())) * 1000 / initialVelocity);
        transAnim.addListener(listener);
        transAnim.setDuration(duration);
        transAnim.start();
    }

    private void animShowInner(int initialVelocity) {
        if (getChildCount() <= 1) {
            return;
        }
        anim = true;
        visibleChange = false;
        hide = false;
        touchAnim = true;
        ObjectAnimator transAnim = ObjectAnimator.ofFloat(this, "movingX", getChildTop().getTranslationX(), 0);
        transAnim.addListener(listener);

        if (initialVelocity < ANIM_SHOW_SPEED) {
            initialVelocity = ANIM_SHOW_SPEED;
        }
        int duration = (int) (Math.abs(getChildTop().getTranslationX()) * 1000 / initialVelocity);
        transAnim.setDuration(duration);
        transAnim.start();
    }

    private Animator.AnimatorListener listener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            post(new Runnable() {
                @Override
                public void run() {
                    anim = false;
                    if (hide) {
                        removeView(getChildTop());
                    } else {
                        hideBottom();
                    }
                    if (null != visibleListener && visibleChange) {
                        visibleChange = false;
                        visibleListener.onVisibleChange(FragmentViewContainer.this, !hide, touchAnim);
                    }
                    touchAnim = false;
                    hide = false;
                }
            });

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public void setMovingX(float tranX) {
        touchAnim = true;
        getChildTop().setTranslationX(tranX);

        invalidate();
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (!touchAnim) {
            return super.drawChild(canvas, child, drawingTime);
        }
        float dx = Math.abs(getChildTop().getTranslationX() / getWidth()) * 2;
        int alpha = (int) (128 * (1 - dx));
        if (alpha < 0) {
            alpha = 0;
        }
        backGroundPaint.setColor(Color.argb(alpha, 0, 0, 0));

        Rect shadowRect;
        Rect backGroudRect;
        if (isRtl) {
            int right = (int) (getChildTop().getRight() + getChildTop().getTranslationX()) + getPrefix();
            shadowRect = new Rect(right
                    , getChildTop().getTop(), right + SHADOW_WIDTH, getChildTop().getBottom());
            LinearGradient lg = new LinearGradient(right, 0, right + SHADOW_WIDTH, 0, Color.argb(0x30, 0, 0, 0), Color.argb(0, 0, 0, 0), Shader.TileMode.CLAMP);
            shadowPaint.setShader(lg);
            backGroudRect = new Rect(right
                    , getChildTop().getTop(), getMeasuredWidth(), getChildTop().getBottom());
        } else {
            int left = (int) (getChildTop().getLeft() + getChildTop().getTranslationX()) + getPrefix();
            shadowRect = new Rect(left - SHADOW_WIDTH
                    , getChildTop().getTop(), left, getChildTop().getBottom());
            LinearGradient lg = new LinearGradient(left - SHADOW_WIDTH, 0, left, 0, Color.argb(0, 0, 0, 0), Color.argb(0x30, 0, 0, 0), Shader.TileMode.CLAMP);
            shadowPaint.setShader(lg);
            backGroudRect = new Rect(0
                    , getChildTop().getTop(), left, getChildTop().getBottom());
        }
        canvas.drawRect(backGroudRect, backGroundPaint);
        canvas.drawRect(shadowRect, shadowPaint);
        return super.drawChild(canvas, child, drawingTime);
    }

    public View getChildTop() {
        return getChildAt(getChildCount() - 1);
    }

    public View getChildBottom() {
        if (getChildCount() > 1) {
            return getChildAt(0);
        } else {
            return null;
        }
    }

    public void addTopContent(View top) {
        top.setTranslationX(0);
        if (getChildCount() > 1) {
            removeView(getChildAt(0));
            addView(top, params);
        } else {
            addView(top, params);
        }
        top.setVisibility(VISIBLE);
    }

    public void addBottomContent(View bottom) {
        bottom.setTranslationX(0);
        bottom.setAlpha(1);
        bottom.setVisibility(getChildCount() == 0 ? VISIBLE : GONE);
        if (bottom.getParent() != null) {
            ((ViewGroup) bottom.getParent()).removeView(bottom);
        }
        if (getChildCount() > 1) {
            removeView(getChildAt(0));
            addView(bottom, 0, params);
        } else {
            addView(bottom, 0, params);
        }
    }

    private void hideBottom() {
        View bottom = getChildBottom();
        if (bottom != null) {
            getChildBottom().setVisibility(GONE);
        }
    }

    private void showBottom() {
        View bottom = getChildBottom();
        if (bottom != null) {
            bottom.setTranslationX(0);
            bottom.setVisibility(VISIBLE);
        }
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        checkMove();
    }

    public void showTop(){
        View top = getChildTop();
        if (top != null) {
            top.setTranslationX(0);
            top.setVisibility(VISIBLE);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        child.setTranslationX(0);
        super.addView(child, index, params);
        checkMove();
    }

    private void checkMove() {
        setCanMove(getChildCount() > 1);
    }

    public void setChildTranX(float translate) {
        if (getChildTop() != null)
            getChildTop().setTranslationX(translate);
    }

    public void setChildAlpha(float alpha) {
        if (getChildTop() != null)
            getChildTop().setAlpha(alpha);
    }
}
