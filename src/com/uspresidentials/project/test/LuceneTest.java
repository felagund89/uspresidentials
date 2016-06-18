package com.uspresidentials.project.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
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

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import entity.TweetsEntity;

public class LuceneTest {
	
//  final static String PATH_DEBATES = "/Users/alessiocampanelli/Desktop/debates";
//  final static String PATH_INDEXDIR = "/Users/alessiocampanelli/Desktop/resultQuery";

final static String PATH_DEBATES = "/home/felagund89/Scrivania/Progetto web and social/debates";
final static String PATH_PRIMARY = "/home/felagund89/Scrivania/Progetto web and social/DOCPRIMARYNY";

final static String PATH_INDEXDIR = "/home/felagund89/Scrivania/Progetto web and social/resultQuery/resultQueryDebates";
final static String PATH_INDEXDIR_PRIMAR = "/home/felagund89/Scrivania/Progetto web and social/resultQuery/resultQueryPrimary";

    private static IndexSearcher searcher = null;
    private static QueryParser parser = null;
    
    ArrayList<String> candidateStrings = new ArrayList<String>() {{
        add("Donald J. Trump");
        add("Hillary Clinton");
        add("Bernie Sanders");
        add("Marco Rubio");
        add("Ted Cruz");
        add("John Kasich");
    }};
    
	public static void main(String []args) throws CorruptIndexException, LockObtainFailedException, IOException, TwitterException, ParseException{
		
		createIndex();
//		searchEngine();	
	}
	
	public static void createIndex() throws CorruptIndexException, LockObtainFailedException, IOException, TwitterException {
		
		Directory indexDir = FSDirectory.open(new File(PATH_INDEXDIR_PRIMAR));

		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, new StandardAnalyzer());
		IndexWriter indexWriter = new IndexWriter(indexDir, config);
		
//		indexWriter.deleteAll();

		List<TweetsEntity> listaTweet = new ArrayList<TweetsEntity>();
		List<Document> listaDocLucene = new ArrayList<Document>();
		
		Document docLucene = new Document();     
		
		File dir = new File(PATH_PRIMARY);
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
			System.out.println("<--- elementi nel file corrente : " + elementsP.size());
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
		
				
//				if(tweetEnt.getTweetStatus().getText().toString().toLowerCase().contains("trump")){
//					System.out.println("contiene trump= "+ tweetEnt.getTweetStatus().getText().toLowerCase()); //STAMPA DI PROVA
//				}
				
				//TODO: AGGIUNGERE ALTRI CAMPI UTILI PER LE RICERCHE
				
//				listaDocLucene.add(docLucene);
//				System.out.println("docLucene aggiunto --->"+currentFile.getName());
				indexWriter.addDocument(docLucene);
			}

//			indexWriter.addDocuments(listaDocLucene);
//			System.out.println("aggiunti doc di lucene all'indexwriter");
			indexWriter.commit();
//			System.out.println("commit dell'indexwriter effettuato");
			System.out.println("indexWriter numero di doc lucene = " + indexWriter.numDocs());
			System.out.println(516 -j +"--->");

		}
		closeIndexWriter(indexWriter);			
		System.out.println("fine creazione documenti per lucene");

	}
		
	/* QUA CI SONO DEGLI ESEMPI PER LE QUERY https://gist.github.com/mocobeta/4640268 */
	
    public static  void searchEngine() throws IOException, ParseException {
    	
    	    IndexSearcher searcher = createSearcher();
    	       	    
    	    QueryParser qp = new QueryParser("tweetText", new StandardAnalyzer());
    	    System.out.println("ha creato l'analyzer");
    	    
    	    String qstring = "trump* OR clinton*";
    	    Query q1 = qp.parse(qstring);
    	    TopDocs hits = searcher.search(q1, 1000);
    	    
    	    System.out.println(hits.totalHits + " docs found for the query \"" + q1.toString() + "\"");
    	    int num = 0;
    	    for (ScoreDoc sd : hits.scoreDocs) {
    	      Document d = searcher.doc(sd.doc);
    	      System.out.println(String.format("#%d: %s (rating=%s) - user: %s - tweet: %s", ++num, d.get("idTweet"), sd.score, d.get("tweetUser"), d.get("tweetText")));
    	    }
    }
    
    public static TopDocs performSearch(String queryString, int n)throws IOException, ParseException {
        Query query = parser.parse(queryString);
        return searcher.search(query, n);
    }

    public Document getDocument(int docId)throws IOException {
        return searcher.doc(docId);
    }
		
	//Creo un searcher
    private static IndexSearcher createSearcher() throws IOException {
    	IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(PATH_INDEXDIR_PRIMAR))));
        return searcher;
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
