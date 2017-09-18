package com.nuodundd.viewstack;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by nuodundd on 17/1/17.
 */

public class FragmentManager<T extends Activity> implements IFragmentManager {
    private static final String TAG = FragmentManager.class.getSimpleName();
    private T activity;
    private ViewGroup container;
    private FragmentViewContainer mFragmentViewContainer;
    private LinkedList<Fragment> fragmentstack = new LinkedList<>();
    private Bundle savedInstanceState;
    private VisibleListenerImpl mVisibleListenerImpl = new VisibleListenerImpl();
    private Fragment nowFragment;
    private Fragment lastFragment;
    private List<Fragment> finishFramentList = new ArrayList<>();

    public FragmentManager(T activity, int contentId) {
        this.activity = activity;
        container = (ViewGroup) activity.findViewById(contentId);
        init();
    }

    public FragmentManager(T activity, int contentId, Bundle savedInstanceState) {
        this.activity = activity;
        container = (ViewGroup) activity.findViewById(contentId);
        this.savedInstanceState = savedInstanceState;
        init();
    }

    public FragmentManager(T activity, ViewGroup content) {
        this.activity = activity;
        container = content;
        init();
    }

    public FragmentManager(T activity, ViewGroup content, Bundle savedInstanceState) {
        this.activity = activity;
        container = content;
        this.savedInstanceState = savedInstanceState;
        init();
    }

