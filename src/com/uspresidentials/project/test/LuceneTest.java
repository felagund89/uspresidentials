package com.uspresidentials.project.test;

import java.io.File;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class LuceneTest {

	public static void main(String []args){
		
		//commit by Alessio
		
//		Directory dir= new RAMDirectory();
//		Directory dir= new SimpleFSDirectory(new File("index"));
//		Analyzer analyzer = new StandardAnalyzer(LUCENE_41);
//		IndexWriterConfig cfg= new IndexWriterConfig(LUCENE_41,analyzer);
//		IndexWriter writer = new IndexWriter(dir, cfg);
		String indexDir = args[0];
        int numHits = Integer.parseInt(args[1]);

		LuceneTest tweetSearcher = new LuceneTest();
        try {
			tweetSearcher.wildcardQuery(new File(indexDir), numHits);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		Document doc = new Document();
//		
//		for(all documents) {
//			field.setLongValue(value of doc);
//			writer.addDocument(document);
//		}
//		writer.commit();
//		writer.close();
		
	}
	
	
	 private void wildcardQuery(File indexDir, int numHits) throws Exception {
	        System.out.println("Find tweets that mention another user:");

	        Directory directory = FSDirectory.open(indexDir);
	        DirectoryReader directoryReader = DirectoryReader.open(directory);
	        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

	        Term term = new Term(TweetIndexer.TEXT, "*@*");
	        Query query = new WildcardQuery(term);

	        TopDocs topDocs = indexSearcher.search(query, numHits);

//	        System.out.println(topDocs.scoreDocs, indexSearcher);
	    }	
	
	
	
	
	
}
