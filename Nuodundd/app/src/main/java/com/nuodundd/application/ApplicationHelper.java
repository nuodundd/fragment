package com.nuodundd.application;

import android.content.Context;
import android.os.Handler;

/**
 * Created by admin on 2016/12/16.
 */

public class ApplicationHelper {
    private static final String TAG = ApplicationHelper.class.getSimpleName();
    private static Context context;
    private static Handler mHandler = new Handler();
    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        ApplicationHelper.context = context;
    }

    public static void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    public static void postDelay(Runnable runnable,long delayMillis) {
        mHandler.postDelayed(runnable,delayMillis);
    }
}
