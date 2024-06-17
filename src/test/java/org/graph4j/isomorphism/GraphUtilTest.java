package org.graph4j.isomorphism;

//package org.graph4j.iso;
//
//import org.graph4j.*;
//import org.graph4j.alg.ordering.VertexOrderings;
//import org.graph4j.generate.RandomGnpGraphGenerator;
//import org.junit.Test;
//
//import java.util.Arrays;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class GraphUtilTest {
//
//    @Test
//    public void graphToDigraph() {
//        Graph g = GraphBuilder.vertices(1, 2, 3, 4).buildGraph();
//
//        g.addEdge(1, 2);
//        g.addEdge(2, 3);
//        g.addEdge(2, 4);
//
//        Digraph dg = GraphUtil.convertToDigraph(g);
//
//        System.out.println("Dg:" + dg);
//
//        assertTrue(dg.containsVertex(1));
//        assertTrue(dg.containsVertex(2));
//        assertTrue(dg.containsVertex(3));
//        assertTrue(dg.containsVertex(4));
//
//        assertTrue(dg.containsEdge(1, 2) && dg.containsEdge(2, 1));
//        assertTrue(dg.containsEdge(2, 3) && dg.containsEdge(3, 2));
//        assertTrue(dg.containsEdge(4, 2) && dg.containsEdge(2, 4));
//    }
//
//    @Test
//    public void multiGraphToDigraph() {
//        Graph g = GraphBuilder.vertices(1, 2, 3, 4).buildMultigraph();
//
//        g.addEdge(1, 2);
//        g.addEdge(2, 1);
//        g.addEdge(2, 3);
//        g.addEdge(2, 4);
//
//        Digraph dg = GraphUtil.convertToDigraph(g);
//
//        System.out.println("Dg:" + dg);
//
//        assertTrue(dg instanceof DirectedMultigraph<?,?>);
//
//        assertTrue(dg.containsVertex(1));
//        assertTrue(dg.containsVertex(2));
//        assertTrue(dg.containsVertex(3));
//        assertTrue(dg.containsVertex(4));
//
//        assertTrue(dg.containsEdge(1, 2) && dg.containsEdge(2, 1));
//        assertTrue(dg.containsEdge(2, 3) && dg.containsEdge(3, 2));
//        assertTrue(dg.containsEdge(4, 2) && dg.containsEdge(2, 4));
//    }
//
//    @Test
//    public void pseudoGraphToDigraph() {
//        Graph g = GraphBuilder.vertices(1, 2, 3, 4).buildPseudograph();
//
//        g.addEdge(1, 2);
//        g.addEdge(2, 1);
//        g.addEdge(2, 3);
//        g.addEdge(2, 4);
//        g.addEdge(4, 4);
//
//        System.out.println("Neighbours of 4: " + Arrays.toString(g.neighbors(4)));
//        System.out.println("No. of self-loops of vertex 4: " + ((Pseudograph<?, ?>) g).selfLoops(4));
//
//        Digraph dg = GraphUtil.convertToDigraph(g);
//
//        System.out.println("Dg:" + dg);
//
//        assertTrue(dg instanceof DirectedPseudograph<?,?>);
//
//        assertTrue(dg.containsVertex(1));
//        assertTrue(dg.containsVertex(2));
//        assertTrue(dg.containsVertex(3));
//        assertTrue(dg.containsVertex(4));
//
//        assertTrue(dg.containsEdge(1, 2) && dg.containsEdge(2, 1));
//        assertTrue(dg.containsEdge(2, 3) && dg.containsEdge(3, 2));
//        assertTrue(dg.containsEdge(4, 2) && dg.containsEdge(2, 4));
//        assertTrue(dg.containsEdge(4, 4));
//        assertTrue(((DirectedPseudograph<?, ?>) dg).selfLoops(4) == 2);
//    }
//
//    @Test
//    public void testVertexOrderingsNotCorrectOrder() {
//        Graph g = GraphBuilder.vertices(3, 4, 5).addEdges("3-4, 4-5").buildGraph();
//
//        int[] orderedVertices = VertexOrderings.largestDegreeFirst(g);
//        System.out.println("Largest degree first: " + Arrays.toString(orderedVertices));
//    }
//
//    @Test
//    public void testVertexOrderingsIndexOutOfBounds() {
//        Graph g = GraphBuilder.empty().estimatedNumVertices(3).buildGraph();
//        g.addVertex(42321);
//        g.addVertex(32190);
//
//        int[] orderedVertices = VertexOrderings.largestDegreeFirst(g);
//        System.out.println("Largest degree first: " + Arrays.toString(orderedVertices));
//    }
//}