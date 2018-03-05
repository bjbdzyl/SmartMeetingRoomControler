package com.example.zhangyl.myapplication.Presenter;

import android.util.Log;

import com.example.zhangyl.myapplication.View.ModeCfgDbItem;
import com.example.zhangyl.myapplication.View.ModeCfgDbItemDao;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangYL on 2018/1/26 0026.
 */

public class ModeCfgMgr implements AppCallBack {
    public static final String TAG = "ModeCfgMgr";
    private static ModeCfgMgr g_instance;
    private List<ModeCfgDbItem> mCfgList = new ArrayList<>();
    ModeCfgDbItemDao mDao = DbEntityMgr.getInstance().getMeetingModeCfgDao();
    public static ModeCfgMgr getInstance(){
        if (g_instance == null) {
            g_instance = new ModeCfgMgr();
            g_instance.initCfgs();
            EventBus.getDefault().register(g_instance);
        }
        return g_instance;
    }

    public List<ModeCfgDbItem> getCfgList(){
        if (mCfgList == null){
            initCfgs();
        }

        return mCfgList;
    }

    private void initCfgs() {
        mCfgList = mDao.loadAll();
    }

    public void insertCfg(ModeCfgDbItem newCfg){
        mDao.insert(newCfg);
    }

    public void delCfg(ModeCfgDbItem oldCfg){
        mDao.delete(oldCfg);
    }

    public void updateCfg(ModeCfgDbItem newCfg){
        if (((List<ModeCfgDbItem>)mDao.queryBuilder().where(ModeCfgDbItemDao.Properties.MIoPort.eq(newCfg.getMIoPort()))).size() == 0) {
            mDao.insert(newCfg);
            Log.i(TAG, "updateCfg: no record. insert new");
            return;
        }
        mDao.update(newCfg);
    }

    @Override
    public void onAppStart() {
    }

    @Override
    public void onAppStop() {
        EventBus.getDefault().unregister(g_instance);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessageEvent(ModeCfgChangeEvent event) {
        updateCfg(event.getParam()); // update db on disk
        for (int i = 0; i < mCfgList.size(); i++) {
            if (mCfgList.get(i).getMIoPort() == event.getParam().getMIoPort()){
                mCfgList.set(i, event.getParam()); // update list cache in mem
                return;
            }
        }
    }

}
