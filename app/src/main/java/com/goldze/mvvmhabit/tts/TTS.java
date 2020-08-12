package com.goldze.mvvmhabit.tts;

import android.content.Context;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.goldze.mvvmhabit.tts.TTSContent;

import java.util.HashMap;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TTS extends UtteranceProgressListener {
    private static String TAG = "TTS";
    private TextToSpeech mTextToSpeech;
    Queue<TTSContent> mTtsQueue = new ConcurrentLinkedQueue();

    public TTS() {

    }

    public void init(Context context) {
        mTextToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTextToSpeech.setLanguage(Locale.CHINA);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e(TAG, "语音合成初始化失败!");
                    } else {
                        // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
                        mTextToSpeech.setPitch(1f);
                        //设定语速 ，默认1.0正常语速
                        mTextToSpeech.setSpeechRate(0.82f);
                    }
                }
            }
        });
        mTextToSpeech.setOnUtteranceProgressListener(this);
    }

    private static TTS INSTANCE = null;

    public static TTS getInstance() {
        if (INSTANCE == null) {
            synchronized (TTS.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TTS();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void onStart(String utteranceId) {
        Log.e(TAG, "语音叫号开始" + utteranceId);
    }

    @Override
    public void onDone(String utteranceId) {
        Log.e(TAG, "语音叫号完成！" + utteranceId);
        mTextToSpeech.playSilence(1500, TextToSpeech.QUEUE_ADD, null);
        TTSContent content = mTtsQueue.peek();
        // 播放次数加1
        content.setPlayCount(content.getPlayCount() + 1);
        // 延时15秒开始播放下一条
        if (content.getMode() == content.getPlayCount()) {
            // 播放次数已经够了 移除第一个
            mTtsQueue.poll();
        }
        play();
    }

    @Override
    public void onError(String utteranceId) {
        Log.e(TAG, "语音叫号失败！");
        play();
    }

    /**
     * 播报2次
     */
    public void play2Text(String text) {
        TTSContent content = new TTSContent();
        content.setMode(2);
        content.setText(text);
        mTtsQueue.add(content);
        if (mTtsQueue.size() == 1)
            // 当前队列只有一条马上播放
            play();
    }

    /**
     * 播报1次
     */
    public void play1Text(String text) {
        TTSContent content = new TTSContent();
        content.setMode(1);
        content.setText(text);
        mTtsQueue.add(content);
        if (mTtsQueue.size() == 1)
            // 当前队列只有一条马上播放
            play();
    }

    /**
     * 播放
     */
    private void play() {
        if (mTtsQueue.isEmpty()) {
            return;
        }
        TTSContent content = mTtsQueue.peek();
        HashMap<String, String> ttsOptions = new HashMap<>();
        ttsOptions.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, content.getText());
        mTextToSpeech.speak(content.getText(), TextToSpeech.QUEUE_ADD, ttsOptions);
    }

    public void stopSpeak() {
        if (mTextToSpeech != null) {
            mTextToSpeech.shutdown();
            mTextToSpeech.stop();
        }
    }
}
