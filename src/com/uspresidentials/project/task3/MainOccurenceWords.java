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
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import com.uspresidentials.project.entity.WordEntity;
import com.uspresidentials.project.lucene.LuceneCore;
import com.uspresidentials.project.utils.PropertiesManager;
import com.uspresidentials.project.utils.Util;

public class MainOccurenceWords {

	
	/*
	 * For each candidate, analyze (on the full set of mentions T(M)) the most frequently co-occurring words. You should use Jaccard to avoid selecting words that co-occur by chance. 
	 * 
	 */
	
	final static String PATH_INDEXDIR_PRIMAR = PropertiesManager.getPropertiesFromFile("PATH_INDEXDIR_PRIMAR");
	final static String PATH_INDEXDIR_PRIMAR_7NOV = PropertiesManager.getPropertiesFromFile("PATH_INDEXDIR_PRIMAR_7NOV");
	final static String PATH_INDEXDIR_CANDIDATE_FOR_OCCURRENCE = PropertiesManager.getPropertiesFromFile("PATH_INDEXDIR_CANDIDATE_FOR_OCCURRENCE");

	final static String QUERY_STRING_CANDIDATES_NAME_TRUMP ="donald* OR trump*";
	final static String QUERY_STRING_CANDIDATES_NAME_CLINTON ="hillary* OR clinton*";
	final static String QUERY_STRING_CANDIDATES_NAME_RUBIO ="rubio* OR Rubio*";
	final static String QUERY_STRING_CANDIDATES_NAME_SANDERS ="Sanders* OR sanders*";

	
	public static void main(String[] args) throws IOException, ParseException {

		
		
		//Devo richiamare su ogni candidato la ricerca con lucene e sui documenti trovati cercare le co-occurrence words. Non so a cosa serve Jaccard nel nostro caso.
		//ogni documento equivale ad un tweet in Lucene
		
		
//		ScoreDoc[] scoreDocs = LuceneCore.createIndexForCandidate(PATH_INDEXDIR_PRIMAR_7NOV, PATH_INDEXDIR_CANDIDATE_FOR_OCCURRENCE, QUERY_STRING_CANDIDATES_NAME_TRUMP);
		LuceneCore.createIndexForCandidates(PATH_INDEXDIR_PRIMAR_7NOV, PATH_INDEXDIR_CANDIDATE_FOR_OCCURRENCE, QUERY_STRING_CANDIDATES_NAME_TRUMP);
		Map<String,Double> mapTrump = getTermFrequencyByCandidate(PATH_INDEXDIR_CANDIDATE_FOR_OCCURRENCE);
		Map<String,Integer> mapTermsAndDocuments = getTermsDocFrequency(mapTrump);
		jaccard(mapTrump, mapTermsAndDocuments);
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
	public static Map<String,Double> getTermFrequencyByCandidate(String pathIndexForCandidate){
		
		Map<String,Double> mapTerms = null;
		
		
		try {
			
			mapTerms = LuceneCore.getTerms(pathIndexForCandidate);
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
		
		
		return mapTerms;
		
		
	}
	
	public static Map<String,Integer>  getTermsDocFrequency(Map<String,Double> setTerms ){
		
		Map<String,Integer> mapTerms= new HashMap<String, Integer>(); 
		
		
		mapTerms = LuceneCore.getDocFreqForTwoTerms(setTerms, PATH_INDEXDIR_CANDIDATE_FOR_OCCURRENCE, "tweetText");
		
		System.out.println("FINE term doc frequency");
		
		
		return mapTerms;
		
		
	}
	
	
	
	//ciclo su tutte le parole e mi faccio tornare  una mappa con dentro l'indice di jaccard per ogni coppia di parole
	public static Map<String,Double> jaccard(Map<String,Double> mapWords, Map<String,Integer> mapTerms){
		//num_docs(term1,term2)/(term1.docfreq+term2.docfreq - num_docs(term1,term2))
		//nella prima stringa devo mettere la coppia di parole, divisa da ; in double cè l'indice di jaccard per quelle due parole
		Map<String,Double> wordJaccIndex = new HashMap<>();
		
		//Devo lavorare sugli indici delle parole che ho nel set, ogni oggetto ha la parola, e i dati relativi al numero di volte che compaiono in totale 
		//e al numero di documenti in cui compaiono in totale.
		
		
		//APPLICARE LA FORMULA
		//scorro tutte le coppie di parole, calcolando l'indice di jaccard su ognuna, inserisco tutte le coppie e il risultatnte indice in una mappa.
		 for (Map.Entry<String, Double> entry : mapWords.entrySet()) {
			   String word1 = entry.getKey();
			   double docFreqTerm1 = entry.getValue();

			   for (Map.Entry<String, Double> entry2 : mapWords.entrySet()) {

					String word2= entry2.getKey();
					   double docFreqTerm2 = entry.getValue();

//				String word2= wordEnt2.getWord();
				if(!word1.equalsIgnoreCase(word2)){
					if(mapTerms.containsKey(word1+";"+word2)){
							int docFreq = mapTerms.get(word1+";"+word2);
							System.out.println(docFreq);
		//					Double numDocWords = wordEnt1.getNumDocOcc()+wordEnt2.getNumDocOcc();
							
							Double jaccardIndex = (double) (double)docFreq/(((docFreqTerm1 + docFreqTerm2) - (double)docFreq));
							if(jaccardIndex.isInfinite())
								jaccardIndex=0.0;
							
							wordJaccIndex.put(word1+";"+word2, jaccardIndex);
//						    System.out.println(word1+" "+wordEnt1.getNumDocOcc()+"; "+word2+" "+wordEnt2.getNumDocOcc() +" "+jaccardIndex);
						
					}
				}

			}
			
		}
		
		int count = 0;
		wordJaccIndex = Util.sortByValue(wordJaccIndex);
		Iterator itM = wordJaccIndex.entrySet().iterator();
		while(itM.hasNext() && count<=100){
	        Map.Entry pair = (Map.Entry)itM.next();

			String chiaveM= (String) pair.getKey();
			Double valoreM=(Double) pair.getValue();
			System.out.println(chiaveM+" "+valoreM);
			count++;
		}
		return wordJaccIndex;
	}
	


}
