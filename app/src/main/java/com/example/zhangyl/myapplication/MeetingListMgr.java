package com.example.zhangyl.myapplication;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

import static com.example.zhangyl.myapplication.RegisterMgr.HTTP_TEST_SERVER;

/**
 * Created by ZhangYL on 2018/2/5 0005.
 */

public class MeetingListMgr {
    private static final String TAG = "MeetingListMgr";
    private static MeetingListMgr gInstance;

    public static MeetingListMgr getInstance(){
        if (gInstance == null) {
            gInstance = new MeetingListMgr();
            Log.i(TAG, "getInstance: init instance");
        }

        return gInstance;
    }

    private List<Meeting> meetingList = new ArrayList<>(); // todo 想办法按时间排序

    public List<Meeting> getMeetingList() {
        return meetingList;
    }

    public void setMeetingList(List<Meeting> meetingList) {
        this.meetingList = meetingList;
    }

    public Meeting getCurrentMeeting(){
        if (meetingList == null || meetingList.size() == 0) {
            getInstance().downloadMeetingList();
            return null;
        }

        for (int i = 0; i < meetingList.size(); i++) {
            Meeting meeting = meetingList.get(i);
            //String curr_time = "2017-08-09 10:11";
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String curr_time = formatter.format(new Date());

            if (meeting.start_time.compareTo(curr_time) <= 0 && meeting.end_time.compareTo(curr_time) >= 0){
                return meeting;
            }
        }

        return null;
    }

    class HttpMeetingListBody{
        List<Meeting> meetings;
        public List<Meeting> getMeetings(){
            return meetings;
        }
        public void setMeetings(List<Meeting> meetingList){
            meetings = meetingList;
        }
    }

    // todo 启动监听，接收服务器推送的会议变更消息。监听通道由底层统一维护，分发来自云的各种推送事件，此处只处理会议列表更新事件。
    public void startCloudListener(){

    }

    // todo 接收服务器定时推送的最新会议列表。
    public void downloadMeetingList(){
        Map<String, String> param = new HashMap<>();
        param.put("meeting_room", RegisterMgr.get_instance().roomInfo.getRoom_name());

        SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
        String today = format.format(Calendar.getInstance().getTime());
        cal.add(Calendar.DAY_OF_MONTH, +7);//取当前日期的后7天.
        String last_week = format.format(Calendar.getInstance().getTime());

        //Calendar;
        param.put("from_date", today);
        param.put("to_date", last_week);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HTTP_TEST_SERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        getMeetingListApi api = retrofit.create(getMeetingListApi.class);
        EventBus.getDefault().post(new MeetingMgrEvent(MeetingMgrEvent.EVENT_START_DOWNLOAD_MEETING_LIST, null));
        Call<MeetingMgrHttpResult> resultCall = api.doGetMeetingList(param);

        Log.i(TAG, "doGetMeetingList: request content " + resultCall.request().toString());
        Log.i(TAG, "doGetMeetingList: request body " + resultCall.request().body());
        resultCall.enqueue(new Callback<MeetingMgrHttpResult>() {
            @Override
            public void onResponse(Call<MeetingMgrHttpResult> call, Response<MeetingMgrHttpResult> response) {
                Log.i(TAG, "onResponse: doGetMeetingList return msg " + response.message());
                if (response.body() == null){
                    Log.i(TAG, "onResponse: no response object");
                    EventBus.getDefault().post(new MeetingMgrEvent(MeetingMgrEvent.EVENT_GET_MEETING_LIST_FAILED, "查询会议失败，服务器未返回数据"));
                    return;
                }

                Log.i(TAG, "onResponse: doGetMeetingList response " + response.body());
                if(response.body().getStrResult().equals("suc")){
                    Log.i(TAG, "onResponse: get meeting list succ");
                    meetingList = response.body().getMeetingList();
                    EventBus.getDefault().post(new MeetingMgrEvent(MeetingMgrEvent.EVENT_GET_MEETING_LIST_SUCC, null));
                }
                else {
                    EventBus.getDefault().post(new MeetingMgrEvent(MeetingMgrEvent.EVENT_GET_MEETING_LIST_FAILED, "暂无会议安排"));
                    Log.i(TAG, "onResponse: fail get meeting list");
                }
            }

            @Override
            public void onFailure(Call<MeetingMgrHttpResult> call, Throwable t) {
                Log.i(TAG, "onFailure: doGetMeetingList faild " + t.getMessage());
                EventBus.getDefault().post(new MeetingMgrEvent(MeetingMgrEvent.EVENT_GET_MEETING_LIST_FAILED, "查询会议失败" + t.getMessage()));
            }
        });
    }

    public class MeetingMgrEvent{
        public static final int EVENT_GET_MEETING_LIST_FAILED = 0;
        public static final int EVENT_GET_MEETING_LIST_SUCC = 1;
        public static final int EVENT_MEETING_START = 2;
        public static final int EVENT_MEETING_NEARLY_START = 3;
        public static final int EVENT_MEETING_NEARLY_END = 4;
        public static final int EVENT_MEETING_END = 5;
        public static final int EVENT_MEETING_CANCELD = 6;
        public static final int EVENT_ROOM_CHECK_IN = 7;
        public static final int EVENT_START_DOWNLOAD_MEETING_LIST = 8;
        public static final int EVENT_MEETING_CHANGED = 9;

        Object param;
        int event = -1;

        public MeetingMgrEvent(int nEvent, Object oParam){
            event = nEvent;
            param = oParam;
        }
    }

    public class MeetingMgrHttpResult {
        String result;
        public String getStrResult(){
            return result;
        }

        public void setStrResult(String str){
            result = str;
        }

        List<Meeting> meetingList = new ArrayList<>();

        public List<Meeting> getMeetingList(){
            return meetingList;
        }

        public void setMeetingList(List<Meeting> list){
            meetingList = list;
        }
    }


    public interface getMeetingListApi {

        @GET("getMeetingList")
        Call<MeetingMgrHttpResult> doGetMeetingList(@QueryMap Map<String, String> map);//, @Body HttpMeetingListBody body);
    }
}
