package com.goldze.mvvmhabit.multicast;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * 接收多点广播线程
 * Created by Administrator on 2020/7/23.
 */
public class MultiReceiveThread extends Thread {
    public static final String IP = "239.0.0.1";
    MulticastSocket multicastSocket;
    byte[] buffer;
    AudioTrack audioTrk;
    InetAddress address;
    boolean isStop;
    OnReceiveListener onReceiveListener;


    public MultiReceiveThread() {
        // 接收数据时需要指定监听的端口号
        try {
            multicastSocket = new MulticastSocket(10001);
            // 创建组播ID地址
            address = InetAddress.getByName(MultiConfig.IP);
            // 加入地址
            multicastSocket.joinGroup(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initAudioTracker();
    }

    public void setOnReceiveListener(OnReceiveListener onReceiveListener) {
        this.onReceiveListener = onReceiveListener;
    }

    private void initAudioTracker() {
        //扬声器播放
        int streamType = AudioManager.STREAM_MUSIC;
        //播放的采样频率 和录制的采样频率一样
        int sampleRate = MultiConfig.AUDIO_RATE;
        //和录制的一样的
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        //流模式
        int mode = AudioTrack.MODE_STREAM;
        //录音用输入单声道  播放用输出单声道
        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        int recBufSize = AudioTrack.getMinBufferSize(
                sampleRate,
                channelConfig,
                audioFormat);
        System.out.println("****playRecBufSize = " + recBufSize);
        audioTrk = new AudioTrack(
                streamType,
                sampleRate,
                channelConfig,
                audioFormat,
                recBufSize,
                mode);
        audioTrk.setStereoVolume(AudioTrack.getMaxVolume(),
                AudioTrack.getMaxVolume());
        buffer = new byte[recBufSize];

    }

    @Override
    public void run() {
        if (multicastSocket == null)
            return;
        //从文件流读数据
        audioTrk.play();
        // 包长
        while (!isStop) {
            try {
                // 数据报
//                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
//                // 接收数据，同样会进入阻塞状态
//                multicastSocket.receive(datagramPacket);
//                audioTrk.write(datagramPacket.getData(), 0, datagramPacket.getLength());
//                System.out.println("MultiReceiveThread = " + datagramPacket.getData().toString());


                byte[] buffer = new byte[1024];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                // 接收数据，同样会进入阻塞状态
                multicastSocket.receive(datagramPacket);
                String message = new String(buffer, 0, datagramPacket.getLength());
                System.out.println("MultiReceiveThread = " + message);
                if (onReceiveListener != null) {
                    onReceiveListener.onReceive(message);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public void release() {
        isStop = true;

        if (audioTrk != null) {
            audioTrk.release();
        }
        try {
            if (multicastSocket != null) {
                multicastSocket.leaveGroup(address);
            }
            multicastSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public interface OnReceiveListener {
        void onReceive(String message);
    }
}
