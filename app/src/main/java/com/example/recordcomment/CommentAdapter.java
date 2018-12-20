package com.example.recordcomment;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.recordcomment.audiorecord.AudioPlaybackManager;
import com.example.recordcomment.audiorecord.AudioPlaybackView;
import com.example.recordcomment.datas.CommentConfig;
import com.example.recordcomment.datas.CommentData;
import com.example.recordcomment.datas.SubCommentData;
import com.example.recordcomment.utils.DatasUtils;
import com.example.recordcomment.utils.StringUtil;
import com.example.recordcomment.widget.CommentDialog;
import com.example.recordcomment.widget.CommentListView;

import java.util.List;

/**
 * 评论类型有2中：文本，录音
 *
 * 如果只需要文本，则可以去除录音部分的代码
 */
public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	
	public static final String	TAG					= CommentAdapter.class.getSimpleName();
	
	public static final int		CONTENT_TYPE_TEXT	= 0;
	public static final int		CONTENT_TYPE_RECORD	= 1;
	private Context				mContext			= null;
	private List<CommentData>	mCommentDataList	= null;
	private ICommentDataManager	mCommentDataManager	= null;
	private int					mCommentPosition	= 0;
	private int					mAudioPlayPosition	= -1;
	private AnimationDrawable	mAnimationDrawable	= null;

    public CommentAdapter(Context context) {
		this.mContext = context;
	}
	
	public List<CommentData> getData() {
		return mCommentDataList;
	}
	
	public void setData(List<CommentData> commentDataList) {
		this.mCommentDataList = commentDataList;
		notifyDataSetChanged();
	}
	
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		RecyclerView.ViewHolder viewHolder = null;
		View view = LayoutInflater.from(mContext).inflate(R.layout.item_comment, parent, false);
		/**
		 * 根据留言不同类型，分别加载
		 */
		if (viewType == CONTENT_TYPE_TEXT) {
			viewHolder = new TextCommentViewHolder(view);
		} else if (viewType == CONTENT_TYPE_RECORD) {
			viewHolder = new RecordCommentViewHolder(view);
		}
		return viewHolder;
	}
	
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
	    BaseCommentViewHolder holder = (BaseCommentViewHolder) viewHolder;
	    CommentData commentData = mCommentDataList.get(position);
        mCommentPosition = position;
	    if(commentData.getUser() == null) {
	        return;
	    }

        String headUrl = commentData.getUser().getAvatar();
        if(StringUtil.isNullOrEmpty(headUrl)) {
            holder.headIconIv.setImageResource(R.mipmap.ic_launcher_round);
        }else {
            // 如果是使用图片框架，则使用 headUrl 加载
            holder.headIconIv.setImageResource(R.mipmap.ic_launcher_round);
        }

        String name = commentData.getUser().getNickname();
        if(StringUtil.isNullOrEmpty(name)) {
            holder.userNameTv.setText(R.string.default_name);
        }else {
            holder.userNameTv.setText(name);
        }

        if(commentData.getLikes() > 0) {
            holder.likeCountTv.setText(String.valueOf(commentData.getLikes()));
        }else {
            holder.likeCountTv.setText("");
        }

        if(commentData.getToggleLike() == 1) {
            holder.likeIv.setImageResource(R.mipmap.like);
        }else {
            holder.likeIv.setImageResource(R.mipmap.unlike);
        }

        holder.dateTv.setText(commentData.getCreateTime());

        /**
         * 设置不同留言类型
         */
        switch (commentData.getContentType()) {
            case CONTENT_TYPE_TEXT :
                if(holder instanceof TextCommentViewHolder) {
                    ((TextCommentViewHolder)holder).contentText.setText(commentData.getContent());
                }
                break;
            case CONTENT_TYPE_RECORD :
                if(holder instanceof RecordCommentViewHolder) {
                    //todo
                    ((RecordCommentViewHolder)holder).audioPlaybackView.setDuration(Integer.parseInt(commentData.getVoiceTime()));
                    ((RecordCommentViewHolder)holder).audioPlaybackView.setRecordFile(commentData.getVoice());
                    setAudioPlaybackView((RecordCommentViewHolder) holder, position);
                }
                break;
            default:
                break;
        }

        setClickListener(holder, commentData);
        dealSubComment(holder, commentData);
	}

	private AudioPlaybackManager mAudioPlaybackManager = AudioPlaybackManager.getInstance(mContext);


    /**
     * 这里做了很多繁琐的处理，主要是UI复用，导致动画混乱的问题
     */
    private void setAudioPlaybackView(final RecordCommentViewHolder holder, final int position) {
        if(mAudioPlayPosition != position) {
            holder.audioPlaybackView.mRecordPlayIv.setBackground(mContext.getResources().getDrawable(R.drawable.record_play_animation));
        }else {
            if(mAnimationDrawable != null && mAudioPlaybackManager != null && mAudioPlaybackManager.isPlaying()) {
                ((AnimationDrawable)holder.audioPlaybackView.mRecordPlayIv.getBackground()).start();
            }
        }

        holder.audioPlaybackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAnimationDrawable != null) {
                    mAnimationDrawable.stop();
                    mAnimationDrawable.selectDrawable(0);
                }
                if(mAudioPlaybackManager == null) {
                    mAudioPlaybackManager = AudioPlaybackManager.getInstance(mContext);
                }
                if (mAudioPlaybackManager.isPlaying() && mAudioPlayPosition == position) {
                    mAudioPlaybackManager.stopAudio();
                } else {
                    mCommentPosition = holder.getLayoutPosition();
                    mAudioPlayPosition = mCommentPosition;
                    final ImageView recordIv = ((AudioPlaybackView)v).mRecordPlayIv;
                    mAudioPlaybackManager.stopAudio();
                    mAnimationDrawable = (AnimationDrawable) recordIv.getBackground();
                    mAnimationDrawable.start();
                    mAudioPlaybackManager.playAudio(mCommentDataList.get(mCommentPosition).getVoice().getPath());
                    mAudioPlaybackManager.setOnPlayingListener(new AudioPlaybackManager.OnPlayingListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onStop() {
                            if (mAnimationDrawable != null) {
                                mAnimationDrawable.stop();
                                mAnimationDrawable.selectDrawable(0);
                            }
                        }

                        @Override
                        public void onComplete() {
                            if (mAnimationDrawable != null) {
                                mAnimationDrawable.stop();
                                mAnimationDrawable.selectDrawable(0);
                                mAnimationDrawable = null;
                                mAudioPlayPosition = -1;
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });
    }


    /**
     * 子评论的处理
     * @param holder
     * @param commentData
     */
    private void dealSubComment(final BaseCommentViewHolder holder, final CommentData commentData) {
	    if(commentData == null) {
	        return;
	    }
        if(commentData.hasSubCommentReplies()) {
            holder.subCommentListView.setVisibility(View.VISIBLE);
            final List<SubCommentData> subCommentReplies = commentData.getSubCommentReplies();
            if(subCommentReplies != null && subCommentReplies.size() > 0) {
                holder.subCommentListView.setDatas(subCommentReplies);
            }

            /**
             * 点击 子评论
             */
            holder.subCommentListView.setOnItemClickListener(new CommentListView.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    /**
                     * 防止位置复用
                     */
                    mCommentPosition = holder.getLayoutPosition();
                    if(subCommentReplies == null || subCommentReplies.size() <= 0) {
                        return;
                    }
                    if(mCommentDataManager == null) {
                        return;
                    }
                    SubCommentData subCommentData= subCommentReplies.get(position);
                    /**
                     * 点击自己的评论，显示删除，否则回复别人
                     */
                    if(subCommentData.getWhoReply().getUserId().equals(DatasUtils.mCurUser.getUserId())) {
                        CommentDialog commentDialog = new CommentDialog(mContext, mCommentDataManager, commentData, subCommentReplies.get(position), false);
                        commentDialog.show();

                    }else {
                        CommentConfig commentConfig = new CommentConfig();
                        commentConfig.commentPosition = mCommentPosition;
                        commentConfig.subCommentPosition = position;
                        commentConfig.commentType = CommentConfig.TYPE_COMMENT_PRIVATE;
                        commentConfig.replyWho = subCommentData.getWhoReply();
                        commentConfig.contentType = CONTENT_TYPE_TEXT;
                        commentConfig.comment_id = subCommentData.getCommentId();
                        mCommentDataManager.replyWho(commentConfig);
                    }
                }
            });

            /**
             * 长按 子评论
             */
            holder.subCommentListView.setOnItemLongClickListener(new CommentListView.OnItemLongClickListener() {
                @Override
                public void onItemLongClick(int position) {
                    if(subCommentReplies == null || subCommentReplies.size() <= 0) {
                        return;
                    }
                    if(mCommentDataManager == null) {
                        return;
                    }
                    CommentDialog commentDialog = new CommentDialog(mContext, mCommentDataManager, commentData, subCommentReplies.get(position),true);
                    commentDialog.show();
                }
            });

        }else {
            holder.subCommentListView.setVisibility(View.GONE);
        }
    }

    private void setClickListener(final BaseCommentViewHolder holder, final CommentData commentData){
        /**
         * 点击点赞图标
         *
         * 这里只是简单判断，一般是：通过服务器返回数据，判断当前是否点赞
         */
        holder.likeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentData.getToggleLike() == 1) {
                    holder.likeIv.setImageResource(R.mipmap.unlike);
                    commentData.setToggleLike(0);
                }else {
                    holder.likeIv.setImageResource(R.mipmap.like);
                    commentData.setToggleLike(1);
                }
            }
        });

        /**
         * 点击留言图标
         */
        holder.commentIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentPosition = holder.getLayoutPosition();
                if(mCommentDataManager != null) {
                    CommentConfig commentConfig = new CommentConfig();
                    commentConfig.commentPosition = mCommentPosition;
                    commentConfig.commentType = CommentConfig.TYPE_COMMENT_PUBLIC;
                    commentConfig.contentType = CONTENT_TYPE_TEXT;
                    mCommentDataManager.replyWho(commentConfig);
                }
            }
        });

        /**
         * 点击删除
         * 当前用户的留言，才会显示删除
         */
        String userId = commentData.getUser().getUserId();
        if(!StringUtil.isNullOrEmpty(userId) && userId.equals(DatasUtils.mCurUser.getUserId())) {
            holder.deleteTv.setVisibility(View.VISIBLE);
            holder.deleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCommentPosition = holder.getLayoutPosition();
                    if(mCommentDataManager != null) {
                        mCommentDataManager.deleteCommentItem(commentData);
                    }
                }
            });
        }else {
            holder.deleteTv.setVisibility(View.GONE);
        }
    }

    @Override
	public int getItemCount() {
	    if(mCommentDataList != null) {
		    return mCommentDataList.size();
	    }
	    return 0;
	}
	
	/**
	 * @param position
	 * @return 返回留言内容的类型
	 */
	@Override
	public int getItemViewType(int position) {
		if (mCommentDataList != null && mCommentDataList.size() > 0) {
			int type = mCommentDataList.get(position).getContentType();
			if (type == CONTENT_TYPE_TEXT) {
				return CONTENT_TYPE_TEXT;
			} else if (type == CONTENT_TYPE_RECORD) {
				return CONTENT_TYPE_RECORD;
			}
		}
		return CONTENT_TYPE_TEXT;
	}


	public void setCommentManeger(ICommentDataManager commentDataManager){
        mCommentDataManager = commentDataManager;
    }

    public int getLayoutPosition() {
        return mCommentPosition;
    }

    public void onDestroy() {
        if (mAudioPlaybackManager != null) {
            mAudioPlaybackManager.releaseAll();
            mAudioPlaybackManager = null;
        }
        if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
            mAnimationDrawable.selectDrawable(0);
            mAnimationDrawable = null;
        }
        if(mCommentDataManager != null) {
            mCommentDataManager = null;
        }
    }
}
