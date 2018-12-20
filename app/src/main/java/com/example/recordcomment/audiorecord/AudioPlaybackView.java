package com.example.recordcomment.audiorecord;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.recordcomment.R;
import com.example.recordcomment.utils.TimeUtil;

import java.io.File;

/**
 * Created by pzm on 2018/8/15
 */

public class AudioPlaybackView extends RelativeLayout{
	
	private Context					mContext				= null;
	private View					mRootView				= null;
	private TextView				mAudioDurationTv		= null;
	public  ImageView               mRecordPlayIv           = null;
	private AudioPlaybackManager	mAudioPlaybackManager	= null;
	
	private long					mRecortTime				= 0L;
	private String					mRecortUrl				= null;
	private File					mRecortFile				= null;


	public AudioPlaybackView(Context context) {
		this(context, null);
	}
	
	public AudioPlaybackView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public AudioPlaybackView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		initView();
		initData();
	}
	
	private void initView() {
		mRootView = LayoutInflater.from(mContext).inflate(R.layout.audio_record_playback, this, true);
		mAudioDurationTv = mRootView.findViewById(R.id.tv_audio_duration);
		mRecordPlayIv = mRootView.findViewById(R.id.iv_voice_icon);
	}
	
	private void initData() {
		mAudioPlaybackManager = AudioPlaybackManager.getInstance(mContext);
		setDuration();
	}
	
	public void setDuration() {
		mRecortTime = mAudioPlaybackManager.getDuration();
		mAudioDurationTv.setText(TimeUtil.formatRecordTime((int) mRecortTime));
	}
	
	public void setDuration(int time) {
		mRecortTime = time;
		mAudioDurationTv.setText(TimeUtil.formatRecordTime((int) mRecortTime));
	}

	public void setRecordFile(File file){
		mRecortFile = file;
	}

	public void setUrl(String audioUrl) {
		mRecortUrl = audioUrl;
	}
}
