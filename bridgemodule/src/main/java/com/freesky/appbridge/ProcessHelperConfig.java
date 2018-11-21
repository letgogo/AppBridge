package com.freesky.appbridge;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by letgogo on 2018/11/20.
 */
public class ProcessHelperConfig implements IProcessHelperConfig {
    private final static String TAG = ProcessHelperConfig.class.getSimpleName();
    private final static boolean DEBUG = BuildConfig.DEBUG;

    private final static String AUTH_PREFIX = "content://" + ProcessHelper.REAL_PACKAGE_NAME;

    private class BinderRecord {
        public IBinder iBinder;

        private BinderDeath mBd;

        public BinderRecord(@NonNull IBinder iBinder) {
            this.iBinder = iBinder;
        }

        public void linkToDeath() throws RemoteException {
            synchronized (this) {
                if (iBinder != null && mBd == null) {
                    mBd = new BinderDeath(this);
                    iBinder.linkToDeath(mBd, 0);
                }
            }
        }

        public void unlinkToDeath() {
            synchronized (this) {
                if (iBinder != null && mBd != null) {
                    iBinder.unlinkToDeath(mBd, 0);
                    iBinder = null;
                    mBd = null;
                }
            }
        }

        private class BinderDeath implements IBinder.DeathRecipient {
            private final ProcessHelperConfig.BinderRecord mBr;

            BinderDeath(@NonNull ProcessHelperConfig.BinderRecord br) {
                mBr = br;
            }

            public void binderDied() {
                synchronized (mBr) {
                    if (mBr.iBinder == null) {
                        return;
                    }

                    mBr.iBinder.unlinkToDeath(this, 0);
                    mBr.iBinder = null;
                }
            }
        }
    }

    private final static HashMap<String, BinderRecord> sRemoteServiceCache = new HashMap<>();
    private static Application sApp;

    public ProcessHelperConfig(Object obj) {
        if (DEBUG) Log.d(TAG, "ProcessHelperConfig obj : " + obj);
        if (obj instanceof WakeupCallback) {
            WakeupService.setInitCallback((WakeupCallback) obj);
        }
        if (obj instanceof Application) {
            sApp = (Application) obj;
        }
    }

    @Override
    public void registerService(@ProcessHelper.ProcessType int processType,
                                @ProcessHelper.ServiceName @NonNull String serviceName,
                                @NonNull Class<? extends Binder> serviceClazz) {
        final int type = ProcessHelper.getCurrentProcessType();
        System.out.println("processType: " + processType + ", type: " + type);
        if (type == processType) {
            ProcessServiceManager.registerService(serviceName, serviceClazz);
        } else {
            if (DEBUG) {
                throw new RuntimeException("not support add service=" + serviceName
                        + ", processType=" + processType);
            }
        }
    }

    @Override
    public IBinder getService(@ProcessHelper.ProcessType int processType,
                              @ProcessHelper.ServiceName @NonNull String serviceName) {
        final int type = ProcessHelper.getCurrentProcessType();
        if (DEBUG) {
            Log.d(TAG, "getService type: " + type + ", processType: " + processType);
        }
        if (type == processType) {
            return ProcessServiceManager.getService(serviceName);
        } else {
            synchronized (sRemoteServiceCache) {
                BinderRecord br = sRemoteServiceCache.get(serviceName);
                if (br != null && br.iBinder != null) {
                    return br.iBinder;
                }

                try {
                    // binder not fetch or died
                    String processName = ProcessHelper.getProcessNameSurfix(processType);
                    String uriString = AUTH_PREFIX;
                    if (!TextUtils.equals(processName, ProcessHelper.REAL_PACKAGE_NAME)) {
                        uriString = new StringBuilder(".")
                                .append(ProcessHelper.getProcessNameSurfix(processType))
                                .toString();
                    }
                    final Uri uri = Uri.parse(uriString);
                    if (DEBUG) {
                        Log.d(TAG, "getService uri: " + uri);
                    }
                    final ContentResolver cr = sApp.getContentResolver();
                    final Bundle bundle = cr.call(uri, "get_service",
                            serviceName, null);
                    if (bundle != null) {
                        bundle.setClassLoader(ProcessHelperConfig.class.getClassLoader());
                        BinderWrapper bp = bundle.getParcelable("proc_binder");

                        if (bp != null) {
                            br = new BinderRecord(bp.mIBinder);
                            br.linkToDeath();
                            sRemoteServiceCache.put(serviceName, br);
                            return bp.mIBinder;
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    if (DEBUG) {
                        throw new RuntimeException("getService service=" + serviceName
                                + ", processType=" + processType);
                    }
                }
            }
            return null;
        }
    }

    @Override
    public void removeService(@ProcessHelper.ProcessType int processType,
                              @ProcessHelper.ServiceName @NonNull String serviceName) {
        final int type = ProcessHelper.getCurrentProcessType();
        if (type == processType) {
            ProcessServiceManager.removeService(serviceName);
        } else {
            // not support client to remove service
            if (DEBUG) {
                throw new RuntimeException("not support client remove service=" + serviceName
                        + ", processType=" + processType);
            }
        }
    }

    public static Context getAppContext() {
        return sApp;
    }
}
