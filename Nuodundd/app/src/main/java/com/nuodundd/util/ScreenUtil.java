package com.nuodundd.util;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.nuodundd.application.ApplicationHelper;


/**
 * Created by nuodundd on 16/12/26.
 */

public class ScreenUtil {
    private static final String TAG = ScreenUtil.class.getSimpleName();
    public static float density = 1;
    public static Point displaySize = new Point();
    private static int statusBarHeight;
    private static int actionBarHeight;

    static {
        density = ApplicationHelper.getContext().getResources().getDisplayMetrics().density;
        checkDisplaySize();

        int resourceId = ApplicationHelper.getContext().getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = ApplicationHelper.getContext().getResources().getDimensionPixelSize(
                    resourceId);
        } else {
            statusBarHeight = (int) dip2px(25);
        }
        final TypedArray styledAttributes = ApplicationHelper.getContext().getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        if (actionBarHeight <= 0) {
            actionBarHeight = (int) dip2px(56);
        }
    }

    public static void checkDisplaySize() {
        try {
            WindowManager manager = (WindowManager) ApplicationHelper.getContext().getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();

                boolean isScreenLandscape = ApplicationHelper.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

                Point displaySizeOrginal = new Point();
                if (display != null) {
                    if (android.os.Build.VERSION.SDK_INT < 13) {
                        displaySizeOrginal.set(display.getWidth(), display.getHeight());
                    } else {
                        display.getSize(displaySizeOrginal);
                    }
                    Log.i(TAG, "displaySizeOrginal size = " + displaySizeOrginal.x + " " + displaySizeOrginal.y + " isScreenLandscape:" + isScreenLandscape);
                    if (isScreenLandscape) {
                        displaySize.x = displaySizeOrginal.y;
                        displaySize.y = displaySizeOrginal.x;
                    } else {
                        displaySize.x = displaySizeOrginal.x;
                        displaySize.y = displaySizeOrginal.y;
                    }
                }
            }
        } catch (Exception e) {
            Log.w(TAG, e.toString());
        }
    }

    public final static float dip2px(float dpValue) {
        return (dpValue * density);
    }

    public final static float px2dip(float pxValue) {
        return (pxValue / density);
    }

    public final static float px2sp(float pxValue) {
        final float scale = ApplicationHelper.getContext().getResources()
                .getDisplayMetrics().scaledDensity;
        return (pxValue / scale);
    }

    public final static float sp2px(float spValue) {
        final float scale = ApplicationHelper.getContext().getResources()
                .getDisplayMetrics().scaledDensity;
        return (spValue * scale);
    }

    public static int getScreenWidth() {
        return displaySize.x;
    }

    public static int getScreenHeight() {
        return displaySize.y;
    }

    public static int getStatusBarHeight() {
        return statusBarHeight;
    }

    public static boolean isScreenOn() {
        // return m_isScreenOn;
        Context context = ApplicationHelper.getContext();
        KeyguardManager km = (KeyguardManager) context
                .getSystemService(Context.KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {
            return false;
        }
        return true;
    }

    public static int getActionBarHeight() {
        return actionBarHeight;
    }

}
