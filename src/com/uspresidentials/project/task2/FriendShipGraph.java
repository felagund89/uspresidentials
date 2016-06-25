package com.uspresidentials.project.task2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public class FriendShipGraph {

	public static void main(String[] args) throws IOException, TwitterException {
		// TODO Auto-generated method stub

		//Authentication.InitializeTwitterObj("");
		
		getFriendShip("felagund89");
		//createGraph();
		//writeUsersOnFile();
	}
	
	public static void getFriendShip(String userName) throws TwitterException, FileNotFoundException, UnsupportedEncodingException{
		
		  User u1 = null ;
	      long cursor = -1;
	      IDs ids;
	      Twitter twitter = TwitterFactory.getSingleton();
	      twitter.setOAuthConsumer("1HERcFVCy5SkpI23hl3FRpJy3", "5PFPlMp3NAsAT1Qbrk1RStXWLMX795ghSUfubtwILl5vR2keyW");
	      AccessToken accessToken = new AccessToken("2977694199-o286ySyyQbCTJsMXcxSfoeSwQ6CkVGQSNl8ILMO", "SKo5MvolhkJxmoG3ADgb2tzW5oOFV7p6A44hmcHY1Pzz1");
	      twitter.setOAuthAccessToken(accessToken);
	      String listFriends = "";
	      
	      System.out.println("Listing followers's ids.");
	      do {
	              ids = twitter.getFollowersIDs(userName, cursor);  //felagund89
	      
	          for (long id : ids.getIDs()) {
	              System.out.println(id);
	              User user = twitter.showUser(id);
	              System.out.println(user.getName());
	              
	              listFriends = listFriends + user.getName() + ";" ;
	          }
	      } while ((cursor = ids.getNextCursor()) != 0);
	      
	      
	      String content = userName + ":" + listFriends;
	      writeUsersOnFile(content);  //scrive su file tutte le relationship dei vari utenti
	      //crea il grafo leggendo tale file
	}
	
	public static ListenableGraph<String, DefaultEdge> createGraph(){
		
		ListenableGraph<String, DefaultEdge> g = new ListenableDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		 String v1 = "Vertex1";
		 String v2 = "Vertex2";
		 String v3 = "Vertex3";
		 String v4 = "Vertex4";
		 
		 g.addVertex(v1);
		 g.addVertex(v2);
		 g.addVertex(v3);
		 g.addVertex(v4);
	
		 g.addEdge(v1, v2);
		 g.addEdge(v1, v3);
		 g.addEdge(v2, v3);
		 
		 System.out.println("created graph: " + g.toString());
		 
		 return g;
	}	
	
	public static void writeUsersOnFile(String content) throws FileNotFoundException, UnsupportedEncodingException{
		//nomeUtente1:amico1;amico2;amico3
		//nomeUtente2:amico1;amico2;amico3
		
		PrintWriter writer = new PrintWriter("/Users/alessiocampanelli/Desktop/friendshipTwitter.txt", "UTF-8");
		writer.println(content);
		writer.close();
	}
	
	public static void writeCandidatesOnFile(){
		//trump:ut1;ut2;ut3
		//clinton:ut2;ut8;ut6
	}
}
