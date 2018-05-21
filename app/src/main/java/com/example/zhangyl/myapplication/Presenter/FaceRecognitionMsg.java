package com.example.zhangyl.myapplication.Presenter;

import android.util.Log;

/**
 * Created by ZhangYL on 2018/4/12 0012.
 */

// todo 尝试用json框架自动转化字符串成FaceRecognitionMsg对象
public class FaceRecognitionMsg {

    private final static String TAG = "FaceRecognitionMsg";
    public FaceRecognitionMsg(String strWebMsg){
        String[] strArray = strWebMsg.split("\\|");
        strFaceName = strArray[1];
        strImgUrl = "http://192.168.1.220:8210/" + strArray[0];
        Log.i(TAG, "FaceRecognitionMsg: str array " + strArray[0] + " " + strArray[1]);
        Log.i(TAG, "FaceRecognitionMsg: face name " + strFaceName + " img url " + strImgUrl);
    }

    private String strFaceName;
    private String strImgUrl;

    public String getFaceName(){
        return strFaceName;
    }

    public String getImgUrl(){
        return strImgUrl;
    }
}
