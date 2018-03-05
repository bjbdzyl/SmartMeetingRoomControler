package com.example.zhangyl.myapplication.Service;

import com.example.zhangyl.myapplication.Presenter.AppCallBack;

/**
 * Created by ZhangYL on 2018/2/27 0027.
 *
 */

public class DataReportService implements AppCallBack {
    private static DataReportService gInstance;
    public static DataReportService getInstance() {
        if (gInstance == null) {
            gInstance = new DataReportService();
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
