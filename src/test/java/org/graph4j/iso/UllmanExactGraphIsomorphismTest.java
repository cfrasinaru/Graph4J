package org.graph4j.iso;


import org.graph4j.*;
import org.graph4j.generate.RandomGnpGraphGenerator;
import org.graph4j.iso.general.GraphIsomorphism;
import org.graph4j.iso.general.UllmanExactGraphIsomorphism;
import org.junit.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UllmanExactGraphIsomorphismTest {
    private boolean testIsomorphism4J(Graph<?,?> g1, Graph<?,?> g2) {
        System.gc();
        Runtime runtime = Runtime.getRuntime();

        long initialTime = System.currentTimeMillis();
        long usedMemoryBefore =
                runtime.totalMemory() - runtime.freeMemory();

        GraphIsomorphism iso = new UllmanExactGraphIsomorphism(g1, g2);
        boolean isomorphic = iso.areIsomorphic();

        long runningTime = System.currentTimeMillis() - initialTime;
        long usedMemoryAfter =
                runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = usedMemoryAfter - usedMemoryBefore;

        String library = "Graph4J -> isomorphism";
        System.out.println("\t[" + library + "] Running time: " + runningTime + " ms");
        System.out.println("\t[" + library + "]  Memory increase: " + memoryIncrease + " bytes\n\n");

        return isomorphic;
    }

    private List<IsomorphicGraphMapping> testGetAllMappings4J(Graph<?,?> g1, Graph<?,?> g2){
        long initialTime = System.currentTimeMillis();
        long usedMemoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        GraphIsomorphism iso = new UllmanExactGraphIsomorphism(g1, g2);
        List<IsomorphicGraphMapping> mappings = iso.getAllMappings();

        long runningTime = System.currentTimeMillis() - initialTime;
        long usedMemoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryIncrease = usedMemoryAfter - usedMemoryBefore;

        String library = "Graph4J -> all mappings";
        System.out.println("\t[" + library + "] Running time: " + runningTime + " ms");
        System.out.println("\t[" + library + "]  Memory increase: " + memoryIncrease + " bytes\n\n");

        return mappings;
    }

    @Test(expected = NullPointerException.class)
    public void nullGraphs(){
        new UllmanExactGraphIsomorphism(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void differentTypesOfGraphs(){
        Graph<?,?> g1 = GraphBuilder.empty().estimatedNumVertices(5).buildDirectedPseudograph();
        Graph<?,?> g2 = GraphBuilder.empty().estimatedNumVertices(5).buildDirectedMultigraph();

        new UllmanExactGraphIsomorphism(g1, g2);
    }

    @Test
    public void testAutomorphism(){
        Graph<?,?> g = GraphBuilder.vertices(1, 2, 3)
                .addEdges("1-2,2-3,3-1").buildGraph();

        assertTrue(testIsomorphism4J(g, g));

        List<IsomorphicGraphMapping> mapping_list = testGetAllMappings4J(g, g);

        Set<String> mappings = new HashSet<>(Set.of(
                """
                {
                \t1 -> 1
                \t2 -> 2
                \t3 -> 3
                }
                """,
                """
                {
                \t1 -> 1
                \t2 -> 3
                \t3 -> 2
                }
                """,
                """
                {
                \t1 -> 2
                \t2 -> 1
                \t3 -> 3
                }
                """,
                """
                {
                \t1 -> 2
                \t2 -> 3
                \t3 -> 1
                }
                """,
                """
                {
                \t1 -> 3
                \t2 -> 1
                \t3 -> 2
                }
                """,
                """
                {
                \t1 -> 3
                \t2 -> 2
                \t3 -> 1
                }
                """
        ));

        assertEquals(6, mapping_list.size());
        for (var mapping : mapping_list) {
            assertTrue(mapping.isValidIsomorphism());
            assertTrue(mappings.remove(mapping.toString()));
        }
        assertTrue(mappings.isEmpty());
    }

    @Test
    public void testAutomorphismDigraph() {
        Digraph<?,?> g = GraphBuilder.vertices(1, 2, 3)
                .addEdges("1-2,3-2").buildDigraph();

        assertTrue(testIsomorphism4J(g, g));

        List<IsomorphicGraphMapping> mapping_list = testGetAllMappings4J(g, g);
        Set<String> mappings = new HashSet<>(Set.of(
                """
                {
                \t1 -> 1
                \t2 -> 2
                \t3 -> 3
                }
                """,
                """
                {
                \t1 -> 3
                \t2 -> 2
                \t3 -> 1
                }
                """
        ));

        assertEquals(2, mapping_list.size());
        for (var mapping : mapping_list) {
            assertTrue(mapping.isValidIsomorphism());
            System.out.println(mapping);
            mappings.remove(mapping.toString());
        }
        assertTrue(mappings.isEmpty());
    }

    @Test
    public void testWikiExample() {
        // a = 1, b = 2, c = 3, d = 4, g = 5, h = 6, i = 7, j = 8
        Graph<?,?> g1 = GraphBuilder.vertices(1,2,3,4,5,6,7,8).buildGraph();
        g1.addEdge(1, 5);   // a -> g
        g1.addEdge(1, 6);   // a -> h
        g1.addEdge(1, 7);   // a -> i
        g1.addEdge(2, 5);   // b -> g
        g1.addEdge(2, 6);   // b -> h
        g1.addEdge(2, 8);   // b -> j
        g1.addEdge(3, 5);   // c -> g
        g1.addEdge(3, 7);   // c -> i
        g1.addEdge(3, 8);   // c -> j
        g1.addEdge(4, 6);   // d -> h
        g1.addEdge(4, 7);   // d -> i
        g1.addEdge(4, 8);   // d -> j

        Graph<?,?> g2 = GraphBuilder.vertices(1,2,3,4,5,6,7,8).buildGraph();
        g2.addEdge(1, 2);
        g2.addEdge(1, 4);
        g2.addEdge(1, 5);
        g2.addEdge(2, 3);
        g2.addEdge(2, 6);
        g2.addEdge(3, 4);
        g2.addEdge(3, 7);
        g2.addEdge(4, 8);
        g2.addEdge(5, 6);
        g2.addEdge(5, 8);
        g2.addEdge(6, 7);
        g2.addEdge(7, 8);

        assertTrue(testIsomorphism4J(g1, g2));

        List<IsomorphicGraphMapping> mapping_list = testGetAllMappings4J(g1, g2);
        System.out.println("All mappings: ");
        for (var m : mapping_list) {
            System.out.println(m);
            assertTrue(m.isValidIsomorphism());
        }
    }

    @Test
    public void testIsomorphismSimpleGraph() {
        Digraph<?,?> g1 = GraphBuilder.empty().estimatedNumVertices(5).buildDigraph();
        g1.addVertex(1);
        g1.addVertex(2);
        g1.addVertex(3);
        g1.addVertex(4);

        g1.addEdge(1, 2);
        g1.addEdge(2, 3);
        g1.addEdge(3, 4);
        g1.addEdge(4, 1);


        Digraph<?,?> g2 = GraphBuilder.empty().estimatedNumVertices(5).buildDigraph();
        g2.addVertex(65);
        g2.addVertex(66);
        g2.addVertex(67);
        g2.addVertex(68);

        g2.addEdge(65, 66);
        g2.addEdge(66, 68);
        g2.addEdge(68, 67);
        g2.addEdge(67, 65);

        boolean isomorphic = testIsomorphism4J(g1, g2);
        System.out.println("Are isomorphic: " + isomorphic);

        List<IsomorphicGraphMapping> mapping_list = testGetAllMappings4J(g1, g2);
        System.out.println("All mappings: ");
        for (var m : mapping_list) {
            System.out.println(m);
            assertTrue(m.isValidIsomorphism());
        }
    }

    @Test
    public void testIsomorphismSimpleGraph2(){
        Digraph<?,?> g1 = GraphBuilder.vertices(0,1,2,3,4,5).buildDigraph();
        g1.addEdge(0, 1);
        g1.addEdge(0, 2);
        g1.addEdge(0, 3);
        g1.addEdge(0, 4);
        g1.addEdge(0, 5);

        Digraph<?,?> g2 = GraphBuilder.vertices(0,1,2,3,4,5).buildDigraph();
        g2.addEdge(1, 0);
        g2.addEdge(1, 2);
        g2.addEdge(1, 3);
        g2.addEdge(1, 4);
        g2.addEdge(1, 5);

        assertTrue(testIsomorphism4J(g1, g2));

        List<IsomorphicGraphMapping> mapping_list = testGetAllMappings4J(g1, g2);
        System.out.println("All mappings: ");
        for (var m : mapping_list) {
            System.out.println(m);
            assertTrue(m.isValidIsomorphism());
        }
    }

    @Test
    public void testIsoSimpleGraphFalse() {
        Graph<?,?> g1 = GraphBuilder.vertexRange(0, 5).buildGraph();
        g1.addEdge(0, 1);
        g1.addEdge(1, 2);
        g1.addEdge(2, 3);
        g1.addEdge(3, 4);
        g1.addEdge(5, 2);
        g1.addEdge(5, 3);
        g1.addEdge(5, 4);

        Graph<?,?> g2 = GraphBuilder.vertexRange(0, 5).buildGraph();
        g2.addEdge(0, 1);
        g2.addEdge(1, 2);
        g2.addEdge(2, 3);
        g2.addEdge(4, 5);
        g2.addEdge(4, 2);
        g2.addEdge(5, 2);
        g2.addEdge(5, 3);

        assertFalse(testIsomorphism4J(g1, g2));

        /*
            g1
            0 -- 1 -- 2 -- 3 -- 4 -- 5
                      |    |         |
                      |    |_________|
                      |______________|

            g2
            0 -- 1 -- 2 -- 3 -- 5 -- 4
                      |         |    |
                      |_________|    |
                      |______________|

            -> not isomorphic
         */
    }

    @Test
    public void testNoEdges(){
        Digraph<?,?> g1 = GraphBuilder.empty().estimatedNumVertices(3).buildDigraph();
        Digraph<?,?> g2 = GraphBuilder.empty().estimatedNumVertices(3).buildDigraph();

        for (int i = 0; i < 3; ++i)
            g1.addVertex(i);

        for (int i = 3; i < 6; ++i)
            g2.addVertex(i);

        assertTrue(testIsomorphism4J(g1, g2));

        List<IsomorphicGraphMapping> mapping_list = testGetAllMappings4J(g1, g2);
        System.out.println("All mappings: ");
        for (var m : mapping_list) {
            System.out.println(m);
            assertTrue(m.isValidIsomorphism());
        }
    }

    @Test
    public void testStarGraph() {
        Digraph<?,?> g1 = GraphBuilder.vertices(0,1,2,3,4,5).buildDigraph();
        g1.addEdge(0, 1);
        g1.addEdge(0, 2);
        g1.addEdge(0, 3);
        g1.addEdge(0, 4);
        g1.addEdge(0, 5);

        Digraph<?,?> g2 = GraphBuilder.vertices(0,1,2,3,4,5).buildDigraph();
        g2.addEdge(1, 0);
        g2.addEdge(1, 2);
        g2.addEdge(1, 3);
        g2.addEdge(1, 4);
        g2.addEdge(1, 5);

        assertTrue(testIsomorphism4J(g1, g2));
    }

    @Test
    public void testTriangle() {
        Digraph<?,?> g1 = GraphBuilder.empty().estimatedNumVertices(3).buildDigraph();
        for (int i = 0; i < 3; ++i)
            g1.addVertex(i);
        g1.addEdge(0, 1);
        g1.addEdge(0, 2);
        g1.addEdge(1, 2);

        Digraph<?,?> g2 = GraphBuilder.empty().estimatedNumVertices(3).buildDigraph();
        for (int i = 3; i < 6; ++i)
            g2.addVertex(i);
        g2.addEdge(3, 4);
        g2.addEdge(3, 5);
        g2.addEdge(4, 5);

        assertTrue(testIsomorphism4J(g1, g2));

        List<IsomorphicGraphMapping> mapping_list = testGetAllMappings4J(g1, g2);
        System.out.println("All mappings: ");
        for (var m : mapping_list) {
            System.out.println(m);
            assertTrue(m.isValidIsomorphism());
        }
    }

    @Test
    public void testSquare() {
        Digraph<?,?> g1 = GraphBuilder.vertices(1,2,3,4).buildDigraph();
        g1.addEdge(1, 2);
        g1.addEdge(1, 4);
        g1.addEdge(3, 2);
        g1.addEdge(3, 4);

        Digraph<?,?> g2 = GraphBuilder.vertices(1,2,3,4).buildDigraph();
        g2.addEdge(1, 3);
        g2.addEdge(1, 4);
        g2.addEdge(2, 3);
        g2.addEdge(2, 4);

        assertTrue(testIsomorphism4J(g1, g2));

        List<IsomorphicGraphMapping> mapping_list = testGetAllMappings4J(g1, g2);
        System.out.println("All mappings: ");
        for (var m : mapping_list) {
            System.out.println(m);
            assertTrue(m.isValidIsomorphism());
        }
    }

    @Test
    public void testSquare2() {
        Digraph<?,?> g1 = GraphBuilder.vertices(1,2,3,4,5).buildDigraph();
        g1.addEdge(1, 2);
        g1.addEdge(1, 4);
        g1.addEdge(3, 2);
        g1.addEdge(3, 4);
        g1.addEdge(4, 5);

        Digraph<?,?> g2 = GraphBuilder.vertices(1,2,3,4,5).buildDigraph();
        g2.addEdge(1, 3);
        g2.addEdge(1, 4);
        g2.addEdge(2, 3);
        g2.addEdge(2, 4);
        g2.addEdge(4, 5);

        assertTrue(testIsomorphism4J(g1, g2));

        List<IsomorphicGraphMapping> mapping_list = testGetAllMappings4J(g1, g2);
        System.out.println("All mappings: ");
        for (var m : mapping_list) {
            System.out.println(m);
            assertTrue(m.isValidIsomorphism());
        }
    }

    @Test
    public void testSquare3() {
        Digraph<?,?> g1 = GraphBuilder.vertices(1,2,3,4,5,6).buildDigraph();
        g1.addEdge(1, 2);
        g1.addEdge(1, 4);
        g1.addEdge(3, 2);
        g1.addEdge(3, 4);
        g1.addEdge(4, 5);
        g1.addEdge(3, 6);

        Digraph<?,?> g2 = GraphBuilder.vertices(1,2,3,4,5,6).buildDigraph();
        g2.addEdge(1, 3);
        g2.addEdge(1, 4);
        g2.addEdge(2, 3);
        g2.addEdge(2, 4);
        g2.addEdge(4, 5);
        g2.addEdge(2, 6);

        assertTrue(testIsomorphism4J(g1, g2));

        List<IsomorphicGraphMapping> mapping_list = testGetAllMappings4J(g1, g2);
        System.out.println("All mappings: ");
        for (var m : mapping_list) {
            System.out.println(m);
            assertTrue(m.isValidIsomorphism());
        }
    }

    @Test
    public void testDifferentOrders(){
        Digraph<?,?> g1 = GraphBuilder.vertices(1).buildDigraph();
        Digraph<?,?> g2 = GraphBuilder.vertices(1,2).buildDigraph();

        assertFalse(testIsomorphism4J(g1, g2));
    }

    @Test
    public void testDifferentSize(){
        Digraph<?,?> g1 = GraphBuilder.vertices(1, 2).buildDigraph();
        Digraph<?,?> g2 = GraphBuilder.vertices(1, 2).buildDigraph();
        g2.addEdge(1, 2);

        assertFalse(testIsomorphism4J(g1, g2));
    }

    @Test
    public void testSquareMultiGraphs(){
        DirectedMultigraph<?,?> g1 = GraphBuilder.vertices(0, 1, 2, 3, 4)
                .addEdges("0-1,1-2,2-3,0-1,3-4,4-0").buildDirectedMultigraph();

        DirectedMultigraph<?,?> g2 = GraphBuilder.vertices(0, 1, 2, 3, 4)
                .addEdges("0-1,1-3,3-4,0-1,4-2,2-0").buildDirectedMultigraph();

        System.out.println("g1: " + g1);
        System.out.println("g2: " + g2);

        assertTrue(testIsomorphism4J(g1, g2));

        List<IsomorphicGraphMapping> mapping_list = testGetAllMappings4J(g1, g2);
        System.out.println("All mappings: ");
        for (var m : mapping_list) {
            System.out.println(m);
            assertTrue(m.isValidIsomorphism());
        }
    }

    @Test
    public void testNonIsomorphicMultiGraphs(){
        DirectedMultigraph<?,?> g1 = GraphBuilder.vertices(0, 1, 2, 3, 4)
                .addEdges("0-1,1-3,1-2,0-1,3-4").buildDirectedMultigraph();

        DirectedMultigraph<?,?> g2 = GraphBuilder.vertices(0, 1, 2, 3, 4)
                .addEdges("0-1,1-2,0-1,3-4").buildDirectedMultigraph();

        System.out.println("g1: " + g1);
        System.out.println("g2: " + g2);

        assertFalse(testIsomorphism4J(g1, g2));
    }

    @Test
    public void testPseudoGraph() {
        Pseudograph<?,?> g1 = GraphBuilder.vertices(0, 1, 2, 3, 4, 5)
                .addEdges("0-1,0-2,1-2,1-3,2-2,2-4,3-1,3-2,4-2,4-5,5-4").buildPseudograph();

        Pseudograph<?,?> g2 = GraphBuilder.vertices(0, 1, 2, 3, 4, 5)
                .addEdges("0-0,0-3,1-3,2-0,2-5,3-0,3-1,4-0,4-5,5-0,5-2").buildPseudograph();

        assertTrue(testIsomorphism4J(g1, g2));

        List<IsomorphicGraphMapping> mapping_list = testGetAllMappings4J(g1, g2);
        System.out.println("All mappings: ");
        for (var m : mapping_list) {
            System.out.println(m);
            assertTrue(m.isValidIsomorphism());
        }
    }

    @Test
    public void testDirectedPseudoGraph() {
        DirectedPseudograph<?,?> g1 = GraphBuilder.vertices(0, 1, 2, 3, 4, 5)
                .addEdges("0-1,0-2,1-2,1-3,2-2,2-4,3-1,3-2,4-2,4-5,5-4").buildDirectedPseudograph();

        DirectedPseudograph<?,?> g2 = GraphBuilder.vertices(0, 1, 2, 3, 4, 5)
                .addEdges("0-0,0-3,1-3,2-0,2-5,3-0,3-1,4-0,4-5,5-0,5-2").buildDirectedPseudograph();

        assertTrue(testIsomorphism4J(g1, g2));

        List<IsomorphicGraphMapping> mapping_list = testGetAllMappings4J(g1, g2);
        System.out.println("All mappings: ");
        for (var m : mapping_list) {
            System.out.println(m);
            assertTrue(m.isValidIsomorphism());
        }
    }

    @Test
    public void testLargeGraph() {
        int n = 100;
        Graph g1 = new RandomGnpGraphGenerator(n, 0.2).createGraph();
        var iso_graph = TestUtil.generateIsomorphicGraph(g1);
        Graph g2 = iso_graph.first();
        Map<Integer, Integer> mapping = iso_graph.second();

        assertTrue(testIsomorphism4J(g1, g2));
    }
}

