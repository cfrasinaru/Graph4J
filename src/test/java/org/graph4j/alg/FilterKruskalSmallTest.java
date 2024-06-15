package org.graph4j.alg;

import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.Graphs;
import org.graph4j.generate.RandomGnpGraphGenerator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import org.graph4j.alg.mst.ParallelFilterKruskal;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilterKruskalSmallTest {
    @Test
    public void smallGraphThatIsNotTree() {
        // for so few edges, it goes for the version without partitioning
        Graph graph = GraphBuilder.vertexRange(0, 5).buildGraph();
        graph.addWeightedEdge(0, 1, 5.1);
        graph.addWeightedEdge(0, 2, 1.7);
        graph.addWeightedEdge(0, 3, 2.5);
        graph.addWeightedEdge(0, 4, 0.2);
        graph.addWeightedEdge(1, 2, 1.1);
        graph.addWeightedEdge(1, 3, 22.6);
        graph.addWeightedEdge(2, 5, 10.3);
        graph.addWeightedEdge(3, 5, 3);

        ParallelFilterKruskal filterKruskal = new ParallelFilterKruskal(graph);
        assertEquals(5, filterKruskal.getEdges().size());
        assertEquals(8.5, filterKruskal.getWeight());
        assertTrue(Graphs.isConnected(filterKruskal.getTree()));

        assertTrue(Arrays.stream(filterKruskal.getTree().edges()).anyMatch(edge -> edge.source() == 0 && edge.target() == 2));
        assertTrue(Arrays.stream(filterKruskal.getTree().edges()).anyMatch(edge -> edge.source() == 0 && edge.target() == 3));
        assertTrue(Arrays.stream(filterKruskal.getTree().edges()).anyMatch(edge -> edge.source() == 0 && edge.target() == 4));
        assertTrue(Arrays.stream(filterKruskal.getTree().edges()).anyMatch(edge -> edge.source() == 3 && edge.target() == 5));
        assertTrue(Arrays.stream(filterKruskal.getTree().edges()).anyMatch(edge -> edge.source() == 1 && edge.target() == 2));
    }

    @Test
    public void smallTree() {
        // for so few edges, it goes for the version without partitioning
        Graph graph = GraphBuilder.vertexRange(0, 5).buildGraph();
        graph.addWeightedEdge(0, 4, 0.2);
        graph.addWeightedEdge(1, 2, 1.1);
        graph.addWeightedEdge(0, 2, 1.7);
        graph.addWeightedEdge(0, 3, 2.5);
        graph.addWeightedEdge(3, 5, 3);

        ParallelFilterKruskal filterKruskal = new ParallelFilterKruskal(graph);
        assertEquals(5, filterKruskal.getEdges().size());
        assertEquals(8.5, filterKruskal.getWeight());
        assertTrue(Graphs.isConnected(filterKruskal.getTree()));
        assertArrayEquals(graph.vertices(), filterKruskal.getTree().vertices());
        assertArrayEquals(graph.edges(), filterKruskal.getTree().edges());
    }

    @Test
    public void smallCompleteGraph1() {
        // equal weights
        // for so few edges, it goes for the version without partitioning
        Graph graph = new RandomGnpGraphGenerator(10, 1).createGraph();
        Arrays.stream(graph.edges())
                .forEach(edge -> graph.setEdgeWeight(edge.source(), edge.target(), 1));
        ParallelFilterKruskal filterKruskal = new ParallelFilterKruskal(graph);
        assertEquals(9, filterKruskal.getEdges().size());
        assertEquals(9, filterKruskal.getWeight());
        assertTrue(Graphs.isConnected(filterKruskal.getTree()));
    }

    @Test
    public void smallCompleteGraph2() {
        // different weights
        // for so few edges, it goes for the version without partitioning
        Graph graph = new RandomGnpGraphGenerator(5, 1).createGraph();
        graph.setEdgeWeight(0, 1, 1.7);
        graph.setEdgeWeight(0, 2, 100.6);
        graph.setEdgeWeight(0, 3, 12.4);
        graph.setEdgeWeight(0, 4, 4.7);

        graph.setEdgeWeight(1, 2, 40.7);
        graph.setEdgeWeight(1, 3, 0.5);
        graph.setEdgeWeight(1, 4, 7.9);

        graph.setEdgeWeight(2, 3, 23.1);
        graph.setEdgeWeight(2, 4, 3);

        graph.setEdgeWeight(3, 4, 11);

        ParallelFilterKruskal filterKruskal = new ParallelFilterKruskal(graph);
        assertEquals(4, filterKruskal.getEdges().size());
        assertEquals(9.9, filterKruskal.getWeight());
        assertTrue(Graphs.isConnected(filterKruskal.getTree()));

        assertTrue(Arrays.stream(filterKruskal.getTree().edges()).anyMatch(edge -> edge.source() == 1 && edge.target() == 3));
        assertTrue(Arrays.stream(filterKruskal.getTree().edges()).anyMatch(edge -> edge.source() == 0 && edge.target() == 1));
        assertTrue(Arrays.stream(filterKruskal.getTree().edges()).anyMatch(edge -> edge.source() == 2 && edge.target() == 4));
        assertTrue(Arrays.stream(filterKruskal.getTree().edges()).anyMatch(edge -> edge.source() == 0 && edge.target() == 4));
    }

    @Test
    public void smallCycleGraph() {
        //some edges have the same weight, others do not
        Graph graph = GraphBuilder.vertexRange(0, 6).buildGraph();
        graph.addWeightedEdge(0, 1, 50);
        graph.addWeightedEdge(1, 2, 10);
        graph.addWeightedEdge(2, 3, 20);
        graph.addWeightedEdge(3, 4, 10);
        graph.addWeightedEdge(4, 5, 20);
        graph.addWeightedEdge(5, 6, 30);
        graph.addWeightedEdge(6, 0, 25.2);

        ParallelFilterKruskal filterKruskal = new ParallelFilterKruskal(graph);
        assertEquals(6, filterKruskal.getEdges().size());
        assertEquals(115.2, filterKruskal.getWeight());
        assertTrue(Graphs.isConnected(filterKruskal.getTree()));
    }
}
