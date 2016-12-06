package com.uspresidentials.project.task1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.LockObtainFailedException;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import twitter4j.TwitterException;

import com.uspresidentials.project.entity.UserCustom;
import com.uspresidentials.project.lucene.LuceneCore;
import com.uspresidentials.project.utils.ComparatorRank;
import com.uspresidentials.project.utils.PropertiesManager;
import com.uspresidentials.project.utils.Util;

import edu.uci.ics.jung.algorithms.scoring.ClosenessCentrality;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.SparseMultigraph;
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
	final static String PATH_INDEXDIR_PRIMAR_7NOV = PropertiesManager.getPropertiesFromFile("PATH_INDEXDIR_PRIMAR_7NOV");

	
	final static String PATH_FILE_UTENTI_ID = PropertiesManager.getPropertiesFromFile("PATH_FILE_UTENTI_ID");
	final static String PATH_FILE_UTENTI_ID_TEST = PropertiesManager.getPropertiesFromFile("PATH_FILE_UTENTI_ID_TEST");
	
	final static String PATH_FILE_USER_OCCURRENCE = PropertiesManager.getPropertiesFromFile("PATH_FILE_USER_OCCURRENCE");
	final static String PATH_FILE_IDUSER_IN_FILE_JSON = PropertiesManager.getPropertiesFromFile("PATH_FILE_IDUSER_IN_FILE_JSON");
	final static String PATH_FILE_FRIENDSHIP_JSON_UPDATED = PropertiesManager.getPropertiesFromFile("PATH_FILE_FRIENDSHIP_JSON_UPDATED");
	final static String PATH_FILE_USER_JSON_COMPLETE = PropertiesManager.getPropertiesFromFile("PATH_FILE_USER_JSON_COMPLETE");
	final static int NUM_USERS = 10;
	
	
	/**
	 * QUERY
	 */
	final static String QUERY_STRING_CANDIDATES_NAME_STRING ="donald* OR hillary* OR rubio* OR trump* OR clinton* OR Sanders*";

	final static Logger logger = Logger.getLogger(IdentifyUsers.class);
	final static Logger loggerComponents = Logger.getLogger("loggerComponents");
	final static Logger loggerPageRank = Logger.getLogger("loggerPageRank");
	final static Logger loggerCentrality = Logger.getLogger("loggerCentrality");

	public static void main(String[] args) throws CorruptIndexException, LockObtainFailedException, IOException, TwitterException, ParseException, org.json.simple.parser.ParseException {
		
			//creazione del dataset attraverso lucene
			//createDataset();
		
			//verifico il numero degli utenti e dei tweets realativi al dataset creato
			//getTotNumbersUsersAndTweets();
			
			//costruisco un hashmap contenent gli utenti e i loro tweet
		    //getHashMapUser_Tweets();
		    
			//cerco le occorrenze dei vari candidati nei tweets degli utenti
			//occurrenceCandidatesInTweets();
		    
		
			ListenableDirectedGraph<String, DefaultEdge> graphFriendShip = FriendShipGraph.createGraphFromFriendShip(); 																							
			//System.out.println("\n\n\n-----Graph FriendShip-----\n\n\n" + graphFriendShip.toString());
	
			// ********COMPONENTE CONNESSE - write in folder 'log4j_logs'
//			FriendShipGraph.searchConnectedComponents(graphFriendShip);
	
			// ********PAGE RANK
	
			SparseMultigraph<String, DefaultEdge> graphSparse = FriendShipGraph.convertListenableGraph(graphFriendShip);
			calculatePageRank(graphSparse, NUM_USERS);
			
			
		
			// ********CENTRALITY OF M' USERS (who mentioned a candidate)
//			HashMap<String, String> userCentrality = new HashMap<>();
//			userCentrality=calculateCentrality(graphSparse);
			
//			Partion user in M
//			Hashtable<String, HashMap<String, String>> tableM = new Hashtable<>();
//			tableM = partitionUsers();
			
			//cerco i 10  utenti per ogni candidato  che hanno la centrality piu alta e hanno menzionato di piu i candidati.
//			findUserByMentionsAndCentrality(tableM,userCentrality,"TRUMP",NUM_USERS);
//			findUserByMentionsAndCentrality(tableM,userCentrality,"CLINTON",NUM_USERS);
//			findUserByMentionsAndCentrality(tableM,userCentrality,"RUBIO",NUM_USERS);
//			findUserByMentionsAndCentrality(tableM,userCentrality,"SANDERS",NUM_USERS);

			
			
			
	}
	
	public static TopDocs createDataset() throws ParseException {
		
		TopDocs resultDocs = null;
		
		try {
		
		//Richiamo l'indexer, commentare se già fatto
		LuceneCore.createIndex(PATH_PRIMARY, PATH_INDEXDIR_PRIMAR_7NOV);
	    LuceneCore.createIndex(PATH_DEBATES, PATH_INDEXDIR_PRIMAR_7NOV);
		//1)identify tweets of users that mention one of the U.S. presidential candidates.
		
	    //Richiamo il searcher con la query per i candidati menzionati
	    //resultDocs = LuceneCore.searchEngine(PATH_INDEXDIR_PRIMAR, "tweetText", QUERY_STRING_CANDIDATES_NAME_STRING);
	    
	    
		} catch (IOException e) {
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
	
	
	public static TreeSet<UserCustom> calculatePageRank(SparseMultigraph<String, DefaultEdge> graph, int numUsers) {

		loggerPageRank.info("First"+ numUsers+" users for pageRank\n");

		PageRank<String, DefaultEdge> rankerManager = new PageRank<String, DefaultEdge>(graph, 0.15);
		rankerManager.evaluate();

		TreeSet<UserCustom> orderedPageRankUser = new TreeSet<UserCustom>(new ComparatorRank());

		for (String v : graph.getVertices()) {
			double pageRankScore ;

				pageRankScore= rankerManager.getVertexScore(v);
				UserCustom user = new UserCustom(v, pageRankScore);
				orderedPageRankUser.add(user);
		}
		
		int max = 0;
		for(UserCustom u : orderedPageRankUser){
			if(max>=numUsers)
				break;
			loggerPageRank.info("User: " + u.getUserName() + " score: " + u.getPageRank());
			max++;
		}
		
		return orderedPageRankUser;
	}
	
	
	public static HashMap<String, String> calculateCentrality(SparseMultigraph<String, DefaultEdge> graph) {
			
		HashMap<String, String> userCentrality = new HashMap<>();
		
	    ClosenessCentrality<String,DefaultEdge> centralityUser = new ClosenessCentrality<String, DefaultEdge>(graph);
	    
	    for (String v : graph.getVertices()){
	    	double userCenScore = centralityUser.getVertexScore(v);
	    	if(!Double.isNaN(userCenScore)){
		    	//loggerCentrality.info("Vertext: " + v + " centrality-score: " + userCenScore);
		    	userCentrality.put(v, String.valueOf(userCenScore));
	    	}
	    }     
	    //ordino per valore l'hashmap delle centrality	
	    userCentrality = (HashMap<String, String>) Util.sortByValue(userCentrality);  
	    return userCentrality;
	}
	
	

	//*3.    Partition the users in M according to the candidates they mention (each user can mention more that one candidate more than one time). Identify the users mentioning more frequently each candidate and measure their centrality. 
	//Find the 10 (for each candidate)  who both mention the candidate frequently and are highly central (define some combined measure to select such candidates). 
	//	Let M' in M be these users.
	// prendo in input il file json contenente tutti gli utenti, creo 4 liste contenenti le 10 persone più attive per ogni candidato.
	public static Hashtable<String, HashMap<String, String>> partitionUsers(){
		
		Hashtable<String, HashMap<String, String>> tableM = new Hashtable<>();

		HashMap<String, String> hashTrump = new HashMap<>();
		HashMap<String, String> hashClinton = new HashMap<>();
		HashMap<String, String> hashRubio = new HashMap<>();
		HashMap<String, String> hashSanders = new HashMap<>();

		hashTrump = Util.getPartitionUsers("Trump");
		hashClinton = Util.getPartitionUsers("Clinton");
		hashRubio = Util.getPartitionUsers("Rubio");
		hashSanders = Util.getPartitionUsers("Sanders");
		
		hashTrump = (HashMap<String, String>) Util.sortByValue(hashTrump);
		hashClinton = (HashMap<String, String>) Util.sortByValue(hashClinton);
		hashRubio = (HashMap<String, String>) Util.sortByValue(hashRubio);
		hashSanders = (HashMap<String, String>) Util.sortByValue(hashSanders);
 	
		tableM.put("TRUMP", hashTrump);
		tableM.put("CLINTON", hashClinton);
		tableM.put("RUBIO", hashRubio);
		tableM.put("SANDERS", hashSanders);
				
		System.out.println("FINE PARTIZIONE UTENTI");
		
		return tableM;
		
	}
	
	public static List<String> findUserByMentionsAndCentrality(Hashtable<String, HashMap<String, String>> tableM, HashMap<String, String> userCentrality, String candidateName, int numUsers  ) {
		
		//trovare il valore medio per la centrality totale e per le menzioni. poi cercare i primi 10 elementi che siano sopra la mediae prendere quelli 
		//con i valori piu bilanciati
		
//		double mValueCentrality =userCentrality.keySet(). userCentrality.size()
		
		loggerCentrality.info("\nTEN FIRST USERS FOR --> " +candidateName);
		
		
		HashMap<String, String> hashApp = new HashMap<>();
		List<String> firstTenCentr = new ArrayList<String>();
		
		hashApp = tableM.get(candidateName);

		List<String> userM = new ArrayList<>();
		List<String> userC= new ArrayList<>();

		//prendo i primi 100 elementi degli hasmap
		int count=0;
		Iterator itM = hashApp.entrySet().iterator();
		while(itM.hasNext() && count<=300){
	        Map.Entry pair = (Map.Entry)itM.next();

			String chiaveM= (String) pair.getKey();
			String valoreM=(String) pair.getValue();
			userM.add(chiaveM);
			count++;
			
		}
		count =0;
		Iterator itC = userCentrality.entrySet().iterator();
		while(itC.hasNext() && count<=300){
	        Map.Entry pair = (Map.Entry)itC.next();

			String chiaveC= (String) pair.getKey();
			String valoreC=(String) pair.getValue();
			userC.add(chiaveC);
			count++;
		}
		
		int uCount=numUsers;
		for (String stringM : userM) {		
			for (String stringC : userC) {
				if(stringC.equalsIgnoreCase(stringM)){
					//System.out.println(stringC);
					if(uCount>0){
						firstTenCentr.add(stringC);
						loggerCentrality.info(stringC);
						uCount--;
					}
				}
			}
		}
		
	return firstTenCentr;
		
	}
	
}