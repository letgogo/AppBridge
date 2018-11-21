package com.freesky.hostapp;

import android.app.Application;

import com.freesky.appbridge.ProcessHelper;
import com.freesky.appbridge.ProcessHelperConfig;
import com.freesky.hostapp.service.DispatchServiceStub;

/**
 * Created by letgogo on 2018/11/20.
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ProcessHelper.init(new ProcessHelperConfig(this));
        ProcessHelper.registerService(ProcessHelper.PROCESS_HOST_MSG,
                ProcessHelper.HOST_MSG_SERVICE, DispatchServiceStub.class);
    }

}
