package com.uspresidentials.project.rest;

import java.io.IOException;

import com.uspresidentials.project.utils.Authentication;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class RestCall {

	public static void main(String[] args) throws TwitterException, IOException{
		ConfigurationBuilder cfg= Authentication.getAuthtentication();
		Twitter twitter = new TwitterFactory(cfg.build()).getInstance();
		
		
		long [] ids=twitter.getFriendsIDs("USERID",-1).getIDs();
		//PRINT IDS...
		
	
	}
	
	
}
