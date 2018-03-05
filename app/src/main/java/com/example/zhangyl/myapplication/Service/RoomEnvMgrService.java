package com.example.zhangyl.myapplication.Service;

import com.example.zhangyl.myapplication.Presenter.AppCallBack;

/**
 * Created by ZhangYL on 2018/2/27 0027.
 * 室内环境管理
 */

public class RoomEnvMgrService implements AppCallBack {

    private static RoomEnvMgrService gInstance;

    public static RoomEnvMgrService getInstance() {
        if (gInstance == null) {
            gInstance = new RoomEnvMgrService();
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
