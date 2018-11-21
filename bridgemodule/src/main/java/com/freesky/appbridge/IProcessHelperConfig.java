package com.freesky.appbridge;

import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.freesky.appbridge.ProcessHelper.ProcessType;
import com.freesky.appbridge.ProcessHelper.ServiceName;

/**
 * Created by letgogo on 2018/11/20.
 */
public interface IProcessHelperConfig {

    void registerService(@ProcessType int processType,
                         @ServiceName @NonNull String serviceName,
                         @NonNull Class<? extends Binder> serviceClazz);

    IBinder getService(@ProcessType int processType,
                       @ServiceName @NonNull String serviceName);

    /**
     * NOTE: 不建议跨进程移除service
     */
    void removeService(@ProcessType int processType,
                       @ServiceName @NonNull String serviceName);
}
