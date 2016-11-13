package com.uspresidentials.project.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.User;

import com.uspresidentials.project.entity.TweetInfoEntity;
import com.uspresidentials.project.entity.TweetsEntity;
import com.uspresidentials.project.entity.WordEntity;

public class LuceneCore {


    private static IndexSearcher searcher = null;
    private static QueryParser parser = null;
    
	final static Logger loggerUsersAndTweets = Logger.getLogger("usersAndTweets");
	final static Logger loggerOccurrenceMentions = Logger.getLogger("loggerOccurrenceMentions");

	final static Logger logger = Logger.getLogger("default");
	
	Set<User> userList = null;

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
			
			//indexWriter.deleteAll();
	
			List<TweetsEntity> listaTweet = new ArrayList<TweetsEntity>();
			List<Document> listaDocLucene = new ArrayList<Document>();
			
			Document docLucene = new Document();     
			
			File dir = new File(pathDataset);
			File[] files = dir.listFiles();
			File currentFile;
			String currentContent;
			
			//Creazione documenti per LUCENE
			for(int j=0;j<files.length;j++){    //ciclo su ogni File del dataset
			
				currentFile = files[j];
				currentContent = readContentFile(currentFile);
					
				//scraping di un singolo oggetto in un file del dataset
				org.jsoup.nodes.Document docJsoup = Jsoup.parse(currentContent);
				
				Elements elementsP = docJsoup.select("p");					   
				Elements elementsT = docJsoup.select("t");					   
				Elements elementsL = docJsoup.select("l");		
				
				//logger.info("<--- elementi nel file corrente : " + elementsP.size());
				
				for (int i = 0; i < elementsP.size(); i++) {   
	
					TweetsEntity tweetEnt = new TweetsEntity();
					tweetEnt.setId(elementsT.get(i).text());
					tweetEnt.setLanguage(elementsL.get(i).text());
	
					//creazione dell'oggetto Tweet4J passando in input il contenuto json del tag 'p'  - ora prendo il primo tweet
					String jsonContent = elementsP.get(i).text();           
					Status status = TwitterObjectFactory.createStatus(jsonContent);
				
					//User currentUser = TwitterObjectFactory.createUser(jsonContent);
					//logger.info("currentUser: " + status.getUser().getStatus().);
				
					tweetEnt.setTweetStatus(status);
	//				listaTweet.add(tweetEnt);
					
					docLucene = new Document();
					
					docLucene.add(new StringField("idTweet", tweetEnt.getId(),Field.Store.YES));
					docLucene.add(new StringField("languageTweet", tweetEnt.getLanguage(),Field.Store.YES));
					docLucene.add(new TextField("tweetUser", tweetEnt.getTweetStatus().getUser().getName().toString().toLowerCase(),Field.Store.YES));
					docLucene.add(new LongField("tweetUserId", tweetEnt.getTweetStatus().getUser().getId(),Field.Store.YES)); //aggiunto il campo id
					docLucene.add(new TextField("tweetText", tweetEnt.getTweetStatus().getText().toString().toLowerCase(),Field.Store.YES));
					
					//aggiunto l'index per il campo tweetText 7/novembre
					FieldType type = new FieldType();
					type.setIndexed(true);
					type.setStored(true);
					type.setStoreTermVectors(true);
					Field field = new Field("tweetTextIndexed", tweetEnt.getTweetStatus().getText().toString().toLowerCase(), type);
					docLucene.add(field);
					
						
					//TODO: AGGIUNGERE ALTRI CAMPI UTILI PER LE RICERCHE
					
	//				listaDocLucene.add(docLucene);
					indexWriter.addDocument(docLucene);
				}
	
	//			indexWriter.addDocuments(listaDocLucene);
				indexWriter.commit();
				//logger.info("indexWriter numero di doc lucene = " + indexWriter.numDocs());
				//logger.info(files.length -j +"--->");	
				System.out.println("indexWriter numero di doc lucene = " + indexWriter.numDocs());

			}
			
			closeIndexWriter(indexWriter);			
			System.out.println("fine creazione documenti per lucene");

