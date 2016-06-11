package com.uspresidentials.project.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import entity.TweetsEntity;

public class LuceneTest {
	
//	final static String PATH_DEBATES = "/Users/alessiocampanelli/Desktop/debates";
    final static String PATH_DEBATES = "/home/felagund89/Scrivania/Progetto web and social/debates";
    private static IndexSearcher searcher = null;
    private static QueryParser parser = null;
    
	public static void main(String []args) throws CorruptIndexException, LockObtainFailedException, IOException, TwitterException, ParseException{
		
		
		
		
		createIndex();
		searchEngine();
		
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
		
		
		//LA RICERCA PER ORA AVVIENE SEMPRE SULLO STESSO FILE, IL 3 NEL MIO CASO
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
			while(count <= 1000){
				
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
				docLucene.add(new StringField("tweetText", element.getTweetStatus().getText().toLowerCase(),Field.Store.YES));
//				docLucene.add(new StringField("", element.getTweetStatus().getUser().getName(),Field.Store.YES));
				System.out.println("idTweet= "+element.getId()); //STAMPA DI PROVA
				//AGGIUNGERE ALTRI CAMPI UTILI PER LE RICERCHE
				
				listaDocLucene.add(docLucene);

			}
			
			indexWriter.addDocuments(listaDocLucene);
			System.out.println("indexWriter numero di doc lucene = " +indexWriter.numDocs());

			
			closeIndexWriter(indexWriter);			

		//}
	}
	

	
	
	/**
	 * QUA CI SONO DEGLI ESEMPI PER LE QUERY https://gist.github.com/mocobeta/4640268
	 * DIVERSI TIPI DI QUERY PER LUCENE
	 * 
	 * 
	 * 
	 */
		
	
    public static  void searchEngine() throws IOException, ParseException {
//		 Directory indexDir = FSDirectory.open(new File("/home/felagund89/Scrivania/Progetto web and social/debates/resultQuery"));

    	 searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File("/home/felagund89/Scrivania/Progetto web and social/debates/resultQuery"))));
//	     parser = new QueryParser("tweetText", new StandardAnalyzer());
   
	     //funziona cercando nel field language la parola en, ma se provo a cercare una parola nel contenuto tweetText trova sempre 0 risultati.
	     //bisogna vedere bene come cercare le parole
	     	Term t = new Term("tweetText", "trump");
			Query query = new TermQuery(t);
			TopDocs topDocs = searcher.search(query, 1000);
	     
	     
//	     TopDocs topDocs = performSearch("trump", 10); 
//	     
//	     // obtain the ScoreDoc (= documentID, relevanceScore) array from topDocs
	     ScoreDoc[] hits = topDocs.scoreDocs;
	     System.out.println("numero hits parola cercata = "+hits.length);
//	     
//	     // retrieve each matching document from the ScoreDoc array
//	     for (int i = 0; i < hits.length; i++) {
//	         Document doc = instance.getDocument(hits[i].doc);
//	         String tweetID = doc.get("name");
//	        
//	     }
    
    }

    public static TopDocs performSearch(String queryString, int n)throws IOException, ParseException {
        Query query = parser.parse(queryString);
        return searcher.search(query, n);
    }

    public Document getDocument(int docId)throws IOException {
        return searcher.doc(docId);
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
