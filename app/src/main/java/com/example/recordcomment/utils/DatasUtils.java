package com.example.recordcomment.utils;

import com.example.recordcomment.datas.CommentData;
import com.example.recordcomment.datas.SubCommentData;
import com.example.recordcomment.datas.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by pzm on 2018/10/11
 */
public class DatasUtils {


    public static final String[]	CONTENT_TEXT	= {
            "啦啦啦啦啦",
            "bilibilinili",
            "大家快来围观这个...屎",
            "路漫漫兮其修远，吾将上下而求索",
            "来个很长很长的文字吧，加长版的。一般是多长呢？一般限制200字左右的评论就可以了，如果超出长度，就限制输入。就是干、干、干。。。",
            "看什么看，想什么呢？说的就是你，看着这句话的人"
    };

    public static final String[]	SUB_CONTENT_TEXT	= {
            "你好啊",
            "哈哈哈",
            "这个太搞笑",
            "我们做个朋友吧，很好很好的那种",
    };

    public static List<User> mUserList = new ArrayList<>();
    public static User mCurUser = new User("a1","ellen","");

    static {
        mUserList.add(mCurUser);
        mUserList.add(new User("a2","米老鼠",""));
        mUserList.add(new User("a3","小刺猬",""));
        mUserList.add(new User("a4","两只tiger，两只tiger",""));
        mUserList.add(new User("a5","大猩猩",""));
        mUserList.add(new User("a6","小姐姐",""));
        mUserList.add(new User("a7","小哥哥",""));
        mUserList.add(new User("a8","毛毛虫",""));

    }


    public static List<CommentData> getConmentDatas(){
        List<CommentData> commentDataList = new ArrayList<>();
        for (int i=0; i<30; i++){
            CommentData commentData = new CommentData();
            commentData.setUser(getUser());
            commentData.setContentType(getContentType());
            commentData.setLikes(getRandomNum(999));
            commentData.setToggleLike(getRandomNum(2));
            if(commentData.getContentType() == -1) { //record
                commentData.setVoice(null); //目前录音文件是空的，只显示视图。需自行添加录音
                commentData.setVoiceTime(String.valueOf(getRandomNum(60)));
            }else {
                commentData.setContent(getContent());//text
            }
            commentData.setCreateTime("2020-8-28");
            commentData.setSubCommentReplies(getSubCommentDatas());
            commentDataList.add(commentData);
        }
        return commentDataList;
    }

    private static int commentId = 0;

    public static List<SubCommentData> getSubCommentDatas(){
        List<SubCommentData>  SubCommentDataList = new ArrayList<>();

        for (int i=0; i<getRandomNum(10); i++){

            SubCommentData item = new SubCommentData();

            item.setCommentId(String.valueOf(commentId++));
            User whoReply = getUser();
            item.setWhoReply(getUser());
            item.setContent(SUB_CONTENT_TEXT[getRandomNum(SUB_CONTENT_TEXT.length)]);
            if (getRandomNum(10) % 2 == 0) {
                while (true) {
                    User replyWho = getUser();
                    if (!whoReply.getUserId().equals(replyWho.getUserId())) {
                        item.setReplyWho(replyWho);
                        break;
                    }
                }
            }
            SubCommentDataList.add(item);
        }


        return SubCommentDataList;
    }

    /**
     * 创建回复评论
     */
    public static SubCommentData createPublicSubComment(String content) {
        SubCommentData item = new SubCommentData();
        item.setCommentId(String.valueOf(commentId++));
        item.setContent(content);
        item.setWhoReply(mCurUser);
        return item;
    }

    /**
     * 创建回复 别人的回复
     */
    public static SubCommentData createReplySubComment(User replyWho, String content) {
        SubCommentData item = new SubCommentData();
        item.setCommentId(String.valueOf(commentId++));
        item.setContent(content);
        item.setWhoReply(mCurUser);
        item.setReplyWho(replyWho);
        return item;
    }

    public static User getUser() {
        return mUserList.get(getRandomNum(mUserList.size()));
    }

    public static int getContentType(){
        return getRandomNum(1);
    }

    public static String getContent(){
        return CONTENT_TEXT[getRandomNum(CONTENT_TEXT.length)];
    }

    public static int getRandomNum(int max) {
        Random random = new Random();
        int result = random.nextInt(max);
        if(result < 0) {
            result = 0;
        }
        return result;
    }

}
