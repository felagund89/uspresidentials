package com.uspresidentials.project.utils;

import java.util.ArrayList;
import java.util.HashMap;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public final class AuthenticationManager {

	public int ACCOUNTS_NUMBER;
	public Twitter twitter;

	private int accountIndex;
	private ConfigurationBuilder configurationBuilder;

	private ArrayList<HashMap<String, String>> accounts;

	public AuthenticationManager() {
		InitAccountList();
		ACCOUNTS_NUMBER = accounts.size();

		accountIndex = 0;
		setAuthentication(accountIndex);
	}

	public int getAccountIndex() {
		return accountIndex;
	}

	private void InitAccountList() {
		
		accounts = new ArrayList<>();

		// ANTONIO
		HashMap<String, String> map = new HashMap<>();
		map.put("OAuthConsumerKey", "");
		map.put("OAuthConsumerSecret","");
		map.put("OAuthAccessToken","");
		map.put("OAuthAccessTokenSecret","");
		accounts.add(map);

		

	}

	public void setAuthentication(int i) {
		accountIndex = i % ACCOUNTS_NUMBER; // setto l'account index
		HashMap<String, String> map = accounts.get(accountIndex);

		setAuthentication(map.get("OAuthConsumerKey"),map.get("OAuthConsumerSecret"), map.get("OAuthAccessToken"),map.get("OAuthAccessTokenSecret"));
		System.out.println("Passo all'account numero: " + accountIndex);
	}

	private void setAuthentication(String OAuthConsumerKey,String OAuthConsumerSecret, String OAuthAccessToken,String OAuthAccessTokenSecret) {
		configurationBuilder = new ConfigurationBuilder();

		configurationBuilder.setOAuthAccessToken(OAuthAccessToken);
		configurationBuilder.setOAuthAccessTokenSecret(OAuthAccessTokenSecret);
		configurationBuilder.setOAuthConsumerKey(OAuthConsumerKey);
		configurationBuilder.setOAuthConsumerSecret(OAuthConsumerSecret);

		twitter = (new TwitterFactory(configurationBuilder.build())).getInstance();
	}
}