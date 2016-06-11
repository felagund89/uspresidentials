package com.uspresidentials.project.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import entity.TweetsEntity;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

public class LuceneTest {
	
//	final static String PATH_DEBATES = "/Users/alessiocampanelli/Desktop/debates";
    final static String PATH_DEBATES = "/home/felagund89/Scrivania/Progetto web and social/debates";
	
    
	public static void main(String []args) throws CorruptIndexException, LockObtainFailedException, IOException, TwitterException{
		
		createIndex();
	
	
	}
	
	public static void createIndex() throws CorruptIndexException, LockObtainFailedException, IOException, TwitterException {
		
		Analyzer analyzer = new StandardAnalyzer();	
//		Directory indexDir = FSDirectory.open(new File("/Users/alessiocampanelli/Desktop/resultQuery"));
		Directory indexDir = FSDirectory.open(new File("/home/felagund89/Scrivania/Progetto web and social/debates/resultQuery"));

		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, new StandardAnalyzer());
		IndexWriter indexWriter = new IndexWriter(indexDir, config);

		List<TweetsEntity> listaTweet = new ArrayList<TweetsEntity>();
		List<Document> listaDocLucene = new ArrayList<Document>();
		
		Document docLucene = new Document();     //Lucene document
		
		File dir = new File(PATH_DEBATES);
		File[] files = dir.listFiles();
		
		File currentFile;
		String currentContent;
		
		//for(int i=0;i<files.length;i++){    //ciclo su ogni File del dataset
		
			currentFile = files[3];
			currentContent = readContentFile(currentFile);
	
			//scraping di un singolo oggetto in un file del dataset
			org.jsoup.nodes.Document docJsoup = Jsoup.parse(currentContent);
			Elements elementsP = docJsoup.select("p");					   
			Elements elementsT = docJsoup.select("t");					   
			Elements elementsL = docJsoup.select("l");					   

			      
			
			//Compongo oggetto tweetsEntity
			int countElements = elementsP.size();
			int count=0;
			while(count <= 500){
				
				TweetsEntity tweetEnt = new TweetsEntity();
				tweetEnt.setId(elementsT.get(count).text());
				tweetEnt.setLanguage(elementsL.get(count).text());

				//creazione dell'oggetto Tweet4J passando in input il contenuto json del tag 'p'  - ora prendo il primo tweet
				String jsonContent = elementsP.get(count).text();           
				Status status = TwitterObjectFactory.createStatus(jsonContent);

				tweetEnt.setTweetStatus(status);
				listaTweet.add(tweetEnt);
				count++;
				
			}
			
			
			//Creazione documenti per lucene
			for (TweetsEntity element : listaTweet) {
				
				docLucene.add(new StringField("idTweet", element.getId(),Field.Store.YES));
				docLucene.add(new StringField("languageTweet", element.getLanguage(),Field.Store.YES));
				docLucene.add(new StringField("tweetUser", element.getTweetStatus().getUser().getName(),Field.Store.YES));
				docLucene.add(new StringField("tweetText", element.getTweetStatus().getText(),Field.Store.YES));
//				docLucene.add(new StringField("", element.getTweetStatus().getUser().getName(),Field.Store.YES));
				
				//AGGIUNGERE ALTRI CAMPI UTILI PER LE RICERCHE
				
				indexWriter.addDocument(docLucene);

			}
			
			System.out.println("indexWriter numero di doc lucene = " +indexWriter.numDocs());
			
		//}
	}
	
	
	public void indexTweetsEntity(List<TweetsEntity> listaTweet){
		
		
		
		
		
	}
	
	
	
	
	public static String readContentFile(File file) throws IOException{
		
		String content = null;
		FileReader reader = null;
		
		try {
	        reader = new FileReader(file);
	        char[] chars = new char[(int) file.length()];
	        reader.read(chars);
	        content = new String(chars);
	        reader.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        if(reader !=null){
	        	reader.close();
	        	}
	    }
	    return content;
	}
}
