package com.uspresidentials.project.utils;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Util {

	final static String PATH_FILE_FRIENDSHIP_JSON = PropertiesManager.getPropertiesFromFile("PATH_FILE_FRIENDSHIP_JSON");

	
	
	public static void main(String[] args) {

		
		
		
		cleanFileUserFriendsTweets(PATH_FILE_FRIENDSHIP_JSON);
			
		
		
		
	}

	
	
	private static void cleanFileUserFriendsTweets(String filePath){
		
		
		JSONParser parser = new JSONParser();
		List<String> idUserDaEliminareList = new ArrayList<String>();
        try {
 
            Object obj = parser.parse(new FileReader(filePath));
 
            JSONObject jsonObject = (JSONObject) obj;
 
//            String listUsers = (String) jsonObject.get("ListUsers");
            JSONArray listUsers = (JSONArray) jsonObject.get("ListUsers");
//            System.out.println("listUsers: " + listUsers);
            List<String> idUserTotalList = new ArrayList<String>();
           
            //prendo tutti gli id degli user
            for (int i = 0; i < listUsers.size(); i++) {
            	JSONObject userJsonObject = (JSONObject) listUsers.get(i);
            	idUserTotalList.add((String) userJsonObject.get("idUser").toString());
			}
            
            //scorro i vari array degli amici , e controllo se ogni amico esiste come 
            //id dentro il json, se non esiste lo devo togliere dalla lista di amici.
            for (int i = 0; i < listUsers.size(); i++) {
            	JSONObject userJsonObject = (JSONObject) listUsers.get(i);
            	
            	JSONArray arrayFriends = (JSONArray) userJsonObject.get("friends"); 
            	
            	if(arrayFriends!=null){
	            	Object[] arrayFriendsObject =  arrayFriends.toArray();
	            	for (int j = 0; j < arrayFriendsObject.length; j++) {     
	            		String[] utenteSplittatoString = arrayFriendsObject[j].toString().split(";");
	                	if(! idUserTotalList.contains(utenteSplittatoString[1])){                		
	                		idUserDaEliminareList.add(utenteSplittatoString[1]);          
	                		
	                		//aggiungere l'update dell'array di friends
	                		arrayFriends.toString().replace(","+arrayFriendsObject[j], "");
	                		//bisogna ricreare la struttura json e salvarla.
	                	}          
	            	}        
            	}
			}
            
           
            //stampo la lista degli id degli utenti che vanno tolti dalle liste di friends dei vari utenti del file
            for (String string : idUserDaEliminareList) {
				System.out.println(string);
			}
          
  
        } catch (Exception e) {
            e.printStackTrace();
        }
    }	
}
