package com.uspresidentials.project.task5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.json.simple.parser.ParseException;

import com.uspresidentials.project.entity.UserCustom;
import com.uspresidentials.project.task1.FriendShipGraph;
import com.uspresidentials.project.task1.IdentifyUsers;
import com.uspresidentials.project.task2.SentiWordNetMain;
import com.uspresidentials.project.utils.PropertiesManager;

import edu.uci.ics.jung.graph.SparseMultigraph;
import twitter4j.TwitterException;


public class PredictWinner {

	
	/*
	 * Use the whatever method to predict the winner. Temporal series of candidate mentions, Google Trends, Twitter sentiment on candidates.. 
	 * A very simple method is fully ok, provided it is better than guessing, but you must explain your method or heuristics.
	 * 
	 * 
	 * Sarebbe interessante confrontare un range di tweet, esempio di Maggio e tirare fuori il sentiment dei tweet rispetto clinton e trump,
	 * confrontare con il sentiment tirato fuori dall'algo del sentiment e anche con le parole calcolate sul dataset delle scraping news,
	 * verificando cosi se il trend del giudizio verso i candidati e rimasto lo stesso nei mesi o se e cambiato.
	 * 
	 */
	
	final static String QUERY_STRING_CANDIDATES_NAME_TRUMP ="donald* OR trump*";
	final static String QUERY_STRING_CANDIDATES_NAME_CLINTON ="hillary* OR clinton*";
	final static String QUERY_STRING_CANDIDATES_NAME_RUBIO ="rubio* OR Rubio*";
	final static String QUERY_STRING_CANDIDATES_NAME_SANDERS ="Sanders* OR sanders*";
	final static String pathFileJsonTrump = PropertiesManager.getPropertiesFromFile("PATH_FILE_JACCARD_JSON_CANDIDATES")+"Trump_jaccard.json";
	final static String pathFileJsonClinton = PropertiesManager.getPropertiesFromFile("PATH_FILE_JACCARD_JSON_CANDIDATES")+"Clinton_jaccard.json";
	final static int NUM_USERS = 100;
	final static Logger loggerPredictWinner = Logger.getLogger("loggerPredictWinner");

	
	public static void main(String[] args) {
		
	
		PredictWinner.predict();
		
	
	}
		
		
	public static void predict(){
		loggerPredictWinner.info("PREVISIONE DEL VINCITORE DELLE ELEZIONI PRESIDENZIALI AMERICANE 2016");
		
		
		try {
			//1. Lista dei primi 10 user con pageRank maggiore

			ListenableDirectedGraph<String, DefaultEdge> graphFriendShip;
			graphFriendShip = FriendShipGraph.createGraphFromFriendShip();
			SparseMultigraph<String, DefaultEdge> graphSparse = FriendShipGraph.convertListenableGraph(graphFriendShip);
			TreeSet<UserCustom> rankedUsers= IdentifyUsers.calculatePageRank(graphSparse,NUM_USERS);
			List<String> rankedUsersName = new ArrayList<>();
			for(UserCustom u : rankedUsers){
				rankedUsersName.add(u.getUserName());
			}
			
			
			//2. liste degli utenti con maggiore centrality per trump e clinton
			HashMap<String, String> userCentrality = new HashMap<>();
			userCentrality=IdentifyUsers.calculateCentrality(graphSparse);
			
//			Partion user in M
			Hashtable<String, HashMap<String, String>> tableM = new Hashtable<>();
			tableM = IdentifyUsers.partitionUsers();
			
//			cerco i 10  utenti per ogni candidato  che hanno la centrality piu alta e hanno menzionato di piu i candidati.
			List<String> trumpTopCentralityUsers = IdentifyUsers.findUserByMentionsAndCentrality(tableM,userCentrality,"TRUMP",NUM_USERS);
			List<String> clintonTopCentralityUsers = IdentifyUsers.findUserByMentionsAndCentrality(tableM,userCentrality,"CLINTON",NUM_USERS);
			
			//3. Sentiment su tutti gli utenti e check sugli utenti con piu influenza (pagerank e centrality)
			loggerPredictWinner.info(" ");	

			loggerPredictWinner.info("Calcolando il valore del sentiment sul dataset per i candidati si ha che:");	

			
			double sentimentValueTrump = SentiWordNetMain.processTweets(QUERY_STRING_CANDIDATES_NAME_TRUMP,rankedUsersName,trumpTopCentralityUsers );
			double sentimentValueClinton = SentiWordNetMain.processTweets(QUERY_STRING_CANDIDATES_NAME_CLINTON,rankedUsersName,clintonTopCentralityUsers );
			
			loggerPredictWinner.info("Sentiment per Donald Trump = "+sentimentValueTrump);	
			loggerPredictWinner.info("Sentiment per Hillary Clinton = "+sentimentValueClinton);	
			loggerPredictWinner.info(" ");	

			loggerPredictWinner.info("Calcolando il valore del sentiment sulle coppie di parole che co-occorrono di piu secondo l'indice di jaccard si ha:");	

			double sentimentJaccardTrump = SentiWordNetMain.processJaccardWords(pathFileJsonTrump, "TermsForTrump");
			double sentimentJaccardClinton = SentiWordNetMain.processJaccardWords(pathFileJsonClinton, "TermsForClinton");

			loggerPredictWinner.info("Sentiment per le co-occurrence words per Donald Trump = "+sentimentJaccardTrump);	
			loggerPredictWinner.info("Sentiment per le co-occurrence words per Hillary Clinton = "+sentimentJaccardClinton);	
			loggerPredictWinner.info(" ");	

			
			
			double totalTrump = sentimentValueTrump + sentimentJaccardTrump;
			double totalClinton = sentimentValueClinton + sentimentJaccardClinton;
			
			loggerPredictWinner.info("La somma dei sentiment per ogni candidato:");	
			loggerPredictWinner.info("Totale Donald Trump = "+totalTrump);	
			loggerPredictWinner.info("Totale Hillary Clinton = "+totalClinton);	
			loggerPredictWinner.info(" ");	


			System.out.println("sentimentValueTrump: "+sentimentValueTrump +" sentimentValueClinton: "+sentimentValueClinton+" sentimentJaccardTrump:"+sentimentJaccardTrump+" sentimentJaccardClinton:"+sentimentJaccardClinton);
			System.out.println("totalTrump: "+totalTrump+" totalClinton:"+totalClinton);
			
			predictWinner(totalTrump,totalClinton);
		
		
		} catch (TwitterException | IOException | ParseException e) {
			e.printStackTrace();
		} catch (org.apache.lucene.queryparser.classic.ParseException e) {
			e.printStackTrace();
		} 		
		

	}
	
	
	public static void predictWinner(double totalTrump, double totalClinton  ){
		
		String winner = "";
		if(totalTrump > totalClinton)
			winner = "DONALD TRUMP";
		else {
			winner = "HILLARY CLINTON";

		}
			
			System.out.println("******************************************************");
			System.out.println("*****                                         ********");
			System.out.println("*****                                         ********");
			System.out.println("*****                                         ********");
			System.out.println("******             "+winner                +"*******");
			System.out.println("*****                                         ********");
			System.out.println("*****                                         ********");
			System.out.println("*****                                         ********");
			System.out.println("******************************************************");


			loggerPredictWinner.info(" ");	
			loggerPredictWinner.info(" ");	

			loggerPredictWinner.info("******************************************************");
			loggerPredictWinner.info("*****                                         ********");
			loggerPredictWinner.info("*****                                         ********");
			loggerPredictWinner.info("*****                                         ********");
			loggerPredictWinner.info("******             "+winner                +"*******");
			loggerPredictWinner.info("*****                                         ********");
			loggerPredictWinner.info("*****                                         ********");
			loggerPredictWinner.info("*****                                         ********");
			loggerPredictWinner.info("******************************************************");
	
	}

}