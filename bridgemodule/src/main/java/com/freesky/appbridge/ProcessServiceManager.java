package com.freesky.appbridge;

import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.freesky.appbridge.ProcessHelper.ServiceName;

import java.util.HashMap;

/**
 * Created by letgogo on 2018/11/20.
 */
public class ProcessServiceManager {
    private final static String TAG = ProcessServiceManager.class.getSimpleName();
    private final static boolean DEBUG = BuildConfig.DEBUG;

    private final static HashMap<String, IBinder> sServiceCache = new HashMap<>();

    private final static HashMap<String, Class<? extends Binder>> sServiceStubClass = new HashMap<>();

    /**
     * 注册service binder class，getService时如果没有，现创建service binder对象
     */
    public static void registerService(@NonNull String serviceName,
                                       @NonNull Class<? extends Binder> serviceClazz) {
        sServiceStubClass.put(serviceName, serviceClazz);
    }

    @Nullable
    public static IBinder getService(@ServiceName @NonNull String serviceName) {
        synchronized (sServiceCache) {
            IBinder ibinder = sServiceCache.get(serviceName);
            if (ibinder == null) {
                final Class<? extends Binder> clazz = sServiceStubClass.get(serviceName);
                try {
                    ibinder = clazz.newInstance();
                    sServiceCache.put(serviceName, ibinder);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (DEBUG) {
                        throw new RuntimeException("create fail! service:" + serviceName);
                    }
                }
            }
            return ibinder;
        }
    }

    public static void removeService(@NonNull String serviceName) {
        synchronized (sServiceCache) {
            sServiceCache.remove(serviceName);
        }
    }
}
