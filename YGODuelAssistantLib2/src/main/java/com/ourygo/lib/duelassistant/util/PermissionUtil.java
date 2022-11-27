package com.ourygo.lib.duelassistant.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

public class PermissionUtil {

    //判断应用是否开启了通知权限
    public static boolean isNotificationListenerEnabled(Context context) {
//        return NotificationManager.from(context).areNotificationsEnabled();
        return false;
    }

    //判断是否有悬浮窗权限
    public static boolean isServicePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context))
                return false;
        }
        return true;
    }

}
