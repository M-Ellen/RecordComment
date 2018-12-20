package com.example.recordcomment;

import android.view.View;
import android.view.ViewStub;

import com.example.recordcomment.audiorecord.AudioPlaybackView;

/**
 * 留言类型：录音
 */
public class RecordCommentViewHolder extends BaseCommentViewHolder {

    public AudioPlaybackView audioPlaybackView;

    public RecordCommentViewHolder(View itemView) {
        super(itemView);
        initView();
    }

    @Override
    public void initSubView(ViewStub viewStub) {
        if (viewStub == null) {
            throw new IllegalArgumentException("viewStub is null...");
        }
        viewStub.setLayoutResource(R.layout.comment_type_record_layout);
        View subViw = viewStub.inflate();
        AudioPlaybackView audioPlaybackView = subViw.findViewById(R.id.view_content_record);
        if (audioPlaybackView != null) {
            this.audioPlaybackView = audioPlaybackView;
        }
    }
}
