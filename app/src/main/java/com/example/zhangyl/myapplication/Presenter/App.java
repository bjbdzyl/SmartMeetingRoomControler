package com.example.zhangyl.myapplication.Presenter;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import retrofit2.Retrofit;

/**
 * Created by ZhangYL on 2018/1/22 0022.
 */

public class App extends Application {

    private static App g_app = null;

    private final static String TAG = "App";

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

        if (!RegisterMgr.get_instance().needRegister()){
            Log.i(TAG, "onCreate: all ready register this room. start gloable mgr");
            GlobalMgr.getInstance().onAppStart();
        }
        else {
            Log.i(TAG, "onCreate: neet register. new room");
        }
    }

    public Context getContext() {
        return mContext;
    }


}
