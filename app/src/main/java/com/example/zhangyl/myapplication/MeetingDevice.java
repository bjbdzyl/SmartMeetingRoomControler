package com.example.zhangyl.myapplication;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by ZhangYL on 2018/1/18 0018.
 */

@Entity(indexes = {
        @Index(value = "lIoPort DESC", unique = true)
})

class MeetingDevice {
    public static final int SINGLE_ON_OFF = 1; //单一开关，只有开关两态
    public static final int TRIPLE_ON_OFF = 2; //三档开关
    public static final int SEEK_CONTROL = 3; //滑动开关，无级调节

    @Id
    private long lIoPort;

    @NotNull
    private String strDevName;
    private String strDevMode;
    private int nControlType;
    private String strPosition;

    public MeetingDevice(String devName, String devPosition, String devMode, int ctrlType, int nIoPort) {
        strDevName = devName;
        strDevMode = devMode;
        strPosition = devPosition;
        nControlType = ctrlType;
        this.lIoPort = nIoPort;
    }

    @Generated(hash = 2118828404)
    public MeetingDevice(long lIoPort, @NotNull String strDevName, String strDevMode, int nControlType,
            String strPosition) {
        this.lIoPort = lIoPort;
        this.strDevName = strDevName;
        this.strDevMode = strDevMode;
        this.nControlType = nControlType;
        this.strPosition = strPosition;
    }

    @Generated(hash = 1910083272)
    public MeetingDevice() {
    }

    public long getLIoPort() {
        return this.lIoPort;
    }

    public void setLIoPort(long lIoPort) {
        this.lIoPort = lIoPort;
    }

    public String getStrDevName() {
        return this.strDevName;
    }

    public void setStrDevName(String strDevName) {
        this.strDevName = strDevName;
    }

    public String getStrDevMode() {
        return this.strDevMode;
    }

    public void setStrDevMode(String strDevMode) {
        this.strDevMode = strDevMode;
    }

    public int getNControlType() {
        return this.nControlType;
    }

    public void setNControlType(int nControlType) {
        this.nControlType = nControlType;
    }

    public String getStrPosition() {
        return this.strPosition;
    }

    public void setStrPosition(String strPosition) {
        this.strPosition = strPosition;
    }
}
