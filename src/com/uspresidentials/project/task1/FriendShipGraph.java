package com.uspresidentials.project.task1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.jgrapht.ListenableGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import twitter4j.IDs;
import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

/*
 * Costruire grafo delle amicizie e cercare la componente connessa piu grande, 
 * per il momento non disegnamo il grafo delle amicizie.
 * 
 *  */


public class FriendShipGraph {

	final long MAX_USERS = 500;
	final static String PATH_FILE_UTENTI_ID = "/home/felagund89/Scrivania/utentiTwitter.txt";
	static int NUMERO_UTENTI;
	static Boolean isPrivateFriends=false;
	
	public static void main(String[] args) throws IOException, TwitterException {
		// TODO Auto-generated method stub

		//Authentication.InitializeTwitterObj("");
		  Twitter twitter = TwitterFactory.getSingleton();
	      twitter.setOAuthConsumer("1HERcFVCy5SkpI23hl3FRpJy3", "5PFPlMp3NAsAT1Qbrk1RStXWLMX795ghSUfubtwILl5vR2keyW");
	      AccessToken accessToken = new AccessToken("2977694199-o286ySyyQbCTJsMXcxSfoeSwQ6CkVGQSNl8ILMO", "SKo5MvolhkJxmoG3ADgb2tzW5oOFV7p6A44hmcHY1Pzz1");
	      twitter.setOAuthAccessToken(accessToken);
	      long idUser = twitter.getId() ;
	      //recupero gli amici a partire da un account specifico 180 amici alla volta, trovare modo per automatizzare il crawling 
	      //per trovare e salvare tutti gli amici su file
		  getGlobalFriendship(twitter,"Alex_Campanels",1676105413L, -1,-1,"Alex_campanels");

	      
	     
			
		//Creo grafo e cerco la componente connessa piu grande
	    //ListenableDirectedGraph<String, DefaultEdge> myGraph = (ListenableDirectedGraph<String, DefaultEdge>) FriendShipGraph.createGraph();
	    //dato un grafo cerco la componente connessa piu grande
	    //FriendShipGraph.searchConnectedComponents(myGraph);
	    
	    
		//createGraph();
		//writeUsersOnFile();
	}
	
	
	public static void getGlobalFriendship(Twitter twitter, String userName, long idUser, long cursor, long currentCursor, String currentUser) throws TwitterException, FileNotFoundException, IOException{
		
		
		
	     try (BufferedReader br = new BufferedReader(new FileReader(PATH_FILE_UTENTI_ID))) {
			    String line;
			    
			    while ((line = br.readLine()) != null  ) {
			    	//definiamo l'id utente, split[0] è il nome utente

			    	 idUser= Long.parseLong((line.split(";")[1]));
					 IDs ids = twitter.getFriendsIDs(idUser, -1);  //calcola il numero totale degli amici relativi a idUser 

			    	 userName=line.split(";")[0];
			    	 isPrivateFriends=false;
			    	 NUMERO_UTENTI=ids.getIDs().length;
			    	 int numberOfFriends= ids.getIDs().length;
			         System.out.println("IDS COUNT " + numberOfFriends);
		             writeUsersOnFile(userName+":");

				     getFriendShipRecursive(twitter,userName,idUser, -1,-1,"Alex_campanels",numberOfFriends);
			    	
			    	
			    }
	     }
		
	}
	
	
	
