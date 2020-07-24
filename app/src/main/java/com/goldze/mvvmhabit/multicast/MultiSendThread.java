package com.goldze.mvvmhabit.multicast;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.LinkedList;

/**
 * 发送多点广播线程
 * Created by Administrator on 2020/7/23.
 */
public class MultiSendThread extends Thread {
    MulticastSocket multicastSocket;
    InetAddress address;
    boolean isStop;

    protected LinkedList<byte[]> mRecordQueue;
    int minBufferSize;
    private static AcousticEchoCanceler aec;
    private static AutomaticGainControl agc;
    private static NoiseSuppressor nc;
    private  AudioRecord audioRec;
    private   byte[] buffer;
    private OnSendListener onSendListener;

    public MultiSendThread() {
        // 侦听的端口
        try {
            multicastSocket = new MulticastSocket(8082);
            // 使用D类地址，该地址为发起组播的那个ip段，即侦听10001的套接字
            address = InetAddress.getByName(MultiConfig.IP);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initAudio();
    }

    public void setOnSendListener(OnSendListener onSendListener) {
        this.onSendListener = onSendListener;
    }

    private void initAudio() {
        //播放的采样频率 和录制的采样频率一样
        int sampleRate = MultiConfig.AUDIO_RATE;
        //和录制的一样的
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        //录音用输入单声道  播放用输出单声道
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;

        minBufferSize = AudioRecord.getMinBufferSize(
                sampleRate,
                channelConfig,
                audioFormat);
        System.out.println("****record minBufferSize = " + minBufferSize);
        audioRec = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                minBufferSize);
        buffer = new byte[minBufferSize];

        if (audioRec == null) {
            return;
        }
        //声学回声消除器 AcousticEchoCanceler 消除了从远程捕捉到音频信号上的信号的作用
        if (AcousticEchoCanceler.isAvailable()) {
            aec = AcousticEchoCanceler.create(audioRec.getAudioSessionId());
            if (aec != null) {
                aec.setEnabled(true);
            }
        }

        //自动增益控制 AutomaticGainControl 自动恢复正常捕获的信号输出
        if (AutomaticGainControl.isAvailable()) {
            agc = AutomaticGainControl.create(audioRec.getAudioSessionId());
            if (agc != null) {
                agc.setEnabled(true);
            }
        }

        //噪声抑制器 NoiseSuppressor 可以消除被捕获信号的背景噪音
        if (NoiseSuppressor.isAvailable()) {
            nc = NoiseSuppressor.create(audioRec.getAudioSessionId());
            if (nc != null) {
                nc.setEnabled(true);
            }
        }
        mRecordQueue = new LinkedList<byte[]>();
    }

    int i = 0;

    @Override
    public void run() {
        if (multicastSocket == null)
            return;
        try {
            audioRec.startRecording();
            while (!isStop) {
                try {
                    byte[] bytes_pkg = buffer.clone();
                    if (mRecordQueue.size() >= 2) {
                        int length = audioRec.read(buffer, 0, minBufferSize);
                        // 组报
                        DatagramPacket datagramPacket = new DatagramPacket(buffer, length);
                        // 向组播ID，即接收group /239.0.0.1  端口 10001
                        datagramPacket.setAddress(address);
                        // 发送的端口号
                        datagramPacket.setPort(10001);
                        System.out.println("AudioRTwritePacket = " + datagramPacket.getData().toString());

                        multicastSocket.send(datagramPacket);
                        if (onSendListener != null) {
                            onSendListener.onSend(i++ + ",");
                        }
                    }
                    mRecordQueue.add(bytes_pkg);


//                    String message = "abc:" + i++;
//                    System.out.println("AudioRTwritePacket = " + message);
//                    byte[] bytes_pkg = message.getBytes();
//                    // 组报
//                    DatagramPacket datagramPacket = new DatagramPacket(bytes_pkg, bytes_pkg.length);
//                    // 向组播ID，即接收group /239.0.0.1  端口 10001
//                    datagramPacket.setAddress(address);
//                    datagramPacket.setPort(10001);
//                    multicastSocket.send(datagramPacket);
//                    if (onSendListener != null) {
//                        onSendListener.onSend(message);
//                    }
//                    Thread.sleep(500);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        isStop = true;
        if (audioRec != null) {
            audioRec.release();
        }
        try {
            if (multicastSocket != null) {
                multicastSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnSendListener {
        void onSend(String message);
    }
}
