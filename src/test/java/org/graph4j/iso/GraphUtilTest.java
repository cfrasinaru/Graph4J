package org.graph4j.iso;

import org.graph4j.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class GraphUtilTest {

    @Test
    void graphToDigraph() {
        Graph g = GraphBuilder.vertices(1, 2, 3, 4).buildGraph();

        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(2, 4);

        Digraph dg = GraphUtil.convertToDigraph(g);

        System.out.println("Dg:" + dg);

        assertTrue(dg.containsVertex(1));
        assertTrue(dg.containsVertex(2));
        assertTrue(dg.containsVertex(3));
        assertTrue(dg.containsVertex(4));

        assertTrue(dg.containsEdge(1, 2) && dg.containsEdge(2, 1));
        assertTrue(dg.containsEdge(2, 3) && dg.containsEdge(3, 2));
        assertTrue(dg.containsEdge(4, 2) && dg.containsEdge(2, 4));
    }

    @Test
    void multiGraphToDigraph() {
        Graph g = GraphBuilder.vertices(1, 2, 3, 4).buildMultigraph();

        g.addEdge(1, 2);
        g.addEdge(2, 1);
        g.addEdge(2, 3);
        g.addEdge(2, 4);

        Digraph dg = GraphUtil.convertToDigraph(g);

        System.out.println("Dg:" + dg);

        assertTrue(dg instanceof DirectedMultigraph<?,?>);

        assertTrue(dg.containsVertex(1));
        assertTrue(dg.containsVertex(2));
        assertTrue(dg.containsVertex(3));
        assertTrue(dg.containsVertex(4));

        assertTrue(dg.containsEdge(1, 2) && dg.containsEdge(2, 1));
        assertTrue(dg.containsEdge(2, 3) && dg.containsEdge(3, 2));
        assertTrue(dg.containsEdge(4, 2) && dg.containsEdge(2, 4));
    }

    @Test
    void pseudoGraphToDigraph() {
        Graph g = GraphBuilder.vertices(1, 2, 3, 4).buildPseudograph();

        g.addEdge(1, 2);
        g.addEdge(2, 1);
        g.addEdge(2, 3);
        g.addEdge(2, 4);
        g.addEdge(4, 4);

        System.out.println("Neighbours of 4: " + Arrays.toString(g.neighbors(4)));
        System.out.println("No. of self-loops of vertex 4: " + ((Pseudograph<?, ?>) g).selfLoops(4));

        Digraph dg = GraphUtil.convertToDigraph(g);

        System.out.println("Dg:" + dg);

        assertTrue(dg instanceof DirectedPseudograph<?,?>);

        assertTrue(dg.containsVertex(1));
        assertTrue(dg.containsVertex(2));
        assertTrue(dg.containsVertex(3));
        assertTrue(dg.containsVertex(4));

        assertTrue(dg.containsEdge(1, 2) && dg.containsEdge(2, 1));
        assertTrue(dg.containsEdge(2, 3) && dg.containsEdge(3, 2));
        assertTrue(dg.containsEdge(4, 2) && dg.containsEdge(2, 4));
        assertTrue(dg.containsEdge(4, 4));
        assertTrue(((DirectedPseudograph<?, ?>) dg).selfLoops(4) == 2);
    }
}