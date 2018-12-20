package com.example.recordcomment;

import com.example.recordcomment.datas.CommentConfig;
import com.example.recordcomment.datas.CommentData;
import com.example.recordcomment.datas.SubCommentData; /**
 * Created by pzm on 2018/10/11
 */
public interface ICommentDataManager {

    void replyWho(CommentConfig commentConfig);

    void deleteCommentItem(CommentData commentData);

    void deleteSubCommentItem(CommentData commentData, SubCommentData subCommentData);
}
