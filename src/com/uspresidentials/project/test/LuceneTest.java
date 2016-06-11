package com.uspresidentials.project.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

public class LuceneTest {
	
	final static String PATH_DEBATES = "/Users/alessiocampanelli/Desktop/debates";
  //final static String PATH_DEBATES = "/home/felagund89/Scrivania/Progetto web and social/debates";
	
	public static void main(String []args) throws CorruptIndexException, LockObtainFailedException, IOException, TwitterException{
		createIndex();
	}
	
	public static void createIndex() throws CorruptIndexException, LockObtainFailedException, IOException, TwitterException {
		
		Analyzer analyzer = new StandardAnalyzer();	
		Directory indexDir = FSDirectory.open(new File("/Users/alessiocampanelli/Desktop/resultQuery"));
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, new StandardAnalyzer());
		IndexWriter indexWriter = new IndexWriter(indexDir, config);

		Document doc = new Document();     //Lucene document
		
		File dir = new File(PATH_DEBATES);
		File[] files = dir.listFiles();
		
		File currentFile;
		String currentContent;
		
		//for(int i=0;i<files.length;i++){    //ciclo su ogni File del dataset
		
			currentFile = files[1];
			currentContent = readContentFile(currentFile);
	
			//scraping di un singolo oggetto in un file del dataset
			org.jsoup.nodes.Document docJsoup = Jsoup.parse(currentContent);
			Elements newsHeadlines = docJsoup.select("p");					   
			
			//creazione dell'oggetto Tweet4J passando in input il contenuto json del tag 'p'  - ora prendo il primo tweet
			String jsonContent = newsHeadlines.get(0).text();                 
			Status status = TwitterObjectFactory.createStatus(jsonContent);
		//}
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
