package com.uspresidentials.project.utils;

import twitter4j.conf.ConfigurationBuilder;

public class Authentication {

	
	public static ConfigurationBuilder getAuthtentication(){
		
		//Per l'accesso a USPresidentials app, con account Antonio
		final String access_token ="2977694199-o286ySyyQbCTJsMXcxSfoeSwQ6CkVGQSNl8ILMO";
		final String access_token_secret ="SKo5MvolhkJxmoG3ADgb2tzW5oOFV7p6A44hmcHY1Pzz1";
		final String consumer_key ="1HERcFVCy5SkpI23hl3FRpJy3";
		final String consumer_secret ="5PFPlMp3NAsAT1Qbrk1RStXWLMX795ghSUfubtwILl5vR2keyW";

		ConfigurationBuilder cfg= new ConfigurationBuilder();
		cfg.setOAuthAccessToken(access_token);
		cfg.setOAuthAccessTokenSecret(access_token_secret);
		cfg.setOAuthConsumerKey(consumer_key);
		cfg.setOAuthConsumerSecret(consumer_secret);
		
		
		return cfg;
	}
}
