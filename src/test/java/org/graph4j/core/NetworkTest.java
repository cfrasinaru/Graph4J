/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.graph4j.core;

import java.util.List;
import org.graph4j.Digraph;
import static org.graph4j.Graph.WEIGHT;
import org.graph4j.GraphUtils;
import org.graph4j.Network;
import static org.graph4j.Network.CAPACITY;
import static org.graph4j.Network.COST;
import static org.graph4j.Network.FLOW;
import org.graph4j.NetworkBuilder;
import org.graph4j.generators.RandomGnpGraphGenerator;
import org.graph4j.measures.GraphMeasures;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Cristian Frăsinaru
 */
public class NetworkTest {

    @Test
    public void simple() {        
        Network g = NetworkBuilder.edges("0-1,1-2,2-3")
                .named("Test")
                .buildNetwork();        
        g.setEdgeData(CAPACITY, 0, 1, 100);
        g.setEdgeData(CAPACITY, 1, 2, 200);
        g.setEdgeData(CAPACITY, 2, 3, 300);
        g.setEdgeData(COST, 0, 1, 10);
        g.setEdgeData(COST, 1, 2, 20);
        g.setEdgeData(COST, 2, 3, 30);
        g.setEdgeData(FLOW, 0, 1, 1);
        g.setEdgeData(FLOW, 1, 2, 2);
        g.setEdgeData(FLOW, 2, 3, 3);
        
        g.setEdgeDataSize(5);
        final int EXTRA = 4;
        g.setEdgeData(EXTRA, 0, 1, 1000);
        
        double totalWeight = 0, totalCapacity = 0, totalCost = 0, totalFlow = 0, totalExtra = 0;
        for (var it = g.edgeIterator(); it.hasNext();) {
            it.next();
            totalCapacity += it.getData(WEIGHT);
            totalCapacity += it.getData(CAPACITY);
            totalCost += it.getData(COST);
            totalFlow += it.getData(FLOW);
            totalExtra += it.getData(EXTRA);
        }
        assertEquals(0, totalWeight);
        assertEquals(600, totalCapacity);
        assertEquals(60, totalCost);
        assertEquals(6, totalFlow);
        assertEquals(1000, totalExtra);
    }

    @Test
    public void copy() {
        Network g = NetworkBuilder.edges("0-1,1-2,2-3,3-1,3-4").buildNetwork();
        var copy = g.copy();
        assertEquals(g, copy);
    }

    @Test
    public void subgraphVertices() {
        Network g = NetworkBuilder.edges("0-1,1-2,2-3,3-1,3-4").buildNetwork();
        g.setEdgeData(CAPACITY, 1, 2, 30);
        g.setEdgeData(COST, 1, 2, 20);
        g.setEdgeData(FLOW, 1, 2, 10);
        var sub = g.subgraph(1, 2, 3);
        assertEquals(3, sub.numVertices());
        assertEquals(3, sub.numEdges());
        assertEquals(30, sub.getEdgeData(CAPACITY, 1, 2));
        assertEquals(20, sub.getEdgeData(COST, 1, 2));
        assertEquals(10, sub.getEdgeData(FLOW, 1, 2));
    }

    @Test
    public void subgraphEdges() {
        Network g = NetworkBuilder.edges("0-1,1-2,2-3,3-1,3-4").buildNetwork();
        g.setEdgeData(CAPACITY, 1, 2, 30);
        g.setEdgeData(COST, 1, 2, 20);
        g.setEdgeData(FLOW, 1, 2, 10);
        var sub = g.subgraph(List.of(g.edge(1, 2), g.edge(2, 3), g.edge(3, 1)));
        assertEquals(3, sub.numVertices());
        assertEquals(3, sub.numEdges());
        assertEquals(30, sub.getEdgeData(CAPACITY,1, 2));
        assertEquals(20, sub.getEdgeData(COST, 1, 2));
        assertEquals(10, sub.getEdgeData(FLOW, 1, 2));
    }

    @Test
    public void digraphToNetwork() {
        int n = 20;
        Digraph g = new RandomGnpGraphGenerator(n, 0.5).createDigraph();
        Network net = GraphUtils.toNetwork(g, 0, n - 1);
        assertEquals(0, net.getSource());
        assertEquals(n - 1, net.getSink());
        assertEquals(g.numVertices(), net.numVertices());
        assertEquals(g.numEdges(), net.numEdges());
        assertArrayEquals(GraphMeasures.indegreeHistogram(g), GraphMeasures.indegreeHistogram(net));
        assertArrayEquals(GraphMeasures.outdegreeHistogram(g), GraphMeasures.outdegreeHistogram(net));
    }
    
    

}
