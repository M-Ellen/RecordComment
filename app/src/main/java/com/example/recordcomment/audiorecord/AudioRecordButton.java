package com.example.recordcomment.audiorecord;

import java.io.File;
import java.io.IOException;

import com.common.lamejava.mp3.LameConvert;
import com.example.recordcomment.R;
import com.example.recordcomment.utils.FileUtils;
import com.example.recordcomment.widget.RecordDialog;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


public class AudioRecordButton extends Button {
	
	public static final int RECORDED_MANAGER = 0; //0:MediaRecordManager    1:AudioRecordManager

	private static final String	TAG						= AudioRecordButton.class.getSimpleName();
	/**
	 * 手指向上滑动 取消距离
	 */
	
	private static final int	DISTANCE_Y_CANCEL		= 80;
	/**
	 * 录音 状态
	 */
	private static final int	STATE_NORMAL			= 1;
	private static final int	STATE_RECORDING			= 2;
	private static final int	STATE_STOP				= 3;
	private static final int	STATE_WANT_TO_CANCEL	= 4;
	private int					mCurState				= STATE_NORMAL;
	
	/**
	 * 是否准备录音
	 */
	private boolean				mIsReaded				= false;
	/**
	 * 录制的时间
	 */
	private float				mRecordTime				= 0;
	private static final float	MIN_RECORD_TIME			= 1.2f;
	private static final float	MAX_RECORD_TIME			= 60f;
	
	private static final int	MSG_AUDIO_START			= 0X110;
	private static final int	MSG_VOICE_CHANGED		= 0X111;
	private static final int	MSG_DIALOG_DISMISS		= 0X112;
	
	private Context				mContext				= null;
	private RecordDialog		mRecordDialog			= null;
	private BaseRecordManager	mRecordManager			= null;
	private VoiceLevelThread	mThread					= null;
	private int					mOldVoiceLevel			= 0;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_AUDIO_START :
					mRecordDialog.show(getContext());
					changeState(STATE_RECORDING);
					break;
				case MSG_VOICE_CHANGED :
					int curVoiceLevel = mRecordManager.getVoiceLevel(7);
					Log.e("debug", "音量 curVoiceLevel = " + curVoiceLevel);
					if(mOldVoiceLevel != curVoiceLevel) {
						mRecordDialog.updateVoiceLevel(curVoiceLevel);
						mOldVoiceLevel = curVoiceLevel;
					}
					break;
				case MSG_DIALOG_DISMISS :
					mRecordDialog.hide();
					break;
				default:
					break;
			}
		}
	};


	public AudioRecordButton(Context context) {
		this(context, null);
		
	}
	
	public AudioRecordButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public AudioRecordButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		initData();
		initView();
	}
	
	private void initData() {
		if(RECORDED_MANAGER == 0) {
			mRecordManager = MediaRecordManager.getInstance();
		}else {
			mRecordManager = AudioRecordManager.getInstance();

		}
	}
	
	private void initView() {
		mRecordDialog = new RecordDialog();
		//响应长按，才开始录音
		setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Log.e("debug", "==onLongClick==");
				mIsReaded = true;
				mRecordTime = 0;
				startRecord();
				return false;
			}
		});

	}

	private void startRecord() {
		mRecordManager.startRecord(FileUtils.getRecordCacheDirPath(mContext));
		mHandler.sendEmptyMessage(MSG_AUDIO_START);
		if(mThread == null) {
			mThread = new VoiceLevelThread();
		}
		mThread.start();
	}
	private void stopRecord() {
		mRecordManager.stopRecord();
	}
	private void cancelRecord() {
		mRecordManager.cancelRecord();
	}


	private void reset(){
		mOldVoiceLevel = 0;
		mIsReaded = false;
		changeState(STATE_NORMAL);
		if(mThread != null) {
			mThread = null;
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN :
				changeState(STATE_RECORDING);
				break;
			case MotionEvent.ACTION_MOVE :
				if(wantToCancel(x, y)) {
					changeState(STATE_WANT_TO_CANCEL);
				} else {
					changeState(STATE_RECORDING);
				}
				break;
			case MotionEvent.ACTION_UP :
				if(!mIsReaded) {
				    reset();
				    return super.onTouchEvent(event);
				}
				if(tooShort()) {
					mRecordDialog.tooShort();
					cancelRecord();
					mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 500);
				}else {
					if(mCurState == STATE_WANT_TO_CANCEL) {
						cancelRecord();
					}else if(mCurState == STATE_RECORDING) {
						stopRecord();
						/**
						 * AudioRecordManager 格式转换
						 */
						if(mRecordManager instanceof AudioRecordManager) {
							AwvToMp3Thread awvToMp3Thread = new AwvToMp3Thread();
							awvToMp3Thread.execute(mRecordManager.getRecordPath());
						}else {
							if(mRecordStatusListener != null) {
								mRecordStatusListener.OnRecordCompleteListener(getRecordTime(), mRecordManager.getRecordFile());
							}

						}
					}else {
						stopRecord();
					}
					mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 0);
				}
				reset();
				break;
			default :
				break;
		}
		return super.onTouchEvent(event);
	}

	private boolean tooShort(){
		return !mIsReaded || mRecordTime < MIN_RECORD_TIME;
	}

	/**
	 * 这里  暂时不考虑时间长 问题
	 */
