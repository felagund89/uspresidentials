package com.uspresidentials.project.task2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONObject;

import com.uspresidentials.project.entity.UserCustom;
import com.uspresidentials.project.lucene.LuceneCore;
import com.uspresidentials.project.utils.PropertiesManager;
import com.uspresidentials.project.utils.Util;

public class SentiWordNetMain {

	private Map<String, Double> dictionary;
	private static final String PATH_SENTIMENT_WORDNET_FILE = PropertiesManager.getPropertiesFromFile("PATH_SENTIMENT_WORDNET_FILE");
	final static String PATH_INDEXDIR_PRIMAR = PropertiesManager.getPropertiesFromFile("PATH_INDEXDIR_PRIMAR");
	
	final static String QUERY_STRING_CANDIDATES_NAME_TRUMP ="donald* OR trump*";
	final static String QUERY_STRING_CANDIDATES_NAME_CLINTON ="hillary* OR clinton*";
	final static String QUERY_STRING_CANDIDATES_NAME_RUBIO ="rubio* OR Rubio*";
	final static String QUERY_STRING_CANDIDATES_NAME_SANDERS ="Sanders* OR sanders*";
	
	public static final String[] negationWords = new String[] {"not" };
	
	public SentiWordNetMain(String pathToSWN) throws IOException {
		// This is our main dictionary representation
		dictionary = new HashMap<String, Double>();

		// From String to list of doubles.
		HashMap<String, HashMap<Integer, Double>> tempDictionary = new HashMap<String, HashMap<Integer, Double>>();

		BufferedReader csv = null;
		try {
			csv = new BufferedReader(new FileReader(pathToSWN));
			int lineNumber = 0;

			String line;
			while ((line = csv.readLine()) != null) {
				lineNumber++;

				// If it's a comment, skip this line.
				if (!line.trim().startsWith("#")) {
					// We use tab separation
					String[] data = line.split("\t");
					String wordTypeMarker = data[0];

					// Example line:
					// POS ID PosS NegS SynsetTerm#sensenumber Desc
					// a 00009618 0.5 0.25 spartan#4 austere#3 ascetical#2
					// ascetic#2 practicing great self-denial;...etc

					// Is it a valid line? Otherwise, through exception.
					if (data.length != 6) {
						throw new IllegalArgumentException(
								"Incorrect tabulation format in file, line: "
										+ lineNumber);
					}

					// Calculate synset score as score = PosS - NegS
					Double synsetScore = Double.parseDouble(data[2])
							- Double.parseDouble(data[3]);

					// Get all Synset terms
					String[] synTermsSplit = data[4].split(" ");

					// Go through all terms of current synset.
					for (String synTermSplit : synTermsSplit) {
						// Get synterm and synterm rank
						String[] synTermAndRank = synTermSplit.split("#");
						String synTerm = synTermAndRank[0] + "#"
								+ wordTypeMarker;

						int synTermRank = Integer.parseInt(synTermAndRank[1]);
						// What we get here is a map of the type:
						// term -> {score of synset#1, score of synset#2...}

						// Add map to term if it doesn't have one
						if (!tempDictionary.containsKey(synTerm)) {
							tempDictionary.put(synTerm,
									new HashMap<Integer, Double>());
						}

						// Add synset link to synterm
						tempDictionary.get(synTerm).put(synTermRank,
								synsetScore);
					}
				}
			}

			// Go through all the terms.
			for (Map.Entry<String, HashMap<Integer, Double>> entry : tempDictionary
					.entrySet()) {
				String word = entry.getKey();
				Map<Integer, Double> synSetScoreMap = entry.getValue();

				// Calculate weighted average. Weigh the synsets according to
				// their rank.
				// Score= 1/2*first + 1/3*second + 1/4*third ..... etc.
				// Sum = 1/1 + 1/2 + 1/3 ...
				double score = 0.0;
				double sum = 0.0;
				for (Map.Entry<Integer, Double> setScore : synSetScoreMap
						.entrySet()) {
					score += setScore.getValue() / (double) setScore.getKey();
					sum += 1.0 / (double) setScore.getKey();
				}
				score /= sum;

				dictionary.put(word, score);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (csv != null) {
				csv.close();
			}
		}
	}

	public double extract(String word, String pos) {
		return dictionary.get(word + "#" + pos);
	}
		
	public static Double processTweets(String queryStringCandidates, List<String> rankedUsers, List<String> topCentralityUsers ) throws IOException, ParseException {
			
			double finalSentiment = 0;
		
			double boost = 0.3;
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(PATH_INDEXDIR_PRIMAR))));
			ScoreDoc[] scoredocs = LuceneCore.getTweetsCoreForSentiment(PATH_INDEXDIR_PRIMAR, "tweetText", queryStringCandidates);
			for (ScoreDoc sd : scoredocs) {
		    	Document d = searcher.doc(sd.doc);
		    	String tweetCleaned = Util.deleteUnnecessaryWords(d.get("tweetText"));
		    	String userScanned = d.get("tweetUser");
		    	
		    	Double returnedSentiment = analyzeSentimentPhrase(tweetCleaned, userScanned);
		    	finalSentiment += returnedSentiment;
		    	
//		    	System.out.println("tweet cleaned: " + tweetCleaned + " for user: " + userScanned);

		    	if(topCentralityUsers.contains(userScanned) || rankedUsers.contains(userScanned) ){
		    		
		    		if(returnedSentiment > 0 )
		    			finalSentiment += boost;
		    		else if( returnedSentiment < 0)
		    			finalSentiment-=boost;
		    			
		    		System.out.println("utente ranked e centr, aumento di 0.3");
		    	}
		    	
	 	   }
			
			
			
			
			return finalSentiment;
	}
	
	
	
	public static Double processJaccardWords(String pathFileJson, String fieldJson ) throws IOException, ParseException {
		
		double finalSentiment = 0;
		Map<String, Double> mapWords = Util.readJsonFromFile(pathFileJson, fieldJson);
		
		for (Map.Entry<String, Double> entry : mapWords.entrySet()) {
			
			if(entry.getValue() > 0.7){
				finalSentiment += analyzeSentimentPhrase(entry.getKey().replace(";", " "), "user");
		    	System.out.println("words: " + entry.getKey().replace(";", " ") );
			}
		}
		
		return finalSentiment;
	}
	
	
	
	public static Double processTweetsJsonUsers(String pathFileJson, String fieldJson ) throws IOException, ParseException {
		
		double finalSentiment = 0;
		Map<String, String> mapWords = Util.readUsersFromJsonFile();
		
		for (Map.Entry<String, String> entry : mapWords.entrySet()) {
			
				finalSentiment += analyzeSentimentPhrase(entry.getValue(), entry.getKey());
		    	System.out.println("words: " + entry.getKey() );
			
		}
		
		return finalSentiment;
	}
	
	
	
	
	private static double analyzeSentimentPhrase(String tweet, String user) throws IOException{
		
		int countPositive = 0;
		int countNegative = 0;
		int countNeutral = 0;
		
		boolean isNegation = false;
		boolean isClauseLevelPunctuation = false;
		
		String currentWord;
		
		double sumSentiment = 0;
		
		String[] splitted = tweet.split(" ");
		for(int i=0;i<splitted.length;i++){
			currentWord = splitted[i];
			
			if(isNegation == false)
				isNegation = isNegation(currentWord);
			
			if(isNegation){   //se c'é stata una parola di negazione in precedenza
				if(isClauseLevelPunctuation(currentWord) == false){   //se non é una punteggiatura
					currentWord = currentWord + "_NEG";
				}else{
					isNegation = false;   								//reset della negazione....
				}
			}
						
			if(currentWord.length() > 1){
				double sentimentValue = getSentimentWordValue(currentWord);
//				System.out.println("word to examine : " + currentWord + " - " + sentimentValue);
			
			/*if(sentimentValue > 0)
				countPositive++;
			else if(sentimentValue < 0)
				countNegative++;
			else
				countNeutral++; */
			
			//check parola precedente con array negationWords
			if(sentimentValue != -1)
				sumSentiment += sentimentValue;
			}
		}
		
//		if(sumSentiment == 0){
//			System.out.println("************\n" + user + " is Neutral!" + "************\n");
//		}else if(sumSentiment > 0)
//			System.out.println("************\n" + user + " is a Supporter!" + "************\n");
//		else
//			System.out.println("************\n" + user + " is a Opponent!" + "************\n");
		
		/*if((countPositive > countNegative) && (countPositive > countNeutral))
			System.out.println("************\n" + user + " is a Supporter!" + "************\n");
		else if((countNegative > countNegative) && (countNegative > countNeutral))
			System.out.println("************\n" + user + " is a Opponent!" + "************\n");
		else
			System.out.println("************\n" + user + " is Neutral!" + "************\n"); */
		
		return sumSentiment;
	}
	
	private static boolean isNegation(String word){
		return word.matches("(?:^(?:never|no|nothing|nowhere|noone|none|not|havent|hasnt|hadnt|cant|couldnt|shouldnt|wont|wouldnt|dont|doesnt|didnt|isnt|arent|aint)$)|(n't|don't|isn't|haven't|hasn't|hadn't|can't|couldn't|shouldn't|wouldn't|didn't|aren't|ain't)");
	}
	
	private static boolean isClauseLevelPunctuation(String word){
		return word.matches("^[.:;!?]$");
	}
	
	public static double getSentimentWordValue(String s) throws IOException
	{
		SentiWordNetMain sentiwordnet = new SentiWordNetMain(PATH_SENTIMENT_WORDNET_FILE);
		String[] idsDB = new String[] {"a", "n", "r", "v" };
		int moltiplic = 1;
		int indexIds = 0;
		
		if(s.contains("_NEG")){
			s = s.replace("_NEG", "");
			moltiplic = -1;
		}
		
		while(indexIds < 4){
			if(sentiwordnet.dictionary.containsKey(s.toLowerCase() + "#" + idsDB[indexIds])){
				return (sentiwordnet.extract(s, idsDB[indexIds]) * moltiplic);
			}else{
				indexIds++;
			}
		}
		return -1;
	}
	
	public static void main(String [] args) throws IOException, ParseException {
		
		processTweets(QUERY_STRING_CANDIDATES_NAME_TRUMP, null, null);
		
		SentiWordNetMain sentiwordnet = new SentiWordNetMain(PATH_SENTIMENT_WORDNET_FILE);
		
		System.out.println("good#a "+sentiwordnet.extract("good", "a"));
		System.out.println("bad#a "+sentiwordnet.extract("bad", "a"));
		System.out.println("blue#a "+sentiwordnet.extract("blue", "a"));
		System.out.println("blue#n "+sentiwordnet.extract("blue", "n"));
		System.out.println("hello#a "+sentiwordnet.extract("good", "a"));
		System.out.println("sad#a "+sentiwordnet.extract("sad", "a"));
		System.out.println("beautiful#a "+sentiwordnet.extract("beautiful", "a"));
		System.out.println("fuck#a "+sentiwordnet.extract("fucking", "a"));
	}
	
}