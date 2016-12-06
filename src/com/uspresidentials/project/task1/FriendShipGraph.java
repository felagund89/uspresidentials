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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.transform.Transformer;

import com.uspresidentials.project.entity.UserCustom;
import com.uspresidentials.project.lucene.LuceneCore;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.lucene.search.TopDocs;
import org.jgrapht.ListenableGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.uspresidentials.project.utils.AuthenticationManager;
import com.uspresidentials.project.utils.ComparatorRank;
import com.uspresidentials.project.utils.PropertiesManager;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.algorithms.scoring.ClosenessCentrality;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import twitter4j.IDs;
import twitter4j.JSONException;
import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.StreamController.FriendsIDs;

/*
 * Costruire grafo delle amicizie e cercare la componente connessa piu grande, 
 * per il momento non disegnamo il grafo delle amicizie.
 * 
 *  */

public class FriendShipGraph {

	final long MAX_USERS = 500;
	final static String PATH_FILE_UTENTI_ID = PropertiesManager.getPropertiesFromFile("PATH_FILE_UTENTI_ID");
	final static String PATH_FILE_UTENTI_ID_TEST = PropertiesManager.getPropertiesFromFile("PATH_FILE_UTENTI_ID_TEST");
	final static String PATH_FILE_FILTER_USERS = PropertiesManager.getPropertiesFromFile("PATH_FILE_FILTER_USERS");
	
	final static String PATH_FILE_FRIENDSHIP = PropertiesManager.getPropertiesFromFile("PATH_FILE_FRIENDSHIP");
	final static String PATH_FILE_FRIENDSHIP_JSON = PropertiesManager.getPropertiesFromFile("PATH_FILE_FRIENDSHIP_JSON");
	final static String PATH_FILE_LOG4J_CONNECTED_COMPONENTS = PropertiesManager.getPropertiesFromFile("PATH_FILE_LOG4J_CONNECTED_COMPONENTS");
	final static String PATH_FILE_LOG4J_PAGERANK = PropertiesManager.getPropertiesFromFile("PATH_FILE_LOG4J_PAGERANK");
	final static String PATH_FILE_USER_JSON_COMPLETE = PropertiesManager.getPropertiesFromFile("PATH_FILE_USER_JSON_COMPLETE");

	
	
	static int NUMERO_UTENTI; 
	static Boolean isPrivateFriends = false;

	static AuthenticationManager authenticationManager = new AuthenticationManager();

	static JSONObject objFather = new JSONObject();
	static List<JSONObject> objUtenti = new ArrayList<JSONObject>();
	static JSONObject objUtente;

	final static Logger loggerComponents = Logger.getLogger("loggerComponents");
	final static Logger loggerPageRank = Logger.getLogger("loggerPageRank");
	final static Logger loggerCentrality = Logger.getLogger("loggerCentrality");

	static Hashtable<String, String> hashMap_Id_Username;
	static HashMap<String, ArrayList<String>> hashMapUsersTweets = new HashMap<>(); 
	
	public static void main(String[] args) throws IOException, TwitterException, JSONException, ParseException, org.apache.lucene.queryparser.classic.ParseException {

		// recupero gli amici a partire da un account specifico 180 amici alla
		// volta
		// per trovare e salvare tutti gli amici su file

		//hashMap_Id_Username = getUserFromFileAndSplit(2000, PATH_FILE_FILTER_USERS,-1);
		//hashMapUsersTweets = IdentifyUsers.getHashMapUser_Tweets();
		//System.out.println("fine");
		// ******** CREATE FILE WITH FRIEND FOR EACH USER
		//getGlobalFriendship(authenticationManager.twitter); //verificare se
		// serve ancora passare l'argomento
				
		// ******** FRIENDSHIP  // read from JSON File
//		ListenableDirectedGraph<String, DefaultEdge> graphFriendShip = createGraphFromFriendShip(); 																							
////		System.out.println("\n\n\n-----Graph FriendShip-----\n\n\n" + graphFriendShip.toString());
//
//		// ********COMPONENTE CONNESSE - write in folder 'log4j_logs'
//		searchConnectedComponents(graphFriendShip);
//
//		// ********PAGE RANK
//
//		SparseMultigraph<String, DefaultEdge> graphSparse = convertListenableGraph(graphFriendShip);
//		calculatePageRank(graphSparse);
//		
//		// ********CENTRALITY OF M' USERS (who mentioned a candidate)
//		calculateCentrality(graphSparse);
	}

