package com.uspresidentials.project.task1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.ListenableGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.uspresidentials.project.utils.AuthenticationManager;
import com.uspresidentials.project.utils.PropertiesManager;

import twitter4j.IDs;
import twitter4j.JSONException;
import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

import edu.uci.ics.jung.algorithms.scoring.*;
import edu.uci.ics.jung.graph.Hypergraph;

/*
 * Costruire grafo delle amicizie e cercare la componente connessa piu grande, 
 * per il momento non disegnamo il grafo delle amicizie.
 * 
 *  */


public class FriendShipGraph {

	final long MAX_USERS = 500;
	final static String PATH_FILE_UTENTI_ID = PropertiesManager.getPropertiesFromFile("PATH_FILE_UTENTI_ID");
	final static String PATH_FILE_FRIENDSHIP = PropertiesManager.getPropertiesFromFile("PATH_FILE_FRIENDSHIP");
	final static String PATH_FILE_FRIENDSHIP_JSON = PropertiesManager.getPropertiesFromFile("PATH_FILE_FRIENDSHIP_JSON");

	static int NUMERO_UTENTI;
	static Boolean isPrivateFriends=false;

	static AuthenticationManager authenticationManager = new AuthenticationManager();
	
	static JSONObject objFather = new JSONObject();
	static List<JSONObject> objUtenti = new ArrayList<JSONObject>();
	static JSONObject objUtente;
	
	public static void main(String[] args) throws IOException, TwitterException, JSONException, ParseException {


		//recupero gli amici a partire da un account specifico 180 amici alla volta
		//per trovare e salvare tutti gli amici su file
		  
	     
	    //getGlobalFriendship(authenticationManager.twitter); //verificare se serve ancora passare l'argomento
		ListenableDirectedGraph<String, DefaultEdge> graphFriendShip = createGraphFromFriendShip();
		System.out.println("\n\n\n-----Graph FriendShip-----\n\n\n" + graphFriendShip);
		searchConnectedComponents(graphFriendShip);
		
		/*Hypergraph<String, DefaultEdge> hyperGraph = new Hypergraph<String, DefaultEdge>(graphFriendShip);
		PageRank<String, DefaultEdge> pageRank = new PageRank<String, DefaultEdge>(graphFriendShip, 0.1);
		pageRank.initialize(); */
		
		
		//Creo grafo e cerco la componente connessa piu grande
	    //ListenableDirectedGraph<String, DefaultEdge> myGraph = (ListenableDirectedGraph<String, DefaultEdge>) FriendShipGraph.createGraph();
	    //dato un grafo cerco la componente connessa piu grande
	    //FriendShipGraph.searchConnectedComponents(myGraph);
	   
	}
	
	
	public static void getGlobalFriendship(Twitter twitter) throws TwitterException, FileNotFoundException, IOException, JSONException{
		
	     try (BufferedReader br = new BufferedReader(new FileReader(PATH_FILE_UTENTI_ID))) {
			    String line;
			    
			    while ((line = br.readLine()) != null  ) {
			    	//definiamo l'id utente, split[0] è il nome utente
			    	
			    	try{
				    	 String userName;
				    	 long idUser; 
				    	 idUser= Long.parseLong((line.split(";")[1]));
						 IDs ids = authenticationManager.twitter.getFriendsIDs(idUser, -1);  //calcola il numero totale degli amici relativi a idUser 
				    	 userName=line.split(";")[0];
				    	 isPrivateFriends=false;
				    	 int numberOfFriends= ids.getIDs().length;
				    	//creo l'oggetto per lutente corrente con i campi nome e id, 
				    	 //verra accodato un jsonarray per ogni utente contenente tutti gli amici
				    	 objUtente = new JSONObject();
				    	 objUtente.put("userName", userName);
				    	 objUtente.put("idUser", idUser);
				     
				         System.out.println("Utente " +userName+" ha "+numberOfFriends+ " amici.");
			             //scrivo su file il nome dell'utente che stiamo analizzando
				         writeUsersOnFile(userName+"===>");
					     getFriendShipRecursive(authenticationManager.twitter,userName,idUser, -1,numberOfFriends);
					     
					     writeJsonUserOnFile(objUtente);
					     
			    	}catch(TwitterException e){
				    	 if(e.getStatusCode() == 401){
				    		 System.out.println("user no longer on Twitter");
				    	 }
				     }
			    }
			    System.out.println("FINE ANALISI FILE UTENTI");
	     }catch(Exception e){
	    	 e.printStackTrace();
	     }
	}
	
