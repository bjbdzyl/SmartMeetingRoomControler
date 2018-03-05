package com.example.zhangyl.myapplication.Service;

import com.example.zhangyl.myapplication.Presenter.AppCallBack;

/**
 * Created by ZhangYL on 2018/2/27 0027.
 * 语音识别服务
 */

public class VoiceService implements AppCallBack {
    private static VoiceService gInstance;
    public static VoiceService getInstance(){
        if (gInstance == null) {
            gInstance = new VoiceService();
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
