package com.example.recordcomment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recordcomment.widget.CommentListView;

/**
 * Created by pzm on 2018/10/11
 */
public abstract class BaseCommentViewHolder extends RecyclerView.ViewHolder {

    /**
     * 用户头像，项目使用的是fresco框架，这里默认使用本地图片
     */
    public ImageView headIconIv;
    public TextView userNameTv;
    public TextView likeCountTv;
    public ImageView likeIv;
    public ImageView commentIv;
    public ViewStub viewStub;
    public TextView dateTv;
    public TextView deleteTv;
    public CommentListView subCommentListView;


    public BaseCommentViewHolder(View itemView) {
        super(itemView);
        headIconIv = itemView.findViewById(R.id.iv_head_icon);
        userNameTv = itemView.findViewById(R.id.tv_user_name);
        likeCountTv = itemView.findViewById(R.id.tv_like_count);
        likeIv = itemView.findViewById(R.id.iv_like_icon);
        commentIv = itemView.findViewById(R.id.iv_comment_icon);
        dateTv = itemView.findViewById(R.id.tv_date);
        deleteTv = itemView.findViewById(R.id.tv_delete);
        subCommentListView = itemView.findViewById(R.id.sub_comment_list_view);
        viewStub = itemView.findViewById(R.id.view_stub_comment_type);
    }

    public void initView(){
        initSubView(viewStub);
    }

    public abstract void initSubView(ViewStub viewStub);
}