//	private boolean tooLong(){
//		return mIsReaded && mCurState == STATE_RECORDING && mRecordTime > MAX_RECORD_TIME;
//	}

	private boolean wantToCancel(int x, int y){
//		//如果左右滑出 button
//		if (x < 0 || x > getWidth()) {
//			return true;
//		}
		//如果上下滑出 button  加上我们自定义的距离
		if (y < - DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
			return true;
		}
		return false;
	}

	/**
	 * 改变状态，更新UI
	 */
	private void changeState(int state) {
		if (mCurState != state) {
			mCurState = state;
			switch (state) {
				case STATE_NORMAL:
					setBackgroundResource(R.drawable.record_comment_bg);
					setText(R.string.record_input_normal);
					break;
				case STATE_RECORDING:
					setBackgroundResource(R.drawable.record_comment_bg2);
					setText(R.string.record_input_recording);
					mRecordDialog.recording();
					break;
				case STATE_WANT_TO_CANCEL:
					setBackgroundResource(R.drawable.record_comment_bg2);
					setText(R.string.record_input_recording);
					mRecordDialog.wantToCancel();
					break;

//				case STATE_STOP:
//					setBackgroundResource(R.drawable.record_comment_bg);
//					setText(R.string.record_input_normal);
//					mRecordDialog.dismiss();
//					break;
				default:
					break;
			}
		}
	}

	private int getRecordTime(){
		if(mRecordTime <= 0) {
			mRecordTime = 0;
		}
//		else if(mRecordTime >= MAX_RECORD_TIME) {
//			mRecordTime = 60;
//		}
		return Math.round(mRecordTime);
	}

	/**
	 * 获取音量大小、计算时间 的线程
	 */
	private class VoiceLevelThread extends Thread{
		@Override
		public void run() {
			super.run();
			while (mCurState == STATE_RECORDING || mCurState == STATE_WANT_TO_CANCEL){
				try {
					Thread.sleep(200);
					mRecordTime += 0.2;
					Log.e("debug", "mRecordTime = " + mRecordTime);
					mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}


	/**
	 * 使用AudioRecord时，进行音频装换
	 */
	class AwvToMp3Thread extends AsyncTask<String, Integer, File> {
		@Override
		protected File doInBackground(String... strings) {
			if(strings == null || strings.length <= 0){
				return null;
			}
			String path = strings[0];
			if(path == null || TextUtils.isEmpty(path)){
				return null;
			}

			if(!FileUtils.fileIsExists(path)) {
				Log.e("pzm","the path no exist" );
				return null;
			}
			final String mp3FileName = path.replace(".wav", ".mp3");
			try {
				new LameConvert().convertWAVToMP3(path, mp3FileName);
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				File file = new File(mp3FileName);
				return file;
			}
		}

		@Override
		protected void onPostExecute(File file) {
			if(file != null) {
				if(mRecordStatusListener != null) {
					mRecordStatusListener.OnRecordCompleteListener(getRecordTime(), mRecordManager.getRecordFile());
				}
			}
		}
	}

	private IRecordStatusListener mRecordStatusListener;
	public void setRecordStatusListener(IRecordStatusListener listener){
		mRecordStatusListener = listener;
	}

	public interface IRecordStatusListener {
//		void OnRecordStatusListener(int statusType);
		void OnRecordCompleteListener(int recordTime, File recordFile);
	}
}
