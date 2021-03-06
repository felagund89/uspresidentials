package com.uspresidentials.project.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.uspresidentials.project.utils.AuthenticationManager;
import com.uspresidentials.project.utils.PropertiesManager;

import twitter4j.JSONException;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;


public class FilterUsers {
	
	
	
	final static String PATH_FILE_FILTER_U_BY_LAN_AND_LOC = PropertiesManager.getPropertiesFromFile("PATH_FILE_FILTER_U_BY_LAN_AND_LOC");
	static int NUMERO_UTENTI;
	final static Logger loggerFilteredUsers = Logger.getLogger("filteredUsers");

	static AuthenticationManager authenticationManager = new AuthenticationManager();

	static JSONObject objFather = new JSONObject();
	static List<JSONObject> objUtenti = new ArrayList<JSONObject>();
	static JSONObject objUtente;

	
	
	
	public static void main(String[] args) throws FileNotFoundException,TwitterException, IOException, JSONException {
		try {
			
			getUsersAfterFilter(authenticationManager.twitter);
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
		   
		   
   
	public static void getUsersAfterFilter(Twitter twitter)throws TwitterException, FileNotFoundException, IOException,JSONException, InterruptedException {

		String line;
		String userName = null;
		long idUser = 0;
		int countUserFiltered=0;
		try (BufferedReader br = new BufferedReader(new FileReader(PATH_FILE_FILTER_U_BY_LAN_AND_LOC))) {
			
			//filtrando con questi parametri abbiamo 7096 user.
			loggerFilteredUsers.info("UTENTI FILTRATI PER: \n1-lingua, \n2-localita, \n3-numero totale di tweet, \n4-numero di followers");
			
			
			while ((line = br.readLine()) != null) {
				
				try {
				// definiamo l'id utente, split[0] è il nome utente
				idUser = Long.parseLong((line.split(";")[1]));
				userName = line.split(";")[0];

				// scrivo su file il nome dell'utente che stiamo analizzando
					if (userName != null) {
	
						String location;
						String language;
						int followers;
						int numberOfTotalTweets;
	
						User userAnalize;
						userAnalize = authenticationManager.twitter.showUser(idUser);
	
						if (userAnalize != null) {
							//System.out.println("Analizzo utente "+ userAnalize.getName()+ " rate limit attuale = "+ userAnalize.getRateLimitStatus());
							if (userAnalize.getLang() != null && userAnalize.getTimeZone()!=null) {
								language = userAnalize.getLang();
								location = userAnalize.getTimeZone().toLowerCase();
								followers = userAnalize.getFollowersCount();
								numberOfTotalTweets = userAnalize.getStatusesCount();
								System.out.println("user: " + userAnalize.getName() + " lingua: " + language + " localita: " + location+" numTweet: "+numberOfTotalTweets +" followers: "+followers);
								if (language.equalsIgnoreCase("en") && ( location.contains("us") || location.contains("america"))) {
									if(followers>=1500 && numberOfTotalTweets>=1500){
										countUserFiltered++;
										loggerFilteredUsers.info(countUserFiltered+")"+" User: " + userAnalize.getName() + " Lingua: " + language + " Localita: " + location+" NumTweet: "+numberOfTotalTweets +" Followers: "+followers);
										writeUsersFilteredOnFile(userName+ ";" + idUser + ";");
									}
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
									System.out.println(">>>>>>>>>>>>>>>>l'attesa è finita, ricomincio a filtrare ");
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