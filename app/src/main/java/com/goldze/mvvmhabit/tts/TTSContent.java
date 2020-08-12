package com.goldze.mvvmhabit.tts;

public class TTSContent {
    private int mode;       //1:单次播报，2：播报2次
    private String text;
    private int playCount;// 已播放次数


    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }
}