	public static void getFriendShipRecursive(Twitter twitter, String userName, long idUser, long cursor, long currentCursor, String currentUser,int numberOfFriends) throws TwitterException, IOException{
		
	     
	      String listFriends = "";
	      String content;
	      System.out.println("Listing followers's ids.");
	     
	
	     
			    	  try {
			    		  if(isPrivateFriends)
			    			  return;
				            
			    		  PagableResponseList<User> pagableFollowings;
				            do {
				            	listFriends="";
				                pagableFollowings = twitter.getFriendsList(idUser, cursor);
				                
				                for (User user : pagableFollowings) {
//				                    listFriends.add(user.getName()); // ArrayList<User>
				                	listFriends = listFriends + user.getName() +";";
				                	//System.out.println(listFriends);
				                }
				                content =  listFriends;
			     			    writeUsersOnFile(content);  //scrive su file tutte le relationship dei vari utenti
				                numberOfFriends-= pagableFollowings.size();
				                if(numberOfFriends <=0  ){
				                	break;
				                }
				            } while ((cursor = pagableFollowings.getNextCursor()) != 0);
						
				           
				            
				            
					} catch (TwitterException e) {
						
				
						if(e.getStatusCode() != 429)
			            {
							isPrivateFriends=true;
							System.out.println("Users " + userName + " has a private list. Extraction denied!");
							
							//scorre al prossimo idUser e azzera currentCursor (-1)
			            }
							    
					    try {
					    	
							int toSleep = twitter.getRateLimitStatus().get("/friends/ids").getSecondsUntilReset() + 1;
							System.out.println("Sleeping for " + toSleep + " seconds.");
							Thread.sleep(toSleep * 1000);
							getFriendShipRecursive(twitter,userName, 0L, cursor, idUser, currentUser,numberOfFriends);
							
							
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					    
						e.printStackTrace();
					}
			    	
			    
			
	      
	    
	      
	    //crea il grafo leggendo tale file
	}
	
	
	public static Hashtable<String, String> getUserFromFileAndSplit(Integer maxNumUser,String  PATH_FILE_UTENTI_ID, int cursor) throws FileNotFoundException, IOException{
		
		Hashtable<String, String> hashUsers = new Hashtable<String,String>();
		
		int countUsers = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(PATH_FILE_UTENTI_ID))) {
		    String line;
		    
		    while ((line = br.readLine()) != null && countUsers < maxNumUser ) {
		    	
		    	hashUsers.put(line.split(";")[1], line.split(";")[0]);
		    
		    	countUsers++;
		    }
		}
		return hashUsers;
		
	}
	
	
	public static ListenableGraph<String, DefaultEdge> createGraph(){
		
		ListenableGraph<String, DefaultEdge> g = new ListenableDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		 String v1 = "Vertex1";
		 String v2 = "Vertex2";
		 String v3 = "Vertex3";
		 String v4 = "Vertex4";
		 String v5 = "Vertex5";
		 String v6 = "Vertex6";
		 String v7 = "Vertex7";
		 String v8 = "Vertex8";
		 String v9 = "Vertex9";

		 
		 g.addVertex(v1);
		 g.addVertex(v2);
		 g.addVertex(v3);
		 g.addVertex(v4);
		 g.addVertex(v5);
		 g.addVertex(v6);
		 g.addVertex(v7);
		 g.addVertex(v8);
		 g.addVertex(v9);

		 g.addEdge(v1, v2);
		 g.addEdge(v1, v3);
		 g.addEdge(v2, v3);
//		 g.addEdge(v2, v4);
		 g.addEdge(v2, v5);
		 g.addEdge(v2, v6);
		 g.addEdge(v2, v7);
		 g.addEdge(v2, v8);
		 g.addEdge(v2, v9);

		 
		
		 System.out.println("created graph: " + g.toString());
		 
		 return g;
	}
	
	public static void searchConnectedComponents(ListenableDirectedGraph<String, DefaultEdge> g)
	{
		ConnectivityInspector conn = new ConnectivityInspector(g);
		Set<String> listVertexConnected = conn.connectedSetOf(g.vertexSet().iterator().next());
		System.out.println("list connected vertex: " + listVertexConnected.toString());
	}
	
	public static void writeUsersOnFile(String content) throws FileNotFoundException, UnsupportedEncodingException{
		//nomeUtente1:amico1;amico2;amico3
		//nomeUtente2:amico1;amico2;amico3
		
//		PrintWriter writer = new PrintWriter("/Users/alessiocampanelli/Desktop/friendshipTwitter.txt", "UTF-8");
//		PrintWriter writer = new PrintWriter("/home/felagund89/Scrivania/friendshipTwitter.txt", "UTF-8");

		PrintWriter writer = new PrintWriter(new FileOutputStream(new File("/home/felagund89/Scrivania/friendshipTwitterAll.txt"),true));
		
		writer.println(content);
		writer.close();
	}
	

	
	public static void writeCandidatesOnFile(){
		//trump:ut1;ut2;ut3
		//clinton:ut2;ut8;ut6
	}
}
