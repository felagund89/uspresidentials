package com.uspresidentials.project.task2;

import java.awt.Frame;
import java.io.IOException;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.uspresidentials.project.utils.Authentication;

import edu.uci.ics.jung.graph.DirectedGraph;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public class FriendShipGraph {

	public static void main(String[] args) throws IOException, TwitterException {
		// TODO Auto-generated method stub

		//Authentication.InitializeTwitterObj("");
		//getFriendShip();
		createGraph();
	}
	
	public static void getFriendShip() throws TwitterException{
		
		  User u1 = null ;
	      long cursor = -1;
	      IDs ids;
	      Twitter twitter = TwitterFactory.getSingleton();
	      twitter.setOAuthConsumer("1HERcFVCy5SkpI23hl3FRpJy3", "5PFPlMp3NAsAT1Qbrk1RStXWLMX795ghSUfubtwILl5vR2keyW");
	      AccessToken accessToken = new AccessToken("2977694199-o286ySyyQbCTJsMXcxSfoeSwQ6CkVGQSNl8ILMO", "SKo5MvolhkJxmoG3ADgb2tzW5oOFV7p6A44hmcHY1Pzz1");
	      twitter.setOAuthAccessToken(accessToken);
	      
	      System.out.println("Listing followers's ids.");
	      do {
	              ids = twitter.getFollowersIDs("Carlo Pisani", cursor);  //felagund89
	          for (long id : ids.getIDs()) {
	              System.out.println(id);
	              User user = twitter.showUser(id);
	              System.out.println(user.getName());
	          }
	      } while ((cursor = ids.getNextCursor()) != 0);
	}
	
	public static void createGraph(){
		
		 DefaultDirectedGraph<String, DefaultEdge> g = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		 String v1 = "Vertex1";
		 String v2 = "Vertex2";
		 String v3 = "Vertex3";
		 
		 g.addVertex(v1);
		 g.addVertex(v2);
		 g.addVertex(v3);
	
		 g.addEdge(v1, v2);
		 g.addEdge(v1, v3);
		 g.addEdge(v2, v3);
		 
		 System.out.println("created graph: " + g.toString());
		 
		 //adjustDisplaySettings(g);
		 //getContentPane(  ).add( g );
	}
	
	/*
	 *  private void adjustDisplaySettings( JGraph jg ) {
        jg.setPreferredSize( DEFAULT_SIZE );

        Color  c        = DEFAULT_BG_COLOR;
        String colorStr = null;

        try {
            colorStr = getParameter( "bgcolor" );
        }
         catch( Exception e ) {}

        if( colorStr != null ) {
            c = Color.decode( colorStr );
        }

        jg.setBackground( c );
    }


    private void positionVertexAt( Object vertex, int x, int y ) {
        DefaultGraphCell cell = m_jgAdapter.getVertexCell( vertex );
        Map              attr = cell.getAttributes(  );
        Rectangle        b    = GraphConstants.getBounds( attr );

        GraphConstants.setBounds( attr, new Rectangle( x, y, b.width, b.height ) );

        Map cellAttr = new HashMap(  );
        cellAttr.put( cell, attr );
        m_jgAdapter.edit( cellAttr, null, null, null, null );
    }
    */
	
}
