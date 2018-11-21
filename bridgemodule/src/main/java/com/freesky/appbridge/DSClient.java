package com.freesky.appbridge;

import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.freesky.appbridge.service.IDispatchService;
import com.freesky.appbridge.service.IMessageCallback;

/**
 * Created by letgogo on 2018/11/20.
 */
@SuppressWarnings("unused")
public class DSClient {
    private static final String TAG = DSClient.class.getSimpleName();
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private IDispatchService mService;
    private String mClientPkgName;

    private static volatile DSClient sInstance;

    public static DSClient getInstance() {
        return SingleHolder.INSTANCE;
    }

    private static class SingleHolder {
        private static DSClient INSTANCE = new DSClient();
    }

    private DSClient() {
        mClientPkgName = ProcessHelperConfig.getAppContext().getPackageName();
    }

    @Nullable
    private IDispatchService ensureService(IDispatchService service) {
        if (service != null) {
            final IBinder iBinder = service.asBinder();
            if (iBinder != null && iBinder.isBinderAlive()) {
                return service;
            }
        }

        if (DEBUG) Log.d(TAG, "ensureService binder not alive");

        long startTime = 0;
        if (DEBUG) {
            startTime = System.currentTimeMillis();
        }

        IBinder iBinder = ProcessHelper.getService(ProcessHelper.PROCESS_HOST_MSG,
                ProcessHelper.HOST_MSG_SERVICE);
        service = IDispatchService.Stub.asInterface(iBinder);

        if (DEBUG) {
            try {
                Log.d(TAG, "ensureService time:" + (System.currentTimeMillis() - startTime));
            } catch (Exception e) {
                Log.e(TAG, "ensureService error", e);
            }
        }

        mService = service;
        return service;
    }

    @Nullable
    public String send(String msg) {
        final IDispatchService service = ensureService(mService);
        if (DEBUG) {
            Log.d(TAG, "service : " + service + ", send msg: " + msg);
        }
        if (service != null) {
            try {
                return service.send(mClientPkgName, msg);
            } catch (RemoteException e) {
                if (DEBUG) Log.e(TAG, "send fail!", e);
            }
        }
        return null;
    }

    public void registerMessageCallback(IMessageCallback messageCallback) {
        final IDispatchService service = ensureService(mService);
        if (DEBUG) {
            Log.d(TAG, "registerMessageCallback pkg: " + mClientPkgName + "service: " + service);
        }
        if (service != null) {
            try {
                service.registerMessageCallback(mClientPkgName, messageCallback);
            } catch (RemoteException e) {
                if (DEBUG) Log.e(TAG, "send fail!", e);
            }
        }
    }

}



