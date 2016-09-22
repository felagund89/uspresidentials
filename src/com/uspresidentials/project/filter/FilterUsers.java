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
		    try {
				getUsersAfterFilter(authenticationManager.twitter);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}     
		  } 
		   
		   
		   
		   
		  public static void getUsersAfterFilter(Twitter twitter) throws TwitterException, FileNotFoundException, IOException, JSONException, InterruptedException { 
		 
		    String line; 
	        String userName = null; 
	        long idUser = 0; 
		    try (BufferedReader br = new BufferedReader(new FileReader(PATH_FILE_UTENTI_ID))) { 
		       
		      
		      
		      while ((line = br.readLine()) != null) { 
		        // definiamo l'id utente, split[0] è il nome utente 
		 
		        
		        idUser = Long.parseLong((line.split(";")[1])); 
		        userName = line.split(";")[0]; 
		         
		 
		        // scrivo su file il nome dell'utente che stiamo analizzando 
		        if(userName != null){		        	      	
//		        	int min = authenticationManager.twitter.getRateLimitStatus().get("/users/search").getSecondsUntilReset();
//		        	int time=0;
//		        	for (int i = 0; i < authenticationManager.ACCOUNTS_NUMBER; i++) {
//		        		time = authenticationManager.twitter.getRateLimitStatus().get("/users/search").getSecondsUntilReset();
//		        		if (time < min){
//		        			min = time;
//		        			authenticationManager.setAuthentication(i); 
//		        		}        		
//					}
//		        	if(min > 0 ){        		
//		        		 System.out.println("Sleeping for " + min + " seconds."); 
//		        		 Thread.sleep(min * 1000);
//		        	}
		        	getUsersFiltered(authenticationManager.twitter,userName, idUser); 
		        }
		      } 
		      System.out.println("FINE ANALISI FILE UTENTI"); 
		    } catch (TwitterException e) { 
				 
			      if (e.getStatusCode() != 429) { 
			        System.out.println("Chiamata non riuscita"); 
			      }	
			      System.out.println(">>>>>>>>>>>>>>>>passo al prossimo account per l'analisi ");
			      authenticationManager.setAuthentication(authenticationManager.getAccountIndex()+1);
			      if(authenticationManager.getAccountIndex() == authenticationManager.ACCOUNTS_NUMBER-1){ 
		              
				    	//
						int toSleep = authenticationManager.twitter.getRateLimitStatus().get("/users/search").getSecondsUntilReset() + 1;
						System.out.println("Sleeping for " + toSleep + " seconds.");
						Thread.sleep(toSleep * 1000);
					    System.out.println(">>>>>>>>>>>>>>>>l'attesa è finita ");

			        	getUsersFiltered(authenticationManager.twitter,userName, idUser); 
						
			      }      
			}catch (NumberFormatException e){
		        System.out.println("Nome utente non conforme"); 
			}  
		  } 
		 
		  public static void getUsersFiltered(Twitter twitter,String userName, long idUser) throws TwitterException, IOException, JSONException { 
		 
		   
		    System.out.println("Analizzo utente "+ userName); 
		    String location; 
		    String language; 
		     
		    try { 
		 
		      User userAnalize;      
		      userAnalize = authenticationManager.twitter.showUser(idUser); 
		      
		      if(userAnalize != null){ 
		    	  if(userAnalize.getLang() != null){
				      language = userAnalize.getLang(); 
//				      location = userAnalize.getTimeZone().toLowerCase(); 
				      System.out.println("lingua: "+language); 
				      if(language.equalsIgnoreCase("en")){ 
				        writeUsersFilteredOnFile(userAnalize.getName() + ";"+idUser +";");    
				      } 
		    	  }
		      }	           
		    } catch (TwitterException e) { 
		 
		      if (e.getStatusCode() != 429) { 
		        System.out.println("Chiamata non riuscita"); 
		      }	   
		    }catch (NumberFormatException e){
		        System.out.println("Valore non conforme"); 
			}   
		  } 
		 
		   
		   
		  public static void writeUsersFilteredOnFile(String content) throws FileNotFoundException{ 
		     
		    PrintWriter writer = new PrintWriter(new FileOutputStream(new File(PropertiesManager.getPropertiesFromFile("PATH_FILE_FILTER_USERS")),true)); 
		    writer.println(content); 
		    writer.close(); 
		  } 
}
