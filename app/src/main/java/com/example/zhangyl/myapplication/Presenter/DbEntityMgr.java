package com.example.zhangyl.myapplication.Presenter;

import com.example.zhangyl.myapplication.View.ModeCfgDbItemDao;

/**
 * Created by ZhangYL on 2018/1/22 0022.
 */

public class DbEntityMgr {

    private static DbEntityMgr entityManager;
    private MeetingDeviceDao deviceDao;
    private ModeCfgDbItemDao modeDevCfgDao;

    /**
     * 创建User表实例
     *
     * @return
     */
    public MeetingDeviceDao getMeetingDevDao(){
        if (deviceDao == null) {
            deviceDao = GreenDaoMgr.getInstance().getSession().getMeetingDeviceDao();
        }
        return deviceDao;
    }

    /**
     * 创建单例
     *
     * @return
     */
    public static DbEntityMgr getInstance() {
        if (entityManager == null) {
            entityManager = new DbEntityMgr();
        }
        return entityManager;
    }

    public ModeCfgDbItemDao getMeetingModeCfgDao() {
        if (modeDevCfgDao == null) {
            modeDevCfgDao = GreenDaoMgr.getInstance().getSession().getModeCfgDbItemDao();
        }

        return modeDevCfgDao;
    }
}
