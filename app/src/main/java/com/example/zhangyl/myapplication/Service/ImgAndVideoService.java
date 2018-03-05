package com.example.zhangyl.myapplication.Service;

import com.example.zhangyl.myapplication.Presenter.AppCallBack;

/**
 * Created by ZhangYL on 2018/2/27 0027.
 * 图像识别服务
 */

public class ImgAndVideoService implements AppCallBack {
    private static ImgAndVideoService gInstance;

    public static ImgAndVideoService getInstance() {
        if (gInstance == null) {
            gInstance = new ImgAndVideoService();
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