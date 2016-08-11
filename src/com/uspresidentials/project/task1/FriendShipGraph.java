package com.uspresidentials.project.task1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
	      getFriendShipRecursive(twitter,"felagund89",idUser, -1,-1,"felagund89");

			
		//Creo grafo e cerco la componente connessa piu grande
	    ListenableDirectedGraph<String, DefaultEdge> myGraph = (ListenableDirectedGraph<String, DefaultEdge>) FriendShipGraph.createGraph();
	    //dato un grafo cerco la componente connessa piu grande
	    FriendShipGraph.searchConnectedComponents(myGraph);
	    
	    
		//createGraph();
		//writeUsersOnFile();
	}
	
	public static void getFriendShipRecursive(Twitter twitter, String userName, long idUser, long cursor, long currentCursor, String currentUser) throws TwitterException, FileNotFoundException, UnsupportedEncodingException{
		
	      IDs ids;
	     
	      String listFriends = "";
	      String content;
	      userName = currentUser;
	      cursor = currentCursor;
	      System.out.println("Listing followers's ids.");
	      
	      try {
//			do {
//			          ids = twitter.getFriendsIDs(userName, cursor);  //felagund89
//			          
//			      for (long id : ids.getIDs()) {
//			          System.out.println(id);
//			          User user = twitter.showUser(id);
//			          System.out.println(id +":"+ user.getName()); 
//				      currentUser = user.getName();
//			          currentCursor=ids.getNextCursor();
//
//			          listFriends = listFriends + user.getName() + ";" ;
//			      }
////			      currentCursor = cursor;    						//salvo il cursore per i prossimi 180 ids
//			  } while ((cursor = ids.getNextCursor()) != 0);
	    	  
//	    	  long cursor = -1;
	            PagableResponseList<User> pagableFollowings;
	            do {
	                pagableFollowings = twitter.getFriendsList(idUser, cursor);
	                for (User user : pagableFollowings) {
//	                    listFriends.add(user.getName()); // ArrayList<User>
	                	listFriends = listFriends + user.getName() +";";
	                	
	                }
	            } while ((cursor = pagableFollowings.getNextCursor()) != 0);
			
		} catch (TwitterException e) {
			
			
			 
			
			
//			if(e.getStatusCode() != 429)
//            {
//				System.out.println("Users " + userName + " has a private list. Extraction denied!");
//              break;
//            }
			
			content = userName + ":" + listFriends;
		    writeUsersOnFile(content);  //scrive su file tutte le relationship dei vari utenti
		    
		    try {
		    	
				int toSleep = twitter.getRateLimitStatus().get("/friends/ids").getSecondsUntilReset() + 1;
				System.out.println("Sleeping for " + toSleep + " seconds.");
				Thread.sleep(toSleep * 1000);
				getFriendShipRecursive(twitter,"", 0L, currentCursor, currentUser);
				
				
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		    
			e.printStackTrace();
		}
	      
	    //crea il grafo leggendo tale file
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
