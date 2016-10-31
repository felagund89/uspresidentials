package com.uspresidentials.project.task1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.LockObtainFailedException;

import twitter4j.TwitterException;

import com.uspresidentials.project.lucene.LuceneCore;
import com.uspresidentials.project.utils.PropertiesManager;
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
	final static String PATH_DEBATES = PropertiesManager.getPropertiesFromFile("PATH_DEBATES");
	final static String PATH_PRIMARY = PropertiesManager.getPropertiesFromFile("PATH_PRIMARY");

	final static String PATH_INDEXDIR_PRIMAR = PropertiesManager.getPropertiesFromFile("PATH_INDEXDIR_PRIMAR");
	final static String PATH_FILE_UTENTI_ID = PropertiesManager.getPropertiesFromFile("PATH_FILE_UTENTI_ID");
	final static String PATH_FILE_UTENTI_ID_TEST = PropertiesManager.getPropertiesFromFile("PATH_FILE_UTENTI_ID_TEST");
	
	final static String PATH_FILE_USER_OCCURRENCE = PropertiesManager.getPropertiesFromFile("PATH_FILE_USER_OCCURRENCE");
	final static String PATH_FILE_IDUSER_IN_FILE_JSON = PropertiesManager.getPropertiesFromFile("PATH_FILE_IDUSER_IN_FILE_JSON");
	final static String PATH_FILE_FRIENDSHIP_JSON_UPDATED = PropertiesManager.getPropertiesFromFile("PATH_FILE_FRIENDSHIP_JSON_UPDATED");

	
	/**
	 * QUERY
	 */
	final static String QUERY_STRING_CANDIDATES_NAME_STRING ="donald* OR hillary* OR rubio* OR trump* OR clinton* OR Sanders*";

	final static Logger logger = Logger.getLogger(IdentifyUsers.class);


	public static void main(String[] args) throws CorruptIndexException, LockObtainFailedException, IOException, TwitterException, ParseException {
		
			//creazione del dataset attraverso lucene
			//createDataset();
		
			//verifico il numero degli utenti e dei tweets realativi al dataset creato
			//getTotNumbersUsersAndTweets();
			
			//costruisco un hashmap contenent gli utenti e i loro tweet
		    //getHashMapUser_Tweets();
		    
			//cerco le occorrenze dei vari candidati nei tweets degli utenti
			occurrenceCandidatesInTweets();
		    
		    
	}
	
	
	
	
	public static TopDocs createDataset() {
		
		TopDocs resultDocs = null;
		
		try {
		
		//Richiamo l'indexer, commentare se già fatto
		LuceneCore.createIndex(PATH_PRIMARY, PATH_INDEXDIR_PRIMAR);
	    LuceneCore.createIndex(PATH_DEBATES, PATH_INDEXDIR_PRIMAR);
		//1)identify tweets of users that mention one of the U.S. presidential candidates.
		
	    //Richiamo il searcher con la query per i candidati menzionati
	    resultDocs = LuceneCore.searchEngine(PATH_INDEXDIR_PRIMAR, "tweetText", QUERY_STRING_CANDIDATES_NAME_STRING);
	    
	    
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		}

		return resultDocs;
	}
	
	
	//1)How many users you get? How many tweets?
	public static void  getTotNumbersUsersAndTweets(){
		
		TopDocs resultDocs;
		try {
			//Richiamo il searcher con la query voluta

			resultDocs = LuceneCore.searchEngine(PATH_INDEXDIR_PRIMAR, "tweetText", QUERY_STRING_CANDIDATES_NAME_STRING);
		
			//calcolo il numero degli utenti twitter 
		    Set<String> setUniqUser = LuceneCore.numberOfUser(LuceneCore.getIndexSearcher(PATH_INDEXDIR_PRIMAR), resultDocs);
			
		    long numeroUniqUser = setUniqUser.size();
		    
		    //calcolo il numero totale dei tweets contenenti menzioni ai candidati
		    long numeroTweet = LuceneCore.numberOfTweets(LuceneCore.getIndexSearcher(PATH_INDEXDIR_PRIMAR), resultDocs);
			
			System.out.println("NUMERO UTENTI = "+numeroUniqUser+ " NUMERO TOTALE TWEETS PER I CANDIDATI = "+numeroTweet);
		    
		    
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
				
	}
	
	

	public static HashMap<String, ArrayList<String>> getHashMapUser_Tweets() throws IOException, ParseException {
		//Richiamo il searcher con la query voluta
		TopDocs resultDocs = LuceneCore.searchEngine(PATH_INDEXDIR_PRIMAR, "tweetText", QUERY_STRING_CANDIDATES_NAME_STRING);
		
		
		//creo la lista contenente utenti-idutente e relativi tweet
	    HashMap<String, ArrayList<String>> hashMapUsersTweets = LuceneCore.getUserAndRelTweets(new HashSet<String>(), resultDocs, PATH_FILE_UTENTI_ID_TEST, false);  // aggiunto path per scrivere su file		    
	    System.out.println("fine esecuzione getHashMapUser_Tweets");
	    return hashMapUsersTweets;
	}
	
	
	
	public static void occurrenceCandidatesInTweets(){
		
		
		try {
		//Richiamo il searcher con la query voluta
		TopDocs resultDocs = LuceneCore.searchEngine(PATH_INDEXDIR_PRIMAR, "tweetText", QUERY_STRING_CANDIDATES_NAME_STRING);
		

		LuceneCore.occurrenceCandidates(PATH_FILE_IDUSER_IN_FILE_JSON, PATH_FILE_USER_OCCURRENCE, PATH_INDEXDIR_PRIMAR);
		
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
			
		
	}
	
	
	// prendo in input il file json contenente tutti gli utenti, creo 4 liste contenenti le 10 persone più attive per ogni candidato.
	public static void partitionUsers(){
		
		HashMap<String, String> hashTrump = new HashMap<>();
		HashMap<String, String> hashClinton = new HashMap<>();
		HashMap<String, String> hashRubio = new HashMap<>();
		HashMap<String, String> hashSanders = new HashMap<>();

		
		
		
	}
	
	
	
	
	
}
