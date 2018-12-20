package com.example.recordcomment.audiorecord;


import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.text.TextUtils;

/**
 * 录音播放管理器
 *
 * 单例模式，便于管理
 */
public class AudioPlaybackManager implements OnCompletionListener {
	
	private MediaPlayer					mPlayer				= null;
	private static AudioPlaybackManager	instance			= null;
	private OnPlayingListener			mOnPlayingListener	= null;
	private String						mRecordPath			= null;
	private long						mRecordTime			= 0L;
	private String						mRecordName			= null;
	private boolean                     mIsPlaying          = false;
	private Context                     mContent            = null;

	private AudioPlaybackManager(Context context){
		mContent = context;
		initMediaPlayer();
	}

	public static AudioPlaybackManager getInstance(Context context) {
		if (instance == null) {
			synchronized (AudioPlaybackManager.class) {
				if (instance == null) {
					instance = new AudioPlaybackManager(context);
				}
			}
		}
		return instance;
	}

	private void initMediaPlayer(){
		mPlayer = new MediaPlayer();
		mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mPlayer.start();
				mIsPlaying = true;
				if (mOnPlayingListener != null) {
					mOnPlayingListener.onStart();
				}

			}
		});
		mPlayer.setOnCompletionListener(this);
	}

	public boolean isPlaying(){
		return mIsPlaying;
	}

	
	public void playAudio(String url) {
		// Todo 将语音缓存在文件夹中
		if (TextUtils.isEmpty(url)) {
			return;
		} else {
			mRecordPath = url;
			startPlaying(mRecordPath);
		}
	}
	
	private void startPlaying(String path) {
		if (TextUtils.isEmpty(path)) {
			return;
		}
		try {
			if(mPlayer == null) {
				initMediaPlayer();
			}
			mPlayer.reset();
			mPlayer.setDataSource(path);
			mPlayer.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void releasePlayer() {
		mIsPlaying = false;
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
		if(mOnPlayingListener != null) {
			mOnPlayingListener = null;
		}
	}

	public void releaseAll(){
		releasePlayer();
		instance = null;
	}

	private void stopPlayer() {
//		releasePlayer();
		if(mPlayer == null) {
			initMediaPlayer();
		}
		mPlayer.stop();
		mIsPlaying = false;
		mRecordPath = null;
		if (mOnPlayingListener != null) {
			mOnPlayingListener.onStop();
		}
	}

	public void stopAudio() {
		stopPlayer();
		mRecordPath = null;
	}
	
	@Override
	public void onCompletion(MediaPlayer player) {
		mIsPlaying = false;
		mRecordPath = null;
		if (mOnPlayingListener != null) {
			mOnPlayingListener.onComplete();
		}
	}
	
	public long getDuration() {
		mRecordTime = AudioRecordManager.getInstance().getRecordDuration();
		return mRecordTime;
	}
	
	public void setOnPlayingListener(OnPlayingListener listener) {
		mOnPlayingListener = listener;
	}
	
	public interface OnPlayingListener {
		void onStart();
		
		void onStop();
		
		void onComplete();
	}
}
