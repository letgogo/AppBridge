package com.freesky.appbridge;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by letgogo on 2018/11/20.
 */

public class WakeupService extends Service {

    private static final String TAG = WakeupService.class.getSimpleName();
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static WakeupCallback sWakeupCallback;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) {
            Log.d(TAG, "onStartCommand flags: " + flags + ", startId: " + startId);
        }
        //TODO 拓展代码时防止恶意调用
        if (sWakeupCallback != null) {
            sWakeupCallback.onWakeup();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void setInitCallback(WakeupCallback initCallback) {
        sWakeupCallback = initCallback;
    }
}
