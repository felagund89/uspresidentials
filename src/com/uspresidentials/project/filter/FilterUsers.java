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
	
	
	
	final static String PATH_FILE_FILTER_USERS_BY_LANGUAGE = PropertiesManager .getPropertiesFromFile("PATH_FILE_FILTER_USERS_BY_LANGUAGE"); 
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
		   
		   
		   
		   
	public static void getUsersAfterFilter(Twitter twitter)throws TwitterException, FileNotFoundException, IOException,JSONException, InterruptedException {

		String line;
		String userName = null;
		long idUser = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(PATH_FILE_FILTER_USERS_BY_LANGUAGE))) {

			while ((line = br.readLine()) != null) {
				
				try {
				// definiamo l'id utente, split[0] è il nome utente
				idUser = Long.parseLong((line.split(";")[1]));
				userName = line.split(";")[0];

				// scrivo su file il nome dell'utente che stiamo analizzando
					if (userName != null) {
	
						String location;
						String language;
	
						User userAnalize;
						userAnalize = authenticationManager.twitter.showUser(idUser);
	
						if (userAnalize != null) {
							//System.out.println("Analizzo utente "+ userAnalize.getName()+ " rate limit attuale = "+ userAnalize.getRateLimitStatus());
							if (userAnalize.getLang() != null && userAnalize.getTimeZone()!=null) {
								language = userAnalize.getLang();
								location = userAnalize.getTimeZone().toLowerCase();
								System.out.println("user: " + userAnalize.getName() + "lingua: " + language + "localita: " + location);
								if (language.equalsIgnoreCase("en") && ( location.contains("us") || location.contains("america"))) {
									writeUsersFilteredOnFile(userName+ ";" + idUser + ";");
								}
							}
						}
					}
					} catch (TwitterException e) {
	
						if (e.getStatusCode() == 429) {
							System.out.println(">>>>>>>>>>>>>>>>passo al prossimo account per l'analisi ");
							authenticationManager.setAuthentication(authenticationManager.getAccountIndex() + 1);
	
							try {
								if (authenticationManager.getAccountIndex() == authenticationManager.ACCOUNTS_NUMBER - 1) 
								{
	
									//
									int toSleep = authenticationManager.twitter.getRateLimitStatus().get("/users/search").getSecondsUntilReset();
									System.out.println("Sleeping for " + toSleep + " seconds.");
									Thread.sleep(toSleep * 1000);
									System.out.println(">>>>>>>>>>>>>>>>l'attesa è finita ");
								}
									
	
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						
						
						}else{
							System.out.println("Chiamata non riuscita");
	
						}			
				} catch (NumberFormatException e) {
					System.out.println("Nome utente non conforme");
				}
			
			}	
			System.out.println("FINE ANALISI FILE UTENTI");

		} catch (Exception e) {
			System.out.println("Errore");
		}
		
	}
		 
		   
	  public static void writeUsersFilteredOnFile(String content) throws FileNotFoundException{ 
	     
	    PrintWriter writer = new PrintWriter(new FileOutputStream(new File(PropertiesManager.getPropertiesFromFile("PATH_FILE_FILTER_USERS")),true)); 
	    writer.println(content); 
	    writer.close(); 
	  } 
}
