package com.uspresidentials.project.task4;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.queries.function.valuesource.DivFloatFunction;
import org.apache.lucene.queryparser.classic.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.uspresidentials.project.lucene.LuceneCore;
import com.uspresidentials.project.task3.MainOccurenceWords;
import com.uspresidentials.project.utils.PropertiesManager;
import com.uspresidentials.project.utils.Util;

public class ScrapeNews {

	private final static String link ="https://news.google.com/news/story?ncl=dzirMhj5Jge-9iMGB6zrye9MkG-zM&q=hillary+clinton&lr=English&hl=en&sa=X&ved=0ahUKEwjS1ISPjtHPAhWG6xoKHZmgDKkQqgIITTAH";
	final static Logger loggerScraping = Logger.getLogger("loggerScraping");
	static List<String> listaLink= new ArrayList<>();
	final static String PATH_INDEXDIR_FOR_SCRAP_NEWS = PropertiesManager.getPropertiesFromFile("PATH_INDEXDIR_FOR_SCRAP_NEWS");

	
	
	public static void main(String[] args) throws IOException, ParseException {
	   
		
//		for (int i = 0; i <= 300; i=i+10) {
//		    String linkModString = "http://www.google.com/search?hl=en&gl=us&q=hillary+clinton&num=100&authuser=0&biw=1745&bih=850&tbm=nws&ei=jOsDWObLFYv_Ur2BmZAD&start="+i+"&sa=N&dpr=1.1&gws_rd=cr";
//			listaLink.add(linkModString);
//			System.out.println(i+"   "+linkModString);
//		}
		
		
		// 1)Chiamata per effettuare scraping su google news.
		//scrapeNewsFromGoogle(link);
		
		
		
		//2)Creo indice con le news e richiamare algoritmo per cercare le main occurrence word.
		LuceneCore.createIndexForScrapingNews(PropertiesManager.getPropertiesFromFile("PATH_FILE_SCRAPING_NEWS_JSON"));
		Map<String,Double> mapCandidate = MainOccurenceWords.getTermFrequencyByCandidate(PATH_INDEXDIR_FOR_SCRAP_NEWS,"bodyIndexed");
		Map<String,Double> mapTermsAndDocuments = MainOccurenceWords.getTermsDocFrequency(mapCandidate, PATH_INDEXDIR_FOR_SCRAP_NEWS, "bodyIndexed");
		jaccard(mapCandidate, mapTermsAndDocuments,"Clinton");
		
		
		//3)Confrontare le main occurrence words del task3 con quelle trovate nelle scraping news.
		
		
		
		
		
		
		
		
		
	}
	
	
	
	
	public static void scrapeNewsFromGoogle(String linkString) throws IOException{
		System.out.println("Scraping news for candidate: HILLARY CLINTON");
		Document doc = null;
		JSONArray jsonArrayNews = new JSONArray();
        JSONObject objNews = new JSONObject();

		for (String string : listaLink) {

			try {
				doc = Jsoup.connect(string).userAgent("Mozilla").get();
				Elements topics = doc.select("div[id=ires]");
				Elements stories = topics.select("div[class=g]");
		        for (Element story : stories) {	            
		            Elements titles = story.select("h3[class=r]");
		            //prendere il body del messaggio che sta nel Div con class st
		        	Elements body = story.select("div[class=st]");

		            System.out.println("Title: "+titles.text());
		            System.out.println("Body: "+body.text()+"\n");
		            objNews = new JSONObject();
		            objNews.put("title", titles.text());
		            objNews.put("body", body.text());
		            jsonArrayNews.add(objNews);
		        }

		        
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
//			firstTime=false;
			doc=null;
		}
        

        writeJsonNewsOnFile(jsonArrayNews);

		System.out.println("STOP SCRAPING NEWS");
	}
	
	
	
	
	//ciclo su tutte le parole e mi faccio tornare  una mappa con dentro l'indice di jaccard per ogni coppia di parole
		public static Map<String,Double> jaccard(Map<String,Double> mapWords, Map<String,Double> mapTerms, String nameCandidate){
			//num_docs(term1,term2)/(term1.docfreq+term2.docfreq - num_docs(term1,term2))
			//nella prima stringa devo mettere la coppia di parole, divisa da ; in double cè l'indice di jaccard per quelle due parole
			Map<String,Double> wordJaccIndex = new HashMap<>();
			JSONObject wordsObject = new JSONObject();
			JSONArray wordsArray = new JSONArray();
			String pathFileDestination = PropertiesManager.getPropertiesFromFile("PATH_FILE_JACCARD_JSON_FROM_SCRAPING_NEWS");
			
			
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
				
				
				
			 wordsObject.put("TermsFor"+nameCandidate, Util.sortJsonFileByValue(wordsArray,"jaccard"));
			 
//			wordJaccIndex = Util.sortByValue(wordJaccIndex);
			
			
			 
			//scrivo json su file
			Util.writeJsonJaccardCandidate(wordsObject,pathFileDestination);
			
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			
			return wordJaccIndex;
		}
	
	
	
	
	
	public static void writeJsonNewsOnFile(JSONArray jsonArrNews) throws IOException {

		// inserire [] inizio e fine cosí da avere un json completo
		loggerScraping.info(jsonArrNews.toString());
	}
	
}
