package com.uspresidentials.project.stream;

import java.io.IOException;

import com.uspresidentials.project.utils.Authentication;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Stream {

	public static void main(String[] args) throws TwitterException, IOException{
		StatusListener listener = new StatusListener(){
			public void onStatus(Status status) {
				System.out.println(status.getUser().getName() + " : "+ status.getText());
			}
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
			public void onException(Exception ex) { ex.printStackTrace(); }
			@Override
			public void onScrubGeo(long arg0, long arg1) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub
				
			}
		};
		ConfigurationBuilder cfg= Authentication.getAuthtentication(); //SET AUTH PARAMETER
		TwitterStream twitterStream = new TwitterStreamFactory(cfg.build()).getInstance();
		twitterStream.addListener(listener );
		// sample() method internally creates a thread which manipulates TwitterStream
		// and calls these adequate listener methods continuously.
		twitterStream.sample();
		}
	
}
