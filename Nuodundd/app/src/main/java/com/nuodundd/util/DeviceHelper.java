package com.nuodundd.util;

import android.content.res.Configuration;
import android.os.Build;
import android.util.LayoutDirection;
import com.nuodundd.application.ApplicationHelper;

/**
 * Created by admin on 2016/12/16.
 */

public class DeviceHelper {
    public static boolean isRtlLanguage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Configuration config = ApplicationHelper.getContext().getResources().getConfiguration();
            return LayoutDirection.RTL == config.getLayoutDirection();
        }
        return false;
    }
}
