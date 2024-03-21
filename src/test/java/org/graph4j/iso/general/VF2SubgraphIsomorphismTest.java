package org.graph4j.iso.general;

import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.generate.RandomGnpGraphGenerator;
import org.graph4j.iso.IsomorphicGraphMapping;
import org.graph4j.iso.TestUtil;
import org.graph4j.iso.general.GraphIsomorphism;
import org.graph4j.iso.general.VF2SubGraphIsomorphism;
import org.graph4j.iso.jgrapht_util.TestIsomorphism;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class VF2SubgraphIsomorphismTest {
    private boolean testIsomorphism4J(Graph<?,?> g1, Graph<?,?> g2) {
        System.gc();
        Runtime runtime = Runtime.getRuntime();

        long initialTime = System.currentTimeMillis();
        long usedMemoryBefore =
                runtime.totalMemory() - runtime.freeMemory();

        GraphIsomorphism iso = new VF2SubGraphIsomorphism(g1, g2);
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

        GraphIsomorphism iso = new VF2SubGraphIsomorphism(g1, g2);
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
    public void testHugeGraph()
    {
        int n = 100;
        Digraph g1 = new RandomGnpGraphGenerator(n, 0.3).createDigraph();
        Digraph g2 = TestUtil.generateSubgraphFromDigraph(g1, 0.5);

//        System.out.println("Graph1_4j: " + g1);
//        System.out.println("Graph2_4j: " + g2);

        org.jgrapht.Graph<Integer, DefaultEdge> g1_t = TestIsomorphism.convertToJGraphT(g1);
        org.jgrapht.Graph<Integer, DefaultEdge> g2_t = TestIsomorphism.convertToJGraphT(g2);

//        System.out.println("\n--------------------------------------------\nGraph1: " + g1_t);
//        System.out.printf("Graph2: " + g2_t);

        var vf2 = new VF2SubgraphIsomorphismInspector<>(g1_t, g2_t);
        long initialTime = System.currentTimeMillis();
        long usedMemoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        assertTrue(vf2.isomorphismExists());
        long usedMemoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long runningTime = System.currentTimeMillis() - initialTime;
        long memoryIncrease = usedMemoryAfter - usedMemoryBefore;

        System.out.println("\n\t[JGraphT] Running time: " + runningTime + " ms");
        System.out.println("\t[JGraphT]  Memory increase: " + memoryIncrease + " bytes\n\n");


        boolean isomorphic = testIsomorphism4J(g2, g1);
        System.out.println("Isomorphic: " + isomorphic);

    }
}