			//logger.info("fine creazione documenti per lucene");
		}
		
	
	public static IndexSearcher getIndexSearcher(String pathIndexer) throws IOException{
	 	IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(pathIndexer))));
	 	return searcher;
	}
	
	
	
	public static  TopDocs searchEngine(String pathIndexer, String fieldForQuery, String queryLucene) throws IOException, ParseException {
    	
 	       	    
	 	//IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(pathIndexer))));
		searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(pathIndexer))));
		
 	    QueryParser qp = new QueryParser(fieldForQuery, new StandardAnalyzer());
 	    
 	    Query q1 = qp.parse(queryLucene);
 	    TopDocs hits = searcher.search(q1, 100000);
 	    
		loggerUsersAndTweets.info("##### "+hits.totalHits + " Docs found for the query \"" + q1.toString() + "\"");

 	    int num = 0;
 	    for (ScoreDoc sd : hits.scoreDocs) {
 	      Document d = searcher.doc(sd.doc);
 	     loggerUsersAndTweets.info(String.format("#%d: %s (rating=%s) - user: %s - tweet: %s", ++num, d.get("idTweet"), sd.score, d.get("tweetUser"), d.get("tweetText")));
 	     loggerUsersAndTweets.info("==================================================================================================================");
 		
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
	 	 logger.info("##### Number of tweet in this set of documents:" + numOfTweet);

		 return numOfTweet;			
	}
	 
	public static HashMap<String, ArrayList<String>> getUserAndRelTweets(Set<String> usersName, TopDocs resultDocs, String pathFileUtenti, boolean isWriteOnFile) throws IOException, ParseException{
		
		TweetInfoEntity userAndTweets = new TweetInfoEntity();
		TopDocs hits;
		PrintWriter writer=null;
		
		if(isWriteOnFile)
			writer = new PrintWriter(pathFileUtenti, "UTF-8");
		   
		loggerUsersAndTweets.info("UTENTI E RELATIVI TWEETS");
		        	
		HashMap<String, ArrayList<String>> hashMapUser = new HashMap<String, ArrayList<String>>();
		String currentUserName = null;
		String currentTweet = null;
		ArrayList<String> tempArrayTweets = null;
		
		QueryParser qp = new QueryParser("tweetUser", new StandardAnalyzer());
		for (ScoreDoc sd : resultDocs.scoreDocs) {
			
			Document d = searcher.doc(sd.doc);
			currentUserName =  d.getField("tweetUser").stringValue()+";"+d.getField("tweetUserId").stringValue(); //aggiunto l'id
			currentTweet = d.getField("tweetText").stringValue();
			
			if(!hashMapUser.containsKey(currentUserName)){
				tempArrayTweets = new ArrayList<String>();
				tempArrayTweets.add(currentTweet);
				hashMapUser.put(currentUserName, tempArrayTweets);
				
				//loggerUsersAndTweets.info("appena aggiunto user: " + currentUserName);
				if(isWriteOnFile)
					writer.println((currentUserName+";"));   //salvo anche su file la lista di utenti/idutente
				
			}else{
				hashMapUser.get(currentUserName).add(currentTweet);
				//loggerUsersAndTweets.info("aggiunto tweet for user: " + currentUserName);
			}
			
//			printHashMap(hashMapUser);
			
//			logger.info("L'utente: "+)
		}
		
		if(isWriteOnFile)
			writer.close();

	    Iterator iterator = hashMapUser.keySet().iterator();

	    while (iterator.hasNext()) {
	       String key = iterator.next().toString();
	       loggerUsersAndTweets.info(key + ": ");

	       ArrayList<String> value = hashMapUser.get(key);
	       for (String string : value) {
	    	   loggerUsersAndTweets.info("- "+string);
	       }
	    }
	    
	    System.out.println(hashMapUser.size());
	    
		return hashMapUser;
		//return userAndTweets;
	}
	 
	
	public static void occurrenceCandidates(String pathFileUtentiFiltrati, String pathFileOccurrenceCandidates, String pathIndexer) throws  ParseException, IOException{
		
//		PrintWriter writer=null;
		
//		writer = new PrintWriter(pathFileOccurrenceCandidates, "UTF-8");
		   
		        	
		
		searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(pathIndexer))));

		QueryParser qp = new QueryParser("tweetText", new StandardAnalyzer());
	 	
 	    Query qTrump = qp.parse("trump* OR donald* OR donaldtrump* OR trumpdonald*");
 	    Query qClinton = qp.parse("clinton* hillary* OR hillaryclinton* OR clintonhillary*");
 	    Query qRubio = qp.parse("rubio* OR marco* OR marcorubio* OR rubiomarco*");
 	    Query qSanders = qp.parse("sanders* OR bernie* OR sandersbernie* OR berniesanders*");

 	    TopDocs hitsTrump = searcher.search(qTrump, 50000);
 	    TopDocs hitsClinton = searcher.search(qClinton, 50000);
 	    TopDocs hitsRubio = searcher.search(qRubio, 50000);
 	    TopDocs hitsSanders = searcher.search(qSanders, 50000);
		

			//apro il file con gli utenti e verifico se presente in lista
		try (BufferedReader br = new BufferedReader(new FileReader(pathFileUtentiFiltrati))) {
				String line;

				while ((line = br.readLine()) != null) {
						String occurrenceTotalString = findOccurrence(hitsTrump, hitsClinton, hitsRubio, hitsSanders,line);
//						System.out.println(occurrenceTotalString);
//						writer.println(occurrenceTotalString);
						loggerOccurrenceMentions.info(occurrenceTotalString);
					
				
				}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		writer.close();

	   
	}
	
	
	public static String findOccurrence(TopDocs scoreDocTrump,TopDocs scoreDocClinton, TopDocs scoreDocRubio,TopDocs scoreDocSanders, String currentUser) throws ParseException,IOException {
		
		int occHillary = 0;
		int occTrump = 0;
		int occRubio = 0;
		int occSanders = 0;
		

		for (ScoreDoc sd : scoreDocTrump.scoreDocs) {
			Document d = searcher.doc(sd.doc);
			String usernameOfDocString =d.getField("tweetUserId").stringValue();
			if (currentUser.contains(usernameOfDocString)){
				occTrump++;
			}
		}

		for (ScoreDoc sd : scoreDocClinton.scoreDocs) {
			Document d = searcher.doc(sd.doc);
			String usernameOfDocString =d.getField("tweetUserId").stringValue();
			if (currentUser.contains(usernameOfDocString)){
				occHillary++;
			}
		}
		for (ScoreDoc sd : scoreDocRubio.scoreDocs) {
			Document d = searcher.doc(sd.doc);
			String usernameOfDocString =d.getField("tweetUserId").stringValue();
			if (currentUser.contains(usernameOfDocString)){
				occRubio++;
			}
		}
		for (ScoreDoc sd : scoreDocSanders.scoreDocs) {
			Document d = searcher.doc(sd.doc);
			String usernameOfDocString =d.getField("tweetUserId").stringValue();
			if (currentUser.contains(usernameOfDocString)){
				occSanders++;
			}
		}

		return currentUser+";Trump:" + occTrump+ " Clinton:" + occHillary + " Rubio:" + occRubio + " Sanders:"+ occSanders;
		//return currentUser + "; mentionsCandidates:" + "[ Trump:" + occTrump+ " Clinton:" + occHillary + " Rubio:" + occRubio + " Sanders:"+ occSanders + "]";

	}
	
	
	
	//Cerco tutti i termini e la loro frequenza nei documenti tirati fuori da lucene.
	public static  Set<WordEntity> getTerms(String pathIndexer, String fieldForQuery, String queryLucene) throws IOException, ParseException {
    	IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(pathIndexer))); 
		searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(pathIndexer))));

        Set<WordEntity> words = new HashSet<>();
        
 	    QueryParser qp = new QueryParser(fieldForQuery, new StandardAnalyzer());
 	    
 	    Query q1 = qp.parse(queryLucene);
 	    TopDocs hits = searcher.search(q1, 1000);
 	    
