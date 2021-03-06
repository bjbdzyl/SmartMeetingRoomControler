package com.example.zhangyl.myapplication.Presenter;

import java.util.List;

/**
 * Created by ZhangYL on 2018/2/7 0007.
 */

public class Meeting {

    String name;
    String start_time; // 格式 "2017-08-09 10:11";
    String end_time;
    String host;
    String room_name;
    String title;
    List<MeetingMember> memberList;
    int state = -1;

    public static final int MEETING_STATE_NEARLY_START = 0;
    public static final int MEETING_STATE_START = 1;
    public static final int MEETING_STATE_NEARLY_END = 2;
    public static final int MEETING_STATE_END = 3;
    public static final int MEETING_STATE_CANCELD = 4;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public boolean sameMeeting(Meeting meeting) {
        return meeting.getStart_time().compareTo(start_time) == 0
                && meeting.getName().compareTo(name) == 0
                && meeting.getTitle().compareTo(title) == 0
                && meeting.getEnd_time().compareTo(end_time) == 0;
    }

    class MeetingMember{
        String name;
        String email;
    }

    public List<MeetingMember> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<MeetingMember> memberList) {
        this.memberList = memberList;
    }
}
