package com.example.zhangyl.myapplication.Presenter;

/**
 * Created by ZhangYL on 2018/2/26 0026.
 */

public class DevStateChangeEvent {

    private long mPort;
    private boolean mOnoff;
    private int mStateValue;

    /**
     * @param port 端口号
     * @param on_off 开、关
     * @param state_value 强度值，适用于滑动开关类设备
     */
    public DevStateChangeEvent(long port, boolean on_off, int state_value) {
        mPort = port;
        mOnoff = on_off;
        mStateValue = state_value;
    }

    public long getPort() {
        return mPort;
    }

    public boolean isOnoff() {
        return mOnoff;
    }

    public int getStateValue() {
        return mStateValue;
    }
}
