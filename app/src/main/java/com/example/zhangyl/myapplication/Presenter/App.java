package com.example.zhangyl.myapplication.Presenter;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.zhangyl.myapplication.Service.MQTTService;

/**
 * Created by ZhangYL on 2018/1/22 0022.
 */

public class App extends Application {

    private static App g_app = null;

    private final static String TAG = "App";

    public static App getInstance(){
        return g_app;
    }

    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        g_app = this;
        mContext = g_app.getApplicationContext();
        if (mContext == null) {
            Log.e(TAG, "fail to init app context");
        } else {
            Log.i(TAG, "onCreate: init context succ");
        }

        Log.i(TAG, "onCreate: try to start mqtt service");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent srvIntent = new Intent(mContext, MQTTService.class);
                if(null == startService(srvIntent)){
                    Log.e(TAG, "onCreate: fail to start mqtt servcice");
                }
                if (!RegisterMgr.get_instance().needRegister()){
                    Log.i(TAG, "onCreate: all ready register this room. start gloable mgr");
                    GlobalMgr.getInstance().onAppStart();
                }
                else {
                    Log.i(TAG, "onCreate: neet register. new room");
                }
            }
        }).start();
    }

    public Context getContext() {
        return mContext;
    }
}
