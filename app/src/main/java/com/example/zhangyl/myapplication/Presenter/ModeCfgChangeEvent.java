package com.example.zhangyl.myapplication.Presenter;

import com.example.zhangyl.myapplication.View.ModeCfgDbItem;

/**
 * Created by ZhangYL on 2018/1/26 0026.
 */

public class ModeCfgChangeEvent {
    private final ModeCfgDbItem param;

    public ModeCfgChangeEvent(ModeCfgDbItem modeCfg) {
        param = modeCfg;
    }

    public ModeCfgDbItem getParam() {
        return param;
    }
}
