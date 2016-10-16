package com.uspresidentials.project.task4;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.queries.function.valuesource.DivFloatFunction;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ScrapeNews {

	private final static String link ="https://news.google.com/news/story?ncl=dzirMhj5Jge-9iMGB6zrye9MkG-zM&q=hillary+clinton&lr=English&hl=en&sa=X&ved=0ahUKEwjS1ISPjtHPAhWG6xoKHZmgDKkQqgIITTAH";
	static String number ="0";
	private static boolean firstTime=true;
	private static String LINK_STRING ="https://www.google.com/search?hl=en&gl=us&tbm=nws&authuser=0&q=hillary+clinton&oq=hillary+clinton&gs_l=news-cc.3..43j0l9j43i53.324553.326534.0.328574.15.7.0.8.8.0.130.603.5j2.7.0...0.0...1ac.1.QU4duoZIL4M#q=hillary+clinton&safe=off&hl=en&gl=us&authuser=0&tbm=nws&start=";
	final static Logger loggerScraping = Logger.getLogger("loggerScraping");

	public static void main(String[] args) {
		
		scrapeNewsFromGoole(link);
	}
	
	
	
	
	public static void scrapeNewsFromGoole(String linkString){
		loggerScraping.info("Scraping news for candidate: HILLARY CLINTON");
		Document doc = null;
		
		for (int i = 0; i <=300 ; i=i+10) {
			
			if(!firstTime){
				int val = Integer.parseInt(number);
				val+=10;
				number = String.valueOf(val);
				//LINK_STRING = LINK_STRING+number;
						}
			try {
				doc = Jsoup.connect(LINK_STRING+number).userAgent("Mozilla").get();
				//System.out.println(doc.toString());
				System.out.println("===========================================");
				loggerScraping.info("===========================================");
				Elements topics = doc.select("div[id=ires]");
				
				
				Elements stories = topics.select("div[class=g]");
				loggerScraping.info("TITOLI pag "+i/10);
		        for (Element story : stories) {
		            
//		            String sto = story.text();
		            Elements titles = story.select("h3[class=r]");
		            
	//	            Elements bodies = topic.select("span[class=titleText]");
	//	            String body = bodies.text();
		            
		            //prendere il body del messaggio che sta nel Div con class st
//		        	Element body = story.select("div[class=st]");

		            loggerScraping.info(titles.text());
		            System.out.println("Title: "+titles.text());
	//	            System.out.println("Body: "+body+"\n");
		        }
			
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			firstTime=false;
			doc=null;
		}
		
		
		loggerScraping.info("STOP SCRAPING NEWS");
		
	}
	
	
}
