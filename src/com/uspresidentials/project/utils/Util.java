package com.uspresidentials.project.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import twitter4j.TwitterException;

public class Util {

	final static String PATH_FILE_FRIENDSHIP_JSON = PropertiesManager.getPropertiesFromFile("PATH_FILE_FRIENDSHIP_JSON");
	final static String PATH_FILE_FRIENDSHIP_JSON_UPDATED = PropertiesManager.getPropertiesFromFile("PATH_FILE_FRIENDSHIP_JSON_UPDATED");

	
	
	public static void main(String[] args) {

		
		
		
		cleanFileUserFriendsTweets();
			
		
		
		
	}

	
	
	private static void cleanFileUserFriendsTweets(){
		
		
		JSONParser parser = new JSONParser();
		List<String> idUserDaEliminareList = new ArrayList<String>();
        try {
 
            Object obj = parser.parse(new FileReader(PATH_FILE_FRIENDSHIP_JSON));
 
            JSONObject jsonObject = (JSONObject) obj;
 
            JSONArray listUsers = (JSONArray) jsonObject.get("ListUsers");
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
	            	for(int j = 0; j < arrayFriendsObject.length; j++) {     
	            		String[] utenteSplittatoString = arrayFriendsObject[j].toString().split(";");
	                	if(! idUserTotalList.contains(utenteSplittatoString[1])){                		
	                		idUserDaEliminareList.add(arrayFriendsObject[j].toString());          
	                	}          
	            	}        
            	}
			}
            
            
            
            System.out.println("persone da togliere");
            //stampo la lista degli id degli utenti che vanno tolti dalle liste di friends dei vari utenti del file
            for (String string : idUserDaEliminareList) {
				System.out.println(string);
			}
            
            
            //aggiorno il file  json 

            replace(idUserDaEliminareList);
            System.out.println("Fine replace");
  
        } catch (Exception e) {
            e.printStackTrace();
        }
    }	
	
	
	
	 private static void replace(List<String> idUserDaEliminareList) {
	     

	      BufferedReader br = null;
	      BufferedWriter bw = null;
	      try {
	         br = new BufferedReader(new FileReader(PATH_FILE_FRIENDSHIP_JSON));
	         bw = new BufferedWriter(new FileWriter(PATH_FILE_FRIENDSHIP_JSON_UPDATED));
	         String line;
	         while ((line = br.readLine()) != null) {
	        	 
	        	 for (String string : idUserDaEliminareList) {
	        		 if (line.contains("\""+string+"\""))
	  	               line = line.replace("\""+string+"\"", " ");
				}
	        	 
	  	        bw.write(line+"\n");

	         }
	      } catch (Exception e) {
	         return;
	      } finally {
	         try {
	            if(br != null)
	               br.close();
	         } catch (IOException e) {
	            //
	         }
	         try {
	            if(bw != null)
	               bw.close();
	         } catch (IOException e) {
	            //
	         }
	      }
	      File oldFile = new File(PATH_FILE_FRIENDSHIP_JSON);
	      oldFile.delete();

	      File newFile = new File(PATH_FILE_FRIENDSHIP_JSON_UPDATED);
	      newFile.renameTo(oldFile);

	   }
	
	
	private void updateInfoFileJson(){
		//prendo il file json
		JSONParser parser = new JSONParser();
		List<String> tweets = new ArrayList<String>();
        try {
 
            Object obj = parser.parse(new FileReader(PATH_FILE_FRIENDSHIP_JSON));
 
            JSONObject jsonObject = (JSONObject) obj;
 
            JSONArray listUsers = (JSONArray) jsonObject.get("ListUsers");
            List<String> idUserTotalList = new ArrayList<String>();
           
            //prendo tutti gli i tweet degli user
            for (int i = 0; i < listUsers.size(); i++) {
            	JSONObject userJsonObject = (JSONObject) listUsers.get(i);
            	tweets.add((String) userJsonObject.get("tweets").toString());
            	
			}
            
            //scorro i vari array degli amici , e controllo se ogni amico esiste come 
            //id dentro il json, se non esiste lo devo togliere dalla lista di amici.
            for (int i = 0; i < listUsers.size(); i++) {
            	JSONObject userJsonObject = (JSONObject) listUsers.get(i);
            	
            	JSONArray arrayFriends = (JSONArray) userJsonObject.get("friends"); 
            	
            	if(arrayFriends!=null){
	            	Object[] arrayFriendsObject =  arrayFriends.toArray();
	            	for(int j = 0; j < arrayFriendsObject.length; j++) {     
	            		String[] utenteSplittatoString = arrayFriendsObject[j].toString().split(";");
	                	if(! idUserTotalList.contains(utenteSplittatoString[1])){                		
	                		idUserDaEliminareList.add(arrayFriendsObject[j].toString());          
	                	}          
	            	}        
            	}
			}
            
            
            
           
            
            
            //aggiorno il file  json 

            System.out.println("Fine update file json");
		
		
		
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	 
	 
	 
	 
}
