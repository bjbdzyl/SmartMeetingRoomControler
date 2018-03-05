package com.example.zhangyl.myapplication.Presenter;

/**
 * Created by ZhangYL on 2018/1/22 0022.
 */

public class GreenDaoMgr {
    private static GreenDaoMgr mInstance;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;


    private GreenDaoMgr() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(App.getInstance().getContext(), "SmartMeeting", null);
        DaoMaster mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    public static GreenDaoMgr getInstance() {
        if (mInstance == null) {
            mInstance = new GreenDaoMgr();
        }
        return mInstance;
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

    public DaoSession getNewSession() {
        mDaoSession = mDaoMaster.newSession();
        return mDaoSession;
    }
}
