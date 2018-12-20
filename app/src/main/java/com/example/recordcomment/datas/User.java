package com.example.recordcomment.datas;

import java.io.Serializable;


public class User implements Serializable {
	private static final long	serialVersionUID	= 1L;
	
	// "user": {
	// "userid": "13598765412",
	// "nickname": "Ken",
	// "avatar":
	// "http://localhost:8558/comment/getMediaFile?filename=Tulips_1535010527472.jpg"
	// },
	
	private String				userid;
	private String				nickname;
	private String				avatar;//头像
	
	public User(String userid, String nickname, String avatar) {
		this.userid = userid;
		this.nickname = nickname;
		this.avatar = avatar;
	}
	
	public String getUserId() {
		return userid;
	}
	
	public void setUserId(String id) {
		this.userid = id;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public String getAvatar() {
		return avatar;
	}
	
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	@Override
	public String toString() {
		return "id = " + userid + "; name = " + nickname + "; headUrl = " + avatar;
	}
}
