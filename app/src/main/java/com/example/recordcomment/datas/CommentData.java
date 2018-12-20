package com.example.recordcomment.datas;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Created by pzm on 2018/10/11
 */
public class CommentData implements Serializable {
	
	private static final long	serialVersionUID	= 1L;
	
	// public final static String TYPE_CONTENT = "1";
	// public final static String TYPE_AUDIO = "2";
	
	/**
	 * postId : 20 postType : 0 root_comment_id : 20#0#20#1535350233803 user :
	 * {"userid":"20","nickname":"pzm","avatar":null} content : 测试20 contentType : 0
	 * voiceTime : null status : 0 tag : 0 createTime : 2018-08-27 14:10:33 likes :
	 * null reports : null commentReplies : []
	 */
	
	private int					postId;
	private int					postType;
	private String				root_comment_id;
	private User				user;
	private String				content;
	private int					contentType;
	private String				voiceTime;
	private File				voice;
	private int					status;
	private int					tag;
	private String				createTime;
	private int		            likes;
	private int					toggleLike;					// 0：未赞；1：已赞
	private String				reports;
	private List<SubCommentData>	subcommentReplies;
	
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
	
	public String getRootCommentId() {
		return root_comment_id;
	}
	
	public void setRootCommentId(String root_comment_id) {
		this.root_comment_id = root_comment_id;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public int getContentType() {
		return contentType;
	}
	
	public void setContentType(int contentType) {
		this.contentType = contentType;
	}
	
	public String getVoiceTime() {
		return voiceTime;
	}
	
	public void setVoiceTime(String voiceTime) {
		this.voiceTime = voiceTime;
	}
	
	public File getVoice() {
		return voice;
	}
	
	public void setVoice(File voice) {
		this.voice = voice;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getTag() {
		return tag;
	}
	
	public void setTag(int tag) {
		this.tag = tag;
	}
	
	public String getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	public int getLikes() {
		return likes;
	}
	
	public void setLikes(int likes) {
		this.likes = likes;
	}
	
	public int getToggleLike() {
		return toggleLike;
	}
	
	public void setToggleLike(int toggleLike) {
		this.toggleLike = toggleLike;
	}
	
	public String getReports() {
		return reports;
	}
	
	public void setReports(String reports) {
		this.reports = reports;
	}
	
	public List<SubCommentData> getSubCommentReplies() {
		return subcommentReplies;
	}
	
	public void setSubCommentReplies(List<SubCommentData> subcommentReplies) {
		this.subcommentReplies = subcommentReplies;
	}
	
	public boolean hasSubCommentReplies() {
		if (subcommentReplies != null && subcommentReplies.size() > 0) {
			return true;
		}
		return false;
	}
	
}
