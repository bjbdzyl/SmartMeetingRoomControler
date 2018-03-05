package com.example.zhangyl.myapplication.Service;

import com.example.zhangyl.myapplication.Presenter.AppCallBack;

/**
 * Created by ZhangYL on 2018/2/27 0027.
 * 自动升级服务。APP升级。协议库升级。
 */

public class AppUpdateService implements AppCallBack {
    private static AppUpdateService gInstance;
    @Override
    public void onAppStart() {

    }

    @Override
    public void onAppStop() {

    }

    public static AppUpdateService getInstance() {
        if (gInstance == null) {
            gInstance = new AppUpdateService();
        }
        return gInstance;
    }
}
