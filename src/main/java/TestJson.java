package main.java;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.json.simple.parser.JSONParser;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

public class TestJson {

	
	static String utentiEAmiciString="{'utenti':[{'friends': ['MadD3E', 'Gianluca Lupardini', 'Francesca Coratti', 'Gabriele Capeccia', 'John Sonmez', 'Aggile Giggi', 'The VOICE of ITALY', 'Maurizio_mortadella', 'Luca', 'Cristiano Baldassari', 'Giuseppe Canto', 'GLISTOCKISTI', 'Sindone 2015', 'iSpazio', 'iPhoneItalia', 'devAPP', 'Red Hot ChiliPeppers', 'Cesare Cremonini', 'Lorenzo Jovanotti', 'negramaro', 'Fabio Volo', 'Roberto Saviano', 'Air Soft Italy', 'Hobby Pesca Sport', 'Luciano Ligabue', 'Papa Francesco', 'Movimento 5 Stelle', 'Matteo Renzi', 'Beppe Grillo'], 'ROCCO_SPANU': 1671231413 },{'friends': ['MadD3E', 'Gianluca Lupardini', 'Francesca Coratti', 'Gabriele Capeccia', 'ROCCO_SPANU', 'Aggile Giggi', 'The VOICE of ITALY', 'Luca Casalenuovo', 'Luca', 'Cristiano Baldassari', 'Maurizio_mortadella', 'GLISTOCKISTI', 'Sindone 2015', 'iSpazio', 'iPhoneItalia', 'devAPP', 'Red Hot ChiliPeppers', 'Cesare Cremonini', 'Lorenzo Jovanotti', 'negramaro', 'Fabio Volo', 'Roberto Saviano', 'Air Soft Italy', 'Movimento 5 Stelle', 'Matteo Renzi', 'Beppe Grillo'], 'Maurizio_mortadella': 1674378413 },{'friends': ['MadD3E', 'Gianluca Lupardini', 'Francesca Coratti', 'Gabriele Capeccia', 'John Sonmez', 'ROCCO_SPANU', 'The VOICE of ITALY', 'Luca Casalenuovo', 'devAPP', 'Red Hot ChiliPeppers', 'Cesare Cremonini', 'Lorenzo Jovanotti', 'negramaro', 'Fabio Volo', 'Roberto Saviano', 'Air Soft Italy', 'Hobby Pesca Sport', 'App Store', 'MacRumors.com', 'DJ TechTools', 'Ableton', 'Traktor', 'AudioReel', 'Luciano Ligabue', 'Papa Francesco', 'Movimento 5 Stelle', 'Matteo Renzi', 'Beppe Grillo'], 'Stefino': 1123105413 }]}";
	
	static JSONObject jsonObject = new JSONObject();
	
	
	
	
	public static void main(String[] args) throws JSONException {


//		JSONObject objFather = new JSONObject();
//		
//		List<String> listaUtenti = new ArrayList<String>();
//		List<Long> listaIdUtenti = new ArrayList<Long>();
//
//		listaUtenti.add("franco");
//		
//		listaUtenti.add("antonio");
//		listaUtenti.add("camilla");
//		listaUtenti.add("boboVieri");
//		listaUtenti.add("gustavo");
//		listaUtenti.add("mario");
//		listaUtenti.add("pietro");
//		listaUtenti.add("jessica");
//		listaUtenti.add("rocco");
//		listaUtenti.add("carmelo");
//		
//		listaIdUtenti.add(343212L);
//		
//		listaIdUtenti.add(123123l);
//		listaIdUtenti.add(1312312L);
//		listaIdUtenti.add(12312312l);
//		listaIdUtenti.add(5345345L);
//		listaIdUtenti.add(234234l);
//		listaIdUtenti.add(123123L);
//		listaIdUtenti.add(123123l);
//		listaIdUtenti.add(324234L);
//		listaIdUtenti.add(421412L);
//
//
//
//		List<JSONObject> objUtenti = new ArrayList<JSONObject>();
//
//		for (String utente : listaUtenti) {
//			for (Long longId : listaIdUtenti) {
//				
//				JSONObject obj = new JSONObject();
//				obj.put(utente, longId);
//				
//				JSONArray list = new JSONArray();
//				
//				list.put(listaIdUtenti.get(1));
//				list.put(listaIdUtenti.get(2));
//				list.put(listaIdUtenti.get(3));
//				list.put(listaIdUtenti.get(4));
//				list.put(listaIdUtenti.get(5));
//				list.put(listaIdUtenti.get(6));
//				list.put(listaIdUtenti.get(7));
//				list.put(listaIdUtenti.get(8));
//				list.put(listaIdUtenti.get(9));
//	
//				obj.put("friends", list);	
//				objUtenti.add(obj);
//			}
//			
//		}
//		objFather.put("Utenti", objUtenti);
//
//		
//
//		try {
//
//			FileWriter file = new FileWriter("/home/felagund89/Scrivania/testJson.json");
//			file.write(objFather.toString());
//			file.flush();
//			file.close();
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		System.out.print(objFather);

		
		createGraph();
		
		
	}

	
	
public static ListenableGraph<String, DefaultEdge> createGraph() throws JSONException{
		
		jsonObject= jsonObject.getJSONObject(utentiEAmiciString);
		
		ListenableGraph<String, DefaultEdge> g = new ListenableDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		
		for(int i = 0; i < jsonObject.getJSONArray("utenti").length(); i++){
			
			System.out.println(jsonObject.getJSONArray("utenti").get(i));
			
		}
		
		
//		String v1 = "Vertex1";
//		 String v2 = "Vertex2";
//		 String v3 = "Vertex3";
//		 String v4 = "Vertex4";
//		 String v5 = "Vertex5";
//		 String v6 = "Vertex6";
//		 String v7 = "Vertex7";
//		 String v8 = "Vertex8";
//		 String v9 = "Vertex9";
//
//		 
//		 g.addVertex(v1);
//		 g.addVertex(v2);
//		 g.addVertex(v3);
//		 g.addVertex(v4);
//		 g.addVertex(v5);
//		 g.addVertex(v6);
//		 g.addVertex(v7);
//		 g.addVertex(v8);
//		 g.addVertex(v9);
//
//		 g.addEdge(v1, v2);
//		 g.addEdge(v1, v3);
//		 g.addEdge(v2, v3);
////		 g.addEdge(v2, v4);
//		 g.addEdge(v2, v5);
//		 g.addEdge(v2, v6);
//		 g.addEdge(v2, v7);
//		 g.addEdge(v2, v8);
//		 g.addEdge(v2, v9);
//
//		 
//		 
//		 
//		 
//		 
//		 
//		 
//		
//		 System.out.println("created graph: " + g.toString());
		 
		 return g;
	}
	

//	public static JSONObject readJsonFromFile(){
//
//		JSONParser parser = new JSONParser();
//
//		try {
//
//			Object obj = parser.parse(new FileReader("/home/felagund89/Scrivania/JsonMockPerTestConnectivity.json"));
//
//			JSONObject jsonObject = (JSONObject) obj;
//
//			String name = (String) jsonObject.get("name");
//			System.out.println(name);
//
//			long age = (Long) jsonObject.get("age");
//			System.out.println(age);
//
//			// loop array
//			JSONArray friends = (JSONArray) jsonObject.get("Friends");
//			Iterator<String> iterator = friends.iterator();
//			while (iterator.hasNext()) {
//				System.out.println(iterator.next());
//			}
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//
//	}


}

