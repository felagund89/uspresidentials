package com.uspresidentials.project.task1;

import static java.lang.System.out;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import twitter4j.IDs;
import twitter4j.RateLimitStatus;
import twitter4j.TwitterException;
import twitter4j.User;

public class FriendsCrawler extends Crawler
	{
	    public FriendsCrawler(LinkedList<Long> frontier)
	    {
	        super(frontier);
	    }

//	    @Override
//	    protected int getRemainingExtractionRequests()
//	    {
//	        try
//	        {
//	            return accountManager.twitter.getRateLimitStatus().get("/friends/ids").getRemaining();
//	        }
//	        catch (TwitterException e)
//	        {
//	            e.printStackTrace();
//	        }
//	        
//	        return -1;
//	    }

//	    @Override
//	    protected long[] getIDs(long id, long cursor)
//	    {
//	        long[] ids = new long[0];
//	        
//	        //se non ho più richieste rimanenti
//	        if(remainingExtractionRequests == 0)
//	        {
//	            //cambio i token di twitter
//	            changeAppId();
//	        }
//	        
//	        try 
//	        {
//	            ids = accountManager.twitter.getFriendsIDs(id, cursor).getIDs();
//	            remainingExtractionRequests--; //decremento le richieste rimanenti
//	        }
//	        catch (TwitterException ex)
//	        {
//	            if (ex.equals("a0c96a69-1935c143"))
//	            {
//	                out.println("Users extraction denied!");
//	            }
//	        }
//	        
//	        return ids;
//	    }
	    
	    @Override
	    protected long[] getIDs(long id, long cursor)
	    {
	        long[] ids = new long[0];
	        int prevIndex = accountManager.getAccountIndex();
	        
	        while(true)
	        {
	            try 
	            {
	                ids = accountManager.twitter.getFriendsIDs(id, cursor).getIDs();
	                
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
	                System.out.println(ex.getMessage() + "Status code: " + ex.getStatusCode() + "\n");
	                
	                System.out.println("Request exhausted for account number " + accountManager.getAccountIndex() + ".");
	                accountManager.setAuthentication(accountManager.getAccountIndex() + 1);
	                
//	                if(getRemainingExtractionRequests() > 0)
//	                {
//	                    continue;
//	                }
	                
	                if(accountManager.getAccountIndex() == prevIndex)
	                {
	                    try 
	                    {
	                        int toSleep = accountManager.twitter.getRateLimitStatus().get("/friends/ids").getSecondsUntilReset() + 1;
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
	        
	        Arrays.sort(ids);
	        return ids;
	    }

	    @Override
	    protected void addEdge(Long user0, Long user1)
	    {
	        graph.addEdge(new Edge(user0, user1));
	    }
	    	
	    @Override
	    protected int getUsersListSize(User user) 
	    {        
	        int size = user != null ? user.getFriendsCount(): 0;
	        
	        return size;
	    }
	    
//	    protected int oldGetAdjacentUsersListSize(long id) 
//	    {
//	        int count = 0;
//	        
//	        try 
//	        {
//	            count = accountManager.twitter.showUser(id).getFriendsCount();
//	        } 
//	        catch (TwitterException ex) 
//	        {
//	            //Logger.getLogger(FriendsCrawler.class.getName()).log(Level.SEVERE, null, ex);
//	            
//	            int toSleep = ex.getRateLimitStatus().getSecondsUntilReset();
//	            toSleep = toSleep < 0 ? 0 : toSleep;
//	            
//	            System.out.println("Sleeping for " + toSleep + " seconds.");
//	            
//	            try 
//	            {
//	                Thread.sleep(toSleep * 1000);
//	            } 
//	            catch (InterruptedException ex1) 
//	            {
//	                Logger.getLogger(FriendsCrawler.class.getName()).log(Level.SEVERE, null, ex1);
//	            }
//	        }
//	        
//	        return count;
//	    }

//	    @Override
//	    protected int getAdjacentUsersListSizeRequests() 
//	    {
//	        try
//	        {
//	            return accountManager.twitter.getRateLimitStatus().get("/friends/ids").getRemaining();
//	        }
//	        catch (TwitterException e)
//	        {
//	            e.printStackTrace();
//	        }
//	        
//	        return -1;
//	    }
}
