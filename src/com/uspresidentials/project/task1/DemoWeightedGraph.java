package com.uspresidentials.project.task1;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.*;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import org.json.simple.parser.ParseException;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;

import twitter4j.TwitterException;

public class DemoWeightedGraph {

    private static void createAndShowGui() throws FileNotFoundException, TwitterException, IOException, ParseException {
        JFrame frame = new JFrame("DemoGraph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ListenableGraph<String, MyEdge> g = buildGraph();
   
       ListenableDirectedGraph<String, org.jgrapht.graph.DefaultEdge> graphFriendShip = FriendShipGraph.createGraphFromFriendShip(); 
     
        JGraphModelAdapter<String, org.jgrapht.graph.DefaultEdge> graphAdapter = 
                new JGraphModelAdapter<String, org.jgrapht.graph.DefaultEdge>(graphFriendShip);

        //mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
      
        //layout.execute(graphAdapter.getDefaultVertexAttributes());
        JGraph jgraph = new JGraph( graphAdapter);
        frame.add(jgraph); //new mxGraphComponent(graphAdapter)

        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
					createAndShowGui();
				} catch (TwitterException | IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    }

    public static class MyEdge extends DefaultWeightedEdge {
        @Override
        public String toString() {
            return String.valueOf(0.5); //getWeight()
        }
    }

    public static ListenableGraph<String, MyEdge> buildGraph() {
        ListenableDirectedWeightedGraph<String, MyEdge> g = 
            new ListenableDirectedWeightedGraph<String, MyEdge>(MyEdge.class);

        String x1 = "x1";
        String x2 = "x2";
        String x3 = "x3";

        g.addVertex(x1);
        g.addVertex(x2);
        g.addVertex(x3);

        MyEdge e = g.addEdge(x1, x2);
        g.setEdgeWeight(e, 1);
        e = g.addEdge(x2, x3);
        g.setEdgeWeight(e, 2);

        e = g.addEdge(x3, x1);
        g.setEdgeWeight(e, 3);

        return g;
    }
}
