package com.example.zhangyl.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by ZhangYL on 2018/1/18 0018.
 */

// todo 此注册类管理房间信息、设备信息、会议模式三个class即可
public class RegisterMgr {
    private static final String TAG = "RegisterMgr";

    public static final String HTTP_BASE_URL = "http://www.smartmeeting.com";
    public static final String HTTP_TEST_SERVER = "http://192.168.1.109:8321";

    public String getCloud_host() {
        return cloud_host;
    }

    public void setCloud_host(String cloud_host) {
        this.cloud_host = cloud_host;
    }

    String cloud_host;

    RoomInfo roomInfo;

    /**
     * 房间信息保存到shared preference配置文件里
     */
    public void save_room_info(){
        SharedPreferences prefers = App.getInstance().getContext().getSharedPreferences("room_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefers.edit();
        editor.putString("room_name", roomInfo.getRoom_name());
        editor.putInt("room_size", roomInfo.getRoom_size());
        editor.putString("srv_ip", getCloud_host());
        editor.putString("ctrl_ip", roomInfo.getControler_ip());
        editor.putString("room_number", roomInfo.getRoom_number());
        editor.commit();
    }

    public void init_room_info(){
        if (roomInfo == null) {
            roomInfo = new RoomInfo();
        }
        SharedPreferences prefers = App.getInstance().getContext().getSharedPreferences("room_info", Context.MODE_PRIVATE);
        roomInfo.setRoom_name(prefers.getString("room_name", ""));
        roomInfo.setRoom_size(prefers.getInt("room_size", 3));
        setCloud_host(prefers.getString("srv_ip", ""));
        roomInfo.setControler_ip(prefers.getString("ctrl_ip", ""));
        roomInfo.setRoom_number(prefers.getString("room_number", ""));
    }

    private static RegisterMgr g_instance = null;

    public static RegisterMgr get_instance(){
        if (g_instance == null) {
            g_instance = new RegisterMgr();
            g_instance.init_room_info();
        }
        return g_instance;
    }

    static class RegisterEvent{
        public static final String RESULT_SUCC = "suc";
        public static final String RESULT_FAILD = "fail";

        public RegisterEvent(int nState, @Nullable String strResult){
            mState = nState;
            mRegResult = strResult;
        }
        public static final int INVALID_STATE = 0;
        public static final int START_REGISTER = 1;
        public static final int DOING_REGISTER = 2;
        public static final int END_REGISTER = 3;
        int mState = INVALID_STATE;

        String mRegResult;
    }

    public void DoRegister() {
        // todo 走HTTP，将room info, device list，以及本终端的GUID发给云。失败重试三次，间隔1秒
        // todo 注册结果进行广播：失败（以及原因）, 成功。界面层监听事件，成功时跳到会议列表界面，异步拉取会议预订信息。失败时，进度对话框里直接提示。

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HTTP_TEST_SERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RoomRegApi api = retrofit.create(RoomRegApi.class);
        Call<HttpResult> resultCall = api.doRegister(roomInfo);
        Log.i(TAG, "DoRegister: request content " + resultCall.request().toString());
        Log.i(TAG, "DoRegister: request body " + resultCall.request().body());
        resultCall.enqueue(new Callback<HttpResult>() {
            @Override
            public void onResponse(Call<HttpResult> call, Response<HttpResult> response) {
                Log.i(TAG, "onResponse: get post return msg " + response.message());
                if (response.body() == null){
                    Log.i(TAG, "onResponse: no response object");
                    RegisterEvent event = new RegisterEvent(RegisterEvent.END_REGISTER, "register faild");
                    EventBus.getDefault().post(event);
                    return;
                }

                Log.i(TAG, "onResponse: register response " + response.body().getStrResult());
                if(response.body().getStrResult().equals(RegisterEvent.RESULT_SUCC)){
                    RegisterEvent event = new RegisterEvent(RegisterEvent.END_REGISTER, RegisterEvent.RESULT_SUCC);
                    EventBus.getDefault().post(event);
                }
                else {
                    RegisterEvent event = new RegisterEvent(RegisterEvent.END_REGISTER, response.body().getStrResult());
                    EventBus.getDefault().post(event);
                }
            }

            @Override
            public void onFailure(Call<HttpResult> call, Throwable t) {
                Log.i(TAG, "onFailure: register faild " + t.getMessage());
                RegisterEvent event = new RegisterEvent(RegisterEvent.END_REGISTER, t.getMessage());
                EventBus.getDefault().post(event);
            }
        });
    }

    public class HttpResult {
        String result;
        public String getStrResult(){
            return result;
        }

        public void setStrResult(String str){
            result = str;
        }
    }

    public interface RoomRegApi {

        @POST("/room_register")
        Call<HttpResult> doRegister(@Body RoomInfo roomInfo);
    }
}
