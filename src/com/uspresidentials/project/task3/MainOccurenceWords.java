package com.uspresidentials.project.task3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.TopDocs;

import com.uspresidentials.project.entity.WordEntity;
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

	
	public static void main(String[] args) throws IOException {

		
		
		//Devo richiamare su ogni candidato la ricerca con lucene e sui documenti trovati cercare le co-occurrence words. Non so a cosa serve Jaccard nel nostro caso.
		//ogni documento equivale ad un tweet in Lucene
		
		Set<WordEntity> setTrump = getTermFrequencyByCandidate(QUERY_STRING_CANDIDATES_NAME_TRUMP);
		Map<String,Integer> mapTermsAndDocuments = getTermsDocFrequency(QUERY_STRING_CANDIDATES_NAME_TRUMP,setTrump);
		jaccard(setTrump, mapTermsAndDocuments);
		//Map<String,Integer> mapHillary = getTermFrequencyByCandidate(QUERY_STRING_CANDIDATES_NAME_CLINTON);
		//Map<String,Integer> mapRubio = getTermFrequencyByCandidate(QUERY_STRING_CANDIDATES_NAME_RUBIO);
		//Map<String,Integer> mapSanders = getTermFrequencyByCandidate(QUERY_STRING_CANDIDATES_NAME_SANDERS);

		//Map<String,Integer> mapTotal = LuceneCore.computeTermIdMap(PATH_INDEXDIR_PRIMAR_7NOV);
//		System.out.println(mapTotal.size());
//		System.out.println(mapTotal.keySet().size());
		System.out.println("FINE MAIN OCCURRENCE");
	}
	
	  
	
	
	

	
	
	//trovo tutti i termini e le rispettive frequenze di tutti i documenti trovati per ogni candidato.
	//per jaccard si dovrebbe usare cosi : jaccard(term1,term2)=num_docs(term1,term2)/(term1.docfreq+term2.docfreq - num_docs(term1,term2))
	public static Set<WordEntity> getTermFrequencyByCandidate(String query){
		
		Set<WordEntity> setTerms = null;
		
		
		try {
			
			setTerms = LuceneCore.getTerms(PATH_INDEXDIR_PRIMAR_7NOV, "tweetText", query);
			//mapTerms = Util.sortByValue(mapTerms);
			
			//PROVA prendo le prime 100 parole con più occorrenze e le stampo.
//			int count = 0;
//			Iterator itM = mapTerms.entrySet().iterator();
//			while(itM.hasNext() && count<=100){
//		        Map.Entry pair = (Map.Entry)itM.next();
//
//				String chiaveM= (String) pair.getKey();
//				int valoreM=(Integer) pair.getValue();
//				System.out.println(chiaveM+" "+valoreM);
//				count++;
//			}
			
			System.out.println("FINE OCCURRENCE TERMS");
		} catch (ParseException | IOException e) {
				e.printStackTrace();
		}
		
		
		return setTerms;
		
		
	}
	
	public static Map<String,Integer>  getTermsDocFrequency(String query, Set<WordEntity> setTerms ){
		
		Map<String,Integer> mapTerms= new HashMap<String, Integer>(); 
		
		
		mapTerms = LuceneCore.getDocFreqForTwoTerms(setTerms, PATH_INDEXDIR_PRIMAR_7NOV, "tweetText", query);
		
		System.out.println("FINE term doc frequency");
		
		
		return mapTerms;
		
		
	}
	
	
	
	//ciclo su tutte le parole e mi faccio tornare  una mappa con dentro l'indice di jaccard per ogni coppia di parole
	public static Map<String,Double> jaccard(Set<WordEntity> mapWords, Map<String,Integer> mapTerms){
		//num_docs(term1,term2)/(term1.docfreq+term2.docfreq - num_docs(term1,term2))
		//nella prima stringa devo mettere la coppia di parole, divisa da ; in double cè l'indice di jaccard per quelle due parole
		Map<String,Double> wordJaccIndex = new HashMap<>();
		
		//Devo lavorare sugli indici delle parole che ho nel set, ogni oggetto ha la parola, e i dati relativi al numero di volte che compaiono in totale 
		//e al numero di documenti in cui compaiono in totale.
		
		
		//APPLICARE LA FORMULA
		//scorro tutte le coppie di parole, calcolando l'indice di jaccard su ognuna, inserisco tutte le coppie e il risultatnte indice in una mappa.
		for (WordEntity wordEnt1 : mapWords) {
			String word1 = wordEnt1.getWord();
			
			for (WordEntity wordEnt2 : mapWords) {
				
				String word2= wordEnt2.getWord();
				if(!word1.equalsIgnoreCase(word2)){
					if(mapTerms.containsKey(word1+";"+word2)){
						int docFreq = mapTerms.get(word1+";"+word2);
	//					Double numDocWords = wordEnt1.getNumDocOcc()+wordEnt2.getNumDocOcc();
						
						Double jaccardIndex = (double) docFreq/(((wordEnt1.getTotalOcc()+wordEnt2.getTotalOcc()) - docFreq));
						if(jaccardIndex.isInfinite())
							jaccardIndex=0.0;
						
						wordJaccIndex.put(word1+";"+word2, jaccardIndex);
					    System.out.println(word1+";"+word2+"  "+jaccardIndex);
					}
				}
			}
		}
		
		wordJaccIndex = Util.sortByValue(wordJaccIndex);
		return wordJaccIndex;
	}
	
	
	public static void Tokenize1(String tweet) {
	    //Approach #1, find token seperators
		String[] tokens = tweet.split(" ");
		for (String token : tokens) {
		    System.out.println("Token: " + token);
		}	
	    
	}
	
	
	 private static double jaccard_coeffecient(String s1, String s2) {

	        double j_coeffecient;
	        ArrayList<String> j1 = new ArrayList<String>();
	        ArrayList<String> j2 = new ArrayList<String>();
	        HashSet<String> set1 = new HashSet<String>();
	        HashSet<String> set2 = new HashSet<String>();
	        
	            s1="$"+s1+"$";
	            s2="$"+s2+"$";
	            int j=0;
	            int i=3;
	        
	            while(i<=s1.length())
	            {
	                j1.add(s1.substring(j, i));
	                    j++;
	                    i++;
	            }    
	            j=0;
	            i=3;
	            while(i<=s2.length())
	            {
	                j2.add(s2.substring(j, i));
	                    j++;
	                    i++;
	            }    

	            
	            Iterator<String> itr1 = j1.iterator();
	            while (itr1.hasNext()) {
	                  String element = itr1.next();
	                  System.out.print(element + " ");
	                }
	                System.out.println();
	                Iterator<String> itr2 = j2.iterator();
	                while (itr2.hasNext()) {
	                  String element = itr2.next();
	                  System.out.print(element + " ");
	                }
	                System.out.println();
	            
	                
	                set2.addAll(j2);
	                set2.addAll(j1);
	                set1.addAll(j1);
	                set1.retainAll(j2);
	                
	                    
	                System.out.println("Union="+set2.size());
	                System.out.println("Intersection="+set1.size());
	                
	                j_coeffecient=((double)set1.size())/((double)set2.size());
	                System.out.println("Jaccard coeffecient="+j_coeffecient);
	                
	                return j_coeffecient;

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
