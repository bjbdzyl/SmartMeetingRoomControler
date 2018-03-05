package com.example.zhangyl.myapplication.Presenter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by ZhangYL on 2018/2/26 0026.
 * 后台服务。会议流程管理，负责会前、会中、会后状态的切换，同步状态到服务器，会议计时，
 */

public class MeetingProgressMgr extends BroadcastReceiver implements AppCallBack {
    public final String TAG = "MeetingProgressMgr";
    private static MeetingProgressMgr gInstance;
    Meeting meeting = null;

    private AlarmManager alarmManager = (AlarmManager)App.getInstance().getContext().getSystemService(ALARM_SERVICE);
    private boolean appStop = false;

    public static MeetingProgressMgr getInstance() {
        if(gInstance == null){
            gInstance = new MeetingProgressMgr();
        }

        return gInstance;
    }

    public void onMeetingNearlyStart(){
        meeting.setState(Meeting.MEETING_STATE_NEARLY_START);
    }

    public void onMeetingStart(){
        // todo 检查签入状态，如果没签入，五分钟后自动签入。
        meeting.setState(Meeting.MEETING_STATE_START);
    }

    public void onMeetingEnd(){
        // todo 检查签出状态，如果没签出，五分钟后自动签出
        meeting.setState(Meeting.MEETING_STATE_END);
        registerNextMeeting();
    }

    public void onMeetingNearlyEnd(){
        meeting.setState(Meeting.MEETING_STATE_NEARLY_END);
    }

    /**
     * 线程，循环尝试获取下一个会议，注册系统闹钟alarm manager
     */
    public void registerNextMeeting(){
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {

                while (meeting == null){
                    if (appStop){
                        return;
                    }
                    meeting = MeetingListMgr.getInstance().getCurrentMeeting();
                    try {
                        Thread.sleep(1000 * 60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (meeting.state == Meeting.MEETING_STATE_START){
                    BroadcastMeetingStart(meeting);
                } else {
                    Intent intent = new Intent(App.getInstance().getContext(), MeetingProgressMgr.class);
                    intent.putExtra("name", meeting.getName());
                    intent.putExtra("start_time", meeting.getStart_time());
                    intent.putExtra("end_time", meeting.getEnd_time());
                    intent.putExtra("host", meeting.getHost());
                    intent.putExtra("title", meeting.getTitle());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try {
                        long meeting_start_at_millis = sdf.parse(meeting.getStart_time()).getTime();

                        // 注册提前5分钟闹钟
                        intent.putExtra("alarm_event", MeetingMgrEvent.EVENT_MEETING_NEARLY_START);
                        PendingIntent pi = PendingIntent.getBroadcast(App.getInstance().getContext(), 0, intent, 0);
                        long pre_meeting_at_millis = meeting_start_at_millis - 5 * 60;
                        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, pre_meeting_at_millis, pi);

                        // 注册开始闹钟
                        intent.putExtra("alarm_event", MeetingMgrEvent.EVENT_MEETING_START);
                        //long meeting_start_at_millis = 0;
                        pi = PendingIntent.getBroadcast(App.getInstance().getContext(), 0, intent, 0);
                        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, meeting_start_at_millis, pi);

                        // 注册结束闹钟
                        intent.putExtra("alarm_event", MeetingMgrEvent.EVENT_MEETING_END);
                        long meeting_end_at_millis = sdf.parse(meeting.getEnd_time()).getTime();
                        pi = PendingIntent.getBroadcast(App.getInstance().getContext(), 0, intent, 0);
                        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, meeting_end_at_millis, pi);

                        // 注册最后5分钟闹钟
                        intent.putExtra("alarm_event", MeetingMgrEvent.EVENT_MEETING_NEARLY_END);
                        long meeting_pre_end_at_millis = meeting_end_at_millis - 5 * 60;
                        pi = PendingIntent.getBroadcast(App.getInstance().getContext(), 0, intent, 0);
                        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, meeting_pre_end_at_millis, pi);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
//                    alarmManager.set(ELAPSED_REALTIME_WAKEUP, pre_alarm_at_millis, "metting_pre_alarm", new AlarmManager.OnAlarmListener() {
//                        @Override
//                        public void onAlarm() {
//                            Log.i(TAG, "onAlarm: get meeting pre alarm");
//                        }
//                    }, null);
                }
            }
        }).start();
    }

    private void BroadcastMeetingStart(Meeting meeting) {
        Intent intent = new Intent(App.getInstance().getContext(), MeetingProgressMgr.class);
        intent.putExtra("name", meeting.getName());
        intent.putExtra("start_time", meeting.getStart_time());
        intent.putExtra("end_time", meeting.getEnd_time());
        intent.putExtra("host", meeting.getHost());
        intent.putExtra("title", meeting.getTitle());
        intent.putExtra("alarm_event", MeetingMgrEvent.EVENT_MEETING_START);

        App.getInstance().getContext().sendBroadcast(intent);
    }

    @Override
    public void onAppStart() {
        EventBus.getDefault().register(this);
        App.getInstance().getContext().registerReceiver(this, null);
        registerNextMeeting();
    }

    @Override
    public void onAppStop() {
        EventBus.getDefault().unregister(this);
        App.getInstance().getContext().unregisterReceiver(this);
        appStop = true;
    }

    public Meeting getCurrentMeeting(){
        return meeting;
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMeetingMgrEvent(MeetingMgrEvent param) {
        // TODO handle meeting mgr event.
        switch (param.event){
            case MeetingMgrEvent.EVENT_MEETING_START:
                onMeetingStart();
                break;
            case MeetingMgrEvent.EVENT_MEETING_NEARLY_START:
                onMeetingNearlyStart();
                break;
            case MeetingMgrEvent.EVENT_MEETING_NEARLY_END:
                onMeetingNearlyEnd();
                break;
            case MeetingMgrEvent.EVENT_MEETING_END:
                onMeetingEnd();
                break;
        }
    }

    @Override
    /**
     * 接收MeetingListMgr设置的新会议AlarmManager闹钟广播，用于接管新会议和流程管理管理工作。
     */
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: get sys tem broad cast msg");
        int alarm_type = intent.getIntExtra("alarm_event", -1);
        switch (alarm_type){
            case MeetingMgrEvent.EVENT_MEETING_START:
            case MeetingMgrEvent.EVENT_MEETING_NEARLY_START:
            case MeetingMgrEvent.EVENT_MEETING_NEARLY_END:
            case MeetingMgrEvent.EVENT_MEETING_END:
                Log.i(TAG, "onReceive: get meeting alarm clock event " + alarm_type);
                EventBus.getDefault().post(new MeetingMgrEvent(intent.getIntExtra("alarm_event", -1), MeetingProgressMgr.getInstance().getCurrentMeeting()));
        }
    }
}
