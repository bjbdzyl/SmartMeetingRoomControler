package com.example.zhangyl.myapplication.Service;

import com.example.zhangyl.myapplication.Presenter.AppCallBack;

/**
 * Created by ZhangYL on 2018/2/27 0027.
 * 心跳
 */

public class HeartBeatService implements AppCallBack {
    private static HeartBeatService gInstance;

    public static HeartBeatService getInstance() {
        if (gInstance == null) {
            gInstance = new HeartBeatService();
        }
        return gInstance;
    }

    @Override
    public void onAppStart() {

    }

    @Override
    public void onAppStop() {

    }
}