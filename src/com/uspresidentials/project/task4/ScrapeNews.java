package com.uspresidentials.project.task4;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ScrapeNews {

	private final static String link ="https://news.google.com/news/story?ncl=dzirMhj5Jge-9iMGB6zrye9MkG-zM&q=hillary+clinton&lr=English&hl=en&sa=X&ved=0ahUKEwjS1ISPjtHPAhWG6xoKHZmgDKkQqgIITTAH";
	
	public static void main(String[] args) {
		
		scrapeNewsFromGoole(link);
	}
	
	
	
	
	public static void scrapeNewsFromGoole(String linkString){
		
		Document doc = null;
		try {
			doc = Jsoup.connect(linkString).userAgent("Mozilla").get();
			System.out.println(doc.toString());
			System.out.println("===========================================");

			Elements topics = doc.select("div[class=topic]");
			
			Elements stories = topics.select("div[class=story from-gxp cid-52779238483091 l-en   headline-story thumbnail-true ]");
	        for (Element story : stories) {
	            
	            String sto = story.text();
	            Elements titles = story.select("h2[class=title]");
//	            Elements bodies = topic.select("span[class=titleText]");
//	            String body = bodies.text();

	            System.out.println("Title: "+sto);
//	            System.out.println("Body: "+body+"\n");
	        }
		
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
	
	
}
