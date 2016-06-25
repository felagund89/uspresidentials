package com.uspresidentials.project.task2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.lang.System.out;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.uci.ics.jung.graph.Graph;
import twitter4j.IDs;
import twitter4j.TwitterException;
import twitter4j.User;


public abstract class Crawler {
//private static final int GRAPH_DEGREE = 25;
    
    private LinkedList<Long> frontier;
    protected TwitterAccountManager accountManager;
    
    private final static float PERCENT = 1.5f;
    private final static int MAX_USERS = 3000;
    
    private HashSet<String> municipalities;
//    protected int remainingExtractionRequests;
    

    protected Graph graph;

    public Crawler(LinkedList<Long> frontier)
    {
        this.frontier = frontier;
        accountManager = new TwitterAccountManager();
        
        graph = new Graph();
        
        try 
        {
            initMunicipalities();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void initMunicipalities() throws FileNotFoundException, IOException
    {
        municipalities = new HashSet<>();
        InputStream fis = null;
        BufferedReader br;
        String line;

        fis = new FileInputStream("../listacomuni.txt");

        br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
        String municipality;
        while ((line = br.readLine()) != null) 
        {
            municipality = line.split(";")[1].toLowerCase();
            municipalities.add(municipality);
        }
        
        //aggiungo anche altre parole che identificano l'Italia
        municipalities.add("italy");
        municipalities.add("italia");
        municipalities.add("florence");
        municipalities.add("venice");
        municipalities.add("milan");
        municipalities.add("rome");
        municipalities.add("naples");
        municipalities.add("tuscany");
        municipalities.add("sicily");
        municipalities.add("vaticano");
        municipalities.add("vatican");

        br.close();
    }
    
    public LinkedList<Long> getFrontier()
    {
        return frontier;
    }

//    protected void changeAppId()
//    {
//        int startAccountIndex = accountManager.getAccountIndex();
//        
//        for(int i = startAccountIndex + 1; ; i++)
//        {
//            accountManager.setAuthentication(i); //utilizzo il prossimo account
//            
//            //se non sono disponibili richieste per questo nuovo account, continuo
//            remainingExtractionRequests = getRemainingExtractionRequests();
//            if(remainingExtractionRequests == 0)
//            {
//                if(accountManager.getAccountIndex() != startAccountIndex)
//                {
//                    //se non ho richieste disponibili, ma devo ancora provare tutti gli account, continuo
//                    continue;
//                }
//                
//                //se ho provato tutti gli account, faccio una sleep di 15 minuti
//                try
//                {
//                    System.out.println("Sleeping...");
//
//                    //sleep di 15 minuti
//                    Thread.sleep(1000*60 * 15 );
//                }
//                catch (InterruptedException e)
//                {
//                    e.printStackTrace();
//                }
//            }
//            else
//            {           
//                System.out.println("Using account number " + accountManager.getAccountIndex() + ".");
//                
//                //se posso fare richieste, termino 
//                return;
//            }
//        }
//    }

    public void bfs(int levelLimit)
    {
        int degreeCounter = 0;

//        remainingExtractionRequests = getRemainingExtractionRequests();
        
//        if(remainingExtractionRequests == 0)
//        {
//            changeAppId();
//        }

        long id;
        long[] ids;
        long cursor = -1;
        User user;
        for (int currLevel = 0; currLevel < levelLimit; currLevel++)
        {
            int currLevelSize = frontier.size();
            
            System.out.println("Level " + currLevel);
            
            //mi scorro il livello corrente
            for (int i = 0; i < currLevelSize; i++)
            {
                //prendo un id del livello corrente
                id = frontier.removeFirst();
                  
                //prendo gli ids adiacenti
                ids = getIDs(id, cursor);
                
                //decido il grado di questo nodo (e di conseguenza quanti adiacenti prendere)
                int nodeDegree = 0;
                int usersListSize = ids.length; //getUsersListSize(id);
                if(usersListSize <= 100)
                {
                    nodeDegree = 1;                    
                }
                else if (usersListSize > 100 && usersListSize <= MAX_USERS)
                {
                    nodeDegree = (int) (usersListSize * PERCENT / 100);
                }
                else if (usersListSize > MAX_USERS)
                {
                    nodeDegree = (int) (MAX_USERS * PERCENT / 100);
                }
                
                for (long idIterator : ids)
                {
                    if (degreeCounter == nodeDegree)
                    {
                        break;
                    }
                                        
                    //se questo utente non ha un numero sufficiente di nodi adiacenti
                    //oppure non è italiano
                    //proseguo all'iterazione successiva    
                    user = getUser(idIterator);
                    if(getUsersListSize(user) < 50 || !isItalian(user))
                    {
                        continue;
                    }
                    
                    //aggiungo un id (idIterator) adiacente ad id
                    frontier.addLast(idIterator);

                    //aggiungo l'arco appena trovato al grafo
                    addEdge(id, idIterator);
                    
                    //incremento il contatore di id estratti e controllo di non aver raggiunto il limite
                    degreeCounter++;
                }
                
                System.out.println("Elaborated " + (i+1) + " of " + currLevelSize + " nodes in level " + currLevel + ".");
                System.out.println("Frontier size in this moment: " + frontier.size() + " users.");

                // setto il counter del grado a zero
                degreeCounter = 0;
            }
        }
    }
    
    public Graph getGraph()
    {
        return graph;
    }
    
//    protected abstract int getRemainingExtractionRequests();
    
    private boolean isItalian(User user)
    {        
        String location = user.getLocation().toLowerCase();
        System.out.println("User " + user.getName() + " is located in: " + location);
        location = location.replaceAll("[\\W_]", " ").replaceAll("\\s+", " ");
        String[] words = location.split("[ ]");
        
        for(String word : words)
        {
            if(municipalities.contains(word))
            {
                System.out.println("The user is italian!");
                return true;
            }
        }
        
        System.out.println("The user is NOT italian!");
        return false;
    }
    
    protected abstract long[] getIDs(long id, long cursor);
    
    protected abstract void addEdge(Long user0, Long user1);
    
    protected abstract int getUsersListSize(User user);
    
    protected User getUser(long id) 
    {
        User user = null;
        int prevIndex = accountManager.getAccountIndex();
        
        while(true)
        {
            try
            {
                user = accountManager.twitter.showUser(id);
                
                if(prevIndex != accountManager.getAccountIndex())
                {
                    System.out.println("Using account number " + accountManager.getAccountIndex() + ".");
                }
                
                break;
            }
            catch (TwitterException ex)
            {
                //if (ex.equals("a0c96a69-1935c143") || ex.getStatusCode() == 401)
                if(ex.getStatusCode() != 429)
                {
                    out.println("Users " + id + " has a private list. Extraction denied!");
                    break;
                }
                
                System.out.println("Request exhausted for account number " + accountManager.getAccountIndex() + ".");
                accountManager.setAuthentication(accountManager.getAccountIndex() + 1);
                
                if(accountManager.getAccountIndex() == prevIndex)
                {
                    try 
                    {
                        int toSleep = accountManager.twitter.getRateLimitStatus().get("/users/show/:id").getSecondsUntilReset() + 1;
                        System.out.println("Sleeping for " + toSleep + " seconds.");
                        Thread.sleep(toSleep * 1000);
                    } 
                    catch (InterruptedException | TwitterException ex1) 
                    {
                        Logger.getLogger(FriendsCrawler.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }
        }
        
        return user;
    }
}
