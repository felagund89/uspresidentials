package com.uspresidentials.project.lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.uspresidentials.project.entity.TweetInfoEntity;
import com.uspresidentials.project.entity.TweetsEntity;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

public class LuceneCore {


    private static IndexSearcher searcher = null;
    private static QueryParser parser = null;
    
	final static Logger logger = Logger.getLogger(LuceneCore.class);

    
    
    ArrayList<String> candidateStrings = new ArrayList<String>() {{
        add("Donald J. Trump");
        add("Hillary Clinton");
        add("Bernie Sanders");
        add("Marco Rubio");
        add("Ted Cruz");
        add("John Kasich");
    }};
	
	
	
	public static void createIndex(String pathDataset, String pathIndexer) throws CorruptIndexException, LockObtainFailedException, IOException, TwitterException {
			
			Directory indexDir = FSDirectory.open(new File(pathIndexer));
	
			IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, new StandardAnalyzer());
			IndexWriter indexWriter = new IndexWriter(indexDir, config);
			
	//		indexWriter.deleteAll();
	
			List<TweetsEntity> listaTweet = new ArrayList<TweetsEntity>();
			List<Document> listaDocLucene = new ArrayList<Document>();
			
			Document docLucene = new Document();     
			
			File dir = new File(pathDataset);
			File[] files = dir.listFiles();
			int numFile = files.length;
			File currentFile;
			String currentContent;
			
			
			//Creazione documenti per LUCENE
			//LA RICERCA PER ORA AVVIENE SEMPRE SULLO STESSO FILE, IL 3 NEL MIO CASO
			for(int j=0;j<516;j++){    //ciclo su ogni File del dataset
			
				currentFile = files[j];
				currentContent = readContentFile(currentFile);
		
				//scraping di un singolo oggetto in un file del dataset
				org.jsoup.nodes.Document docJsoup = Jsoup.parse(currentContent);
				
				Elements elementsP = docJsoup.select("p");					   
				Elements elementsT = docJsoup.select("t");					   
				Elements elementsL = docJsoup.select("l");		
				
				logger.info("<--- elementi nel file corrente : " + elementsP.size());
				
				for (int i = 0; i < elementsP.size(); i++) {   
	
					TweetsEntity tweetEnt = new TweetsEntity();
					tweetEnt.setId(elementsT.get(i).text());
					tweetEnt.setLanguage(elementsL.get(i).text());
	
					//creazione dell'oggetto Tweet4J passando in input il contenuto json del tag 'p'  - ora prendo il primo tweet
					String jsonContent = elementsP.get(i).text();           
					Status status = TwitterObjectFactory.createStatus(jsonContent);
	
					tweetEnt.setTweetStatus(status);
	//				listaTweet.add(tweetEnt);
					
					docLucene = new Document();
					docLucene.add(new StringField("idTweet", tweetEnt.getId(),Field.Store.YES));
					docLucene.add(new StringField("languageTweet", tweetEnt.getLanguage(),Field.Store.YES));
					docLucene.add(new TextField("tweetUser", tweetEnt.getTweetStatus().getUser().getName().toString().toLowerCase(),Field.Store.YES));
					docLucene.add(new TextField("tweetText", tweetEnt.getTweetStatus().getText().toString().toLowerCase(),Field.Store.YES ));
			
					
						
					//TODO: AGGIUNGERE ALTRI CAMPI UTILI PER LE RICERCHE
					
	//				listaDocLucene.add(docLucene);
					indexWriter.addDocument(docLucene);
				}
	
	//			indexWriter.addDocuments(listaDocLucene);
				indexWriter.commit();
				logger.info("indexWriter numero di doc lucene = " + indexWriter.numDocs());
				logger.info(516 -j +"--->");

	
	
			}
			closeIndexWriter(indexWriter);			
			logger.info("fine creazione documenti per lucene");

			
			
	
		}
		
	
	public static IndexSearcher getIndexSearcher(String pathIndexer) throws IOException{
	 	IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(pathIndexer))));
	 	return searcher;
	}
	
	
	
	public static  TopDocs searchEngine(String pathIndexer, String fieldForQuery, String queryLucene) throws IOException, ParseException {
    	
 	       	    
	 	IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(pathIndexer))));

		
 	    QueryParser qp = new QueryParser(fieldForQuery, new StandardAnalyzer());
 	    
 	    Query q1 = qp.parse(queryLucene);
 	    TopDocs hits = searcher.search(q1, 10000000);
 	    
		logger.info("##### "+hits.totalHits + " Docs found for the query \"" + q1.toString() + "\"");

		
 	    
 	    int num = 0;
 	    for (ScoreDoc sd : hits.scoreDocs) {
 	      Document d = searcher.doc(sd.doc);
 			logger.info(String.format("#%d: %s (rating=%s) - user: %s - tweet: %s", ++num, d.get("idTweet"), sd.score, d.get("tweetUser"), d.get("tweetText")));
 			
 		
 	    }
 	    
 	    
 	    	    
 	    return hits;
	 }
	 
	 public static TopDocs performSearch(String queryString, int n)throws IOException, ParseException {
	     Query query = parser.parse(queryString);
	     return searcher.search(query, n);
	 }
	
	 public Document getDocument(int docId)throws IOException {
	     return searcher.doc(docId);
	 }
	 
	 
	 
	 public static Set<String> numberOfUser (IndexSearcher searcher, TopDocs resultDocs) throws IOException{
			

		 Set<String> uniqueUsers = new HashSet<String>();

		 for (ScoreDoc sd : resultDocs.scoreDocs) {
	 	      Document d = searcher.doc(sd.doc);
	 	      
	 	     uniqueUsers.add(d.getField("tweetUser").stringValue());
	 		
	 	    }
		 

	 	 logger.info("##### Number of different user in this set of documents:" +uniqueUsers.size() );

			
		 return uniqueUsers;
			
			
		}
	 
	 
	 
	 public static long numberOfTweets (IndexSearcher searcher, TopDocs resultDocs) throws IOException{
			
		 long numOfTweet;

		 numOfTweet= resultDocs.totalHits;

	 	 logger.info("##### Number of tweet in this set of documents:" +numOfTweet );

			
		 return numOfTweet;
			
			
	}
	 
	 
	public static TweetInfoEntity getUserAndRelTweets(Set<String> usersName, TopDocs resultDocs) throws IOException, ParseException{
		
		TweetInfoEntity userAndTweets = new TweetInfoEntity();
		
		
		QueryParser qp = new QueryParser("tweetUser", new StandardAnalyzer());
 	    
		
// 	   
//		
//		
//		TopDocs hits
//		
//		 for (ScoreDoc sd : resultDocs.scoreDocs) {
//			 
//	 	     Document d = searcher.doc(sd.doc);
//
//			 Query q1 = qp.parse(d.getField("tweetUser").stringValue());
//		 	  hits = searcher.search(q1, 10000000);
//			 
//		 	 
//	 	     
//	 		
//	 	    }
		 

//	 	 logger.info("##### Number of different user in this set of documents:" +uniqueUsers.size() );
		
		
		
		
		
		return userAndTweets;
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
	
	
	public static void closeIndexWriter(IndexWriter indexWriter) throws IOException {
	    if (indexWriter != null) {
	        indexWriter.close();
	    }
	}
	
}
