package com.example.recordcomment.audiorecord;

import java.io.File;

/**
 * 项目中，一般选择一种就可以了，所以这个基类，只是为了方便切换使用
 */
public abstract class BaseRecordManager {

    public abstract File getRecordFile();

    public abstract String getRecordPath();

    public abstract void startRecord(String audioFileName);

    public abstract void stopRecord();

    public abstract void cancelRecord();


    public abstract int getVoiceLevel(int maxLevel);
}
