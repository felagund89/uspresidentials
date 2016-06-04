package com.uspresidentials.project.test;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

public class LuceneTest {

	public static void main(String []args){
		
////		Directory dir= new RAMDirectory();
//		Directory dir= new SimpleFSDirectory(new File("index"));
//		Analyzer analyzer = new StandardAnalyzer(LUCENE_41);
//		IndexWriterConfig cfg= new IndexWriterConfig(LUCENE_41,analyzer);
//		IndexWriter writer = new IndexWriter(dir, cfg);
//		
//		Document doc = new Document();
//		
//		for(all documents) {
//			field.setLongValue(value of doc);
//			writer.addDocument(document);
//		}
//		writer.commit();
//		writer.close();
		
	}
	
}
