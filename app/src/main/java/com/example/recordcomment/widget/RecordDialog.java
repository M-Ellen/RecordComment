package com.example.recordcomment.widget;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recordcomment.R;

/**
 * Created by pzm on 2018/10/13
 */
public class RecordDialog extends DialogFragment {
	public static final String TAG = RecordDialog.class.getSimpleName();
	private View		mRootLayout		= null;
	private ImageView	mRecordIconIv	= null;
	private ImageView	mRecordVoiceIv	= null;
	private TextView	mRecordTipTv	= null;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(RecordDialog.STYLE_NO_FRAME, R.style.Dialog);
	}
	
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mRootLayout = inflater.inflate(R.layout.dialog_record, null);
		mRecordIconIv = (ImageView) mRootLayout.findViewById(R.id.iv_record_icon);
		mRecordVoiceIv = (ImageView) mRootLayout.findViewById(R.id.id_recorder_voice);
		mRecordTipTv = (TextView) mRootLayout.findViewById(R.id.id_recorder_tip);
		return mRootLayout;
	}


	/**
	 * 录制状态
	 */
	public void recording(){
		if(getDialog() != null && getDialog().isShowing()) {
			mRecordVoiceIv.setVisibility(View.VISIBLE);
			mRecordIconIv.setVisibility(View.VISIBLE);
			mRecordIconIv.setImageResource(R.mipmap.recorder);
			mRecordTipTv.setText(R.string.record_normal_tip);
		}

	}

	/**
	 *取消状态
	 */
	public void wantToCancel(){
		if(getDialog() != null && getDialog().isShowing()) {
			mRecordIconIv.setVisibility(View.VISIBLE);
			mRecordVoiceIv.setVisibility(View.GONE);
			mRecordIconIv.setImageResource(R.mipmap.cancel);
			mRecordTipTv.setText(R.string.record_cancel_tip);
		}
	}

	/**
	 * 录制时间太短
	 */
	public void tooShort(){
		if(getDialog() != null && getDialog().isShowing()) {
			mRecordVoiceIv.setVisibility(View.GONE);
			mRecordIconIv.setVisibility(View.VISIBLE);
			mRecordIconIv.setImageResource(R.mipmap.voice_too_short);
			mRecordTipTv.setText(R.string.record_too_short_tip);
		}
	}

	/**
	 * 录制时间太短
	 */
	public void tooLong(){
		if(getDialog() != null && getDialog().isShowing()) {
			mRecordIconIv.setVisibility(View.VISIBLE);
			mRecordVoiceIv.setVisibility(View.GONE);
			mRecordIconIv.setImageResource(R.mipmap.voice_too_short);
			mRecordTipTv.setText(R.string.record_too_long_tip);
		}
	}


	/**
	 * 音量控制
	 */

	public void updateVoiceLevel(int level){
		if(getDialog() != null && getDialog().isShowing()) {
			int resId = getResources().getIdentifier("v" + level, "mipmap", getActivity().getPackageName());
			mRecordVoiceIv.setVisibility(View.VISIBLE);
			mRecordVoiceIv.setImageResource(resId);
		}
	}

    public void show(Context context){
	    show(((Activity)context).getFragmentManager(), TAG);
    }

    public void hide(){
		dismiss();
    }
}
