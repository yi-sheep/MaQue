package com.gaoxianglong.maque.context;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

/**
 * 全局context
 * 需要在AndroidManifest.xml中告诉程序启动的时候需要初始化
 * <application
 * android:name="完整的包名"
 * >
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(context);
    }
    public static Context getContext(){
        return context;
    }
}
