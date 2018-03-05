package com.example.zhangyl.myapplication.Service;

import com.example.zhangyl.myapplication.Presenter.AppCallBack;

/**
 * Created by ZhangYL on 2018/2/27 0027.
 * 云控模块
 */

public class CloudCtrlService implements AppCallBack {
    private static CloudCtrlService gInstance;

    public static CloudCtrlService getInstance() {
        if (gInstance == null) {
            gInstance = new CloudCtrlService();
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