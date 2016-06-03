package com.uspresidentials.project.utils;

import twitter4j.conf.ConfigurationBuilder;

public class Authentication {

	
	public static ConfigurationBuilder getAuthtentication(){
		
		ConfigurationBuilder cfg= new ConfigurationBuilder();
		cfg.setOAuthAccessToken("access_token");
		cfg.setOAuthAccessTokenSecret("access_token_secret");
		cfg.setOAuthConsumerKey("consumer_key");
		cfg.setOAuthConsumerSecret("consumer_secret");
		
		
		return cfg;
	}
	
	
	
}