	public static void getGlobalFriendship(Twitter twitter)
			throws TwitterException, FileNotFoundException, IOException, JSONException {

		try (BufferedReader br = new BufferedReader(new FileReader(PATH_FILE_UTENTI_ID_TEST))) {
			String line;

			while ((line = br.readLine()) != null) {
				// definiamo l'id utente, split[0] è il nome utente

				try {
					String userName;
					long idUser;
					idUser = Long.parseLong((line.split(";")[1]));
//					IDs ids = authenticationManager.twitter.getFriendsIDs(idUser, -1); // calcola
																						// il
																						// numero
																						// totale
																						// degli
																						// amici
																						// relativi
														
					// a
																						// idUser
					userName = line.split(";")[0];	
					isPrivateFriends = false;
//					int numberOfFriends = ids.getIDs().length;
					// creo l'oggetto per lutente corrente con i campi nome e
					// id,
					// verra accodato un jsonarray per ogni utente contenente
					// tutti gli amici
					objUtente = new JSONObject();
					objUtente.put("userName", userName);
					objUtente.put("idUser", idUser);
					
					JSONArray jsonArrayTweets = new JSONArray();
					ArrayList<String> currentTeewts = hashMapUsersTweets.get(userName.toLowerCase()+";"+idUser);
					if (currentTeewts != null) {
						System.out.println("Tweet for User: " + userName);
						for (String t : currentTeewts) {
							System.out.println("currentTweets: " + t);
							jsonArrayTweets.add(t);
						}
					}
					objUtente.put("tweets", jsonArrayTweets);
					
//					System.out.println("Utente " + userName + " ha " + numberOfFriends + " amici.");
					// scrivo su file il nome dell'utente che stiamo analizzando
					writeUsersOnFile(userName + "===>");
					//getFriendShipRecursive(authenticationManager.twitter, userName, idUser, -1, numberOfFriends);
					getFriendShipONLYDatasetRecursive(authenticationManager.twitter, userName, idUser,0, -1, null);

					writeJsonUserOnFile(objUtente);

				} catch (TwitterException e) {
					if (e.getStatusCode() == 401) {
						System.out.println("user no longer on Twitter");
					}
				}
			}
			System.out.println("FINE ANALISI FILE UTENTI");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getFriendShipRecursive(Twitter twitter, String userName, long idUser, long cursor,
			int numberOfFriends) throws TwitterException, IOException, JSONException {

		String listFriends = "";
		String content;
		System.out.println("Analizzo amici...");
		JSONArray jsonArrayFriends = new JSONArray();

		int prevIndex = authenticationManager.getAccountIndex();
		try {
			if (isPrivateFriends)
				return;

			PagableResponseList<User> pagableFollowings;
			do {
				listFriends = "";
				pagableFollowings = authenticationManager.twitter.getFriendsList(idUser, cursor);

				for (User user : pagableFollowings) {
					listFriends = listFriends + user.getName() + ";";
					jsonArrayFriends.add(user.getName() + ";" + user.getId() + ";");
					numberOfFriends--;
				}
				content = listFriends;
				System.out.println("numero di amici restanti da aggiungere: " + numberOfFriends);
				writeUsersOnFile(content); // scrive su file tutte le
											// relationship dei vari utenti
				// numberOfFriends = numberOfFriends - pagableFollowings.size();
				if (numberOfFriends <= 0) {
					objUtente.put("friends", jsonArrayFriends);
					break;
				}
			} while ((cursor = pagableFollowings.getNextCursor()) != 0);
		} catch (TwitterException e) {

			if (e.getStatusCode() != 429) {
				System.out.println("Users " + userName + " has a private list. Extraction denied!");
				isPrivateFriends = true;
				writeUsersOnFile("PRIVATE_FRIENDS;");
				// scorre al prossimo idUser e azzera currentCursor (-1)
			}

			// System.out.println(e.getMessage() + "Status code: " +
			// e.getStatusCode() + "\n");

			// System.out.println("Richieste esaurite per l'account: " +
			// twitter.getScreenName() + ".");
			authenticationManager.setAuthentication(authenticationManager.getAccountIndex() + 1);

			// vecchio modo
			// twitter = getFastCredentialsForQuery(twitter);
			try {
				if (authenticationManager.getAccountIndex() == authenticationManager.ACCOUNTS_NUMBER - 1) {

					//
					int toSleep = authenticationManager.twitter.getRateLimitStatus().get("/friends/list")
							.getSecondsUntilReset() + 1;
					System.out.println("Sleeping for " + toSleep + " seconds.");
					Thread.sleep(toSleep * 1000);
					getFriendShipRecursive(authenticationManager.twitter, userName, idUser, cursor, numberOfFriends);

				} else {
					getFriendShipRecursive(authenticationManager.twitter, userName, idUser, cursor, numberOfFriends);
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		// crea il grafo leggendo tale file
	}
	
	
	public static void getFriendShipONLYDatasetRecursive(Twitter twitter, String userName, long idUser, long cursor,
			int numberOfFriends, JSONArray arrayFriends) throws TwitterException, IOException, JSONException {

		String listFriends = "";
		String content;
		System.out.println("Analizzo amici...");
		IDs friendsIds;
		JSONArray jsonArrayFriends = null;
		
		if(arrayFriends == null)
			jsonArrayFriends = new JSONArray();
		else
			jsonArrayFriends = arrayFriends;

		int prevIndex = authenticationManager.getAccountIndex();
		try {
			if (isPrivateFriends)
				return;

			PagableResponseList<User> pagableFollowings;
			do {
				listFriends = "";
				//pagableFollowings = authenticationManager.twitter.getFriendsList(idUser, cursor);
				friendsIds = authenticationManager.twitter.getFriendsIDs(idUser, cursor);
				numberOfFriends=friendsIds.getIDs().length;
				for(long currentId : friendsIds.getIDs()){
					
					//listFriends = listFriends + hashMap_Id_Username.get(String.valueOf(currentId)) + ";";
					
					if(hashMap_Id_Username.containsKey(String.valueOf(currentId)))
						jsonArrayFriends.add(hashMap_Id_Username.get(String.valueOf(currentId)) + ";" + currentId + ";");
				}
				//content = listFriends;
				System.out.println("numero di amici restanti da aggiungere: " + numberOfFriends);
				//writeUsersOnFile(content); // scrive su file tutte le
											// relationship dei vari utenti
				// numberOfFriends = numberOfFriends - pagableFollowings.size();
				
				/*if (numberOfFriends <= 0) {
					objUtente.put("friends", jsonArrayFriends);
					break;
				} 
				
				for (User user : pagableFollowings) {
					listFriends = listFriends + user.getName() + ";";
					
					System.out.println("idUser Hashmap: " + hashMap_Id_Username.keys().toString());
					System.out.println("user.getId(): " + String.valueOf(user.getId()));
					
					if(hashMap_Id_Username.containsKey(String.valueOf(user.getId())))
						jsonArrayFriends.add(user.getName() + ";" + user.getId() + ";");
					numberOfFriends--;
				}
				content = listFriends;
				System.out.println("numero di amici restanti da aggiungere: " + numberOfFriends);
				writeUsersOnFile(content); // scrive su file tutte le
											// relationship dei vari utenti
				// numberOfFriends = numberOfFriends - pagableFollowings.size();
				
				if (numberOfFriends <= 0) {
					objUtente.put("friends", jsonArrayFriends);
					break;
				} */
				System.out.println("valore cursore:" +cursor);
			} while ((cursor = friendsIds.getNextCursor()) != 0); //cursor = pagableFollowings.getNextCursor()) != 0

			
			
			objUtente.put("friends", jsonArrayFriends);
			//jsonArrayFriends.clear();
			
		} catch (TwitterException e) {

			if (e.getStatusCode() != 429) {
				System.out.println("Users " + userName + " has a private list. Extraction denied!");
				isPrivateFriends = true;
				writeUsersOnFile("PRIVATE_FRIENDS;");
				// scorre al prossimo idUser e azzera currentCursor (-1)
			}

			// System.out.println(e.getMessage() + "Status code: " +
			// e.getStatusCode() + "\n");

			System.out.println("cè stato un piccolo problema : "+ e.getErrorMessage());
			authenticationManager.setAuthentication(authenticationManager.getAccountIndex() + 1);

			// vecchio modo
			// twitter = getFastCredentialsForQuery(twitter);
			try {
				if (authenticationManager.getAccountIndex() == authenticationManager.ACCOUNTS_NUMBER - 1) {

					//
					int toSleep = authenticationManager.twitter.getRateLimitStatus().get("/friends/ids") ///friends/list
							.getSecondsUntilReset() + 1;
					System.out.println("Sleeping for " + toSleep + " seconds.");
					Thread.sleep(toSleep * 1000);
					getFriendShipONLYDatasetRecursive(authenticationManager.twitter, userName, idUser, cursor, numberOfFriends, jsonArrayFriends);
				} else {
					getFriendShipONLYDatasetRecursive(authenticationManager.twitter, userName, idUser, cursor, numberOfFriends, jsonArrayFriends);
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		// crea il grafo leggendo tale file
	}
	
	
	

	public static ListenableDirectedGraph<String, DefaultEdge> createGraphFromFriendShip()
			throws TwitterException, FileNotFoundException, IOException, ParseException {

		// read from friendship file with this format -->
		// //nomeUtente1:amico1;amico2;amico3
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(new FileReader(PATH_FILE_USER_JSON_COMPLETE));
		JSONObject jsonObjectUser = (JSONObject) obj;

		JSONArray listUsers = (JSONArray) jsonObjectUser.get("ListUsers");
		ListenableDirectedGraph<String, DefaultEdge> myGraph = new ListenableDirectedGraph<String, DefaultEdge>(
				DefaultEdge.class);

		Iterator<JSONObject> iterator = listUsers.iterator();
		int  numberOfVertex=0;
		while (iterator.hasNext()) {
			JSONObject currentUs = iterator.next();
			String currentUser = currentUs.get("userName").toString();
			String currentId = currentUs.get("idUser").toString();
			String userToScan = currentUser+";" + currentId + ";";
			
			//if (!myGraph.containsVertex(currentUser))
			if (!myGraph.containsVertex(userToScan)){
				myGraph.addVertex(userToScan);
				numberOfVertex++;
//				System.out.println("1aggiunto nodo:" + userToScan);
			}

			//System.out.println("*****" + currentUser);
			JSONArray listFriends = (JSONArray) currentUs.get("friends");
			if (listFriends!=null) {
				for (int i = 0; i < listFriends.size(); i++) {
					//System.out.println("             - friend: " + listFriends.get(i));

					String currentFriend = listFriends.get(i).toString();

					if (!myGraph.containsVertex(currentFriend)){ // se non contiene
						numberOfVertex++;										// giá quel nodo
						myGraph.addVertex(currentFriend);
//						System.out.println("2aggiunto nodo:" + currentFriend);
					}
					// se non contiene giá quell'arco
					if (!myGraph.containsEdge(userToScan, currentFriend)){   //currentUser
						myGraph.addEdge(userToScan, currentFriend);
//						System.out.println("aggiunto arco: " +userToScan+" "+currentFriend);

					}//currentUser
				}
			}
			
		}
		System.out.println("numberOfVertex createGraph = "+numberOfVertex);
		System.out.println("FINE ------ createGraphFromFriendShip");
		
		//loggerComponents.info("GRAFO: " + myGraph.toString());
		return myGraph;
	}

	public static Hashtable<String, String> getUserFromFileAndSplit(Integer maxNumUser, String PATH_FILE_UTENTI_ID,
			int cursor) throws FileNotFoundException, IOException {

		Hashtable<String, String> hashUsers = new Hashtable<String, String>();

		int countUsers = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(PATH_FILE_FILTER_USERS))) {
			String line;

			while ((line = br.readLine()) != null && countUsers < maxNumUser) {

				hashUsers.put(line.split(";")[1], line.split(";")[0]);

				countUsers++;
			}
		}
		return hashUsers;

	}

	public static ListenableGraph<String, DefaultEdge> createTestGraph() {

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
		// g.addEdge(v2, v4);
		g.addEdge(v2, v5);
		g.addEdge(v2, v6);
		g.addEdge(v2, v7);
		g.addEdge(v2, v8);

		System.out.println("created graph: " + g.toString());

		return g;
	}

	public static void searchConnectedComponents(ListenableDirectedGraph<String, DefaultEdge> g) {
		
		
		Set<String> listVertex = g.vertexSet();
		ConnectivityInspector<String, DefaultEdge> conn = new ConnectivityInspector<String, DefaultEdge>(g);
		ArrayList<String> listGlobalConnected = new ArrayList<String>();
		String majorComponents = "";
		long countMajorComponents = 0;

		for (String currentVertex : listVertex) { // scan the friendship graph

			Set<String> listVertexConnected = conn.connectedSetOf(currentVertex); // g.vertexSet().iterator().next()
			listGlobalConnected.addAll(listVertexConnected);

			if (listVertexConnected.size() > countMajorComponents) {
				countMajorComponents = listVertexConnected.size();
				majorComponents = "*****MAJOR_COMPONENTS - count: " + countMajorComponents + "\nStart Vertex:"
						+ currentVertex.toString() + "\nconnected components: " + listVertexConnected.toString()
						+ "\n\n\n***********";
			}

			loggerComponents.info("****Start Vertex:" + currentVertex.toString()+" - count: " + countMajorComponents + "\nconnected components: "+ listVertexConnected.toString() + "\n\n\n*********************************");
		}

		System.out.println("Search Connected Components COMPLETED!");
		loggerComponents.info(majorComponents);
	}

	public static SparseMultigraph<String, DefaultEdge> convertListenableGraph(
			ListenableDirectedGraph<String, DefaultEdge> graphFriendship)
			throws FileNotFoundException, IOException, ParseException {

		SparseMultigraph<String, DefaultEdge> myGraph = new SparseMultigraph<String, DefaultEdge>();

		// read from friendship file with this format -->
		// //nomeUtente1:amico1;amico2;amico3
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(new FileReader(PATH_FILE_USER_JSON_COMPLETE));
		JSONObject jsonObjectUser = (JSONObject) obj;

		JSONArray listUsers = (JSONArray) jsonObjectUser.get("ListUsers");
		Iterator<JSONObject> iterator = listUsers.iterator();
		int numberOfVertex=0;
		while (iterator.hasNext()) {
			JSONObject currentUs = iterator.next();
			String currentUser = currentUs.get("userName").toString();
			String currentId = currentUs.get("idUser").toString();
			String userToScan = currentUser+";" + currentId + ";";
			
			
			
			
			if (!myGraph.containsVertex(userToScan)){
				numberOfVertex++;
				myGraph.addVertex(userToScan);
			}

//			System.out.println("*****currentUser" + currentUser);
			JSONArray listFriends = (JSONArray) currentUs.get("friends");
			if (listFriends!=null) {
				for (int i = 0; i < listFriends.size(); i++) {
					//System.out.println("             - friend: " + listFriends.get(i));

					String currentFriend = listFriends.get(i).toString();

					if (!myGraph.containsVertex(currentFriend)){
						numberOfVertex++;
						myGraph.addVertex(currentFriend);
					}

					DefaultEdge currentEdge = new DefaultEdge();
					myGraph.addEdge(currentEdge, userToScan, currentFriend, EdgeType.DIRECTED);
				}
			}
			
		}
		System.out.println("numberOfVertex convert in linkedsparsedgraph: "+ numberOfVertex);
		//logger.info("SparseMultigraph GENERATED: \n\n" + myGraph.getVertices().toString());
		
		return myGraph;
	}


	public static void writeUsersOnFile(String content) throws FileNotFoundException, UnsupportedEncodingException {
		// nomeUtente1:amico1;amico2;amico3
		// nomeUtente2:amico1;amico2;amico3

		PrintWriter writer = new PrintWriter(
				new FileOutputStream(new File(PropertiesManager.getPropertiesFromFile("PATH_FILE_FRIENDSHIP")), true));
		writer.println(content);
		writer.close();
	}

	public static void writeJsonUserOnFile(JSONObject jsonUser) throws IOException {

		// inserire [] inizio e fine cosí da avere un json completo

		PrintWriter writer = new PrintWriter(new FileOutputStream(
				new File(PropertiesManager.getPropertiesFromFile("PATH_FILE_FRIENDSHIP_JSON")), true));
		writer.println(jsonUser.toString() + ",");
		writer.close();
	}
}
