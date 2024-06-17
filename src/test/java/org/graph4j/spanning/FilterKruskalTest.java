package org.graph4j.spanning;

//package algorithms;
//
//import org.graph4j.Graph;
//import org.graph4j.GraphBuilder;
//import org.graph4j.generate.RandomGnpGraphGenerator;
//import org.junit.jupiter.api.Test;
//
//import java.util.Arrays;
//import org.graph4j.alg.mst.ParallelFilterKruskal;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//// kruskal() is called multiple times
//public class FilterKruskalTest {
//    @Test
//    public void bigGraphThatIsNotTree1() {
//        // different weights
//        Graph graph = GraphBuilder.vertexRange(0, 700).buildGraph();
//
//        // preparing the weights that should be chosen for MST
//        for(int i = 0; i < 699; i++){
//            int j = i + 1;
//            graph.addEdge(i, j, i + 0.5);
//        }
//
//        // adding other weights that should not be chosen for MST
//        for(int i = 0; i < 599; i++){
//            int j = i + 2;
//            graph.addEdge(i, j, j * 500 + 0.5);
//            j = i + 3;
//            graph.addEdge(i, j, j * 500 + 0.1);
//        }
//
//        ParallelFilterKruskal filterKruskal = new ParallelFilterKruskal(graph);
//        assertEquals(699, filterKruskal.getTreeEdges().length);
//
//        // comparing weight (only first 4 decimals)
//        String expectedValue = String.format("%.4f", 244300.5);
//        String actualValue = String.format("%.4f", filterKruskal.getWeight());
//        assertEquals(expectedValue, actualValue);
//
//        // checking edges
//        for(int i = 0; i < 699; i++){
//            int finalI = i;
//            assertTrue(Arrays.stream(filterKruskal.getTree().edges()).anyMatch(edge -> edge.source() == finalI && edge.target() == finalI +1));
//        }
//    }
//
//    @Test
//    public void bigGraphThatIsNotTree2() {
//        // equal weights
//        Graph graph = GraphBuilder.vertexRange(0, 700).buildGraph();
//
//        // preparing a potential MST
//        for(int i = 0; i < 699; i++){
//            int j = i + 1;
//            graph.addEdge(i, j, 0.5);
//        }
//
//        // adding other equal weights
//        for(int i = 0; i < 599; i++){
//            int j = i + 2;
//            graph.addEdge(i, j, 0.5);
//            j = i + 3;
//            graph.addEdge(i, j,  0.5);
//        }
//
//        ParallelFilterKruskal filterKruskal = new ParallelFilterKruskal(graph);
//        assertEquals(699, filterKruskal.getTreeEdges().length);
//
//        // comparing weight (only first 4 decimals)
//        String expectedValue = String.format("%.4f", 349.5);
//        String actualValue = String.format("%.4f", filterKruskal.getWeight());
//        assertEquals(expectedValue, actualValue);
//    }
//    @Test
//    public void bigTree1() {
//        // different weights
//        Graph graph = GraphBuilder.vertexRange(0, 800).buildGraph();
//        // preparing the weights that should be chosen for MST
//        for(int i = 0; i < 799; i++){
//            int j = i + 1;
//            graph.addEdge(i, j, i + 0.5);
//        }
//
//        ParallelFilterKruskal filterKruskal = new ParallelFilterKruskal(graph);
//        assertEquals(799, filterKruskal.getTreeEdges().length);
//
//        // comparing weight (only first 4 decimals)
//        String expectedValue = String.format("%.4f", 319200.5);
//        String actualValue = String.format("%.4f", filterKruskal.getWeight());
//        assertEquals(expectedValue, actualValue);
//
//        // checking edges
//        for(int i = 0; i < 799; i++){
//            int finalI = i;
//            assertTrue(Arrays.stream(filterKruskal.getTree().edges()).anyMatch(edge -> edge.source() == finalI && edge.target() == finalI +1));
//        }
//    }
//
//    @Test
//    public void bigTree2() {
//        // equal weights
//        Graph graph = GraphBuilder.vertexRange(0, 1000).buildGraph();
//        // preparing the weights that should be chosen for MST
//        for(int i = 0; i < 999; i++){
//            int j = i + 1;
//            graph.addEdge(i, j, 0.5);
//        }
//
//        ParallelFilterKruskal filterKruskal = new ParallelFilterKruskal(graph);
//        assertEquals(999, filterKruskal.getTreeEdges().length);
//
//        // comparing weight (only first 4 decimals)
//        String expectedValue = String.format("%.4f", 499.5);
//        String actualValue = String.format("%.4f", filterKruskal.getWeight());
//        assertEquals(expectedValue, actualValue);
//    }
//
//    @Test
//    public void completeGraph1() {
//        // equal weights
//        Graph graph = new RandomGnpGraphGenerator(500, 1).createGraph();
//        Arrays.stream(graph.edges())
//                .forEach(edge -> graph.setEdgeWeight(edge.source(), edge.target(), 2.2));
//        ParallelFilterKruskal filterKruskal = new ParallelFilterKruskal(graph);
//        assertEquals(499, filterKruskal.getTreeEdges().length);
//
//        // comparing weight (only first 4 decimals)
//        String expectedValue = String.format("%.4f", 1097.8);
//        String actualValue = String.format("%.4f", filterKruskal.getWeight());
//
//        assertEquals(expectedValue, actualValue );
//    }
//
//    @Test
//    public void completeGraph2() {
//        // different weights
//        Graph graph = new RandomGnpGraphGenerator(500, 1).createGraph();
//
//        // some very big weights
//        Arrays.stream(graph.edges())
//                .forEach(edge -> graph.setEdgeWeight(edge.source(), edge.target(), 1000.4 + Math.random()*50));
//
//        // preparing the weights that should be chosen for MST
//        for(int i = 0; i < 499; i++){
//            int j = i + 1;
//            graph.setEdgeWeight(i, j, i + 1.5);
//        }
//
//        ParallelFilterKruskal filterKruskal = new ParallelFilterKruskal(graph);
//        assertEquals(499, filterKruskal.getTreeEdges().length);
//
//        // comparing weight (only first 4 decimals)
//        String expectedValue = String.format("%.4f", 124999.5);
//        String actualValue = String.format("%.4f", filterKruskal.getWeight());
//        assertEquals(expectedValue, actualValue);
//
//        // checking edges
//        for(int i = 0; i < 499; i++){
//            int finalI = i;
//            assertTrue(Arrays.stream(filterKruskal.getTree().edges()).anyMatch(edge -> edge.source() == finalI && edge.target() == finalI +1));
//        }
//    }
//}