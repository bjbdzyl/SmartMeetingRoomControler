package com.example.zhangyl.myapplication.View;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by ZhangYL on 2018/1/25 0025.
 */
@Entity(indexes = {
        @Index(value = "mIoPort DESC", unique = true)
})
public class ModeCfgDbItem { // todo 响应设备时，同步增加这个值到数据库。与MeetingDevice表用IoPort字段关联
    public static final int MODE_BEFORE_MEETING = 1;
    public static final int MODE_IN_MEETING = 2;
    public static final int MODE_AFTER_MEETING = 3;

    @Id
    private long mIoPort = 0;

    @NotNull
    private boolean beforeMeetingOn = false;
    private boolean inMeetingOn = true; // 会中默认全开
    private boolean afterMeetingOn = false;
    private int beforeMeetingSeekbarValue = 0;
    private int inMeetingSeekbarValue = 0;
    private int afterMeetingSeekbarValue = 0;

    public void setCfg(int nMode, boolean bOn, int nSeekBarValue) {
        switch(nMode)
        {
            case MODE_BEFORE_MEETING:
                setBeforeMeetingOn(bOn);
                setBeforeMeetingSeekbarValue(nSeekBarValue);
                break;
            case MODE_IN_MEETING:
                setBeforeMeetingOn(bOn);
                setBeforeMeetingSeekbarValue(nSeekBarValue);
                break;
            case MODE_AFTER_MEETING:
                setBeforeMeetingOn(bOn);
                setBeforeMeetingSeekbarValue(nSeekBarValue);
                break;
        }
    }

    public boolean getOnOffCfg(int nMode){
        switch (nMode){
            case MODE_BEFORE_MEETING:
                return getBeforeMeetingOn();
            case MODE_IN_MEETING:
                return getInMeetingOn();
            case MODE_AFTER_MEETING:
                return getAfterMeetingOn();
        }
        return false;
    }

    public int getSeekBarValueCfg(int nMode){
        switch (nMode){
            case MODE_BEFORE_MEETING:
                return getBeforeMeetingSeekbarValue();
            case MODE_IN_MEETING:
                return getInMeetingSeekbarValue();
            case MODE_AFTER_MEETING:
                return getAfterMeetingSeekbarValue();
        }
        return 0; // 默认返回0，对于某些设备来说，基本等同于关
    }

    @Generated(hash = 1417839504)
    public ModeCfgDbItem(long mIoPort, boolean beforeMeetingOn, boolean inMeetingOn,
            boolean afterMeetingOn, int beforeMeetingSeekbarValue,
            int inMeetingSeekbarValue, int afterMeetingSeekbarValue) {
        this.mIoPort = mIoPort;
        this.beforeMeetingOn = beforeMeetingOn;
        this.inMeetingOn = inMeetingOn;
        this.afterMeetingOn = afterMeetingOn;
        this.beforeMeetingSeekbarValue = beforeMeetingSeekbarValue;
        this.inMeetingSeekbarValue = inMeetingSeekbarValue;
        this.afterMeetingSeekbarValue = afterMeetingSeekbarValue;
    }

    @Generated(hash = 993366068)
    public ModeCfgDbItem() {
    }

    public long getMIoPort() {
        return this.mIoPort;
    }
    public void setMIoPort(long mIoPort) {
        this.mIoPort = mIoPort;
    }
    public boolean getBeforeMeetingOn() {
        return this.beforeMeetingOn;
    }
    public void setBeforeMeetingOn(boolean beforeMeetingOn) {
        this.beforeMeetingOn = beforeMeetingOn;
    }
    public boolean getInMeetingOn() {
        return this.inMeetingOn;
    }
    public void setInMeetingOn(boolean inMeetingOn) {
        this.inMeetingOn = inMeetingOn;
    }
    public boolean getAfterMeetingOn() {
        return this.afterMeetingOn;
    }
    public void setAfterMeetingOn(boolean afterMeetingOn) {
        this.afterMeetingOn = afterMeetingOn;
    }
    public int getBeforeMeetingSeekbarValue() {
        return this.beforeMeetingSeekbarValue;
    }
    public void setBeforeMeetingSeekbarValue(int beforeMeetingSeekbarValue) {
        this.beforeMeetingSeekbarValue = beforeMeetingSeekbarValue;
    }
    public int getInMeetingSeekbarValue() {
        return this.inMeetingSeekbarValue;
    }
    public void setInMeetingSeekbarValue(int inMeetingSeekbarValue) {
        this.inMeetingSeekbarValue = inMeetingSeekbarValue;
    }
    public int getAfterMeetingSeekbarValue() {
        return this.afterMeetingSeekbarValue;
    }
    public void setAfterMeetingSeekbarValue(int afterMeetingSeekbarValue) {
        this.afterMeetingSeekbarValue = afterMeetingSeekbarValue;
    }
}
