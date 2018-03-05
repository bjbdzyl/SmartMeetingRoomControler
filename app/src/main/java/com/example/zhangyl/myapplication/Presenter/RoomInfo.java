package com.example.zhangyl.myapplication.Presenter;

/**
 * Created by ZhangYL on 2018/1/23 0023.
 */

public class RoomInfo {
    public String getControler_ip() {
        return controler_ip;
    }

    public void setControler_ip(String controler_ip) {
        this.controler_ip = controler_ip;
    }

    public String getRoom_number() {
        return room_number;
    }

    public void setRoom_number(String room_number) {
        this.room_number = room_number;
    }

    public int getRoom_size() {
        return room_size;
    }

    public void setRoom_size(int room_size) {
        this.room_size = room_size;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    String controler_ip;
    String room_number;
    int room_size;
    String room_name;

    public void RoomInfo(String strCtrlIp, String strRoomNum, int nRoomSize, String strRoomName){
        controler_ip = strCtrlIp;
        room_number = strRoomNum;
        room_size = nRoomSize;
        room_name = strRoomName;
    }
}
