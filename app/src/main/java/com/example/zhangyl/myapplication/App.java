package com.example.zhangyl.myapplication;

import android.app.Application;
import android.content.Context;

/**
 * Created by ZhangYL on 2018/1/22 0022.
 */

public class App extends Application {

    private static App g_app = null;

    public static App getInstance(){
        if (g_app == null) {
            g_app = new App();
        }

        return g_app;
    }

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public Context getContext() {
        return mContext;
    }
}
