package com.freesky.clientapp;

import android.app.Application;
import android.os.RemoteException;
import android.util.Log;

import com.freesky.appbridge.DSClient;
import com.freesky.appbridge.ProcessHelper;
import com.freesky.appbridge.ProcessHelperConfig;
import com.freesky.appbridge.WakeupCallback;
import com.freesky.appbridge.service.IMessageCallback;

/**
 * Created by letgogo on 2018/11/21.
 */

public class MainApp extends Application implements WakeupCallback {
    private static final String TAG = MainApp.class.getSimpleName();
    private static final boolean DEBUG = BuildConfig.DEBUG;

    @Override
    public void onCreate() {
        super.onCreate();
        initMessageDispatch();
    }


    @Override
    public void onWakeup() {
        initMessageDispatch();
    }

    private void initMessageDispatch() {
        ProcessHelper.init(new ProcessHelperConfig(this));
        if (DEBUG) {
            Log.d(TAG, "onWakeup ###");
        }
        IMessageCallback messageCallback = new IMessageCallback.Stub() {
            @Override
            public String onReceiveMessage(String msg) throws RemoteException {
                if (DEBUG) {
                    Log.d(TAG, "onReceiveMessage from 小程序端 msg: " + msg);
                }
                return "第三方返回给HostApp服务端的的result";
            }
        };
        DSClient.getInstance().registerMessageCallback(messageCallback);
    }

}
