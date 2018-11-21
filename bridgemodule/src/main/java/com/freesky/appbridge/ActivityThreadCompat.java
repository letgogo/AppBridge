package com.freesky.appbridge;

import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by letgogo on 2018/11/20.
 */
public class ActivityThreadCompat {
    private static final String TAG = ActivityThreadCompat.class.getSimpleName();
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static Class<?> sClass_ActivityThread;
    private static Method sMtd_currentActivityThread;
    private static Method sMtd_getProcessName;
    private static Method sMtd_currentProcessName;

    static {
        try {
            sClass_ActivityThread = Class.forName("android.app.ActivityThread");
        } catch (ClassNotFoundException e) {
            if (DEBUG) Log.w(TAG, "class not found", e);
        }
    }

    private ActivityThreadCompat() {
        // nothing to do
    }

    private static void reflect_currentActivityThread() {
        if (sMtd_currentActivityThread != null || sClass_ActivityThread == null) {
            return;
        }

        try {
            // public static ActivityThread currentActivityThread()
            sMtd_currentActivityThread = sClass_ActivityThread.getMethod("currentActivityThread");
        } catch (NoSuchMethodException e) {
            if (DEBUG) Log.w(TAG, "method not found", e);
        }
    }

    @VisibleForTesting
    static boolean checkReflect_currentActivityThread() {
        reflect_currentActivityThread();
        return sMtd_currentActivityThread != null;
    }

    @Nullable
    public static Object currentActivityThread() {
        if (DEBUG && !(Looper.myLooper() == Looper.getMainLooper())
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            throw new RuntimeException("Please invoke the method in UI Thread");
        }

        reflect_currentActivityThread();
        if (sMtd_currentActivityThread != null) {
            try {
                return sMtd_currentActivityThread.invoke(null);
            } catch (IllegalAccessException e) {
                if (DEBUG) Log.w(TAG, "failed to invoke #currentActivityThread()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) Log.w(TAG, "failed to invoke #currentActivityThread() more", e);
            }
        }
        return null;
    }

    private static void reflect_getProcessName() {
        if (sMtd_getProcessName != null || sClass_ActivityThread == null) {
            return;
        }

        try {
            // public String getProcessName()
            sMtd_getProcessName = sClass_ActivityThread.getMethod("getProcessName");
        } catch (NoSuchMethodException e) {
            if (DEBUG) Log.w(TAG, "method not found", e);
        }
    }

    @VisibleForTesting
    @Nullable
    static String getProcessName(@NonNull Object activityThread) {
        reflect_getProcessName();
        if (sMtd_getProcessName != null) {
            try {
                return (String) sMtd_getProcessName.invoke(activityThread);
            } catch (IllegalAccessException e) {
                if (DEBUG) Log.w(TAG, "failed to invoke #getProcessName()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) Log.w(TAG, "failed to invoke #getProcessName() more", e);
            }
        }
        return null;
    }

    private static void reflect_currentProcessName() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return;
        }

        if (sMtd_currentProcessName != null || sClass_ActivityThread == null) {
            return;
        }

        try {
            // public static String currentProcessName()
            sMtd_currentProcessName = sClass_ActivityThread.getMethod("currentProcessName");
        } catch (NoSuchMethodException e) {
            if (DEBUG) Log.w(TAG, "method not found", e);
        }
    }

    @VisibleForTesting
    static boolean checkReflect_currentProcessName() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return false;
        }

        reflect_currentProcessName();
        return sMtd_currentProcessName != null;
    }

    @VisibleForTesting
    @Nullable
    static String currentProcessName() {
        reflect_currentProcessName();
        if (sMtd_currentProcessName != null) {
            try {
                return (String) sMtd_currentProcessName.invoke(null);
            } catch (IllegalAccessException e) {
                if (DEBUG) Log.w(TAG, "failed to invoke #getProcessName()", e);
            } catch (InvocationTargetException e) {
                if (DEBUG) Log.w(TAG, "failed to invoke #getProcessName() more", e);
            }
        }
        return null;
    }

    @Nullable
    public static String getCurrentProcessName() {
        if (DEBUG && !(Looper.getMainLooper() == Looper.myLooper())
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            throw new RuntimeException("Please invoke the method in UI Thread");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            String processName = currentProcessName();
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }
        }

        Object activityThread = currentActivityThread();
        if (activityThread != null) {
            return getProcessName(activityThread);
        }
        return null;
    }
}
