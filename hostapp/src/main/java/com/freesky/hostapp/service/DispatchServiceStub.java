package com.freesky.hostapp.service;

import android.os.RemoteException;
import android.util.Log;

import com.freesky.appbridge.service.IDispatchService;
import com.freesky.appbridge.service.IMessageCallback;
import com.freesky.hostapp.BuildConfig;


/**
 * Created by letgogo on 2018/11/20.
 * 消息分发服务端Binder
 */
public class DispatchServiceStub extends IDispatchService.Stub {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = DispatchServiceStub.class.getSimpleName();

    public DispatchServiceStub() {
        if (DEBUG) {
            Log.d(TAG, "DispatchServiceStub inited");
        }
    }

    @Override
    public String send(String pkg, String msg) throws RemoteException {
        if (DEBUG) {
            Log.d(TAG, "send from pkg: " + pkg + ", msg: " + msg);
        }
        return MessageDispatcher.getInstance().handleClientMessage(new Message(pkg, msg));
    }

    @Override
    public void registerMessageCallback(String pkg,
                                        IMessageCallback messageCallback)
            throws RemoteException {
        if (DEBUG) {
            Log.d(TAG, "registerMessageCallback from pkg: " + pkg + ", messageCallback: " + messageCallback);
        }
        MessageDispatcher.getInstance().registerMessageCallback(pkg, messageCallback);
    }

}
