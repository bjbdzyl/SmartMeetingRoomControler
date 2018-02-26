package com.example.zhangyl.myapplication;

/**
 * Created by ZhangYL on 2018/1/23 0023.
 */

public class DeviceChangeEvent {
    public static final int EVT_ADD_DEV = 0;
    public static final int EVT_DEL_DEV = 1;
    public static final int EVT_UPT_DEV = 2;
    public final MeetingDevice event_param;
    public int nType = EVT_ADD_DEV;

    public DeviceChangeEvent(int nEventType, MeetingDevice event_param) {
        this.event_param = event_param;
        this.nType = nEventType;
    }
}
