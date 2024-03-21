package org.graph4j.iso.general.improved;

import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.iso.IsomorphicGraphMapping;
import org.graph4j.iso.general.GraphIsomorphism;
import org.junit.Test;

import java.util.List;


public class UllmanSubGraphIsomorphismTest {
    private boolean testIsomorphism4J(Graph<?,?> g1, Graph<?,?> g2) {
        System.gc();
        Runtime runtime = Runtime.getRuntime();

        long initialTime = System.currentTimeMillis();
        long usedMemoryBefore =
                runtime.totalMemory() - runtime.freeMemory();

        GraphIsomorphism iso = new UllmanSubGraphIsomorphism(g1, g2);
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

        GraphIsomorphism iso = new UllmanSubGraphIsomorphism(g1, g2);
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
        Graph g1 = GraphBuilder.vertices(2, 3, 4).addEdges("3-2,3-4").buildGraph();
        Graph g2 = GraphBuilder.vertices(1, 2, 3, 4).addEdges("1-2,3-2,3-4").buildGraph();

        System.out.println("Graph1: " + g1);
        System.out.println("Graph2: " + g2);

        boolean isomorphic = testIsomorphism4J(g1, g2);
        System.out.println("Isomorphic: " + isomorphic);

        List<IsomorphicGraphMapping> maps = testGetAllMappings4J(g1, g2);

        for (var map : maps) {
            System.out.println("Mapping: " + map);
        }
    }

}

