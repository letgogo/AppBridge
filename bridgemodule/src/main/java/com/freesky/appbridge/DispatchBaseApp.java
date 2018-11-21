package com.freesky.appbridge;

import android.app.Application;

/**
 * Created by letgogo on 2018/11/20.
 */

public class DispatchBaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessHelper.init(new ProcessHelperConfig(this));
    }

}
