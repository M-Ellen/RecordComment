package com.example.recordcomment.datas;

/**
 * Created by pzm on 2018/10/11
 */
public class SubCommentData {
    private static final long	serialVersionUID	= 1L;

    /**
     * postId : 123 postType : 1 root_commentId : 123#1#13598765412#1535010527173
     * commentId : 13745678932#1535011479798 whoReply :
     * {"userid":"13745678932","nickname":"Linda","avatar":"http://localhost:8558/comment/getMediaFile?filename=Koala_1535011480053.jpg"}
     * replyWho :
     * {"userid":"13598765412","nickname":"Ken","avatar":"http://localhost:8558/comment/getMediaFile?filename=Tulips_1535010527472.jpg"}
     * content : 测试测试，回复测试 createTime : 2018-08-23 16:04:39 status : 1
     */

    private int		postId;
    private int		postType;
    private String	root_commentId;
    private String	commentId;
    private User	whoReply;
    private User	replyWho;
    private String	content;
    private String	createTime;
    private int		status;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getPostType() {
        return postType;
    }

    public void setPostType(int postType) {
        this.postType = postType;
    }

    public String getRoot_commentId() {
        return root_commentId;
    }

    public void setRoot_commentId(String root_commentId) {
        this.root_commentId = root_commentId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public User getWhoReply() {
        return whoReply;
    }

    public void setWhoReply(User whoReply) {
        this.whoReply = whoReply;
    }

    public User getReplyWho() {
        return replyWho;
    }

    public void setReplyWho(User replyWho) {
        this.replyWho = replyWho;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
