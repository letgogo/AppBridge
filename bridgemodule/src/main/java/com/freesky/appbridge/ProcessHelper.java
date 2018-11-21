package com.freesky.appbridge;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by letgogo on 2018/11/20.
 */
public class ProcessHelper {
    private final static String TAG = ProcessHelper.class.getSimpleName();
    private final static boolean DEBUG = BuildConfig.DEBUG;

    public final static String REAL_PACKAGE_NAME = "com.freesky.hostapp";

    /**
     * Process types
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            PROCESS_UNKNOWN,
            PROCESS_HOST_MSG,
    })
    public @interface ProcessType {
    }

    public static final int PROCESS_UNKNOWN = -1;
    public static final int PROCESS_HOST_MSG = 1;


    /**
     * Process name
     */
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({PROCESS_NAME_HOST, PROCESS_NAME_UNKNOWN
    })
    public @interface ProcessName {
    }

    public static final String PROCESS_NAME_UNKNOWN = "";
    public static final String PROCESS_NAME_HOST = REAL_PACKAGE_NAME;

    /**
     * Service name
     */
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({HOST_MSG_SERVICE
    })
    public @interface ServiceName {
    }

    public static final String HOST_MSG_SERVICE = "HOST_MSG_SERVICE";

    @ProcessType
    private static int sProcessType = PROCESS_UNKNOWN;

    private static String sProcessName;

    private static IProcessHelperConfig sConfig;

    private static Context sApplicationContext;

    public static void init(@NonNull IProcessHelperConfig config) {
        sConfig = config;
        init();
        sApplicationContext = ProcessHelperConfig.getAppContext();
    }

    private static void init() {
        sProcessName = getProcessName();
        sProcessType = getProcessType(sProcessName);
    }

    public static void uninit() {
        // do nothing
    }

    public static String getCurrentProcessName() {
        if (TextUtils.isEmpty(sProcessName)) {
            init();
        }
        return sProcessName;
    }

    @ProcessType
    public static int getCurrentProcessType() {
        if (sProcessType == PROCESS_UNKNOWN) {
            init();
        }
        return sProcessType;
    }

    @ProcessType
    public static int getProcessType(@Nullable String processName) {
        if (TextUtils.isEmpty(processName)) {
            return PROCESS_UNKNOWN;
        }

        if (processName.endsWith(PROCESS_NAME_HOST)) {
            return PROCESS_HOST_MSG;
        }
        return PROCESS_UNKNOWN;
    }

    @ProcessName
    public static String getProcessNameSurfix(@ProcessType int processType) {
        switch (processType) {
            case PROCESS_HOST_MSG:
                return PROCESS_NAME_HOST;
            case PROCESS_UNKNOWN:
            default:
                return PROCESS_NAME_UNKNOWN;
        }
    }

    public static void registerService(@ProcessType int processType,
                                       @ServiceName @NonNull String serviceName,
                                       @NonNull Class<? extends Binder> serviceClazz) {
        if (getCurrentProcessType() != ProcessHelper.PROCESS_HOST_MSG) return;
        if (sConfig != null) {
            sConfig.registerService(processType, serviceName, serviceClazz);
        }
    }

    @Nullable
    public static IBinder getService(@ProcessType int processType,
                                     @ServiceName @NonNull String serviceName) {
        if (sConfig != null) {
            return sConfig.getService(processType, serviceName);
        }
        return null;
    }

    public static void removeService(@ProcessType int processType,
                                     @ServiceName @NonNull String serviceName) {
        if (sConfig != null) {
            sConfig.removeService(processType, serviceName);
        }
    }

    private static String getProcessName() {
        final int pid = android.os.Process.myPid();
        final Context context = sApplicationContext;
        String processName = getProcessNameByActivityManager(context, pid);
        if (!TextUtils.isEmpty(processName)) {
            return processName;
        }

        processName = ActivityThreadCompat.getCurrentProcessName();
        if (!TextUtils.isEmpty(processName)) {
            return processName;
        }

        processName = getProcessNameByProc(pid);
        return processName;
    }

    static String getProcessNameByActivityManager(Context cxt, int pid) {
        ActivityManager am = SysManagerUtils.getActivityManager(cxt);
        List<ActivityManager.RunningAppProcessInfo> runningApps = SysManagerUtils.getRunningAppProcesses(am);
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    static String getProcessNameByProc(int pid) {
        String cmdlineFile = "/proc/" + pid + "/cmdline";
        String processName = readFileAsString(cmdlineFile);
        if (processName != null) {
            processName = processName.trim();
        }
        return processName;
    }

    private static String readFileAsString(String filename) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filename);
            return readStreamAsString(fis);
        } catch (FileNotFoundException e) {
            Log.w(TAG, "Unexpected excetion: ", e);
        } catch (IOException e) {
            Log.w(TAG, "Unexpected excetion", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static String readStreamAsString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder result = new StringBuilder();
        String line = null;

        boolean first = true;
        while ((line = reader.readLine()) != null) {
            if (!first) {
                result.append('\n');
            } else {
                first = false;
            }
            result.append(line);
        }

        return result.toString();
    }


}
