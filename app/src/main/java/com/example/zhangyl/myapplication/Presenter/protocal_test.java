package com.example.zhangyl.myapplication.Presenter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhangyl.myapplication.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import base.utils.codec.DecoderException;
import base.utils.codec.binary.Hex;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 中控机WIFI通信协议测试类
 */
public class protocal_test extends AppCompatActivity {
    private static final String TAG = "protocal_test";
    @BindView(R.id.light_on)
    Button lightOn;
    @BindView(R.id.light_off)
    Button lightOff;
    @BindView(R.id.seek_bar_ctrl)
    SeekBar seekBarCtrl;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.debug_view)
    TextView debugView;
    @BindView(R.id.btn_get_all_state)
    Button btnGetAllState;
    @BindView(R.id.txt_view_all_state)
    TextView txtViewAllState;

    OutputStream out_put ;
    BufferedReader buf_reader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocal_test);
        ButterKnife.bind(this);
        init_light_seek_bar();
        init_socket();
    }

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
    Socket m_send_socket = null;
    Socket m_read_socket = null;

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
    public void light_on() { // OK

        if (m_light_on) {
            //return;
        }

        new Thread() {
            public void run() {
                try {
                    if (m_send_socket == null) {
                        Log.i(TAG, "light_on: light on socket null");
                        return;
                    }

                    //m_send_socket.getOutputStream().write(b);
                    out_put.write(Hex.decodeHex(new String("1B43DD0D0A050080DDDDDDDD1B43DD0D0A050000")));
                    //out_put.write(Hex.decodeHex(new String("1B43DD0D0A050000")));
                    char[] buf = new char[4096];
                    if (!buf_reader.ready()){
                        Log.i(TAG, "run: light on socket read not ready");
                        return;
                    }
                    if ((buf_reader.read(buf, 0, buf.length)) > 0) {
                        //String hexStr = Hex.encodeHexString(buf);
                        Log.i(TAG, "light on socket return " + buf);
                    } else {
                        Log.i(TAG, "light on nothing return");
                    }
                    //readSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "light_on: send socket cmd io exception");
                } catch (DecoderException e) {
                    e.printStackTrace();
                    Log.i(TAG, "light on fail to decode hex string");
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
                        } else {
                            Log.i(TAG, "read socket nothing");
                            Thread.sleep(500);
                        }

                } catch (IOException e) {
                    Log.e(TAG, "read socket exception" + e.getMessage());
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    Log.e(TAG, "read socket interrupted exception" + e.getMessage());
                    e.printStackTrace();
                }
                }
            }
        }).start();
    }

    /**
     * 关灯
     */
    public void light_off() { // ok
        if (!m_light_on) {
            //return;
        }
        new Thread() {
            public void run() {
                try {
                    if (m_send_socket == null) {
                        Log.i(TAG, "light_off socket null");
                        return;
                    }

                    out_put.write(Hex.decodeHex(new String("1B43DD0D0A060080")));
                    out_put.write(Hex.decodeHex(new String("1B43DD0D0A060000")));

                    char[] buf = new char[4096];
                    if (!buf_reader.ready()){
                        Log.i(TAG, "run: light off socket read not ready");
                        return;
                    }
                    if ((buf_reader.read(buf, 0, buf.length)) > 0) {
                        String hexStr = Hex.encodeHexString(new String(buf).getBytes());
                        Log.i(TAG, "light off socket return " + buf);
                    } else {
                        Log.i(TAG, "light off nothing return");
                    }

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
    public void on_light_seek_change(final int progress) {

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

    /**
     * 初始化滑动开关
     */
    private void init_light_seek_bar() {
        SeekBar seekBar = (SeekBar) findViewById(R.id.seek_bar_ctrl);
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Toast.makeText(getApplicationContext(), "正在调节亮度", Toast.LENGTH_SHORT).show();
                    on_light_seek_change(progress);
                    //readSocket();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }

    /**
     * 获取所有设备的开关状态
     */
    private void get_all_state() { // doing debug
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (m_send_socket == null) {
                        Log.i(TAG, "get all state faild. socket null");
                        return;
                    }

                    out_put.write(Hex.decodeHex(new String("1b43dd0d0a1F1F80")));
                    out_put.write(Hex.decodeHex(new String("1b43dd0d0a1F1F00")));
                    out_put.flush();
                    //out_put.close();
                    Log.i(TAG, "run: get all state send end.");
//

                    // 从InputStream当中读取返回的数据
                    char[] buf = new char[4096];
                    if (!buf_reader.ready()){
                        Log.i(TAG, "run: get all state socket read not ready");
                        return;
                    }
                    if ((buf_reader.read(buf, 0, buf.length)) > 0) {

                        //String hexStr = Hex.encodeHexString(buf);
                        Log.i(TAG, "get all state socket return " + buf);
                    } else {
                        Log.i(TAG, "get all state failed. nothing return");
                    }
                    //buf_reader.close();
                } catch (IOException e) {
                    Log.i(TAG, "run: get all state socket send failed");
                    e.printStackTrace();
                } catch (DecoderException e) {
                    e.printStackTrace();
                    Log.e(TAG, "get all state decode hex string faild");
                }
            }
        }).start();
    }

    @OnClick(R.id.light_on)
    public void onLightOnClicked() {
        Toast.makeText(getApplicationContext(), "开灯", Toast.LENGTH_SHORT).show();
        light_on();
    }

    @OnClick(R.id.light_off)
    public void onLightOffClicked() {
        Toast.makeText(getApplicationContext(), "关灯", Toast.LENGTH_LONG).show();
        light_off();
    }

    @OnClick(R.id.btn_get_all_state)
    public void onBtnGetAllStateClicked() {
        get_all_state();
    }
}
