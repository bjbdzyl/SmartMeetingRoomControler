package com.example.zhangyl.myapplication.Presenter;

import com.example.zhangyl.myapplication.Service.AppUpdateService;
import com.example.zhangyl.myapplication.Service.BugReportService;
import com.example.zhangyl.myapplication.Service.CloudCtrlService;
import com.example.zhangyl.myapplication.Service.DataReportService;
import com.example.zhangyl.myapplication.Service.HeartBeatService;
import com.example.zhangyl.myapplication.Service.ImgAndVideoService;
import com.example.zhangyl.myapplication.Service.RoomEnvMgrService;
import com.example.zhangyl.myapplication.Service.VoiceService;

/**
 * Created by ZhangYL on 2018/2/26 0026.
 */

public class GlobalMgr implements AppCallBack {

    private static GlobalMgr gInstance;

    public static GlobalMgr getInstance(){
        if (gInstance == null){
            gInstance = new GlobalMgr();
        }
        return gInstance;
    }

    @Override
    public void onAppStart() { // TODO 按前台后台分类，后台任务集中放到一个service里

        MeetingProgressMgr.getInstance().onAppStart();

        MeetingListMgr.getInstance().onAppStart();

        ModeCfgMgr.getInstance().onAppStart();

        DeviceMgr.getInstance().onAppStart();

        MeetingRoomMgr.getInstance().onAppStart();

        AppUpdateService.getInstance().onAppStart();

        BugReportService.getInstance().onAppStart();

        DataReportService.getInstance().onAppStart();

        VoiceService.getInstance().onAppStart();

        ImgAndVideoService.getInstance().onAppStart();

        CloudCtrlService.getInstance().onAppStart();

        HeartBeatService.getInstance().onAppStart();

        RoomEnvMgrService.getInstance().onAppStart();
    }

    @Override
    public void onAppStop() {

    }
}