    private void init() {
        mFragmentViewContainer = new FragmentViewContainer(activity);
        container.addView(mFragmentViewContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFragmentViewContainer.setVisibleListener(mVisibleListenerImpl);
    }

    @Override
    public void startNewFragment(Class<? extends Fragment> frament) {
        startNewFragment(null, frament);
    }

    @Override
    public void startNewFragment(Intent intent, Class<? extends Fragment> frament) {
        startNewFragment(intent, frament, false);
    }

    @Override
    public void startNewFragment(Class<? extends Fragment> frament, boolean pop) {
        startNewFragment(null, frament, pop, true);
    }

    @Override
    public void startNewFragment(Intent intent, Class<? extends Fragment> frament, boolean pop) {
        startNewFragment(intent, frament, pop, true, true);
    }

    @Override
    public void startNewFragment(Intent intent, Class<? extends Fragment> frament, boolean pop, boolean anim) {
        startNewFragment(intent, frament, pop, anim, true);
    }

    @Override
    public void startNewFragment(Intent intent, Class<? extends Fragment> frament, boolean pop, boolean anim, boolean addToStack) {
        startNewFragment(intent, frament, pop, anim, addToStack, -1);
    }

    @Override
    public void startFragmentForResult(Intent intent, Class<? extends Fragment> fragment, int requestCode) {
        startNewFragment(intent, fragment, false, true, true, requestCode);
    }

    public void startNewFragment(Intent intent, Class<? extends Fragment> frament, boolean pop, boolean anim, boolean addToStack, int requestCode) {
        if (pop) {
            if (getIndex(frament) != -1) {
                popTo(intent, frament, anim);
                return;
            }
        }
        try {
            lastFragment = nowFragment;
            boolean reload = false;
            if (intent != null) {
                if ((intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TASK) == Intent.FLAG_ACTIVITY_CLEAR_TASK) {
                    removeTo(-1);
                } else if ((intent.getFlags() & Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) == Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) {
                    int index = getIndex(frament);
                    if (index != -1) {
                        nowFragment = fragmentstack.remove(index);
                        reload = true;
                    }
                } else if ((intent.getFlags() & Intent.FLAG_ACTIVITY_SINGLE_TOP) == Intent.FLAG_ACTIVITY_SINGLE_TOP) {
                    if (nowFragment != null && nowFragment.getClass().equals(frament)) {
                        nowFragment.onNewIntent(intent);
                        return;
                    }
                }
            }

            if (!reload) {
                frament.newInstance();
                nowFragment = frament.newInstance();
            }
            if (nowFragment != null && nowFragment == lastFragment) {
                if (intent != null) {
                    nowFragment.onNewIntent(intent);
                }
                return;
            }
            if (addToStack) {
                fragmentstack.add(nowFragment);
            }
            if (!reload)
                nowFragment.bindManager(this);
            if (intent != null) {
                nowFragment.setIntent(intent);
            }
            if (!reload)
                nowFragment.onAttach(activity);
            if (requestCode > 0) {
                nowFragment.onRequest(requestCode);
            }
            if (!reload)
                nowFragment.onCreate(savedInstanceState);
            createView(nowFragment);
            if (nowFragment.getView() != null) {
                if (nowFragment.getView().getBackground() == null) {
                    nowFragment.getView().setBackgroundColor(Color.WHITE);
                }
            }
            mFragmentViewContainer.addTopContent(nowFragment.getView());
            nowFragment.onViewCreated();
//            contentTop.setBackgroundColor(Color.BLUE);

            if (anim && mFragmentViewContainer.getChildBottom() != null) {
                if (lastFragment != null) {
                    lastFragment.onPause();
                }
                mFragmentViewContainer.animShow();
            } else {
                if (lastFragment != null) {
                    lastFragment.onPause();
                }
                if (lastFragment != null) {
                    lastFragment.onStop();
                }
                nowFragment.onResume();
                setBottom();
            }
            mFragmentViewContainer.setCanMove(mFragmentViewContainer.getChildCount() > 1);
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
    }


    private int getIndex(Class<? extends Fragment> frament) {
        for (int i = fragmentstack.size() - 1; i > 0; i--) {
            if (fragmentstack.get(i).getClass().equals(frament)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void finish() {
        finish(nowFragment);
    }

    @Override
    public void finish(Fragment fragment, boolean anim) {
        if (fragment.isFinished()) {
            return;
        }
        fragment.setFinished(true);
        if (nowFragment != fragment) {
            if (fragmentstack.size() > 1) {
                fragment.onPause();
                fragment.onStop();
                fragmentstack.remove(fragment);
                if (fragment.getView() == null || fragment.getView().getParent() == null || ((View) fragment.getView().getParent()).getVisibility() == View.GONE) {
                    fragment.onDestroyView();
                    fragment.onDestroy();
                    fragment.onDetach();
                } else {
                    finishFramentList.add(fragment);
                }
            }
        } else {
            if (fragmentstack.size() > 0) {
                if (fragmentstack.indexOf(nowFragment) != -1) {
                    if (fragmentstack.size() > 1) {
                        popTo(null, fragmentstack.get(fragmentstack.size() - 2), anim);

                    }
                } else {
                    if (fragmentstack.size() > 0) {
                        popTo(null, fragmentstack.get(fragmentstack.size() - 1), anim);
                    }
                }
            }
        }
    }

    @Override
    public void finish(Fragment fragment) {
        finish(fragment, true);
    }

    @Override
    public void popTo(Intent intent, Fragment fragment, boolean anim) {
        if (fragment == nowFragment) {
            if (intent != null)
                nowFragment.onNewIntent(intent);
            return;
        }
        int index = fragmentstack.indexOf(fragment);
        if (index != -1) {
            pop(index, anim, intent);
        }
    }

    @Override
    public void popTo(Intent intent, Class<? extends Fragment> fragment, boolean anim) {
        if (fragment.equals(nowFragment.getClass())) {
            if (intent != null)
                nowFragment.onNewIntent(intent);
            return;
        }
        int index = getIndex(fragment);
        if (index != -1) {
            pop(index, anim, intent);
        }
    }

    private void pop(int index, boolean anim, Intent intent) {
        lastFragment = nowFragment;
        nowFragment = fragmentstack.get(index);
        removeTo(index);
        createView(nowFragment);
        if (!anim && nowFragment.getView().getParent() == null) {
            mFragmentViewContainer.addBottomContent(nowFragment.getView());
            nowFragment.onViewCreated();
        }
        if (intent != null) {
            nowFragment.onNewIntent(intent);
        }
        if (anim && lastFragment != null) {
            lastFragment.onPause();
            mFragmentViewContainer.animHide();
        } else {
            lastFragment.onPause();
            lastFragment.onStop();
            nowFragment.onResume();
            lastFragment.onDestroyView();
            lastFragment.onDestroy();
            lastFragment.onDetach();
            setBottom();
        }
        if (mFragmentResult != null) {
            nowFragment.onFragmentResult(mFragmentResult.requestCode, mFragmentResult.resultCode, mFragmentResult.intent);
            mFragmentResult = null;
        }
    }

    private void createView(Fragment fragment) {
        if (fragment.getView() == null) {
            fragment.onCreate(savedInstanceState);
            View v = fragment.onCreateView(LayoutInflater.from(activity), mFragmentViewContainer, savedInstanceState);
            if (fragment.getView() == null) {
                fragment.setView(v);
            }
        }
    }


    private void removeTo(int index) {
        int size = fragmentstack.size();

        for (int i = size - 1; i > index; i--) {
            Fragment fragment = fragmentstack.remove(i);
            if (lastFragment == fragment) {
                fragmentstack.remove(fragment);
                continue;
            }
            if (fragment.isActive()) {
                fragment.onPause();
                fragment.onStop();
            }
            if (!fragment.isDestroy()) {
                fragment.onDestroyView();
                fragment.onDestroy();
            }
            fragmentstack.remove(fragment);
            fragment.onDetach();
        }
    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public void onLowMemory() {
        if (fragmentstack.indexOf(nowFragment) >= 0) {
            for (int i = 0; i < fragmentstack.size() - 2; i++) {
                fragmentstack.get(i).onDestroyView();
                fragmentstack.get(i).onLowMemory();
            }
            if (fragmentstack.size() >= 2) {
                fragmentstack.get(fragmentstack.size() - 2).onLowMemory();
            }
        } else {
            for (int i = 0; i < fragmentstack.size() - 1; i++) {
                fragmentstack.get(i).onDestroyView();
                fragmentstack.get(i).onLowMemory();
            }
            if (fragmentstack.size() >= 1) {
                fragmentstack.get(fragmentstack.size() - 1).onLowMemory();
            }
        }
    }

    private boolean canBack() {
        if (fragmentstack.size() <= 0) {
            return false;
        } else if (fragmentstack.size() == 1 && fragmentstack.indexOf(nowFragment) >= 0) {
            return false;
        }
        return true;
    }

    class VisibleListenerImpl implements FragmentViewContainer.VisibleListener {
        @Override
        public void onVisibleChange(FragmentViewContainer view, boolean visible, boolean touchAnim) {
            if (visible) {//from start new
                if (lastFragment != null) {
                    lastFragment.onStop();
                    nowFragment.onResume();
                    if (!canBack()) {
                        mFragmentViewContainer.setCanMove(false);
                        if (lastFragment.getView() != null && lastFragment.getView().getParent() != null)
                            ((ViewGroup) lastFragment.getView().getParent()).removeView(lastFragment.getView());
                    }
                    if (fragmentstack.indexOf(lastFragment) == -1) {//if not exist in stack,destroy it
                        lastFragment.onDestroyView();
                        lastFragment.onDestroy();
                        lastFragment.onDetach();
                    }
                    setBottom();
                } else {
                    nowFragment.onResume();
                    if (fragmentstack.size() <= 1 && fragmentstack.indexOf(nowFragment) != -1) {
                        mFragmentViewContainer.setCanMove(false);
                    }
                }
            } else if (touchAnim) {//from slide finish
                finish(nowFragment, false);
            } else {
                //from manual finish
                if (lastFragment != null) {
                    lastFragment.onStop();
                    nowFragment.onResume();
                    if (!canBack()) {
                        mFragmentViewContainer.setCanMove(false);
                        if (lastFragment.getView() != null && lastFragment.getView().getParent() != null)
                            ((ViewGroup) lastFragment.getView().getParent()).removeView(lastFragment.getView());
                    }
                    if (fragmentstack.indexOf(lastFragment) == -1) {//if not exist in stack,destroy it
                        lastFragment.onDestroyView();
                        lastFragment.onDestroy();
                        lastFragment.onDetach();
                    }
                } else {
                    nowFragment.onResume();
                }

                setBottom();
            }
            checkFinish();
        }

        @Override
        public void onMove(FragmentViewContainer view) {
            if (fragmentstack.indexOf(nowFragment) != -1) {
                if (fragmentstack.size() > 1) {
                    fragmentstack.get(fragmentstack.size() - 2).onBecomeVisible();
                }
            } else {
                if (fragmentstack.size() > 0) {
                    fragmentstack.get(fragmentstack.size() - 1).onBecomeVisible();
                }
            }
            nowFragment.onMove();
        }
    }

    private void checkFinish() {
        if (finishFramentList.isEmpty()) {
            return;
        }
        List<Fragment> removeList = new ArrayList<>();
        for (Fragment fragment : finishFramentList) {
            if (fragment.getView() == null || fragment.getView().getParent() == null || ((View) fragment.getView().getParent()).getVisibility() == View.GONE) {
                fragment.onDestroyView();
                fragment.onDestroy();
                fragment.onDetach();
                removeList.add(fragment);
            }
        }
        finishFramentList.removeAll(removeList);
    }

    private void setBottom() {
        if (fragmentstack.size() > 1) {
            if (nowFragment.getView() != null && nowFragment.getView().getParent() == null) {
                mFragmentViewContainer.addBottomContent(nowFragment.getView());
                mFragmentViewContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        mFragmentViewContainer.showTop();
                    }
                });
            }
            int index = fragmentstack.indexOf(nowFragment);
            Fragment fragment;
            if (index > 0) {
                fragment = fragmentstack.get(index - 1);
            } else {
                fragment = fragmentstack.get(fragmentstack.size() - 1);
            }
            boolean newCreate = false;
            if (fragment.getView() == null) {
                createView(fragment);
                newCreate = true;
            }
            mFragmentViewContainer.addBottomContent(fragment.getView());
            if (newCreate) {
                nowFragment.onViewCreated();
            }
        } else {
            if (fragmentstack.size() > 0 && mFragmentViewContainer.getChildCount() == 0) {
                Fragment fragment = fragmentstack.get(0);
                boolean newCreate = false;
                if (fragment.getView() == null) {
                    createView(fragment);
                    newCreate = true;
                }
                mFragmentViewContainer.addBottomContent(fragment.getView());
                if (newCreate) {
                    nowFragment.onViewCreated();
                }
            }
            mFragmentViewContainer.setCanMove(false);
        }
    }

    public Fragment getNowFragment() {
        return nowFragment;
    }

    private FragmentResult mFragmentResult;

    @Override
    public void setResult(Intent intent, int requestCode, int resultCode) {
        mFragmentResult = new FragmentResult(intent, requestCode, resultCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (nowFragment != null)
            nowFragment.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public boolean onBackKey() {
        if (nowFragment != null && nowFragment.onKeyBack()) {
            return true;
        }
        if (canBack()) {
            finish();
            return true;
        }
        return false;
    }

    class FragmentResult {
        Intent intent;
        int requestCode;
        int resultCode;

        public FragmentResult(Intent intent, int requestCode, int resultCode) {
            this.intent = intent;
            this.requestCode = requestCode;
            this.resultCode = resultCode;
        }
    }

    @Override
    public void onActivityStop() {
        if (nowFragment != null) {
            nowFragment.onStop();
        }
    }

    @Override
    public void onActivityPause() {
        if (nowFragment != null) {
            nowFragment.onPause();
        }
    }

    @Override
    public void onActivityResume() {
        if (nowFragment != null) {
            nowFragment.onResume();
        }
    }

    @Override
    public void onActivityDestroy() {
        for (Fragment fragment : fragmentstack) {
            fragment.onDestroy();
        }
        if (fragmentstack.indexOf(nowFragment) == -1 && nowFragment != null) {
            nowFragment.onDestroy();
        }
    }

    @Override
    public boolean isTaskRoot() {
        return !canBack();
    }

}
