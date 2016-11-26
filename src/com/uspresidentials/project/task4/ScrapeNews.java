package com.uspresidentials.project.task4;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
import com.uspresidentials.project.utils.PropertiesManager;

public class ScrapeNews {

	private final static String link ="https://news.google.com/news/story?ncl=dzirMhj5Jge-9iMGB6zrye9MkG-zM&q=hillary+clinton&lr=English&hl=en&sa=X&ved=0ahUKEwjS1ISPjtHPAhWG6xoKHZmgDKkQqgIITTAH";
	final static Logger loggerScraping = Logger.getLogger("loggerScraping");
	static List<String> listaLink= new ArrayList<>();

	
	
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
	
	
	
	
	
	
	
	public static void writeJsonNewsOnFile(JSONArray jsonArrNews) throws IOException {

		// inserire [] inizio e fine cosí da avere un json completo
		loggerScraping.info(jsonArrNews.toString());
	}
	
}
