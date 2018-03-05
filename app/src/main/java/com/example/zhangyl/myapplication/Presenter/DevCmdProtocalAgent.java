package com.example.zhangyl.myapplication.Presenter;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import base.utils.codec.DecoderException;
import base.utils.codec.binary.Hex;

/**
 * 中控机WIFI通信协议测试类
 */
public class DevCmdProtocalAgent implements AppCallBack {
    private static final String TAG = "DevCmdProtocalAgent";

    OutputStream out_put ;
    BufferedReader buf_reader;
    Socket m_send_socket = null;
    Socket m_read_socket = null;

    /**
     *初始化socket
     *
     */
    //192.168.1.38:9696
    private void init_socket() {
        new Thread() {
            public void run() {
                if (m_send_socket == null) {
                    try {
                        m_send_socket = new Socket("192.168.1.222", 15000);
                        if (m_send_socket != null){
                            out_put = m_send_socket.getOutputStream();
                            //buf_reader = new BufferedReader(new InputStreamReader(m_send_socket.getInputStream()));
                        }
                        m_read_socket = new Socket("192.168.1.222", 15000);
                        if (m_read_socket != null){
                            buf_reader = new BufferedReader(new InputStreamReader(m_read_socket.getInputStream()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (m_send_socket != null) {
                        Log.i("my log", "run:socket init suc");
                    }
                }
            }
        }.start();
    }

    boolean m_light_on = false;


    /**
     * 开灯
     * 输入数字量
     * 1b 43 dd 0d 0a JoinNumL JoinNumH Value
     * 1b 43 dd 0d 0a    02         00   80 按钮编号为2，表示按钮按下
     * 1b 43 dd 0d 0a    02         00   00 按钮编号为2，表示按钮弹起
     * 数字量JoinNumber范围应该在十进制0-998
     *
     * 获取所有按钮的状态
     * 1b 43 dd 0d 0a 1F 1F 80 按钮编号为HEX1F1F（十进制7967），表示要获取所有按钮的当前状态，用来做状态显示。
     *
     * 输入模拟量
     * 1b 43 df 0d 0a JoinNumL JoinNumH AnalogL AnalogH
     * 1b 43 df 0d 0a   00       01        10      20  按钮编号为256 ，模拟量的值为HEX2010（十进制8208）
     * 模拟量JoinNumber范围应该在十进制999-1999，
     */
    public void deviceOn(long lIoPort) { // OK
        new Thread() {
            public void run() {
                try {
                    if (m_send_socket == null) {
                        Log.i(TAG, "light_on: light on socket null");
                        return;
                    }

                    out_put.write(Hex.decodeHex(new String("1B43DD0D0A050080DDDDDDDD1B43DD0D0A050000")));

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "device on: send socket cmd io exception");
                } catch (DecoderException e) {
                    e.printStackTrace();
                    Log.i(TAG, "device on: fail to decode hex string");
                }
                m_light_on = true;
            }
        }.start();
    }

    /**
     * 读socket接收的数据
     */
    private void readSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        //BufferedReader buf_reader = new BufferedReader(new InputStreamReader(m_send_socket.getInputStream()));
                        // 从InputStream当中读取返回的数据
                        String content;

                        if ((content = buf_reader.readLine()) != null) {
                            Log.i(TAG, "read socket return " + content);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "read socket exception" + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 关灯
     */
    public void deviceOff() {
        new Thread() {
            public void run() {
                try {
                    if (m_send_socket == null) {
                        Log.i(TAG, "light_off socket null");
                        return;
                    }
                    out_put.write(Hex.decodeHex(new String("1B43DD0D0A060080")));
                    out_put.write(Hex.decodeHex(new String("1B43DD0D0A060000")));
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "light_off: fail send socket");
                } catch (DecoderException e) {
                    e.printStackTrace();
                    Log.i(TAG, "light_off: fail decode hex string");
                }
                m_light_on = false;
            }
        }.start();
    }

    /**
     * 滑动调节灯亮度
     * @param progress
     */
    public void deviceProgressChange(final int progress) {
        new Thread() {
            public void run() {
                int light_progress = 999 + (progress / 100 * 1000);
                try {
                    if (m_send_socket != null) {
                        out_put.write(Hex.decodeHex(new String("1b43df0d0a0001") + Integer.toHexString(light_progress)));
                        //readSocket();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DecoderException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    @Override
    public void onAppStart() {

    }

    @Override
    public void onAppStop() {

    }
}
