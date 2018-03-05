package com.example.zhangyl.myapplication.Presenter;

import android.support.annotation.NonNull;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.zhangyl.myapplication.Presenter.RegisterMgr.HTTP_TEST_SERVER;

/**
 * Created by ZhangYL on 2018/3/2 0002.
 */

public class CloudApi {
    private static Retrofit retrofit = null;
    @NonNull
    public static Retrofit getRetrofit() {
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(HTTP_TEST_SERVER)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static final String RESULT_SUCC = "suc";
    public static final String RESULT_FAILD = "fail";

    public class SimpleHttpResult {
        String result;
        public String getStrResult(){
            return result;
        }

        public void setStrResult(String str){
            result = str;
        }
    }
}