//		loggerUsersAndTweets.info("##### "+hits.totalHits + " Docs found for the query \"" + q1.toString() + "\"");

 	    int num = 0;
 	    Map<String, Integer> frequencies = new HashMap<>();
 	    
 	    //Nel primo elemento della mappa inserisco il numero dei documenti trovati per quel determinato candidato.
 	    //gli elementi successivi della mappa sono tutti i termini trovati e la loro frequenza in tutti i documenti
 	    //frequencies.put("NumberOfDocs", hits.totalHits); non serve
 	    for (ScoreDoc sd : hits.scoreDocs) {
 	    	Document d = searcher.doc(sd.doc);
 	        boolean countDoc=false;
 	        
 	        //VEDERE COME APPLICARE L ALGORITMO PER TOGLIERE LE PAROLE INUTILI
	 	    Terms vector = reader.getTermVector(sd.doc, "tweetTextIndexed");
	 		TermsEnum termsEnum = null;
	 		termsEnum = vector.iterator(termsEnum);
	 		
	 		//aggiungere il numero di volte che la parola è contenuta piu volte nello stesso tweet
	 		BytesRef text = null;
	 		while ((text = termsEnum.next()) != null) {
	 		    boolean wordIsPresent = false;
	 		    String term = text.utf8ToString();
	 		   //se la parola è gia contenuta nel set, aggiorno i dati relativi a quella parola, altrimenti la aggiungo al set con i dati
	 		   for (Iterator<WordEntity> it = words.iterator(); it.hasNext(); ) {
	 		        WordEntity w = it.next();
	 		        if (w.getWord().equalsIgnoreCase(term)){
	 		        	w.setTotalOcc(w.getTotalOcc()+1);
	 		        	if(!countDoc){
	 		        		w.setNumDocOcc(w.getNumDocOcc()+1);
	 		        		countDoc=true;
	 		        	}
	 		        	//aggiorno il valore booleano indicando che la parola è presente nel set ed è stata solo aggiornata
	 		        	wordIsPresent=true;        	
	 		        }  	
	 		    }	 		    
	 		   //se la parola non era presenta nel set la aggiungo, con frequenza 1 e occorrenza 1
	 		   if(!wordIsPresent){
		 		    WordEntity word = new WordEntity();

		 		    word.setWord(term);
		 		    word.setNumDocOcc(1.0);
		 		    word.setTotalOcc(1.0);
		 		    words.add(word);
	 		   }
	 		   
	 		   
	 		   //passo alla parola successiva
	 
	 		    
	 		}
 	      
 	      
 	    }
 	    System.out.println("fine getTerms"); 	    
 	    return words;
	 }
	
	public static ScoreDoc[] getTweetsCoreForSentiment(String pathIndexer, String fieldForQuery, String queryLucene) throws IOException, ParseException {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(pathIndexer))); 
		searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(pathIndexer))));

        Set<String> terms = new HashSet<>();
        
 	    QueryParser qp = new QueryParser(fieldForQuery, new StandardAnalyzer());
 	    
 	    Query q1 = qp.parse(queryLucene);
 	    TopDocs hits = searcher.search(q1, 100000);
 	   
 	    return hits.scoreDocs;
 	    /*for (ScoreDoc sd : hits.scoreDocs) {
	    	Document d = searcher.doc(sd.doc);
	    	String tweetCleaned = deleteUnnecessaryWords(d.get("tweetText"));
	    	System.out.println("tweet cleaned: " + tweetCleaned + " for user: " + d.get("tweetUser"));
	    	
 	   } */
 	   
 	   //return array with tweets (for each candidates)
 	   //filter unnecessary word
 	   //apply SentimentWordNet (attention for negation not-good / not bad)
	}
	
	private static void printHashMap(HashMap<String, ArrayList<String>> hashMapUser){
		
		for (String key : hashMapUser.keySet()) {
		    // gets the value
		    List<String> value = hashMapUser.get(key);
		    // checks for null value
//		    if (value != null) {
		        // iterates over String elements of value
//		        for (Object element : value) {
		            // checks for null 
//		            if (element != null) {
		                // prints whether the key is equal to the String 
		                // representation of that List's element
//		                System.out.println(key.equals(element.toString()));
		        	logger.info("L'utente :"+key.toString()+" ha scritto "+value.size()+" tweet.");
//		            }
//		        }
//		    }
		}	
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
	
	

	/** PROVA PROVA PROVA
	*  Map term to a fix integer so that we can build document matrix later.
	*  It's used to assign term to specific row in Term-Document matrix
	*/
	public static Map<String, Integer> computeTermIdMap(String pathIndexer) throws IOException {
	   
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(pathIndexer))); 

	    searcher = new IndexSearcher(reader);
		Map<String,Integer> termIdMap = new HashMap<String,Integer>();
	    int id = 0;
	    Fields fields = MultiFields.getFields(reader);
	    Terms terms = fields.terms("tweetTextIndexed");
	    TermsEnum itr = terms.iterator(null);
	    BytesRef term = null;
	    while ((term = itr.next()) != null) {               
	        String termText = term.utf8ToString();              
	        if (termIdMap.containsKey(termText))
	            continue;
	        //System.out.println(termText); 
	        termIdMap.put(termText, id++);
	        System.out.println(termText +" "+ id); 

	    }

	    return termIdMap;
	}

	/**
	*  build term-document matrix for the given directory
	*/
