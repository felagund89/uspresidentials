package com.uspresidentials.project.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import twitter4j.JSONException;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import com.uspresidentials.project.utils.AuthenticationManager;
import com.uspresidentials.project.utils.PropertiesManager;

public class FilterUsers {
	
	
	
	final static String PATH_FILE_UTENTI_ID = PropertiesManager 
		      .getPropertiesFromFile("PATH_FILE_UTENTI_ID"); 
		  static int NUMERO_UTENTI; 
		 
		  static AuthenticationManager authenticationManager = new AuthenticationManager(); 
		 
		  static JSONObject objFather = new JSONObject(); 
		  static List<JSONObject> objUtenti = new ArrayList<JSONObject>(); 
		  static JSONObject objUtente; 
		 
		   
		  public static void main(String[] args) throws FileNotFoundException, TwitterException, IOException, JSONException{ 
		    getUsersAfterFilter(authenticationManager.twitter);     
		  } 
		   
		   
		   
		   
		  public static void getUsersAfterFilter(Twitter twitter) throws TwitterException, FileNotFoundException, IOException, JSONException { 
		 
		   
		    try (BufferedReader br = new BufferedReader(new FileReader(PATH_FILE_UTENTI_ID))) { 
		       
		      String line; 
		 
		      while ((line = br.readLine()) != null) { 
		        // definiamo l'id utente, split[0] è il nome utente 
		 
		        String userName; 
		        long idUser; 
		        idUser = Long.parseLong((line.split(";")[1])); 
		        userName = line.split(";")[0]; 
		         
		        objUtente = new JSONObject(); 
		        objUtente.put(userName, idUser); 
		 
		        // scrivo su file il nome dell'utente che stiamo analizzando 
		        getUsersFiltered(authenticationManager.twitter,userName, idUser); 
		 
		      } 
		      System.out.println("FINE ANALISI FILE UTENTI"); 
		    } 
		 
		  } 
		 
		  public static void getUsersFiltered(Twitter twitter,String userName, long idUser) throws TwitterException, IOException, JSONException { 
		 
		   
		    System.out.println("Analizzo utenti..."); 
		    String location; 
		    String language; 
		     
		    try { 
		 
		      User userAnalize; 
		      userAnalize = authenticationManager.twitter.showUser(idUser); 
		      language = userAnalize.getLang(); 
		      location = userAnalize.getTimeZone().toLowerCase(); 
		      System.out.println("lingua: "+language + "  location: "+location); 
		      if(language.equalsIgnoreCase("en") && location.contains("us")){ 
		        writeUsersFilteredOnFile(userAnalize.getName() + ";"+idUser +";");    
		      } 
		           
		    } catch (TwitterException e) { 
		 
		      if (e.getStatusCode() != 429) { 
		        System.out.println("Users Extraction denied!"); 
		      } 
		 
		      authenticationManager.setAuthentication(authenticationManager.getAccountIndex() + 1); 
		   
		      try { 
		        if (authenticationManager.getAccountIndex() == authenticationManager.ACCOUNTS_NUMBER - 1) { 
		 
		          // 
		          int toSleep = authenticationManager.twitter.getRateLimitStatus().get("/users/search").getSecondsUntilReset() + 1; 
		          System.out.println("Sleeping for " + toSleep + " seconds."); 
		          Thread.sleep(toSleep * 1000); 
		        } 
		         
		      } catch (InterruptedException e1) { 
		        e1.printStackTrace(); 
		      } 
		      e.printStackTrace(); 
		    } 
		 
		  } 
		 
		   
		   
		  public static void writeUsersFilteredOnFile(String content) throws FileNotFoundException, UnsupportedEncodingException{ 
		     
		    PrintWriter writer = new PrintWriter(new FileOutputStream(new File(PropertiesManager.getPropertiesFromFile("PATH_FILE_FILTER_USERS")),true)); 
		    writer.println(content); 
		    writer.close(); 
		  } 
}
