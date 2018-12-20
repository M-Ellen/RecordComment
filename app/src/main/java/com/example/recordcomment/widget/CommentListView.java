package com.example.recordcomment.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recordcomment.R;
import com.example.recordcomment.datas.SubCommentData;
import com.example.recordcomment.datas.User;
import com.example.recordcomment.utils.DensityUtil;
import com.example.recordcomment.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class CommentListView extends LinearLayout {
	private int						mItemNameColor			= 0;
	private int						mItemSelectorBgColor	= 0;
	private OnItemClickListener		onItemClickListener		= null;
	private OnItemLongClickListener	onItemLongClickListener	= null;
	private List<SubCommentData>	mDatas					= null;
	private LayoutInflater			layoutInflater			= null;
	private Context					mContext;
	private String					mReplayStr				= null;
	
	public CommentListView(Context context) {
		this(context, null);
	}
	
	public CommentListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public CommentListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		mReplayStr = getResources().getString(R.string.replay);

		/**
		 * 以下2个值，直接默认，可以自行通过 attrs 配置
		 */
		mItemNameColor = mContext.getResources().getColor(R.color.colorPrimary);
		mItemSelectorBgColor = mContext.getResources().getColor(R.color.gray);
	}
	
	public void setDatas(List<SubCommentData> datas) {
		if (datas == null) {
			datas = new ArrayList<>();
		}
		mDatas = datas;
		notifyDataSetChanged();
	}
	
	public List<SubCommentData> getDatas() {
		return mDatas;
	}

	/**
	 * 数据刷新
	 */
	public void notifyDataSetChanged() {
		
		removeAllViews();
		if (mDatas == null || mDatas.size() == 0) {
			return;
		}
		LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		
		for (int i = 0; i < mDatas.size(); i++) {
			final int index = i;
			/**
			 * 跟据SubComment的数量 ，生成多个的TextView
			 */
			View view = getView(index);
			if (view == null) {
				throw new NullPointerException("listview item layout is null, please check getView()...");
			}
			if (index != mDatas.size() - 1) {
				layoutParams.bottomMargin = DensityUtil.dpTopx(mContext, 5);
			}
			/**
			 * 添加到viewGroup中
			 */
			addView(view, index, layoutParams);
		}
		
	}

	/**
	 *根据SubComment数据，生成textView
	 */
	private View getView(final int position) {
		if (layoutInflater == null) {
			layoutInflater = LayoutInflater.from(getContext());
		}
		View convertView = layoutInflater.inflate(R.layout.item_sub_comment_layout, null, false);
		
		TextView commentTv = convertView.findViewById(R.id.tv_sub_comment);
		final SubCommentData bean = mDatas.get(position);
		if (bean != null) {
			// 谁 回复
			User whoReplyUser = bean.getWhoReply();
			if (whoReplyUser != null && !StringUtil.isNull(whoReplyUser.getUserId())) {
				String whoReplyName = whoReplyUser.getNickname();
				int id = bean.getPostId();
				SpannableStringBuilder builder = new SpannableStringBuilder();
				builder.append(setClickableSpan(whoReplyName, whoReplyUser.getUserId()));
				
				// 回复 谁
				User replyWhoUser = bean.getReplyWho();
				if (replyWhoUser != null && !StringUtil.isNull(replyWhoUser.getUserId())) {
					String replyWhoName = replyWhoUser.getNickname();
					builder.append(mReplayStr);
					builder.append(setClickableSpan(replyWhoName, replyWhoUser.getUserId()));
				}
				builder.append(": ");
				
				String contentBodyStr = bean.getContent();
				
				builder.append(contentBodyStr);
				
				commentTv.setText(builder);
				final MovementMethod circleMovementMethod = new com.example.recordcomment.widget.MovementMethod(mItemSelectorBgColor, mItemSelectorBgColor);
				commentTv.setMovementMethod(circleMovementMethod);
				commentTv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (circleMovementMethod.isPassToTv()) {
							if (onItemClickListener != null) {
								onItemClickListener.onItemClick(position);
								Toast.makeText(getContext(), "onClick", Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
				commentTv.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						if (circleMovementMethod.isPassToTv()) {
							if (onItemLongClickListener != null) {
								onItemLongClickListener.onItemLongClick(position);
								Toast.makeText(getContext(), "onLongClick", Toast.LENGTH_SHORT).show();
							}
						}
						/**
						 * 返回 false ，让他触发MovementMethod的 onTouch()，最终使背景消失
						 */
						return false;
					}
				});
			}
		}
		
		return convertView;
	}
	
	@NonNull
	private SpannableString setClickableSpan(final String textStr, final String id) {
		SpannableString subjectSpanText = new SpannableString(textStr);
		subjectSpanText.setSpan(new SpannableClickable(mItemNameColor) {
			@Override
			public void onClick(View widget) {
				 Toast.makeText(getContext(), textStr + " &id = " + id, Toast.LENGTH_SHORT).show();
			}
		}, 0, subjectSpanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return subjectSpanText;
	}
	
	// public OnItemClickListener getOnItemClickListener() {
	// return onItemClickListener;
	// }
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}
	
	// public OnItemLongClickListener getOnItemLongClickListener() {
	// return onItemLongClickListener;
	// }
	
	public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
		this.onItemLongClickListener = onItemLongClickListener;
	}
	
	public interface OnItemClickListener {
		void onItemClick(int position);
	}
	
	public interface OnItemLongClickListener {
		void onItemLongClick(int position);
	}
}
