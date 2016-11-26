package com.uspresidentials.project.task3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
	final static String QUERY_STRING_CANDIDATES_NAME_RUBIO ="rubio* OR mark*";
	final static String QUERY_STRING_CANDIDATES_NAME_SANDERS ="sanders* OR bernie*";
    static final List<String> queryCandidates = Arrays.asList(QUERY_STRING_CANDIDATES_NAME_TRUMP,QUERY_STRING_CANDIDATES_NAME_CLINTON,QUERY_STRING_CANDIDATES_NAME_RUBIO,QUERY_STRING_CANDIDATES_NAME_SANDERS);
    static final List<String> nameCandidates = Arrays.asList("Trump","Clinton","Rubio","Sanders");
	
    
    public static void main(String[] args) throws IOException, ParseException {

		
		
		//Devo richiamare su ogni candidato la ricerca con lucene e sui documenti trovati cercare le co-occurrence words. Non so a cosa serve Jaccard nel nostro caso.
		//ogni documento equivale ad un tweet in Lucene
		
		
		for (int i = 0; i < queryCandidates.size(); i++) {
			
		
			LuceneCore.createIndexForCandidates(PATH_INDEXDIR_PRIMAR_7NOV, PATH_INDEXDIR_CANDIDATE_FOR_OCCURRENCE, queryCandidates.get(i));
			Map<String,Double> mapCandidate = getTermFrequencyByCandidate(PATH_INDEXDIR_CANDIDATE_FOR_OCCURRENCE,"tweetTextIndexed");
			Map<String,Double> mapTermsAndDocuments = getTermsDocFrequency(mapCandidate,PATH_INDEXDIR_CANDIDATE_FOR_OCCURRENCE, "tweetTextIndexed" );
			jaccard(mapCandidate, mapTermsAndDocuments,nameCandidates.get(i));
			
			
		}
		
		System.out.println("FINE MAIN OCCURRENCE");
	}
	
	  
	
	
	

	
	
	//trovo tutti i termini e le rispettive frequenze di tutti i documenti trovati per ogni candidato.
	//per jaccard si dovrebbe usare cosi : jaccard(term1,term2)=num_docs(term1,term2)/(term1.docfreq+term2.docfreq - num_docs(term1,term2))
	public static Map<String,Double> getTermFrequencyByCandidate(String pathIndexForCandidate, String fieldForQuery){
		
		Map<String,Double> mapTerms = null;
		
		
		try {
			
			mapTerms = LuceneCore.getTerms(pathIndexForCandidate, fieldForQuery);
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
			
			System.out.println("FINE getTerms");
		} catch (ParseException | IOException e) {
				e.printStackTrace();
		}
		
		
		return mapTerms;
		
		
	}
	
	public static Map<String,Double>  getTermsDocFrequency(Map<String,Double> setTerms, String path, String fieldForQuery ){
		
		Map<String,Double> mapTerms= new HashMap<String, Double>(); 
		
		
		mapTerms = LuceneCore.getDocFreqForTwoTerms(setTerms,  path,  fieldForQuery);
		
		System.out.println("FINE getTermsDocFrequency");
		
		
		return mapTerms;
		
		
	}
	
	
	
	//ciclo su tutte le parole e mi faccio tornare  una mappa con dentro l'indice di jaccard per ogni coppia di parole
	public static Map<String,Double> jaccard(Map<String,Double> mapWords, Map<String,Double> mapTerms, String nameCandidate){
		//num_docs(term1,term2)/(term1.docfreq+term2.docfreq - num_docs(term1,term2))
		//nella prima stringa devo mettere la coppia di parole, divisa da ; in double cè l'indice di jaccard per quelle due parole
		Map<String,Double> wordJaccIndex = new HashMap<>();
		JSONObject wordsObject = new JSONObject();
		JSONArray wordsArray = new JSONArray();
		String pathFileDestination = PropertiesManager.getPropertiesFromFile("PATH_FILE_JACCARD_JSON_CANDIDATES")+nameCandidate+"_jaccard.json";
		
		
		//Devo lavorare sugli indici delle parole che ho nel set, ogni oggetto ha la parola, e i dati relativi al numero di volte che compaiono in totale 
		//e al numero di documenti in cui compaiono in totale.
		
		System.out.println("INIZIO JACCARD");
		//APPLICARE LA FORMULA
		
		try {
		
			
			for (Map.Entry<String, Double> entry : mapTerms.entrySet()) {
			 JSONObject coupleWords = new JSONObject();
			 
			 String[] words = entry.getKey().split(";");
			 String word1 = words[0];
			 String word2 = words[1];
			 
			 double countWord1 = mapWords.get(word1);
			 double countWord2 = mapWords.get(word2);
			 double docFreqWord1Word2 = entry.getValue();

			 Double jaccardIndex = docFreqWord1Word2/(((countWord1 + countWord2) - docFreqWord1Word2));
			 wordJaccIndex.put(word1+";"+word2, jaccardIndex);
			 
			 coupleWords.put("term1", word1);
			 coupleWords.put("term2", word2);
			 coupleWords.put("docFreqTerm1", countWord1);
			 coupleWords.put("docFreqTerm2", countWord2);
			 coupleWords.put("docFreqTerm1Term2", docFreqWord1Word2);
			 coupleWords.put("jaccard", jaccardIndex);
			 wordsArray.add(coupleWords);
	 
		 }
			
			
//		 wordsArray= Util.sortJsonFileByValue(wordsArray);	
			
		 wordsObject.put("TermsFor"+nameCandidate, Util.sortJsonFileByValue(wordsArray,"jaccard"));
		 
//		wordJaccIndex = Util.sortByValue(wordJaccIndex);
		
		
		
//		Iterator itM = wordJaccIndex.entrySet().iterator();
//		while(itM.hasNext()){
//	        Map.Entry pair = (Map.Entry)itM.next();
//
//			String chiaveM= (String) pair.getKey();
//			Double valoreM= (Double) pair.getValue();
//			System.out.println(chiaveM+" "+valoreM);
////			count++;
//		}
		
		
		 
		//scrivo json su file
		Util.writeJsonJaccardCandidate(wordsObject,pathFileDestination);
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		return wordJaccIndex;
	}
	
}
