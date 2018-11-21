package com.freesky.hostapp.service;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.freesky.appbridge.BinderWrapper;
import com.freesky.appbridge.ProcessServiceManager;
import com.freesky.hostapp.BuildConfig;

/**
 * Created by letgogo on 2018/11/20.
 * 消息分发Provider
 */
public class MessageDispatchProvider extends ContentProvider {
    private static final String TAG = MessageDispatchProvider.class.getSimpleName();
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public static final String PROC_PROVIDER_KEY = "proc_binder";
    public static final String PROC_PROVIDER_GET_SERVICE = "get_service";

    @Override
    public boolean onCreate() {
        if (DEBUG) {
            Log.d(TAG, "onCreate");
        }
        return true;
    }

    /**
     * @param method methond
     * @param arg    method arg, service name
     * @param extras method args, service binder
     */
    @Nullable
    @Override
    public Bundle call(@NonNull String method, String arg, Bundle extras) {
        if (DEBUG) {
            Log.d(TAG, "call method=" + method + ", arg=" + arg);
        }
        if (!TextUtils.isEmpty(method) && !TextUtils.isEmpty(arg)) {
            if (method.equals(PROC_PROVIDER_GET_SERVICE)) {
                IBinder iBinder = ProcessServiceManager.getService(arg);
                if (iBinder != null) {
                    BinderWrapper binderParcelable = new BinderWrapper(iBinder);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(PROC_PROVIDER_KEY, binderParcelable);
                    return bundle;
                }
            } else {
                // add and remove service not need it for now
                if (DEBUG) {
                    throw new RuntimeException("not support method=" + method);
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        String[] projection,
                        String selection, String[] selectionArgs,
                        String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
