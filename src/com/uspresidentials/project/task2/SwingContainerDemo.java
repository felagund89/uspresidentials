package com.uspresidentials.project.task2;

import java.awt.*;
import java.awt.event.*;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;


public class SwingContainerDemo {

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
	      mainFrame.setSize(400,400);
	      mainFrame.setLayout(new GridLayout(3, 1));
	      mainFrame.addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent windowEvent){
	            System.exit(0);
	         }        
	      });    
	      headerLabel = new JLabel("", JLabel.CENTER);        
	      statusLabel = new JLabel("",JLabel.CENTER);    

	      statusLabel.setSize(350,100);

	      msglabel = new JLabel("Welcome to TutorialsPoint SWING Tutorial.", JLabel.CENTER);

	      controlPanel = new JPanel();
	      controlPanel.setLayout(new FlowLayout());

	      mainFrame.add(headerLabel);
	      mainFrame.add(controlPanel);
	      mainFrame.add(statusLabel);
	      mainFrame.setVisible(true);  
	   }

	   private void showJFrameDemo(){
	      headerLabel.setText("Container in action: JFrame");   

	      final JFrame frame = new JFrame();
	      frame.setSize(300, 300);
	      frame.setLayout(new FlowLayout());       
	      frame.add(msglabel);
	      frame.addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent windowEvent){
	            frame.dispose();
	         }        
	      });
	      
	      JButton okButton = new JButton("Open a Frame");
	      okButton.addActionListener(new ActionListener() {
	         public void actionPerformed(ActionEvent e) {
	            statusLabel.setText("A Frame shown to the user.");
	            frame.setVisible(true);
	         }
	      });
	      
	      m_jgAdapter = new JGraphModelAdapter(createGraph());
	      JGraph jgraph = new JGraph( m_jgAdapter );
	      mainFrame.getContentPane().add(jgraph);
	      
	      controlPanel.add(okButton);
	      mainFrame.setVisible(true);  
	   }
	   
	   
	   public static ListenableGraph<String, DefaultEdge> createGraph(){
		   ListenableGraph<String, DefaultEdge> g = new ListenableDirectedGraph<String, DefaultEdge>( DefaultEdge.class );
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
		 
			 return g;
	   } 
	   
	  /* public static DefaultDirectedGraph<String, DefaultEdge> createGraph(){
			
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
		 
			 return g;
			 //adjustDisplaySettings(g);
			 //getContentPane(  ).add( g );
		} */

}

