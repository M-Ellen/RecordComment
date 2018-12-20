package com.example.recordcomment.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.recordcomment.ICommentDataManager;
import com.example.recordcomment.R;
import com.example.recordcomment.datas.CommentData;
import com.example.recordcomment.datas.SubCommentData;

/**
 * Created by ASUS on 2018/8/19.
 */

public class CommentDialog extends Dialog implements View.OnClickListener {
	private Context				mContext			= null;
	private CommentData			mCommentData		= null;
	private SubCommentData		mSubCommentData		= null;
	private ICommentDataManager	mCommentDataManager	= null;
	private boolean mIsLongClick = false;
	
	public CommentDialog(Context context, ICommentDataManager commentDatasManager, CommentData commentData, SubCommentData subCommentData, boolean isLongClick) {
		super(context, R.style.Theme_AppCompat_Dialog);
		mContext = context;
		mCommentDataManager = commentDatasManager;
		mCommentData = commentData;
		mSubCommentData = subCommentData;
		mIsLongClick = isLongClick;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_comment);
		initWindowParams();
		initView();
	}
	
	private void initWindowParams() {
		Window dialogWindow = getWindow();
		// 获取屏幕宽、高用
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = (int) (display.getWidth() * 0.65); // 宽度设置为屏幕的0.65
		
		dialogWindow.setGravity(Gravity.CENTER);
		dialogWindow.setAttributes(lp);
	}
	
	private void initView() {
		TextView copyTv = findViewById(R.id.tv_copy);
		if(mIsLongClick) {
			copyTv.setVisibility(View.VISIBLE);
			copyTv.setOnClickListener(this);
		}else {
			copyTv.setVisibility(View.GONE);
		}
		TextView deleteTv = findViewById(R.id.tv_delete);
		deleteTv.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_copy :
				if (mSubCommentData != null) {
					ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
					clipboard.setText(mSubCommentData.getContent());
				}
				dismiss();
				break;
			case R.id.tv_delete :
				if (mCommentDataManager != null) {
					mCommentDataManager.deleteSubCommentItem(mCommentData, mSubCommentData);
				}
				dismiss();
				break;
			default :
				break;
		}
	}
	
}
