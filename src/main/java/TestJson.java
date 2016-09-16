package main.java;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.parser.JSONParser;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

public class TestJson {

	public static void main(String[] args) throws JSONException {


		JSONObject objFather = new JSONObject();
		
		List<String> listaUtenti = new ArrayList<String>();
		List<Long> listaIdUtenti = new ArrayList<Long>();

		listaUtenti.add("franco");
		
		listaUtenti.add("antonio");
		listaUtenti.add("camilla");
		listaUtenti.add("boboVieri");
		listaUtenti.add("gustavo");
		listaUtenti.add("mario");
		listaUtenti.add("pietro");
		listaUtenti.add("jessica");
		listaUtenti.add("rocco");
		listaUtenti.add("carmelo");
		
		listaIdUtenti.add(343212L);
		
		listaIdUtenti.add(123123l);
		listaIdUtenti.add(1312312L);
		listaIdUtenti.add(12312312l);
		listaIdUtenti.add(5345345L);
		listaIdUtenti.add(234234l);
		listaIdUtenti.add(123123L);
		listaIdUtenti.add(123123l);
		listaIdUtenti.add(324234L);
		listaIdUtenti.add(421412L);



		List<JSONObject> objUtenti = new ArrayList<JSONObject>();

		for (String utente : listaUtenti) {
			for (Long longId : listaIdUtenti) {
				
				JSONObject obj = new JSONObject();
				obj.put(utente, longId);
				
				JSONArray list = new JSONArray();
				
				list.put(listaIdUtenti.get(1));
				list.put(listaIdUtenti.get(2));
				list.put(listaIdUtenti.get(3));
				list.put(listaIdUtenti.get(4));
				list.put(listaIdUtenti.get(5));
				list.put(listaIdUtenti.get(6));
				list.put(listaIdUtenti.get(7));
				list.put(listaIdUtenti.get(8));
				list.put(listaIdUtenti.get(9));
	
				obj.put("friends", list);	
				objUtenti.add(obj);
			}
			
		}
		objFather.put("Utenti", objUtenti);

		

		try {

			FileWriter file = new FileWriter("D:/Users/aacciard/Desktop/testJson.json");
			file.write(objFather.toString());
			file.flush();
			file.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.print(objFather);

	}


//	public static JSONObject readJsonFromFile(){
//
//		JSONParser parser = new JSONParser();
//
//		try {
//
//			Object obj = parser.parse(new FileReader("c:\\test.json"));
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
//			JSONArray msg = (JSONArray) jsonObject.get("messages");
//			Iterator<String> iterator = msg.iterator();
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

