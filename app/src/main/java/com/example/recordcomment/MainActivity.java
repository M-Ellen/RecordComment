package com.example.recordcomment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.recordcomment.audiorecord.AudioRecordButton;
import com.example.recordcomment.datas.CommentConfig;
import com.example.recordcomment.datas.CommentData;
import com.example.recordcomment.datas.SubCommentData;
import com.example.recordcomment.utils.CommonUtils;
import com.example.recordcomment.utils.DatasUtils;
import com.example.recordcomment.utils.StatusUtil;
import com.example.recordcomment.widget.CommentListView;
import com.example.recordcomment.widget.HorItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pzm
 */
public class MainActivity extends AppCompatActivity implements ICommentDataManager, View.OnClickListener, AudioRecordButton.IRecordStatusListener {
	
	public static final String	TAG						= MainActivity.class.getSimpleName();
	/**
	 * 键盘输入模式
	 */
	public static final int		SOFTINPUT_COMMENT_MODEL	= 0;
	/**
	 * 录音输入模式
	 */
	public static final int		RECORD_COMMENT_MODEL	= 1;
	/**
	 * 当前模式
	 */
	public int					CUR_COMMENT_MODEL		= SOFTINPUT_COMMENT_MODEL;
	
	private RecyclerView		mRecyclerView			= null;
	private ImageView			mSoftOrRecordIv			= null;
	private EditText			mEditCommentEt			= null;
	private AudioRecordButton	mRecordBtn				= null;
	private ImageView			mSendIv					= null;
	private CommentAdapter		mAdapter				= null;
	private List<CommentData>	mCommentDataList		= null;
	
