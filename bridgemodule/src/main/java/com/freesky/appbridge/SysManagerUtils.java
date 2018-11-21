package com.freesky.appbridge;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by letgogo on 2018/11/20.
 */
public class SysManagerUtils {

    @NonNull
    public static List<ActivityManager.RunningServiceInfo> getRunningServices(
            @Nullable ActivityManager am, int maxNum) {
        List<ActivityManager.RunningServiceInfo> runServiceList = null;
        try {
            if (am != null) {
                runServiceList = am.getRunningServices(maxNum);
            }
        } catch (Exception e) {
            // NullPointerException or IndexOutOfBoundsException may be thrown on some devices
            // in the implementation of ActivityManager#getRunningServices().
        }
        if (runServiceList == null) {
            runServiceList = new ArrayList<>();
        }
        return runServiceList;
    }

    @NonNull
    public static List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses(
            @Nullable ActivityManager am) {
        List<ActivityManager.RunningAppProcessInfo> runProcessList = null;
        try {
            if (am != null) {
                runProcessList = am.getRunningAppProcesses();
            }
        } catch (Exception e) {
            // NullPointerException or IndexOutOfBoundsException may be thrown on some devices
            // in the implementation of ActivityManager#getRunningAppProcesses().
        }
        if (runProcessList == null) {
            runProcessList = new ArrayList<>();
        }
        return runProcessList;
    }

    @NonNull
    public static List<PackageInfo> getInstalledPackages(@Nullable PackageManager pm, int flags) {
        List<PackageInfo> installedApps = null;
        try {
            if (pm != null) {
                installedApps = pm.getInstalledPackages(flags);
            }
        } catch (Exception e) {
            // IndexOutOfBoundsException may be thrown on some devices
            // in the implementation of PackageManager#getInstalledPackages().
        }
        if (installedApps == null) {
            installedApps = new ArrayList<>();
        }
        return installedApps;
    }

    @Nullable
    public static PackageManager getPackageManager(@NonNull Context context) {
        PackageManager pm = null;
        try {
            pm = context.getPackageManager();
//            if (PackageCompat.isPackageManagerServiceAlive(pm)) {
//                return pm;
//            }
        } catch (Throwable e) {
            // sometimes RuntimeException may be thrown caused by Package manager has died
            return null;
        }
        return pm;
    }

    @Nullable
    public static ActivityManager getActivityManager(@NonNull Context context) {
        ActivityManager am = null;
        try {
            am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        } catch (Throwable e) {
            // sometimes RuntimeException may be thrown caused by Activity manager has died
        }
        return am;
    }
}
