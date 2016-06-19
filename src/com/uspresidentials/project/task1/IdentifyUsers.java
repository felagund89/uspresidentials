package com.uspresidentials.project.task1;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.LockObtainFailedException;

import twitter4j.TwitterException;

import com.uspresidentials.project.lucene.LuceneCore;




public class IdentifyUsers {

	/**
	 * PATH per dataset e indexer
	 */	
	//  final static String PATH_DEBATES = "/Users/alessiocampanelli/Desktop/debates";
	//  final static String PATH_INDEXDIR = "/Users/alessiocampanelli/Desktop/resultQuery";

	//final static String PATH_DEBATES = "/home/felagund89/Scrivania/Progetto web and social/debates";
	final static String PATH_PRIMARY = "/home/felagund89/Scrivania/Progetto web and social/DOCPRIMARYNY";

	//final static String PATH_INDEXDIR = "/home/felagund89/Scrivania/Progetto web and social/resultQuery/resultQueryDebates";
	final static String PATH_INDEXDIR_PRIMAR = "/home/felagund89/Scrivania/Progetto web and social/resultQuery/resultQueryPrimary";

		

	/**
	 * QUERY
	 */
	final static String QUERY_STRING_CANDIDATES_NAME_STRING ="donald* OR hillary* OR rubio* OR trump* OR clinton*";

	final static Logger logger = Logger.getLogger(IdentifyUsers.class);


	public static void main(String[] args) throws CorruptIndexException, LockObtainFailedException, IOException, TwitterException, ParseException {
		
		
		 ArrayList<String> candidateStrings = new ArrayList<String>() {{
		        add("Donald J. Trump");
		        add("Hillary Clinton");
		        add("Bernie Sanders");
		        add("Marco Rubio");
		        add("Ted Cruz");
		        add("John Kasich");
		    }};
		
		
	
		
			//Richiamo l'indexer, commentare se già fatto
//			LuceneCore.createIndex(PATH_PRIMARY, PATH_INDEXDIR_PRIMAR);
		
		
			//Richiamo il searcher
			LuceneCore.searchEngine(PATH_INDEXDIR_PRIMAR, "tweetText", QUERY_STRING_CANDIDATES_NAME_STRING);
			
		

		
	}
	
	
}
