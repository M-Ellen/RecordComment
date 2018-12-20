package com.example.recordcomment.audiorecord;

import android.media.MediaRecorder;
import android.util.Log;

import com.example.recordcomment.utils.FileUtils;
import com.example.recordcomment.utils.StringUtil;

import java.io.File;
import java.io.IOException;

/**
 * 使用MediaRecorder：使用简单，代码少；但是，格式转换少，不兼容IOS
 *
 * 录制音频的控制器
 */
public class MediaRecordManager  extends  BaseRecordManager{

    private static final String TAG = "MediaRecordManager";
    private volatile static MediaRecordManager mInstance = null;
    private MediaRecorder mMediaRecorder;
    private String audioFileName;
    private boolean mIsPlaying = false;

    private MediaRecordManager(){
    }

    public static MediaRecordManager getInstance(){
        if(mInstance == null){
            synchronized (MediaRecordManager.class){
                if(mInstance == null){
                    mInstance = new MediaRecordManager();
                }
            }
        }
        return mInstance;
    }


    @Override
    public String getRecordPath(){
        return audioFileName;
    }

    @Override
    public File getRecordFile(){
        File file = null;
        if(FileUtils.fileIsExists(audioFileName)) {
            file = new File(audioFileName);
        }
        return file;
    }

    public boolean isPlaying(){
        return mIsPlaying;
    }

    @Override
    public void startRecord(String audioFileName){
        if(StringUtil.isNullOrEmpty(audioFileName)) {
            return;
        }
        reset();
        this.audioFileName = audioFileName;
        if(!mIsPlaying){
            Log.d(TAG,"startRecord()");
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置音频格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
            //设置音频的格式为amr
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setOutputFile(audioFileName);

            try {
                mMediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaRecorder.start();
            mIsPlaying = true;
        }
    }


    private void reset(){
        audioFileName = null;
        mIsPlaying = false;
    }
    @Override
    public void stopRecord(){
        Log.d(TAG,"startRecord()");
        mMediaRecorder.stop();
        release();
    }

    @Override
    public void cancelRecord(){
        Log.d(TAG,"cancelRecord()");
        deleteRecordFile();
        stopRecord();

    }

    private void release(){
        if(mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    /**
     * 取消时，则不保存文件
     */
    private void deleteRecordFile(){
        FileUtils.deleteFile(audioFileName);
    }

    /**
     * 获得录音的音量
     * @return
     */
    @Override
    public int getVoiceLevel(int maxLevel) {
        //获得最大的振幅getMaxAmplitude() 1-32767
        try {
            return maxLevel * mMediaRecorder.getMaxAmplitude()/32768+1;
        } catch (Exception e) {

        }
        return 1;
    }
}
