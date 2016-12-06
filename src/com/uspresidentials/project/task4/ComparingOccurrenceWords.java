package com.uspresidentials.project.task4;

import java.util.HashMap;
import java.util.Map;

import com.uspresidentials.project.utils.PropertiesManager;
import com.uspresidentials.project.utils.Util;

public class ComparingOccurrenceWords {

	private static String PATH_FILE_JACCARD_JSON_CANDIDATES = PropertiesManager.getPropertiesFromFile("PATH_FILE_JACCARD_JSON_CANDIDATES")+"Clinton_jaccard.json";
    private static String PATH_FILE_JACCARD_JSON_FROM_SCRAPING_NEWS = PropertiesManager.getPropertiesFromFile("PATH_FILE_JACCARD_JSON_FROM_SCRAPING_NEWS");
		
	public static void main(String[] args) {
		
		//comparare le parole che occorrono maggiormente dal dataset e dalle scraping news
		
		Map<String,Double> occurrenceDataset = Util.readJsonFromFile(PATH_FILE_JACCARD_JSON_CANDIDATES, "TermsForClinton");
		Map<String,Double> occurrenceScrapNews = Util.readJsonFromFile(PATH_FILE_JACCARD_JSON_FROM_SCRAPING_NEWS, "TermsForClinton");
		
		Map<String,Double> mostOccWords = analyzeOccurrenceWords(occurrenceDataset, occurrenceScrapNews);

	}

	
	public static Map<String,Double> analyzeOccurrenceWords(Map<String,Double> occurrenceDataset, Map<String,Double> occurrenceScrapNews){
		System.out.println("Coppie di parole in comune tra tweet dei dataset e notizie di google news.\n");

		Map<String, Double> mostOccWords = new HashMap<>();

		for (Map.Entry<String, Double> entry : occurrenceDataset.entrySet()) {
			String words1 = entry.getKey();
			String term1 = words1.split(";")[0];
			String term2 = words1.split(";")[1];

			if (entry.getValue() > 0.7) {
				// if(!mostOccWords.containsKey(term1+";"+term2)){
				mostOccWords.put("dataset " + term1 + ";" + term2, entry.getValue());
				System.out.println("Coppia datas: " + term1 + ", " + term2);

			}
		}

		for (Map.Entry<String, Double> entry2 : occurrenceScrapNews.entrySet()) {

			String words2 = entry2.getKey();
			String term3 = words2.split(";")[0];
			String term4 = words2.split(";")[1];

			if (entry2.getValue() > 0.7) {
				mostOccWords.put("scrap " + term3 + ";" + term4, entry2.getValue());
				System.out.println("Coppia scrap: " + term3 + ", " + term4);
			}
		}
		mostOccWords = Util.sortByValue(mostOccWords);
		return mostOccWords;
	}
}