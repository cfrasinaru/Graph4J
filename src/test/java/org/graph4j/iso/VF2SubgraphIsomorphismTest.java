package org.graph4j.iso;

import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.generate.RandomGnpGraphGenerator;
import org.graph4j.iso.general.GraphIsomorphism;
import org.graph4j.iso.general.VF2SubGraphIsomorphism;
import org.graph4j.iso.general.VF2plusSubGraphIsomorphism;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VF2SubgraphIsomorphismTest {
    private boolean testIsomorphism4J(Graph<?,?> g1, Graph<?,?> g2) {
        System.gc();
        Runtime runtime = Runtime.getRuntime();

        long initialTime = System.currentTimeMillis();
        long usedMemoryBefore =
                runtime.totalMemory() - runtime.freeMemory();

        GraphIsomorphism iso = new VF2plusSubGraphIsomorphism(g1, g2);
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

        GraphIsomorphism iso = new VF2plusSubGraphIsomorphism(g1, g2);
        List<IsomorphicGraphMapping> mappings = iso.getAllMappings();

        long runningTime = System.currentTimeMillis() - initialTime;
        long usedMemoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryIncrease = usedMemoryAfter - usedMemoryBefore;

        String library = "Graph4J -> all mappings";
        System.out.println("\t[" + library + "] Running time: " + runningTime + " ms");
        System.out.println("\t[" + library + "]  Memory increase: " + memoryIncrease + " bytes\n\n");

        return mappings;
    }

    @Test
    public void testGraphTypes() {
        Graph dg1 = GraphBuilder.vertices(1, 2).addEdges("1-2").buildDigraph();
        Graph sg1 = GraphBuilder.vertices(1, 2).addEdges("1-2").buildGraph();
        Graph mg1 = GraphBuilder.vertices(1, 2).addEdges("1-2").buildMultigraph();
        Graph pg1 = GraphBuilder.vertices(1, 2).addEdges("1-2").buildPseudograph();

        assertThrows(NullPointerException.class, () -> new VF2SubGraphIsomorphism(null, dg1));
        assertThrows(NullPointerException.class, () -> new VF2SubGraphIsomorphism(dg1, null));

        var iso_1 = new VF2SubGraphIsomorphism(sg1, sg1);
        assertTrue(iso_1.areIsomorphic());
        assertTrue(iso_1.getMapping().isPresent());

        var iso_2 = new VF2SubGraphIsomorphism(dg1, dg1);
        assertTrue(iso_2.areIsomorphic());
        Set<String> mappings = new HashSet<>(Set.of(
                """
                {
                \t1 -> 1
                \t2 -> 2
                }
                """
        ));
        List<IsomorphicGraphMapping> maps = iso_2.getAllMappings();
    }
    @Test
    public void testDigraph() {
        Digraph g1 = GraphBuilder.vertices(2, 3, 4).addEdges("3-2,3-4").buildDigraph();
        Digraph g2 = GraphBuilder.vertices(1, 2, 3, 4).addEdges("1-2,3-2,3-4").buildDigraph();

        System.out.println("Graph1: " + g1);
        System.out.println("Graph2: " + g2);

        boolean isomorphic = testIsomorphism4J(g1, g2);
        System.out.println("Isomorphic: " + isomorphic);

        List<IsomorphicGraphMapping> maps = testGetAllMappings4J(g1, g2);

        for (var map : maps) {
            System.out.println("Mapping: " + map);
        }
    }

    @Test
    public void testLargeGraphs() {
        int n = 100;
        Graph g2 = new RandomGnpGraphGenerator(n, 0.4).createGraph();

        var iso_graph = TestUtil.generateIsomorphicGraph(TestUtil.generateSubgraph(g2, 0.1));
        Graph g1 = iso_graph.first();

        System.out.println("Graph1: " + g1);
        System.out.println("Graph2: " + g2);

        assertTrue(testIsomorphism4J(g1, g2));
    }
}
