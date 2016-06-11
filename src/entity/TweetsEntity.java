package entity;

import java.io.Serializable;

import twitter4j.Status;

public class TweetsEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2352776552646468586L;

	
	private String id;
	
	private String language;
	
	private Status tweetStatus;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public Status getTweetStatus() {
		return tweetStatus;
	}
	public void setTweetStatus(Status tweetStatus) {
		this.tweetStatus = tweetStatus;
	}
	
	
	
}
