package com.uspresidentials.project.task2;

import java.io.IOException;

import com.uspresidentials.project.utils.Authentication;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

public class FriendShipGraph {

	public static void main(String[] args) throws IOException, TwitterException {
		// TODO Auto-generated method stub

		//Authentication.InitializeTwitterObj("");
		
		getFriendShip();
	}
	
	public static void getFriendShip() throws TwitterException{
		  User u1 = null ;
	      long cursor = -1;
	      IDs ids;
	      Twitter twitter = TwitterFactory.getSingleton();
	      twitter.setOAuthConsumer("1HERcFVCy5SkpI23hl3FRpJy3", "5PFPlMp3NAsAT1Qbrk1RStXWLMX795ghSUfubtwILl5vR2keyW");
	      
	      System.out.println("Listing followers's ids.");
	      do {
	              ids = twitter.getFollowersIDs("username", cursor);
	          for (long id : ids.getIDs()) {
	              System.out.println(id);
	              User user = twitter.showUser(id);
	              System.out.println(user.getName());
	          }
	      } while ((cursor = ids.getNextCursor()) != 0);
	}
}
