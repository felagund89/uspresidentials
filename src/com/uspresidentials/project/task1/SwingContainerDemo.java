package com.uspresidentials.project.task1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;


public class SwingContainerDemo extends JApplet {

	  private static final Color DEFAULT_BG_COLOR = Color.decode( "#FAFBFF" );
	  private static final Dimension DEFAULT_SIZE = new Dimension( 600, 600 );
	  private JFrame mainFrame;
	  private JLabel headerLabel;
	  private JLabel statusLabel;
	  private JPanel controlPanel;
	  private JLabel msglabel;
	  private JGraphModelAdapter<String, DefaultEdge> m_jgAdapter;
	   
	  public SwingContainerDemo(){
		      prepareGUI();   
	   }
	   
	public static void main(String[] args){
	      SwingContainerDemo  swingContainerDemo = new SwingContainerDemo();  
		  swingContainerDemo.showJFrameDemo();
	}   
	   
	 private void prepareGUI(){
	      mainFrame = new JFrame("Java Swing Examples");
	      mainFrame.setSize(700,700);
	      mainFrame.setLayout(new GridLayout(3, 1));
	      mainFrame.addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent windowEvent){
	            System.exit(0);
	         }        
	      });    
	      headerLabel = new JLabel("", JLabel.CENTER);        
	      statusLabel = new JLabel("",JLabel.CENTER);    

	      msglabel = new JLabel("Welcome to TutorialsPoint SWING Tutorial.", JLabel.CENTER);

	      controlPanel = new JPanel();
	      //controlPanel.setLocation(0,0);
	      //controlPanel.setLayout(new FlowLayout());

	      mainFrame.add(headerLabel);
	      mainFrame.add(controlPanel);
	     // mainFrame.setVisible(true);  
	   }

	   private void showJFrameDemo(){
	      headerLabel.setText("Container in action: JFrame");   

	      final JFrame frame = new JFrame();
	      frame.setSize(700, 700);
	      controlPanel.setLocation(100, 100);
	      
	      //frame.setLayout(new FlowLayout());       
	      frame.add(msglabel);
	      frame.addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent windowEvent){
	            frame.dispose();
	         }        
	      });
	      
	     int distx = 0;
	     int disty = 0;
	      ListenableDirectedGraph<String, DefaultEdge> myGraph = (ListenableDirectedGraph<String, DefaultEdge>) FriendShipGraph.createGraph();
	      FriendShipGraph.searchConnectedComponents(myGraph);
	      
	      m_jgAdapter = new JGraphModelAdapter<String, DefaultEdge>(myGraph);
	      JGraph jgraph = new JGraph( m_jgAdapter);
	      mainFrame.setVisible(true);  
	      
	      adjustDisplaySettings(jgraph);
	      mainFrame.add(jgraph);
	      int sizeMygraph = myGraph.vertexSet().size();
	      
//	      for(int i = 0; i< sizeMygraph ; i++){
//		      positionVertexAt( myGraph.vertexSet().iterator().next() ,distx+5, disty+15 );
//
//	    	  
//	      }
	      Iterator<String> iterator = myGraph.vertexSet().iterator();
	      while(iterator.hasNext()) {
		      positionVertexAt( iterator.next() ,distx+5, disty+15 );
	      }

	      
	      
//	      positionVertexAt( "Vertex1", distx, disty );
//	      positionVertexAt( "Vertex1", distx, disty );
//	      positionVertexAt( "Vertex1", distx, disty );
//	      positionVertexAt( "Vertex1", distx, disty );
//	      positionVertexAt( "Vertex1", distx, disty );
//	      positionVertexAt( "Vertex1", distx, disty );

//	      positionVertexAt( "Vertex1", 130, 40 );

	      //mainFrame.getContentPane().add(jgraph);
	   }
	   
	   private void adjustDisplaySettings( JGraph jg ) {
	        jg.setPreferredSize( DEFAULT_SIZE );
	        Color  c        = DEFAULT_BG_COLOR;
	        String colorStr = null;
//	        jg.setHandleSize (5);
	        jg.setScale (1.5);
	        jg.setAutoResizeGraph(true);
	    
	        try {
	            colorStr = "#FAFBFF";//getParameter( "bgcolor" );
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
//	        Rectangle        b    = (Rectangle) GraphConstants.getBounds( attr );

	        GraphConstants.setBounds( attr, new Rectangle( x, y, 50, 50 ) );

	        Map cellAttr = new HashMap(  );
	        cellAttr.put( cell, attr );
	        m_jgAdapter.edit( cellAttr, null, null, null);
	    }
	 
}

