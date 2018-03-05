package com.example.zhangyl.myapplication.Presenter;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

import static com.example.zhangyl.myapplication.Presenter.CloudApi.RESULT_SUCC;

/**
 * Created by ZhangYL on 2018/2/5 0005.
 * 会议日程管理，主要负责本会议室预定列表的维护。
 * 具体包括：主动拉取预订会议列表；被动接收服务器推送的会议变更；与MeetingProcessMgr共同管理会议切换;每个会议给一个提前5分钟的AlarmManger闹钟。
 */

public class MeetingListMgr implements AppCallBack {
    private static final String TAG = "MeetingListMgr";
    private static MeetingListMgr gInstance;
    private static final int SLEEP = 0;
    private static final int DOWNLOADING = 1;
    private int nState = SLEEP;
    private int nRetryDlTimes = 0;
    private static final int MAX_RETRY_TIMES = 3;
    private static final int ONE_MIN = 1000 * 60;

    public static MeetingListMgr getInstance(){
        if (gInstance == null) {
            gInstance = new MeetingListMgr();
            Log.i(TAG, "getInstance: init instance");
        }

        return gInstance;
    }

    private List<Meeting> meetingList = new ArrayList<>();

    public List<Meeting> getMeetingList() {
        return meetingList;
    }

    public void setMeetingList(List<Meeting> meetingList) {
        this.meetingList = meetingList;
    }

    /**
     * 获取当前即将开始或已经开始的会议。
     * 注意：这个类管理的会议列表，是与服务器同步的，因此，极端情况下，这里的“当前会议”可能会与MeetingProgressMgr里的那个“当前会议不同”，这是个潜在BUG，也可以说是产品层面的逻辑漏洞，需要想办法处理一下
     * @return
     */
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

    @Override
    public void onAppStart() {
        // TODO 异步执行首次下载。启动后续的定时接收服务器推送。
        downloadMeetingList();
        startCloudListener();
    }

    @Override
    public void onAppStop() {

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
        if (nState != SLEEP){
            Log.i(TAG, "downloadMeetingList: not idle. state " + nState);
            return;
        }

        nState = DOWNLOADING;
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
                .baseUrl(RegisterMgr.HTTP_TEST_SERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MeetingListMgrCloudApi api = retrofit.create(MeetingListMgrCloudApi.class);
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
                    nState = SLEEP;
                    if (nRetryDlTimes++ <= MAX_RETRY_TIMES){
                        Log.i(TAG, "onResponse: no response for dl meeting list. retry " + nRetryDlTimes);
                        try {
                            Thread.sleep(ONE_MIN);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        downloadMeetingList();
                    }
                    else {
                        nRetryDlTimes = 0;
                        EventBus.getDefault().post(new MeetingMgrEvent(MeetingMgrEvent.EVENT_GET_MEETING_LIST_FAILED, "查询会议失败，服务器未返回数据"));
                    }
                    return;
                }

                Log.i(TAG, "onResponse: doGetMeetingList response " + response.body());
                if(response.body().getStrResult().equals("suc")){
                    Log.i(TAG, "onResponse: get meeting list succ");
                    meetingList = response.body().getMeetingList();
                    EventBus.getDefault().post(new MeetingMgrEvent(MeetingMgrEvent.EVENT_GET_MEETING_LIST_SUCC, null));
                }
                else {
                    if (nRetryDlTimes++ <= MAX_RETRY_TIMES){
                        Log.i(TAG, "onResponse: fail get meeting list. retry " + nRetryDlTimes);
                        try {
                            Thread.sleep(ONE_MIN);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        downloadMeetingList();
                    }
                    else {
                        nRetryDlTimes = 0;
                        Log.i(TAG, "onResponse: for all retry fail get meeting list ");
                        EventBus.getDefault().post(new MeetingMgrEvent(MeetingMgrEvent.EVENT_GET_MEETING_LIST_FAILED, "暂无会议安排"));
                    }
                }
                nState = SLEEP;
            }

            @Override
            public void onFailure(Call<MeetingMgrHttpResult> call, Throwable t) {
                Log.i(TAG, "onFailure: doGetMeetingList faild " + t.getMessage());
                if (nRetryDlTimes++ <= MAX_RETRY_TIMES){
                    Log.i(TAG, "onResponse: fail get meeting list. retry " + nRetryDlTimes);
                    try {
                        Thread.sleep(ONE_MIN);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    downloadMeetingList();
                }
                else {
                    nRetryDlTimes = 0;
                    Log.i(TAG, "onResponse: for all retry fail get meeting list ");
                    EventBus.getDefault().post(new MeetingMgrEvent(MeetingMgrEvent.EVENT_GET_MEETING_LIST_FAILED, "查询会议失败" + t.getMessage()));
                }
                nState = SLEEP;
            }
        });
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


    public interface MeetingListMgrCloudApi {
        @GET("getMeetingList")
        Call<MeetingMgrHttpResult> doGetMeetingList(@QueryMap Map<String, String> map);//, @Body HttpMeetingListBoy body);

        @POST("updateMeetingState")
        Call<CloudApi.SimpleHttpResult> doPostUpdateMeetingState(@Body Meeting meeting);
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
     * 同步更新本地和服务端会议状态。
     * @param currtMeeting
     */
    private void updateCurrentMeetingState(Meeting currtMeeting){
        for (int i = 0; i < meetingList.size(); i++) {
            if (currtMeeting.sameMeeting(meetingList.get(i))) {
                meetingList.get(i).setState(currtMeeting.getState());
                MeetingListMgrCloudApi api = CloudApi.getRetrofit().create(MeetingListMgrCloudApi.class);
                Call<CloudApi.SimpleHttpResult> resultCall = api.doPostUpdateMeetingState(currtMeeting);
                Log.i(TAG, "updateCurrentMeetingState: request content " + resultCall.request().toString());
                Log.i(TAG, "updateCurrentMeetingState: request body " + resultCall.request().body());
                resultCall.enqueue(new Callback<CloudApi.SimpleHttpResult>() {
                    @Override
                    public void onResponse(Call<CloudApi.SimpleHttpResult> call, Response<CloudApi.SimpleHttpResult> response) {
                        Log.i(TAG, "updateCurrentMeetingState: get post return msg " + response.message());
                        if (response.body() == null){
                            Log.i(TAG, "updateCurrentMeetingState: no response object");
                            return;
                        }

                        Log.i(TAG, "updateCurrentMeetingState: register response " + response.body().getStrResult());
                        if(response.body().getStrResult().equals(RESULT_SUCC)){
                            Log.i(TAG, "updateCurrentMeetingState: suc");
                        }
                        else {
                            Log.i(TAG, "updateCurrentMeetingState: failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<CloudApi.SimpleHttpResult> call, Throwable t) {
                        Log.i(TAG, "updateCurrentMeetingState: http failed");
                    }
                });
            }
        }
    }

    private void onMeetingNearlyStart(MeetingMgrEvent param) {
        updateCurrentMeetingState((Meeting)param.param);
    }

    private void onMeetingNearlyEnd(MeetingMgrEvent param) {
        updateCurrentMeetingState((Meeting)param.param);
    }

    private void onMeetingEnd(MeetingMgrEvent param) {
        updateCurrentMeetingState((Meeting)param.param);
    }

    private void onMeetingStart(MeetingMgrEvent param) {
        updateCurrentMeetingState((Meeting)param.param);
    }
}
