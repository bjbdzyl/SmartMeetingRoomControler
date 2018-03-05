package com.example.zhangyl.myapplication.Service;

import com.example.zhangyl.myapplication.Presenter.AppCallBack;

/**
 * Created by ZhangYL on 2018/2/27 0027.
 * 崩溃监控和上报
 */

public class BugReportService implements AppCallBack {

    private static BugReportService gInstance;

    public static BugReportService getInstance(){
        if (gInstance == null) {
            gInstance = new BugReportService();
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
