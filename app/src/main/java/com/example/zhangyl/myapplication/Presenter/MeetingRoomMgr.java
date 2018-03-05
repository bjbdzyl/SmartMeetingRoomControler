package com.example.zhangyl.myapplication.Presenter;

/**
 * Created by ZhangYL on 2018/2/27 0027.
 * 会议室房间管理，主要是签入、签出状态变更、状态同步到服务器，本地广播通知。接收会议开始、结束通知，用来同步会议室状态。
 */

class MeetingRoomMgr implements AppCallBack {
    private static MeetingRoomMgr gInstance;
    public static MeetingRoomMgr getInstance(){
        if (gInstance == null){
            gInstance = new MeetingRoomMgr();
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
