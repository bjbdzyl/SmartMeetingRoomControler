package com.example.zhangyl.myapplication.Presenter;

import android.util.Log;

import com.example.zhangyl.myapplication.View.ModeCfgDbItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by ZhangYL on 2018/1/26 0026.
 * 负责设备数据库管理。设备开关等遥控。主机socket监听，读写分离。
 */

public class DeviceMgr implements AppCallBack {
    private static final String TAG = "DeviceMgr";
    public List<MeetingDevice> device_list = new ArrayList<>();

    MeetingDeviceDao devDao = DbEntityMgr.getInstance().getMeetingDevDao();

    private static DeviceMgr g_Instance;

    public static DeviceMgr getInstance(){
        if (g_Instance == null) {
            g_Instance = new DeviceMgr();
            g_Instance.init_dev_list();
        }

        return g_Instance;
    }

    private void init_dev_list() {
        device_list = devDao.loadAll();
    }

    public void add_device(MeetingDevice meetingDevice) {
        device_list.add(meetingDevice);
        devDao.insert(meetingDevice); // todo 异常处理：重复ID，即重复IOPORT
        EventBus.getDefault().post(new DeviceChangeEvent(DeviceChangeEvent.EVT_ADD_DEV, meetingDevice));
    }

    public void delDevice(MeetingDevice item) {
        device_list.remove(item);
        devDao.delete(item);
        EventBus.getDefault().post(new DeviceChangeEvent(DeviceChangeEvent.EVT_DEL_DEV, item));
    }

    @Override
    public void onAppStart() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onAppStop() {
        EventBus.getDefault().unregister(this);
    }

    public class DevListHttpBody{
        private String room_name;
        private List<MeetingDevice> device_list = new ArrayList<>();

        public String getRoom_name() {
            return room_name;
        }

        public void setRoom_name(String room_name) {
            this.room_name = room_name;
        }

        public List<MeetingDevice> getDevice_list() {
            return device_list;
        }

        public void setDevice_list(List<MeetingDevice> device_list) {
            this.device_list = device_list;
        }
    }

    public interface DevListUpdateApi {
        @POST("/dev_list_update")
        Call<CloudApi.SimpleHttpResult> doUpdate(@Body DevListHttpBody body);
    }

    public void postDevList() {
        // todo 失败重试三次，间隔1秒。
        Log.i(TAG, "postDevList: try to post dev list");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RegisterMgr.HTTP_TEST_SERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DevListHttpBody postBody = new DevListHttpBody();
        // todo 根据业务逻辑关系，按需要检查room name 是否为空。devicelist 是否为空
        postBody.setRoom_name(RegisterMgr.get_instance().roomInfo.getRoom_name());
        postBody.setDevice_list(device_list);

        DevListUpdateApi api = retrofit.create(DevListUpdateApi.class);
        Call<CloudApi.SimpleHttpResult> resultCall = api.doUpdate(postBody); // todo 所有http接口统一集中到通信层
        Log.i(TAG, "postDevList: request content " + resultCall.request().toString());
        Log.i(TAG, "postDevList: request body " + resultCall.request().body());
        resultCall.enqueue(new Callback<CloudApi.SimpleHttpResult>() {
            @Override
            public void onResponse(Call<CloudApi.SimpleHttpResult> call, Response<CloudApi.SimpleHttpResult> response) {
                Log.i(TAG, "onResponse: postDevList get post return msg " + response.message());
                if (response.body() == null){
                    Log.i(TAG, "onResponse: no response object");
                    return;
                }

                Log.i(TAG, "onResponse: postDevList response " + response.body().getStrResult());
                if(response.body().getStrResult() == "suc"){
                    Log.i(TAG, "onResponse: dev list post succ");
                }
                else {
                    Log.i(TAG, "onResponse: dev list post failed" + response.body().getStrResult());
                }
            }

            @Override
            public void onFailure(Call<CloudApi.SimpleHttpResult> call, Throwable t) {
                Log.i(TAG, "onFailure: postDevList faild " + t.getMessage());
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMeetingMgrEvent(MeetingMgrEvent param) {
        // TODO handle meeting mgr event.
        switch (param.event){
            case MeetingMgrEvent.EVENT_MEETING_START:
                onMeetingStart(param);
                break;
            case MeetingMgrEvent.EVENT_MEETING_NEARLY_START:
                onMeetingNearlyStart(param);
                break;
            case MeetingMgrEvent.EVENT_MEETING_NEARLY_END:
                onMeetingNearlyEnd(param);
                break;
            case MeetingMgrEvent.EVENT_MEETING_END:
                onMeetingEnd(param);
                break;
        }
    }

    /**
     * 所有设备进入会后模式，节能的节能，关机的关机。
     * @param param 未来每个会议单独设置场景模式时会用到。
     */
    private void onMeetingEnd(MeetingMgrEvent param) {
        for (ModeCfgDbItem devMode :
                ModeCfgMgr.getInstance().getCfgList()) {
            SwicthDevState(devMode.getMIoPort(), devMode.getAfterMeetingOn(), devMode.getAfterMeetingSeekbarValue());
        }
    }

    /**
     * 调用设备协议层提供的接口进行设备控制
     * @param mIoPort
     * @param isOn
     * @param seekbarValue
     */
    private void SwicthDevState(long mIoPort, boolean isOn, int seekbarValue) {

    }

    /** 结束前暂无场景模式。啥都不干。
     * @param param
     */
    private void onMeetingNearlyEnd(MeetingMgrEvent param) {
        return;
    }

    /** 会前模式
     * @param param
     */
    private void onMeetingNearlyStart(MeetingMgrEvent param) {
        for (ModeCfgDbItem devMode :
                ModeCfgMgr.getInstance().getCfgList()) {
            SwicthDevState(devMode.getMIoPort(), devMode.getBeforeMeetingOn(), devMode.getBeforeMeetingSeekbarValue());
        }
    }

    /** 会中模式
     * @param param
     */
    private void onMeetingStart(MeetingMgrEvent param) {
        for (ModeCfgDbItem devMode :
                ModeCfgMgr.getInstance().getCfgList()) {
            SwicthDevState(devMode.getMIoPort(), devMode.getInMeetingOn(), devMode.getInMeetingSeekbarValue());
        }
    }
}