	public static void getFriendShipRecursive(Twitter twitter, String userName, long idUser, long cursor,int numberOfFriends) throws TwitterException, IOException, JSONException{
	
	      String listFriends = "";
	      String content;
	      System.out.println("Analizzo amici...");
		  JSONArray jsonArrayFriends = new JSONArray();

		  int prevIndex = authenticationManager.getAccountIndex(); 
			    	  try {
			    		  if(isPrivateFriends)
			    			  return;
			    		   
			    		  PagableResponseList<User> pagableFollowings;
				            do {
				            	listFriends="";
				                pagableFollowings =authenticationManager.twitter.getFriendsList(idUser, cursor);
				                
				                for (User user : pagableFollowings) {
				                	listFriends = listFriends + user.getName() +";";
				                	jsonArrayFriends.add(user.getName() + ";"+user.getId()+";");
				                	numberOfFriends--;
				                }
				                content =  listFriends;
				                System.out.println("numero di amici restanti da aggiungere: "+numberOfFriends);
			     			    writeUsersOnFile(content);  //scrive su file tutte le relationship dei vari utenti
//				                numberOfFriends = numberOfFriends - pagableFollowings.size();
				                if(numberOfFriends <=0  ){
				                	objUtente.put("friends", jsonArrayFriends);
				                	break;
				                }
				            } while ((cursor = pagableFollowings.getNextCursor()) != 0);            
					} catch (TwitterException e) {
						
				
						if(e.getStatusCode() != 429){
							System.out.println("Users " + userName + " has a private list. Extraction denied!");
							isPrivateFriends=true;
							writeUsersOnFile("PRIVATE_FRIENDS;");
							//scorre al prossimo idUser e azzera currentCursor (-1)
						}
						
						//System.out.println(e.getMessage() + "Status code: " + e.getStatusCode() + "\n");
		                
		                //System.out.println("Richieste esaurite per l'account: " + twitter.getScreenName() + ".");
		                authenticationManager.setAuthentication(authenticationManager.getAccountIndex() + 1);
						
						//vecchio modo
		                //twitter = getFastCredentialsForQuery(twitter);   
		                try {
			                if(authenticationManager.getAccountIndex() == authenticationManager.ACCOUNTS_NUMBER-1){ 
				              
							    	//
									int toSleep = authenticationManager.twitter.getRateLimitStatus().get("/friends/list").getSecondsUntilReset() + 1;
									System.out.println("Sleeping for " + toSleep + " seconds.");
									Thread.sleep(toSleep * 1000);
									getFriendShipRecursive(authenticationManager.twitter,userName,idUser, cursor,numberOfFriends);
									
			                }else{
			            	    	getFriendShipRecursive(authenticationManager.twitter,userName,idUser, cursor,numberOfFriends);
			               }
		                } catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						e.printStackTrace();
					}
	    //crea il grafo leggendo tale file
	}
	
	public static ListenableDirectedGraph<String, DefaultEdge> createGraphFromFriendShip() throws TwitterException, FileNotFoundException, IOException, ParseException{
		
		//read from friendship file with this format --> //nomeUtente1:amico1;amico2;amico3
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(new FileReader(PATH_FILE_FRIENDSHIP_JSON));
		JSONObject jsonObjectUser = (JSONObject) obj;
		
		JSONArray listUsers = (JSONArray) jsonObjectUser.get("ListUsers");
		ListenableDirectedGraph<String, DefaultEdge>  myGraph = new ListenableDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		
		Iterator<JSONObject> iterator = listUsers.iterator();
		
		 while (iterator.hasNext()) {
			 JSONObject currentUs = iterator.next();
			 String currentUser = currentUs.get("userName").toString();
			 
			 if(!myGraph.containsVertex(currentUser))
				 myGraph.addVertex(currentUser);
			 
			 System.out.println("*****" + currentUser);
			 JSONArray listFriends = (JSONArray)currentUs.get("friends");
			 
			 for(int i=0;i<listFriends.size();i++){
				 System.out.println("             - friend: " + listFriends.get(i));
				 
				 String currentFriend = listFriends.get(i).toString();
				 
				 if(!myGraph.containsVertex(currentFriend))  //se non contiene giá quel nodo
					 myGraph.addVertex(currentFriend);
				 
				 if(!myGraph.containsEdge(currentUser, currentFriend)) //se non contiene giá quell'arco
					 myGraph.addEdge(currentUser, currentFriend);
			 }
		 }
		 return myGraph;
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
		
		PrintWriter writer = new PrintWriter(new FileOutputStream(new File(PropertiesManager.getPropertiesFromFile("PATH_FILE_FRIENDSHIP")),true));
		writer.println(content);
		writer.close();
	}
	

	public static void writeJsonUserOnFile(JSONObject jsonUser) throws IOException{
				
		//inserire [] inizio e fine cosí da avere un json completo
		
		PrintWriter writer = new PrintWriter(new FileOutputStream(new File(PropertiesManager.getPropertiesFromFile("PATH_FILE_FRIENDSHIP_JSON")),true));
		writer.println(jsonUser.toString() + ",");
		writer.close();
	}
	
	public static void writeCandidatesOnFile(){
		//trump:ut1;ut2;ut3
		//clinton:ut2;ut8;ut6
	}
}
