package org.graph4j.iso.general.improved;

import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.generate.RandomGnpGraphGenerator;
import org.graph4j.iso.IsomorphicGraphMapping;
import org.graph4j.iso.TestUtil;
import org.graph4j.iso.general.GraphIsomorphism;
import org.graph4j.iso.jgrapht_util.TestIsomorphism;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class VF2SubGraphIsomorphismTest {
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
        int n = 400;
        Digraph g1 = new RandomGnpGraphGenerator(n, 0.3).createDigraph();
        Digraph g2 = TestUtil.generateSubgraphFromDigraph(g1, 0.2);

//        System.out.println("Graph1_4j: " + g1);
//        System.out.println("Graph2_4j: " + g2);

        org.jgrapht.Graph<Integer, DefaultEdge> g1_t = TestIsomorphism.convertToJGraphT(g1);
        org.jgrapht.Graph<Integer, DefaultEdge> g2_t = TestIsomorphism.convertToJGraphT(g2);

//        System.out.println("\n--------------------------------------------\nGraph1: " + g1_t);
//        System.out.printf("Graph2: " + g2_t);

        var vf2 = new org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector<>(g1_t, g2_t);
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