	private CommentConfig		mCommentConfig			= null;
	private LinearLayoutManager	mLayoutManager			= null;
	private LinearLayout		mRootLayout				= null;
	private LinearLayout		mInputCommentLayout		= null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initData();
		initView();
		setListener();
		refreshData();
	}
	
	private void initData() {
		mCommentDataList = new ArrayList<>();
		mCommentDataList = DatasUtils.getConmentDatas();
	}
	
	private void initView() {
		mRootLayout = findViewById(R.id.layout_activity);
		mInputCommentLayout = findViewById(R.id.ll_comment_input);
		mRecyclerView = findViewById(R.id.recycler_view);
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mRecyclerView.addItemDecoration(new HorItemDecoration(this, 1, R.color.gray,false));
		mSoftOrRecordIv = findViewById(R.id.iv_softintput_record);
		mEditCommentEt = findViewById(R.id.et_edit_comment);
		mRecordBtn = findViewById(R.id.btn_record);
		mRecordBtn.setVisibility(View.GONE);
		mSendIv = findViewById(R.id.iv_send);
		mSendIv.setEnabled(false);
	}
	
	private void setListener() {
		setViewTreeObserver();
		mSoftOrRecordIv.setOnClickListener(this);
		mRecordBtn.setRecordStatusListener(this);
		// mRecordBtn.setOnClickListener(this);
		mSendIv.setOnClickListener(this);
		mEditCommentEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					mSendIv.setImageResource(R.mipmap.send);
					mSendIv.setEnabled(true);
				} else {
					mSendIv.setImageResource(R.mipmap.send1);
					mSendIv.setEnabled(false);
				}
				if (s.length() >= 200) {
					Toast.makeText(MainActivity.this, "不能超过200字哦", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	private void refreshData() {
		if (mAdapter == null) {
			mAdapter = new CommentAdapter(this);
			mRecyclerView.setAdapter(mAdapter);
			mAdapter.setCommentManeger(this);
		}
		if (mCommentDataList != null && mCommentDataList.size() > 0) {
			mAdapter.setData(mCommentDataList);
		}
	}
	
	private void showSoftInput() {
		softInputComment();
		mEditCommentEt.requestFocus();
		CommonUtils.showSoftInput(this, mEditCommentEt);
	}
	
	private void hideSoftInput() {
		mCommentConfig = null;
		mEditCommentEt.setText("");
		mEditCommentEt.setHint("说点什么吧....");
		CommonUtils.hideSoftInput(this, mEditCommentEt);
	}
	
	/**
	 * 回复谁
	 * 
	 * @param commentConfig
	 */
	
	@Override
	public void replyWho(CommentConfig commentConfig) {
		if (commentConfig == null) {
			return;
		}
		mCommentConfig = commentConfig;
		calculateCommentHeight();
		if (commentConfig.commentType == CommentConfig.TYPE_COMMENT_PRIVATE) {
			if (commentConfig.replyWho != null) {
				mEditCommentEt.setHint("回复：" + commentConfig.replyWho.getNickname());
			}
		}
		showSoftInput();
	}
	
	@Override
	public void deleteCommentItem(CommentData commentData) {
		if (mCommentDataList != null && mCommentDataList.size() > 0) {
			mCommentDataList.remove(commentData);
			refreshData();
		}
	}
	
	@Override
	public void deleteSubCommentItem(CommentData commentData, SubCommentData subCommentData) {
		if (commentData.getSubCommentReplies() != null && commentData.getSubCommentReplies().size() > 0) {
			commentData.getSubCommentReplies().remove(subCommentData);
			refreshData();
		}
	}
	
	private void addCommentItem(CommentData commentData) {
		if (mCommentDataList != null && mCommentDataList.size() > 0) {
			mCommentDataList.add(0, commentData);
			refreshData();
			mLayoutManager.scrollToPosition(0);
		}
	}
	
	private void addSubCommentItem(CommentConfig commentConfig) {
		SubCommentData subCommentData;
		if (commentConfig.commentType == CommentConfig.TYPE_COMMENT_PUBLIC) { // 评论别人的评论
			subCommentData = DatasUtils.createPublicSubComment(commentConfig.content);
		} else { // 回复 别人的评论或者回复
			subCommentData = DatasUtils.createReplySubComment(commentConfig.replyWho, commentConfig.content);
		}
		mCommentDataList.get(commentConfig.commentPosition).getSubCommentReplies().add(subCommentData);
		refreshData();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_softintput_record :
				if (CUR_COMMENT_MODEL == SOFTINPUT_COMMENT_MODEL) {
					recordComment();
				} else {
					softInputComment();
				}
				break;
			case R.id.iv_send :
				sendComment();
				break;
			default :
				break;
		}
	}
	
	private void softInputComment() {
		CUR_COMMENT_MODEL = SOFTINPUT_COMMENT_MODEL;
		mSoftOrRecordIv.setImageResource(R.mipmap.record);
		mEditCommentEt.setVisibility(View.VISIBLE);
		mRecordBtn.setVisibility(View.GONE);
	}
	
	private void recordComment() {
		CUR_COMMENT_MODEL = RECORD_COMMENT_MODEL;
		mSoftOrRecordIv.setImageResource(R.mipmap.softinput);
		mEditCommentEt.setVisibility(View.GONE);
		mRecordBtn.setVisibility(View.VISIBLE);
		applypermission();
	}
	
	private void sendComment() {
		String textContent = mEditCommentEt.getText().toString();
		if (TextUtils.isEmpty(textContent.trim())) {
			Toast.makeText(MainActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (mCommentConfig == null) {
			// 评论
			CommentData commentData = new CommentData();
			commentData.setContent(textContent);
			commentData.setUser(DatasUtils.mCurUser);
			commentData.setContentType(CommentAdapter.CONTENT_TYPE_TEXT);
			addCommentItem(commentData);
		} else {
			// 2种情况：回复评论 || 回复 别人的回复
			mCommentConfig.content = textContent;
			addSubCommentItem(mCommentConfig);
		}
		hideSoftInput();
	}
	
	private int	mSelectCommentItemHeight			= 0;
	private int	mSelectSubCommentItemOffset			= 0;
	private int	mSelectSubCommentItemTotalOffset	= 0;
	private int	mScreenHeight						= 0;
	private int	mCurrentKeyboardHeight				= 0;
	private int mInputCommentHeight = 0;
	/**
	 * 用于计算 当前点击评论的高度， 用于适配键盘的显示
	 */
	private void calculateCommentHeight() {
		// 获取可见区域第一条 messageItem
		int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
		
		View selectCommenttem = mLayoutManager.getChildAt(mCommentConfig.commentPosition - firstVisibleItemPosition);
		if (selectCommenttem != null) {
			mSelectCommentItemHeight = selectCommenttem.getHeight();
		}
		if (mCommentConfig.commentType == CommentConfig.TYPE_COMMENT_PRIVATE) {
			CommentListView subCommentLv = selectCommenttem.findViewById(R.id.sub_comment_list_view);
			if (subCommentLv != null) {
				// 找到要回复的评论view,计算出该view距离所属动态底部的距离
				View selectSubCommentItem = subCommentLv.getChildAt(mCommentConfig.subCommentPosition);
				if (selectSubCommentItem != null) {
					// 选择的subCommentItem距选择的commentItem底部的距离
					mSelectSubCommentItemOffset = 0;
					View parentView = selectSubCommentItem;
					do {
						int subItemBottom = parentView.getBottom();
						parentView = (View) parentView.getParent();
						if (parentView != null) {
							mSelectSubCommentItemOffset += (parentView.getHeight() - subItemBottom);
						}
					} while (parentView != null && parentView != selectCommenttem);
				}
			}
		} else if (mCommentConfig.commentType == CommentConfig.TYPE_COMMENT_PUBLIC) {
			CommentListView subCommentLv = selectCommenttem.findViewById(R.id.sub_comment_list_view);
			if (subCommentLv != null) {
				mSelectSubCommentItemTotalOffset = subCommentLv.getHeight();
				if (mCommentDataList.get(mCommentConfig.commentPosition) != null) {
					List<SubCommentData> subCommentData = mCommentDataList.get(mCommentConfig.commentPosition).getSubCommentReplies();
					if (subCommentData == null || subCommentData.size() <= 0) {
						mSelectSubCommentItemTotalOffset = 0;
					}
				}
			} else {
				mSelectSubCommentItemTotalOffset = 0;
			}
		}
	}

	/**
	 *
	 * 具体listviewOffset 根据实际UI布局
	 * @param commentConfig
	 * @return
	 */
	private int getListViewOffset(CommentConfig commentConfig) {
		if (commentConfig == null) {
			return 0;
		}
		// 这里如果你的listview上面还有其它占高度的控件，则需要减去该控件高度，listview的headview除外。
		// 减去bottom 减去top 减去 item
		int listviewOffset = mScreenHeight - mCurrentKeyboardHeight - mInputCommentHeight;
		listviewOffset = listviewOffset - mRecyclerView.getTop() - mRecyclerView.getPaddingTop() - mSelectCommentItemHeight;
		if (CommentConfig.TYPE_COMMENT_PUBLIC.equals(commentConfig.commentType)) {
			listviewOffset = listviewOffset + mSelectSubCommentItemTotalOffset;
		} else if (CommentConfig.TYPE_COMMENT_PRIVATE.equals(commentConfig.commentType)) {
			listviewOffset = listviewOffset + mSelectSubCommentItemOffset;
		}
		return listviewOffset;
	}
	
	/**
	 * 监听布局的变化，键盘
	 */
	private void setViewTreeObserver() {
		
		final ViewTreeObserver viewTreeObserver = mRootLayout.getViewTreeObserver();
		viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				Rect r = new Rect();
				/**
				 * 获取当前窗口可视区域大小的
				 */
				mRootLayout.getWindowVisibleDisplayFrame(r);
				int statusBarH = StatusUtil.getStatusBarHeight(getApplicationContext());// 状态栏高度
				int screenH = mRootLayout.getRootView().getHeight();
				if (r.top != statusBarH) {
					/**
					 * 在沉浸式状态栏时r.top＝0; 如果有显示状态栏，在不计算状态栏高度
					 * r.top代表的是状态栏高度
					 */
					r.top = statusBarH;
				}
				int keyboardH = screenH - (r.bottom - r.top);
				if (keyboardH == mCurrentKeyboardHeight) { // 有变化时才处理，否则会陷入死循环
					return;
				}
				mCurrentKeyboardHeight = keyboardH;
				mScreenHeight = screenH;// 应用屏幕的高度
				mInputCommentHeight = mInputCommentLayout.getHeight();//底部输入框高度
				
				if (keyboardH < 150) {// 说明是隐藏键盘的情况
					hideSoftInput();
					return;
				}
				// 偏移listview
				if (mLayoutManager != null && mCommentConfig != null) {
					mLayoutManager.scrollToPositionWithOffset(mCommentConfig.commentPosition, getListViewOffset(mCommentConfig));
				}
			}
		});
	}


	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
				hideSoftInput();
		}

		return super.dispatchTouchEvent(event);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAdapter.onDestroy();
	}

	/**
	 * 当录音正常完成
	 */
	@Override
	public void OnRecordCompleteListener(int recordTime, File recordFile) {
		CommentData commentData = new CommentData();
		commentData.setUser(DatasUtils.mCurUser);
		commentData.setVoiceTime(String.valueOf(recordTime));
		commentData.setVoice(recordFile);
		commentData.setContentType(CommentAdapter.CONTENT_TYPE_RECORD);
		addCommentItem(commentData);
	}

	/**
	 * 权限申请
	 */
	String[] allpermissions = new String[]{Manifest.permission.RECORD_AUDIO,
			
			Manifest.permission.WRITE_EXTERNAL_STORAGE};
	
	public void applypermission() {
		if (Build.VERSION.SDK_INT >= 23) {
			boolean needapply = false;
			for (int i = 0; i < allpermissions.length; i++) {
				int chechpermission = ContextCompat.checkSelfPermission(getApplicationContext(), allpermissions[i]);
				if (chechpermission != PackageManager.PERMISSION_GRANTED) {
					needapply = true;
				}
			}
			if (needapply) {
				ActivityCompat.requestPermissions(MainActivity.this, allpermissions, 1);
			}
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		for (int i = 0; i < grantResults.length; i++) {
			if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(MainActivity.this, permissions[i] + "已授权", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(MainActivity.this, permissions[i] + "拒绝授权", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
}
