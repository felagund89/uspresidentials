package com.uspresidentials.project.task1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.LockObtainFailedException;

import twitter4j.TwitterException;

import com.uspresidentials.project.entity.TweetsEntity;
import com.uspresidentials.project.lucene.LuceneCore;
/**
 * 
 * @author felagund89
 *
 *1.    From the entire dataset, identify tweets of users that mention one of the U.S. presidential candidates. How many users you get? How many tweets? Let M be the set of such users and let T(M) be the set of related tweets.
 *
 *2.    Crawl these users’ friendship relations. You will get a (likely highly disconnected) graph G.  Find the largest connected component and compute Page Rank  on this graph. Then,  and find the 10 highest ranked users.  Who are they?  
 *	    To identify connected component use: JGraphT (http://jgrapht.org/) and
 *	    http://jgrapht.org/javadoc/org/jgrapht/alg/ConnectivityInspector.html
 *	    For Page Rank use  Jung (http://jung.sourceforge.net/): 
 *	    http://jung.sourceforge.net/doc/api/edu/uci/ics/jung/algorithms/scoring/PageRank.html
 *
 *3.    Partition the users in M according to the candidates they mention (each user can mention more that one candidate more than one time). Identify the users mentioning more frequently each candidate and measure their centrality. Find the 10 (for each candidate)  who both mention the candidate frequently and are highly central (define some combined measure to select such candidates). Let M' in M be these users.
 *
 */




public class IdentifyUsers {

	/**
	 * PATH per dataset e indexer
	 */	
	  final static String PATH_DEBATES = "/Users/alessiocampanelli/Desktop/debates";
	  final static String PATH_INDEXDIR = "/Users/alessiocampanelli/Desktop/resultQuery";

	//final static String PATH_DEBATES = "/home/felagund89/Scrivania/Progetto web and social/debates";
	final static String PATH_PRIMARY = "/home/felagund89/Scrivania/Progetto web and social/DOCPRIMARYNY";

	//final static String PATH_INDEXDIR = "/home/felagund89/Scrivania/Progetto web and social/resultQuery/resultQueryDebates";
	final static String PATH_INDEXDIR_PRIMAR = "/home/felagund89/Scrivania/Progetto web and social/resultQuery/resultQueryPrimary";

	
	/**
	 * QUERY
	 */
	final static String QUERY_STRING_CANDIDATES_NAME_STRING ="donald* OR hillary* OR rubio* OR trump* OR clinton* OR Sanders*";

	final static Logger logger = Logger.getLogger(IdentifyUsers.class);


	public static void main(String[] args) throws CorruptIndexException, LockObtainFailedException, IOException, TwitterException, ParseException {
		
		
		 ArrayList<String> candidateStrings = new ArrayList<String>() {{
		        add("Donald J. Trump");
		        add("Hillary Clinton");
		        add("Bernie Sanders");
		        add("Marco Rubio");
		        add("Ted Cruz");
		        add("John Kasich");
		    }};
	
		    List<TweetsEntity> listaTweetsEntities = new ArrayList<TweetsEntity>();
		
			//Richiamo l'indexer, commentare se già fatto
			//LuceneCore.createIndex(PATH_PRIMARY, PATH_INDEXDIR_PRIMAR);
		    //LuceneCore.createIndex(PATH_DEBATES, PATH_INDEXDIR);
		
			//1)identify tweets of users that mention one of the U.S. presidential candidates. How many users you get? How many tweets?
			//Richiamo il searcher con la query voluta
			TopDocs resultDocs = LuceneCore.searchEngine(PATH_INDEXDIR, "tweetText", QUERY_STRING_CANDIDATES_NAME_STRING);
			
			//calcolo il numero degli utenti twitter 
		    Set<String> setUniqUser = LuceneCore.numberOfUser(LuceneCore.getIndexSearcher(PATH_INDEXDIR), resultDocs);
			
		    long numeroUniqUser = setUniqUser.size();
		    long numeroTweet = LuceneCore.numberOfTweets(LuceneCore.getIndexSearcher(PATH_INDEXDIR), resultDocs);
		
		    HashMap<String, ArrayList<String>> hashMapUsersTweets = LuceneCore.getUserAndRelTweets(new HashSet<String>(), resultDocs);
		    
		    String s = null;
		    
	}
}
