package com.uspresidentials.project.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class Authentication {

	
	//Per l'accesso a USPresidentials app, con account Antonio
			final static String access_token ="2977694199-o286ySyyQbCTJsMXcxSfoeSwQ6CkVGQSNl8ILMO";
			final static String access_token_secret ="SKo5MvolhkJxmoG3ADgb2tzW5oOFV7p6A44hmcHY1Pzz1";
			final static String consumer_key ="1HERcFVCy5SkpI23hl3FRpJy3";
			final static String consumer_secret ="5PFPlMp3NAsAT1Qbrk1RStXWLMX795ghSUfubtwILl5vR2keyW";
	
	public static ConfigurationBuilder getAuthtentication(){
		
		ConfigurationBuilder cfg= new ConfigurationBuilder();
		cfg.setOAuthAccessToken(access_token);
		cfg.setOAuthAccessTokenSecret(access_token_secret);
		cfg.setOAuthConsumerKey(consumer_key);
		cfg.setOAuthConsumerSecret(consumer_secret);
		
		return cfg;
	}
	
	public static void InitializeTwitterObj() throws IOException, TwitterException{
		 // The factory instance is re-useable and thread safe.
	    Twitter twitter = TwitterFactory.getSingleton();
	    twitter.setOAuthConsumer(consumer_key, consumer_secret);
	    RequestToken requestToken = twitter.getOAuthRequestToken();
	    AccessToken accessToken = null;
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    while (null == accessToken) {
	      System.out.println("Open the following URL and grant access to your account:");
	      System.out.println(requestToken.getAuthorizationURL());
	      System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
	      String pin = br.readLine();
	      try{
	         if(pin.length() > 0){
	           accessToken = twitter.getOAuthAccessToken(requestToken, pin);
	         }else{
	           accessToken = twitter.getOAuthAccessToken();
	         }
	      } catch (TwitterException te) {
	        if(401 == te.getStatusCode()){
	          System.out.println("Unable to get the access token.");
	        }else{
	          te.printStackTrace();
	        }
	      }
	    }
	    
	    //persist to the accessToken for future reference.
	    //storeAccessToken(twitter.verifyCredentials().getId() , accessToken);
	    //Status status = twitter.updateStatus(args[0]);
	    //System.out.println("Successfully updated the status to [" + status.getText() + "].");
	    //System.exit(0);
	}
	
	  private static void storeAccessToken(int useId, AccessToken accessToken){
		    //store accessToken.getToken()
		    //store accessToken.getTokenSecret()
		  }
}
