package com.uspresidentials.project.task3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.TopDocs;

import com.uspresidentials.project.lucene.LuceneCore;
import com.uspresidentials.project.utils.PropertiesManager;

public class MainOccurenceWords {

	
	final static String PATH_INDEXDIR_PRIMAR = PropertiesManager.getPropertiesFromFile("PATH_INDEXDIR_PRIMAR");
	final static String QUERY_STRING_CANDIDATES_NAME_STRING ="donald* OR hillary* OR rubio* OR trump* OR clinton* OR Sanders*";

	
	public static void main(String[] args) {
		
//		String tweet = "RT @mparent77772: Should Obama's 'internet kill switch' power be curbed? http://bbc.in/hcVGoz";
//		System.out.println("Tweet: " + tweet);
//		String[] tokens = tweet.split(" ");
//		Tokenize1(tweet);
		
		getTermFrequency();
		
		
	}
	
	
	
	public static void getTermFrequency(){
		
		
		
		try {
			
			LuceneCore.getTerms(PATH_INDEXDIR_PRIMAR, "tweetText", QUERY_STRING_CANDIDATES_NAME_STRING);
			
			System.out.println("FINE OCCURRENCE TERMS");
			} catch (ParseException | IOException e) {
				e.printStackTrace();
			}
		
		
		
		
		
	}
	

	public static void Tokenize1(String tweet) {
	    //Approach #1, find token seperators
		String[] tokens = tweet.split(" ");
		for (String token : tokens) {
		    System.out.println("Token: " + token);
		}
	    
	}
	
	
//	public static void jaccardDistance(){
//		JaccardDistance jaccardD = new JaccardDistance(tokFactory);
//		int filteredCount = 0;
//		List candidateTweets 
//		    = filterNormalizedDuplicates(texts, 
//						 tokFactory); //throw out easy cases
//		System.out.println("Normalized duplicate filter leaves " + candidateTweets.size() + " tweets");
//		row = new ArrayList();
//		for (int i = 0; i < candidateTweets.size(); ++i) {
//		    String closestTweet = "default value";
//		    double closestProximity = -1d;
//		    String targetTweet = candidateTweets.get(i);
//		    for (int j = 0; j < candidateTweets.size(); ++j ) {//cross product, ouchy, ow ow. 
//			String comparisionTweet = candidateTweets.get(j);
//			double thisProximity 
//			    = jaccardD.proximity(targetTweet,comparisionTweet);
//			if (i != j) { // can't match self
//			    if (closestProximity < thisProximity) {
//				closestTweet = comparisionTweet;
//				closestProximity = thisProximity;
//			    }
//			}
//		    }
//	}
//	
//	
//		public static List filterTweetsJaccard(List texts,TokenizerFactory tokFactory, double cutoff) {
//			JaccardDistance jaccardD = new JaccardDistance(tokFactory);
//			List filteredTweets = new ArrayList();
//			for (int i = 0; i < texts.size(); ++i) {
//			 String targetTweet = texts.get(i);
//			 boolean addTweet = true;
//			 //big research literature on making the below loop more efficient
//			 for (int j = 0; j = cutoff) {
//				    addTweet = false;
//				    break; //one nod to efficency
//				}
//			 }
//			 if (addTweet) {
//				filteredTweets.add(targetTweet);
//			 }
//			}
//			return filteredTweets;
//			}
//		}
	
}
