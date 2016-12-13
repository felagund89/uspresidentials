package main.java;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.json.simple.parser.ParseException;

import twitter4j.TwitterException;

import com.uspresidentials.project.lucene.LuceneCore;
import com.uspresidentials.project.task1.FriendShipGraph;
import com.uspresidentials.project.task1.IdentifyUsers;
import com.uspresidentials.project.task2.SentiWordNetMain;
import com.uspresidentials.project.task3.MainOccurenceWords;
import com.uspresidentials.project.task4.ScrapeNews;
import com.uspresidentials.project.task5.PredictWinner;
import com.uspresidentials.project.utils.PropertiesManager;

import edu.uci.ics.jung.graph.SparseMultigraph;

public class MainUsPresidentials {

	final static String PATH_DATASET = "";
	final static String PATH_INDEXER = "";
	
	final static String QUERY_STRING_CANDIDATES_NAME_TRUMP ="donald* OR trump*";
	final static String QUERY_STRING_CANDIDATES_NAME_CLINTON ="hillary* OR clinton*";
	final static String QUERY_STRING_CANDIDATES_NAME_RUBIO ="rubio* OR Rubio*";
	final static String QUERY_STRING_CANDIDATES_NAME_SANDERS ="Sanders* OR sanders*";
	final static String PATH_INDEXDIR_PRIMAR_7NOV = PropertiesManager.getPropertiesFromFile("PATH_INDEXDIR_PRIMAR_7NOV");
	final static String PATH_INDEXDIR_CANDIDATE_FOR_OCCURRENCE = PropertiesManager.getPropertiesFromFile("PATH_INDEXDIR_CANDIDATE_FOR_OCCURRENCE");
	final static String PATH_INDEXDIR_FOR_SCRAP_NEWS = PropertiesManager.getPropertiesFromFile("PATH_INDEXDIR_FOR_SCRAP_NEWS");
	final static List<String> queryCandidates = Arrays.asList(QUERY_STRING_CANDIDATES_NAME_TRUMP,QUERY_STRING_CANDIDATES_NAME_CLINTON,QUERY_STRING_CANDIDATES_NAME_RUBIO,QUERY_STRING_CANDIDATES_NAME_SANDERS);
	final static List<String> nameCandidates = Arrays.asList("Trump","Clinton","Rubio","Sanders");
		
