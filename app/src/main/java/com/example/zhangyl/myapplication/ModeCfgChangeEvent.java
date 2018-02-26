package com.example.zhangyl.myapplication;

/**
 * Created by ZhangYL on 2018/1/26 0026.
 */

class ModeCfgChangeEvent {
    private final ModeCfgDbItem param;

    public ModeCfgChangeEvent(ModeCfgDbItem modeCfg) {
        param = modeCfg;
    }

    public ModeCfgDbItem getParam() {
        return param;
    }
}
