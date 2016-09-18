package com.uspresidentials.project.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public final class AuthenticationManager {

	public static int ACCOUNTS_NUMBER;
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
		map.put("OAuthConsumerKey", "1HERcFVCy5SkpI23hl3FRpJy3");
		map.put("OAuthConsumerSecret","5PFPlMp3NAsAT1Qbrk1RStXWLMX795ghSUfubtwILl5vR2keyW");
		map.put("OAuthAccessToken","2977694199-o286ySyyQbCTJsMXcxSfoeSwQ6CkVGQSNl8ILMO");
		map.put("OAuthAccessTokenSecret","SKo5MvolhkJxmoG3ADgb2tzW5oOFV7p6A44hmcHY1Pzz1");
		accounts.add(map);

		// ALESSIA
		map = new HashMap<>();
		map.put("OAuthConsumerKey", "FzkBBBPes89nojrjfiVgzyrmy");
		map.put("OAuthConsumerSecret","MffW9BNFSSo9eKPHGddokTREiUoJXAu93hpOVHcqGMXq8E6Dor");
		map.put("OAuthAccessToken","872321179-FoPaVqfdIf7JP4GwrXRP2TCJSncItXQK6oTBYioW");
		map.put("OAuthAccessTokenSecret","L1gusbWbNvl52CQ965JZpvhCnsHli8royqgVnQllwhLYb");
		accounts.add(map);

		// ALESSIO
		map = new HashMap<>();
		map.put("OAuthConsumerKey", "wJXRIxwvk6ndFQ2KYJqmPs0MS");
		map.put("OAuthConsumerSecret","daeFR8AsQFBfd5PHmfFYwl0tiAJYVn2U4KVEypYp6RyWnaMQqR");
		map.put("OAuthAccessToken","1676105413-EO6Rhu6D0cp7xGH7UkoZxktLasMhHo24uupczRn");
		map.put("OAuthAccessTokenSecret","xuGm37VDuIjGl02XqBl6x9aKqnoaqnKaqIqcCQ4BQDVw2");
		accounts.add(map);

		// CLAUDIO
		map = new HashMap<>();
		map.put("OAuthConsumerKey", "KqYc0PQ1seDR36glxMwxgSq5O");
		map.put("OAuthConsumerSecret","v05RdurBZRCVmeKc6Tig4eeWdghQXrvubtb1GR7PTOFSKfjWnK");
		map.put("OAuthAccessToken","469453578-MeraM7YtOFHKYAiAVBNRG676ZHEgnHAUqaYVGx35");
		map.put("OAuthAccessTokenSecret","OFx87bY2GSxaTkIHKNR9ddST8XChoTg4M4aHQw6PZRA91");
		accounts.add(map);
		
		// GUSTAVO DA
		map = new HashMap<>();
		map.put("OAuthConsumerKey", "IkE88OtO3jv4hLQEinyB6hZ3x");
		map.put("OAuthConsumerSecret","KzIE1BeeClGe6HiGFW1p3c14UFKE5bGLdnflnnQ8xVaLhK5Sdr");
		map.put("OAuthAccessToken","777423871746203648-2Bktf7a0eIj0hhK7MsT1uYFzY7LUXGM");
		map.put("OAuthAccessTokenSecret","XOMYHjFIdhAt1xh2aM0ozDLIXXfd1nb6TBZiOy47e5zfS");
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
