package com.nuodundd.application;

import android.app.Application;

/**
 * Created by nuodundd on 2017/9/18.
 */

public class NuodunddApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationHelper.setContext(this);
    }
}
