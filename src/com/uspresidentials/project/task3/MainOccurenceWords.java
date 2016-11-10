package com.uspresidentials.project.task3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.TopDocs;

import com.uspresidentials.project.lucene.LuceneCore;
import com.uspresidentials.project.utils.PropertiesManager;
import com.uspresidentials.project.utils.Util;

public class MainOccurenceWords {

	
	/*
	 * For each candidate, analyze (on the full set of mentions T(M)) the most frequently co-occurring words. You should use Jaccard to avoid selecting words that co-occur by chance. 
	 * 
	 * guardare http://sujitpal.blogspot.it/2008/09/ir-math-with-java-similarity-measures.html
	 */
	
	final static String PATH_INDEXDIR_PRIMAR = PropertiesManager.getPropertiesFromFile("PATH_INDEXDIR_PRIMAR");
	final static String PATH_INDEXDIR_PRIMAR_7NOV = PropertiesManager.getPropertiesFromFile("PATH_INDEXDIR_PRIMAR_7NOV");

	final static String QUERY_STRING_CANDIDATES_NAME_TRUMP ="donald* OR trump*";
	final static String QUERY_STRING_CANDIDATES_NAME_CLINTON ="hillary* OR clinton*";
	final static String QUERY_STRING_CANDIDATES_NAME_RUBIO ="rubio* OR Rubio*";
	final static String QUERY_STRING_CANDIDATES_NAME_SANDERS ="Sanders* OR sanders*";

	
	public static void main(String[] args) {

		
		
		//Devo richiamare su ogni candidato la ricerca con lucene e sui documenti trovati cercare le co-occurrence words. Non so a cosa serve Jaccard nel nostro caso.
		//ogni documento equivale ad un tweet in Lucene
		
		Map<String,Integer> mapTrump = getTermFrequencyByCandidate(QUERY_STRING_CANDIDATES_NAME_TRUMP);
		Map<String,Integer> mapHillary = getTermFrequencyByCandidate(QUERY_STRING_CANDIDATES_NAME_CLINTON);
		Map<String,Integer> mapRubio = getTermFrequencyByCandidate(QUERY_STRING_CANDIDATES_NAME_RUBIO);
		Map<String,Integer> mapSanders = getTermFrequencyByCandidate(QUERY_STRING_CANDIDATES_NAME_SANDERS);

		
		
		System.out.println("FINE MAIN OCCURRENCE");
	}
	

	
	
	//trovo tutti i termini e le rispettive frequenze di tutti i documenti trovati per ogni candidato.
	//per jaccard si dovrebbe usare cosi : jaccard(term1,term2)=num_docs(term1,term2)/(term1.docfreq+term2.docfreq - num_docs(term1,term2))
	public static Map<String,Integer> getTermFrequencyByCandidate(String query){
		
		Set<String> setTerms = null;
		Map<String,Integer> mapTerms= new HashMap<String, Integer>(); 
		
		
		try {
			
			mapTerms = LuceneCore.getTerms(PATH_INDEXDIR_PRIMAR_7NOV, "tweetText", query);
			mapTerms = Util.sortByValue(mapTerms);
			
			
			//PROVA prendo le prime 100 parole con più occorrenze e le stampo.
			int count = 0;
			Iterator itM = mapTerms.entrySet().iterator();
			while(itM.hasNext() && count<=100){
		        Map.Entry pair = (Map.Entry)itM.next();

				String chiaveM= (String) pair.getKey();
				int valoreM=(Integer) pair.getValue();
				System.out.println(chiaveM+" "+valoreM);
				count++;
			}
			
			System.out.println("FINE OCCURRENCE TERMS");
		} catch (ParseException | IOException e) {
				e.printStackTrace();
		}
		
		
		return mapTerms;
		
		
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
//	
	
	
//	public void testJaccardSimilarityWithLsiVector() throws Exception {
//	    LsiIndexer indexer = new LsiIndexer();
//	    Matrix termDocMatrix = indexer.transform(vectorGenerator.getMatrix());
//	    JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
//	    Matrix similarity = jaccardSimilarity.transform(termDocMatrix);
//	    prettyPrintMatrix("Jaccard Similarity (LSI)", similarity, 
//	      vectorGenerator.getDocumentNames(), new PrintWriter(System.out, true));
//	  }
//	
//	
//	private void prettyPrintMatrix(String legend, Matrix matrix, 
//		      String[] documentNames, PrintWriter writer) {
//		    writer.printf("=== %s ===%n", legend);
//		    writer.printf("%6s", " ");
//		    for (int i = 0; i < documentNames.length; i++) {
//		      writer.printf("%8s", documentNames[i]);
//		    }
//		    writer.println();
//		    for (int i = 0; i < documentNames.length; i++) {
//		      writer.printf("%6s", documentNames[i]);
//		      for (int j = 0; j < documentNames.length; j++) {
//		        writer.printf("%8.4f", matrix.get(i, j));
//		      }
//		      writer.println();
//		    }
//		    writer.flush();
//		  }
//		  
//		  private void prettyPrintResults(String query, 
//		      List<SearchResult> results) {
//		    System.out.printf("Results for query: [%s]%n", query);
//		    for (SearchResult result : results) {
//		      System.out.printf("%s (score = %8.4f)%n", result.title, result.score);
//		    }
//		  }
	
}
