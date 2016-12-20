package com.uspresidentials.project.notUsedFile;

import java.io.Serializable;
import java.util.List;

import com.uspresidentials.project.entity.TweetInfoEntity;

public class UserEntity implements Serializable{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4524559254542516269L;
	

	private String nickName;
	private List<TweetInfoEntity> tweetsEntities;
	
	
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public List<TweetInfoEntity> getTweetsEntities() {
		return tweetsEntities;
	}
	public void setTweetsEntities(List<TweetInfoEntity> tweetsEntities) {
		this.tweetsEntities = tweetsEntities;
	}
	
	
	
	
	
}
