package com.example.zhangyl.myapplication.Presenter;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import base.utils.codec.DecoderException;
import base.utils.codec.binary.Hex;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * 中控机WIFI通信协议测试类.todo netty implement
 */
public class DevCmdProtocalAgent implements AppCallBack {
    private static final String TAG = "DevCmdProtocalAgent";

    OutputStream out_put ;
    //BufferedReader buf_reader;
    Socket m_send_socket = null;
    //Socket m_read_socket = null;
    SocketClientHandler socketClientHandler = new SocketClientHandler();
    private Channel socket_chanel;

    public class SocketClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
        private ChannelHandlerContext mCtx;
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            //当被通知Channel是活跃的时候，发送一条消息
            ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
            mCtx = ctx;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
            System.out.println(
                    "Client received: " + byteBuf.toString(CharsetUtil.UTF_8)
            );
            if (!byteBuf.hasArray()) {//false表示为这是直接缓冲
                int length = byteBuf.readableBytes();//得到可读字节数
                byte[] array = new byte[length];    //分配一个具有length大小的数组
                byteBuf.getBytes(byteBuf.readerIndex(), array); //将缓冲区中的数据拷贝到这个数组中
                Log.i(TAG, "channelRead0: read socket return " + Hex.encodeHex(array));
                // todo 解析自家协议，广播设备开关状态、强度值变化事件
                //System.out.println("rec hex " + DatatypeConverter.printHexBinary(array));
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
    /**
     *netty用socket通信
     *
     */
    private void init_socket() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(RegisterMgr.get_instance().roomInfo.controler_ip, 15000))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(socketClientHandler);
                    }
                });
        try {
            socket_chanel = bootstrap.connect().sync().channel();
            //f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

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
     * 输入模拟量
     * 1b 43 df 0d 0a JoinNumL JoinNumH AnalogL AnalogH
     * 1b 43 df 0d 0a   00       01        10      20  按钮编号为256 ，模拟量的值为HEX2010（十进制8208）
     * 模拟量JoinNumber范围应该在十进制999-1999，
     */
    public void deviceOn(long lIoPort) {
        try {
            // TODO: 2018/3/9 0009 调用具体的协议类拼出数据，往端口发。
            socket_chanel.writeAndFlush(Hex.decodeHex(new String("1B43DD0D0A050080DDDDDDDD1B43DD0D0A050000"))).sync();
        } catch (DecoderException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        new Thread() {
//            public void run() {
//                try {
//                    if (m_send_socket == null) {
//                        Log.i(TAG, "light_on: light on socket null");
//                        return;
//                    }
//
//                    out_put.write(Hex.decodeHex(new String("1B43DD0D0A050080DDDDDDDD1B43DD0D0A050000")));
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.i(TAG, "device on: send socket cmd io exception");
//                } catch (DecoderException e) {
//                    e.printStackTrace();
//                    Log.i(TAG, "device on: fail to decode hex string");
//                }
//                m_light_on = true;
//            }
//        }.start();
    }

    /**
     * 关灯
     */
    public void deviceOff(long lIoPort) {
        try {
            // TODO: 2018/3/9 0009 调用具体的协议类拼出数据，往端口发。
            socket_chanel.writeAndFlush(Hex.decodeHex(new String("1B43DD0D0A060080DDDDDDDD1B43DD0D0A060000"))).sync();
        } catch (DecoderException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        new Thread() {
//            public void run() {
//                try {
//                    if (m_send_socket == null) {
//                        Log.i(TAG, "light_off socket null");
//                        return;
//                    }
//                    out_put.write(Hex.decodeHex(new String("1B43DD0D0A060080")));
//                    out_put.write(Hex.decodeHex(new String("1B43DD0D0A060000")));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Log.i(TAG, "light_off: fail send socket");
//                } catch (DecoderException e) {
//                    e.printStackTrace();
//                    Log.i(TAG, "light_off: fail decode hex string");
//                }
//                m_light_on = false;
//            }
//        }.start();
    }

    /**
     * 滑动调节灯亮度
     * @param progress
     */
    public void deviceProgressChange(long lIoPort, final int progress) {
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
