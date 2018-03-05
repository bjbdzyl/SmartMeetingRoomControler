package com.example.zhangyl.myapplication.Presenter;

/**
 * Created by ZhangYL on 2018/3/1 0001.
 */

public class MeetingMgrEvent {
    public static final int EVENT_GET_MEETING_LIST_FAILED = 0;
    public static final int EVENT_GET_MEETING_LIST_SUCC = 1;
    public static final int EVENT_MEETING_START = 2;
    public static final int EVENT_MEETING_NEARLY_START = 3;
    public static final int EVENT_MEETING_NEARLY_END = 4;
    public static final int EVENT_MEETING_END = 5;
    public static final int EVENT_MEETING_CANCELD = 6;
    public static final int EVENT_ROOM_CHECK_IN = 7;
    public static final int EVENT_START_DOWNLOAD_MEETING_LIST = 8;
    public static final int EVENT_MEETING_CHANGED = 9;

    Object param;
    public int event = -1;

    public MeetingMgrEvent(int nEvent, Object oParam){
        event = nEvent;
        param = oParam;
    }
}