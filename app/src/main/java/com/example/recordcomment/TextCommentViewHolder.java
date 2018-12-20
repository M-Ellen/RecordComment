package com.example.recordcomment;

import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

/**
 * 留言类型：文本
 */
public class TextCommentViewHolder extends BaseCommentViewHolder {

    public TextView contentText;

    public TextCommentViewHolder(View itemView) {
        super(itemView);
        initView();
    }

    @Override
    public void initSubView(ViewStub viewStub) {
        if (viewStub == null) {
            throw new IllegalArgumentException("viewStub is null...");
        }
        viewStub.setLayoutResource(R.layout.comment_type_text_layout);
        View subViw = viewStub.inflate();
        TextView textView = subViw.findViewById(R.id.tv_content_text);
        if (textView != null) {
            this.contentText = textView;
        }
    }
}