	final static int NUM_USERS = 10;
	final static int NUM_ELEMENTS=1000;

	
	final static Logger loggerUSPresidentials = Logger.getLogger("loggerUSPresidentials");

	
	public static void main(String[] args) throws FileNotFoundException, TwitterException, IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException {

/*
 * 1) Identify mentions of candidates
 * 
 */
		
		
		//Creo indice  di Lucene  partendo dai file contenuti nei dataset
		LuceneCore.createIndex(PATH_DATASET, PATH_INDEXER);
		
		//
		
		
		
		
		
		
		loggerUSPresidentials.info("1) Identify mentions of candidates \n");
		//verifico il numero degli utenti e dei tweets realativi al dataset creato
		IdentifyUsers.getTotNumbersUsersAndTweets(PATH_INDEXER);
	
		ListenableDirectedGraph<String, DefaultEdge> graphFriendShip = FriendShipGraph.createGraphFromFriendShip(); 																							

		// ********COMPONENTE CONNESSE - 
		FriendShipGraph.searchConnectedComponents(graphFriendShip);

		// ********PAGE RANK

		SparseMultigraph<String, DefaultEdge> graphSparse = FriendShipGraph.convertListenableGraph(graphFriendShip);
		IdentifyUsers.calculatePageRank(graphSparse, NUM_USERS);
		
		
	
		// ********CENTRALITY OF M' USERS (who mentioned a candidate)
		HashMap<String, String> userCentrality = new HashMap<>();
		userCentrality = IdentifyUsers.calculateCentrality(graphSparse);
		
		//	Partion user in M
		Hashtable<String, HashMap<String, String>> tableM = new Hashtable<>();
		tableM = IdentifyUsers.partitionUsers();
		
		//cerco i 10  utenti per ogni candidato  che hanno la centrality piu alta e hanno menzionato di piu i candidati.
		List<String> trumpTopCentralityUsers = IdentifyUsers.findUserByMentionsAndCentrality(tableM,userCentrality,"TRUMP",NUM_USERS);
		List<String> clintonTopCentralityUsers = IdentifyUsers.findUserByMentionsAndCentrality(tableM,userCentrality,"CLINTON",NUM_USERS);
		List<String> sandersTopCentralityUsers = IdentifyUsers.findUserByMentionsAndCentrality(tableM,userCentrality,"SANDERS",NUM_USERS);
		List<String> rubioTopCentralityUsers = IdentifyUsers.findUserByMentionsAndCentrality(tableM,userCentrality,"RUBIO",NUM_USERS);

/*
 * 2) Supporters and Opponents
 * 
 */
		loggerUSPresidentials.info("2) Supporters and Opponents \n");

		
		SentiWordNetMain.processTweetsM1Users(PATH_INDEXER,QUERY_STRING_CANDIDATES_NAME_TRUMP, trumpTopCentralityUsers,"TRUMP");
		SentiWordNetMain.processTweetsM1Users(PATH_INDEXER,QUERY_STRING_CANDIDATES_NAME_CLINTON, clintonTopCentralityUsers,"CLINTON");
		SentiWordNetMain.processTweetsM1Users(PATH_INDEXER,QUERY_STRING_CANDIDATES_NAME_SANDERS, sandersTopCentralityUsers,"SANDERS");
		SentiWordNetMain.processTweetsM1Users(PATH_INDEXER,QUERY_STRING_CANDIDATES_NAME_RUBIO, rubioTopCentralityUsers,"RUBIO");
		
/*		
 * 3)Co-occurrence analisys
 * 
 */
		
		
		loggerUSPresidentials.info("3)Co-occurrence analisys \n");

		//Devo richiamare su ogni candidato la ricerca con lucene e sui documenti trovati cercare le co-occurrence words.
		//ogni documento equivale ad un tweet in Lucene	
//		for (int i = 0; i < queryCandidates.size(); i++) {				
//			LuceneCore.createIndexForCandidates(PATH_INDEXER, PATH_INDEXDIR_CANDIDATE_FOR_OCCURRENCE, queryCandidates.get(i));
//			Map<String,Double> mapCandidate = MainOccurenceWords.getTermFrequencyByCandidate(PATH_INDEXDIR_CANDIDATE_FOR_OCCURRENCE,"tweetTextIndexed");
//			Map<String,Double> mapTermsAndDocuments = MainOccurenceWords.getTermsDocFrequency(mapCandidate,PATH_INDEXDIR_CANDIDATE_FOR_OCCURRENCE, "tweetTextIndexed" );
//			MainOccurenceWords.jaccard(mapCandidate, mapTermsAndDocuments,nameCandidates.get(i));		
//		}
		
		loggerUSPresidentials.info("Per le informazioni su term occurrence, e indice di Jaccard \n consultare il file dedicato ad ogni candidato al path: /files/Jaccard/  \n");

		
/*
 * 4)Scrape news about one candidate
 * 
 */
		loggerUSPresidentials.info("4)Scrape news about one candidate \n");

//		LuceneCore.createIndexForScrapingNews(PropertiesManager.getPropertiesFromFile("PATH_FILE_SCRAPING_NEWS_JSON"));
//		Map<String,Double> mapCandidate = MainOccurenceWords.getTermFrequencyByCandidate(PATH_INDEXDIR_FOR_SCRAP_NEWS,"bodyIndexed");
//		Map<String,Double> mapTermsAndDocuments = MainOccurenceWords.getTermsDocFrequency(mapCandidate, PATH_INDEXDIR_FOR_SCRAP_NEWS, "bodyIndexed");
//		ScrapeNews.jaccard(mapCandidate, mapTermsAndDocuments,"Clinton");

		loggerUSPresidentials.info("Per le informazioni sulle occorrenze e i valori dell'indice di jaccard per le news estrapolate da google news \n ../files/Jaccard/scrapingNewsJaccard.json");

		
		 
/*
 * 5)Predict the winner
 * 
 */
		loggerUSPresidentials.info("5)Predict the winner \n");

//		PredictWinner.predict();
		

		loggerUSPresidentials.info("Per le informazioni sulla previsione del candidato vincitore consultare il file al path: /files/Utenti/log4j_logs/loggerPredictWinner.log");
		
	}

}