//	public RealMatrix buildTermDocumentMatrix () throws IOException {
//	    //iterate through directory to work with each doc
//	    int col = 0;
//	    int numDocs = countDocs(corpus);            //get the number of documents here      
//	    int numTerms = termIdMap.size();    //total number of terms     
//	    RealMatrix tdMatrix = new Array2DRowRealMatrix(numTerms, numDocs);
//
//	    for (File f : corpus.listFiles()) {
//	        if (!f.isHidden() && f.canRead()) {
//	            //I build term document matrix for a subset of corpus so
//	            //I need to lookup document by path name. 
//	            //If you build for the whole corpus, just iterate through all documents
//	            String path = f.getPath();
//	            BooleanQuery pathQuery = new BooleanQuery();
//	            pathQuery.add(new TermQuery(new Term("path", path)), BooleanClause.Occur.SHOULD);
//	            TopDocs hits = searcher.search(pathQuery, 1);
//
//	            //get term vector
//	            Terms termVector = reader.getTermVector(hits.scoreDocs[0].doc, "contents");
//	            TermsEnum itr = termVector.iterator(null);
//	            BytesRef term = null;
//
//	            //compute term weight
//	            while ((term = itr.next()) != null) {               
//	                String termText = term.utf8ToString();              
//	                int row = termIdMap.get(termText);
//	                long termFreq = itr.totalTermFreq();
//	                long docCount = itr.docFreq();
//	                double weight = computeTfIdfWeight(termFreq, docCount, numDocs);
//	                tdMatrix.setEntry(row, col, weight);
//	            }
//	            col++;
//	        }
//	    }       
//	    return tdMatrix;
//	}
	
	
	
	
}
