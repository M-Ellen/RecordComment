package com.example.recordcomment.datas;

/**
 * 用于评论别人的信息配置
 */

public class CommentConfig {
	
	public static final String	TYPE_COMMENT_PUBLIC	= "public";
	public static final String  TYPE_COMMENT_PRIVATE = "private";
	
	public int					commentPosition;
	public int					subCommentPosition;
	public String				content;
	public int                  contentType;  //内容的类型：0 , 1
	public String				commentType;  //评论的类型:public, reply
	public User					replyWho;
	public String				root_comment_id;
	public String				comment_id;

}
