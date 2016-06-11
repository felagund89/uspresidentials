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

public class LuceneTest {
	
	final static String PATH_DEBATES = "/Users/alessiocampanelli/Desktop/debates";
	
	public static void main(String []args) throws CorruptIndexException, LockObtainFailedException, IOException{
		
		//commit by Alessio
		createIndex();
	}
	
	public static void createIndex() throws CorruptIndexException, LockObtainFailedException, IOException {
		
		Analyzer analyzer = new StandardAnalyzer();	
		Directory indexDir = FSDirectory.open(new File("/Users/alessiocampanelli/Desktop/resultQuery"));
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, new StandardAnalyzer());
		IndexWriter indexWriter = new IndexWriter(indexDir, config);

		Document doc = new Document();
		
		File dir = new File(PATH_DEBATES);
		File[] files = dir.listFiles();
		
		File currentFile;
		String currentContent;
		
		//for(int i=0;i<files.length;i++){
			currentFile = files[0];
			currentContent = readContentFile(currentFile);
			System.out.println("currentContent: " + currentContent);
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
