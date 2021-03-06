package com.uspresidentials.project.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Util {

	final static String PATH_FILE_FRIENDSHIP_JSON = PropertiesManager.getPropertiesFromFile("PATH_FILE_FRIENDSHIP_JSON");
	final static String PATH_FILE_FRIENDSHIP_JSON_UPDATED = PropertiesManager.getPropertiesFromFile("PATH_FILE_FRIENDSHIP_JSON_UPDATED");
	final static String PATH_FILE_USER_OCCURRENCE_TEST = PropertiesManager.getPropertiesFromFile("PATH_FILE_USER_OCCURRENCE_TEST");
	final static String PATH_FILE_USER_JSON_COMPLETE = PropertiesManager.getPropertiesFromFile("PATH_FILE_USER_JSON_COMPLETE");

	public static final List<String> unnecessaryWords = Arrays.asList(""," ", "rt","to","in","and","or", "is", "as", "of", "the", "#", "@","0","1","2","3","4","5","6","7","8","9","10","t.co","t.c","http","https","htt","am","i","pm","p.m","a.m","etc",":","/");
	
	
	public static void main(String[] args) {
		//cleanFileUserFriendsTweets();			
		//updateInfoFileJson();

	}

	public static boolean containsIllegals(String toExamine) {
	    Pattern pattern = Pattern.compile("[~#@*+%{}<>\\[\\]|\"\\_:^]");
	    Matcher matcher = pattern.matcher(toExamine);
	    return matcher.find();
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
	
	
	private static void updateInfoFileJson(){
		//prendo il file json
		JSONParser parser = new JSONParser();

        try {
 
            Object obj = parser.parse(new FileReader(PATH_FILE_FRIENDSHIP_JSON_UPDATED));
 
            JSONObject jsonObject = (JSONObject) obj;
 
            JSONArray listUsers = (JSONArray) jsonObject.get("ListUsers");
            System.out.println("List users old size"+ listUsers.size());
            JSONObject newJsonObject = new JSONObject();
            JSONArray listUsersNew = new JSONArray();
            Hashtable<String, String>userMentionsHashMap = new Hashtable();
            //prendo tutti gli i tweet degli user
            
	            //ciclo sul file contenenente le menzioni dei candidati per ogni utente e creo un hashmap
	            try (BufferedReader br = new BufferedReader(new FileReader(PATH_FILE_USER_OCCURRENCE_TEST))) {
	            	
					String line;
					while ((line = br.readLine()) != null) {
						//splitto i dati per renderli utilizzabili
		            	String[] splittedLineStrings = line.split(";");
		            	String userFile = splittedLineStrings[0];
//		            	System.out.println(userFile);
		            	userMentionsHashMap.put(userFile, splittedLineStrings[1]);
		            	
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
	                  
	            for (int i = 0; i < listUsers.size(); i++) {
	            	JSONObject userJsonObject = (JSONObject) listUsers.get(i);
	            	String idUserJson = Long.toString((long) userJsonObject.get("idUser"));
	            	//appena trovo la corrispondenza tramite idUser procedo ad aggiungere il json array all'object dell'utente già esistente
	            
	            	//System.out.println(idUserJson);

	            	String idUserHash =  userMentionsHashMap.get(idUserJson.trim());
	            	String[] occurrenceStrings = idUserHash.split(" ");

	            	//creo il jsonarray che andra a contenere le nuove informazioni
	            	JSONArray  jsonArrMentions = new JSONArray();
	            	jsonArrMentions.add(occurrenceStrings[0]);
	            	jsonArrMentions.add(occurrenceStrings[1]);
	            	jsonArrMentions.add(occurrenceStrings[2]);
	            	jsonArrMentions.add(occurrenceStrings[3]);

	            	 
            		userJsonObject.put("mentionsCandidates", jsonArrMentions);
//		            System.out.println(count++ +")  idUserJson "+idUserJson+ "  userfile id "+userFile);

            		//System.out.println(userJsonObject.toString());
            		listUsersNew.add(userJsonObject);
	            	//System.out.println("count utente aggiornato num :"+ count++);

	            }
	            
				newJsonObject.put("ListUsers", listUsersNew);


            System.out.println("Prima di aggiornare il json, size newJsonObject :"+newJsonObject.size());
            //aggiorno il file  json con i dati relativi alle menzioni dei candidati
            writeJsonUserOnFile(newJsonObject);
            System.out.println("Fine update file json");

        } catch (Exception e) {
            e.printStackTrace();
        } 
	}
	 
	public static HashMap<String, String> getPartitionUsers(String candidateName){
		
		HashMap<String, String> hashMapCandidateHashMap = new HashMap<>();
		
		//prendo il file json
		JSONParser parser = new JSONParser();
		
        try {
 
            Object obj = parser.parse(new FileReader(PATH_FILE_USER_JSON_COMPLETE));
 
            JSONObject jsonObject = (JSONObject) obj;
 
            JSONArray listUsers = (JSONArray) jsonObject.get("ListUsers");
            //prendo tutti gli i tweet degli user

            for (int i = 0; i < listUsers.size(); i++) {
            	JSONObject userJsonObject = (JSONObject) listUsers.get(i);
                JSONArray mentionsArray = (JSONArray) userJsonObject.get("mentionsCandidates");
                Iterator<String> iterator = mentionsArray.iterator();
                String mentions = mentionsArray.toString();

            	String[] mentionsNum;
            	String numString = null;
        	            
                while (iterator.hasNext()) {
        			String string = iterator.next();

					if(string.contains(candidateName)){
						mentionsNum = string.split(":");
						numString = mentionsNum[1];
						
						//scommentare se si vuole tenere traccia solo degli utenti che hanno menzionato almeno una volta il candidato
						if(!numString.equalsIgnoreCase("0"))  
							hashMapCandidateHashMap.put(userJsonObject.get("userName")+";"+userJsonObject.get("idUser")+";", numString);      	
						break;				
					}		
				}
                
            }          
        }catch(Exception e){
        	e.printStackTrace();
        }
		return hashMapCandidateHashMap;
	}
	
	public static void writeJsonUserOnFile(JSONObject jsonUser) throws IOException {

		// inserire [] inizio e fine cosí da avere un json completo

		PrintWriter writer = new PrintWriter(new FileOutputStream(new File(PropertiesManager.getPropertiesFromFile("PATH_FILE_FRIENDSHIP_JSON_COMPLETE")), true));
		writer.println(jsonUser.toString());
		writer.close();
	}
	 
	
	public static void writeJsonJaccardCandidate(JSONObject jsonUser, String PATH_FILE_JACCARD_JSON) throws IOException {

		// inserire [] inizio e fine cosí da avere un json completo
		PrintWriter writer = new PrintWriter(new FileOutputStream(new File(PATH_FILE_JACCARD_JSON), true));
		writer.println(jsonUser.toString());
		writer.close();
	}
	
	
	public static <K, V> Map<K, V> sortByValue(Map<K, V> map) {
	    List<Entry<K, V>> list = new LinkedList<>(map.entrySet());
	    Collections.sort(list, new Comparator<Object>() {
	        @SuppressWarnings("unchecked")
	        public int compare(Object o1, Object o2) {
	            return ((Comparable<V>) ((Map.Entry<K, V>) (o1)).getValue()).compareTo(((Map.Entry<K, V>) (o2)).getValue());
	        }
	    });

	    Collections.reverse(list);
	    
	    Map<K, V> result = new LinkedHashMap<>();
	    for (Iterator<Entry<K, V>> it = list.iterator(); it.hasNext();) {
	        Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
	        result.put(entry.getKey(), entry.getValue());
	    }
	    return result;
	}
	
	public static String deleteUnnecessaryWords(String completeString) {
		
		String resultClean = "";
		String[] splitted = completeString.split(" ");
		for(int i=0;i<splitted.length;i++){
			if(!unnecessaryWords.contains(splitted[i])){	
				resultClean = resultClean + splitted[i] + " ";
			}
		}
		return resultClean;
	}
	
	
	public static JSONArray sortJsonFileByValue(JSONArray arrayTerms, final String KEY_NAME){
		
		JSONArray sortedJsonArray = new JSONArray();
	    List<JSONObject> jsonValues = new ArrayList<JSONObject>();
	    for (int i = 0; i < arrayTerms.size(); i++) {
	        jsonValues.add((JSONObject) arrayTerms.get(i));
	    }
	    Collections.sort( jsonValues, new Comparator<JSONObject>() {
	        //You can change "Name" with "ID" if you want to sort by ID

	        @Override
	        public int compare(JSONObject a, JSONObject b) {
	            double valA = 0;
	            double valB = 0;

	            try {
	                valA = (double) a.get(KEY_NAME);
	                valB = (double) b.get(KEY_NAME);

	            } 
	            catch (Exception e) {
	            	e.printStackTrace();
	            }
	            
	            if(valA > valB)
	            	return -1;
	            else if (valB > valA) {
					return 1;
				}
	            else return 0;      
	        }
	    });

	    for (int i = 0; i < arrayTerms.size(); i++) {
	        sortedJsonArray.add(jsonValues.get(i));
	    }
		return sortedJsonArray;	
	}
	

	public static Map<String, Double> readJsonFromFile(String pathJsonFile, String fieldJson){
		
		//prendo il file json
		JSONParser parser = new JSONParser();
		Map<String, Double> occurrenceWord = new HashMap<>();
		
        try { 
            Object obj = parser.parse(new FileReader(pathJsonFile));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray listWords = (JSONArray) jsonObject.get(fieldJson);
         
            for (int i = 0; i < listWords.size(); i++) {
            	JSONObject userJsonObject = (JSONObject) listWords.get(i);
            	Double jaccardVal = (double) userJsonObject.get("jaccard");

            	String terms=( (String)userJsonObject.get("term1") +";"+(String) userJsonObject.get("term2"));

            	occurrenceWord.put(terms, jaccardVal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return occurrenceWord;
        
	}
	
//	public static Map<String, String[]> readUsersAndTweetsFromFile(String pathJsonFile, String fieldJson){
//		
//		//prendo il file json
//		JSONParser parser = new JSONParser();
//		Map<String, String[]> userAndTweets = new HashMap<>();
//		ArrayList<String> prova = new ArrayList<String>();
//        try { 
//            Object obj = parser.parse(new FileReader(pathJsonFile));
//            JSONObject jsonObject = (JSONObject) obj;
//            JSONArray listUsers = (JSONArray) jsonObject.get(fieldJson);
//            for (int i = 0; i < listUsers.size(); i++) {
//            	JSONObject userJsonObject = (JSONObject) listUsers.get(i);
//            	String user = (String)userJsonObject.get("userName")+";"+userJsonObject.get("idUser")+";";
//                JSONArray listTweets = (JSONArray) userJsonObject.get("tweets");
//                
//              
//                String[] stringArray = list.toArray(new String[list.size()]);
//                
////                String[] tweets =listTweets.toString().replace("", "").split(" ");
//
//              
//
//            	userAndTweets.put(user,tweets);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//		return userAndTweets;
//        
//	}
//	
//	
	
	
	
	public static Map<String, String> readUsersFromJsonFile (){
		
		//prendo il file json
		JSONParser parser = new JSONParser();
		Map<String, String> tweetsText = new HashMap<>();
        try {
 
            Object obj = parser.parse(new FileReader(PATH_FILE_USER_JSON_COMPLETE));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray listUsers = (JSONArray) jsonObject.get("ListUsers");
            //prendo tutti gli i tweet degli user

	        for (int i = 0; i < listUsers.size(); i++) {
	        	JSONObject userJsonObject = (JSONObject) listUsers.get(i);
	            JSONArray tweetsArray = (JSONArray) userJsonObject.get("tweets");
	            Iterator<String> iterator = tweetsArray.iterator();
	            String tweets = tweetsArray.toString();
  
	            while (iterator.hasNext()) {
	    			String string = iterator.next();
	
						if(!tweets.equalsIgnoreCase("")){  
							tweetsText.put(userJsonObject.get("userName")+";"+userJsonObject.get("idUser")+";", string);   
							System.out.println();
						}						
				}
	        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tweetsText;
	}
}