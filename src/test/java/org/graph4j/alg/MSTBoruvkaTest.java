package org.graph4j.alg;

import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.Graphs;
import org.graph4j.alg.mst.*;
import org.graph4j.generate.EdgeWeightsGenerator;
import org.graph4j.generate.RandomGnpGraphGenerator;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * Test for Boruvka serial and Boruvka parallel
 *
 * @author Sorodoc Cosmin
 */
public class MSTBoruvkaTest {

    public final static int NUM_TESTS_FOR_ANALISE = 20;

    @Test
    public void simpleGraph() {

        // you can visualize the graph using the following link :
        // https://en.wikipedia.org/wiki/Bor%C5%AFvka%27s_algorithm#/media/File:Boruvka's_algorithm_(Sollin's_algorithm)_Anim.gif
        Graph graph = GraphBuilder
                .numVertices(12)
                .buildGraph();

        graph.addEdge(0, 1, 13.0);//for node A
        graph.addEdge(0, 2, 6.0);

        graph.addEdge(1, 2, 7.0);//for node B
        graph.addEdge(1, 3, 1.0);

        graph.addEdge(2, 3, 14.0);//..etc
        graph.addEdge(2, 4, 8.0);
        graph.addEdge(2, 7, 20.0);

        graph.addEdge(3, 4, 9.0);
        graph.addEdge(3, 5, 3.0);

        graph.addEdge(4, 9, 18.0);

        graph.addEdge(6, 7, 15.0);
        graph.addEdge(6, 10, 10.0);
        graph.addEdge(6, 9, 19.0);
        graph.addEdge(6, 8, 5.0);

        graph.addEdge(7, 9, 17.0);

        graph.addEdge(9, 10, 16.0);
        graph.addEdge(9, 11, 4.0);

        graph.addEdge(8, 10, 11.0);
        graph.addEdge(10, 11, 12.0);

        var mst = new BoruvkaMinimumSpanningTreeDefault(graph);

        assertEquals(89.0, mst.getWeight());
        assertEquals(12, mst.getTree().numVertices());
        assertTrue(Graphs.isConnected(mst.getTree()));

        var mstParallel = new BoruvkaMinimumSpanningTreeParallel(graph, 2);
        assertEquals(89.0, mstParallel.getWeight());
        assertEquals(12, mstParallel.getTree().numVertices());
        assertTrue(Graphs.isConnected(mstParallel.getTree()));

    }

    @Test
    public void connectedTree() {
        //if the graph is connected, the MST will also need to be connected
        for (int i = 0; i < 10; ++i) {
            //the number of vertexes is random, between 1 and 100
            //the edge probability is also random
            Graph randomGraph = new RandomGnpGraphGenerator((int) (Math.random() * 100 + 1), Math.random())
                    .createGraph();
            EdgeWeightsGenerator.randomDoubles(randomGraph, 0, 100);

            var mst = new BoruvkaMinimumSpanningTreeDefault(randomGraph);
            var mstParallel = new BoruvkaMinimumSpanningTreeParallel(randomGraph, 2);

            //if the graph is connected, the MST MUST be connected
            if (Graphs.isConnected(randomGraph)) {
                assertTrue(Graphs.isConnected(mst.getTree()));
                assertTrue(Graphs.isConnected(mstParallel.getTree()));
            }
        }
    }

    @Test
    public void comparingToOtherAlgorithms() {
        Random random = new Random();
        double eps = 1.0E-9;
        for (int i = 0; i < 10; ++i) {
            int n = random.nextInt(100) + 10;
            double p = Math.random();
            Graph randomGraph = new RandomGnpGraphGenerator(n, p).createGraph();
            EdgeWeightsGenerator.randomDoubles(randomGraph, 1, 100);

            var mstPrim = new PrimMinimumSpanningTreeDefault(randomGraph);
            double primWeight = mstPrim.getWeight();

            var mstKruskal = new KruskalMinimumSpanningTree(randomGraph);
            double kruskalWeight = mstKruskal.getWeight();

            var mstBoruvka = new BoruvkaMinimumSpanningTreeDefault(randomGraph);
            double boruvkaWeight = mstBoruvka.getWeight();

            var mstKruskalParallel = new KruskalMinimumSpanningTree(randomGraph);
            double kruskalParallelWeight = mstKruskalParallel.getWeight();

            var mstBoruvkaParallel = new BoruvkaMinimumSpanningTreeParallel(randomGraph, 2);
            double boruvkaParallelWeight = mstBoruvkaParallel.getWeight();

            assertTrue(Math.abs(primWeight - boruvkaWeight) < eps);
            assertTrue(Math.abs(kruskalWeight - boruvkaWeight) < eps);

            assertTrue(Math.abs(boruvkaParallelWeight - boruvkaWeight) < eps);
            assertTrue(Math.abs(kruskalParallelWeight - boruvkaParallelWeight) < eps);

            assertTrue(Math.abs(kruskalParallelWeight - boruvkaWeight) < eps);

        }
    }

    //@Test
    public void analiseTimeSmallInstances() {
        //the number of vertexes is random, between 100 and 1_000
        analiseTimeNrVarNodes(100, 1_000);

    }

    //@Test
    public void analiseTimeBigInstances() {
        //the number of vertexes is random, between 1_000 and 2_000
        analiseTimeNrVarNodes(1_000, 2_000);

    }

    //@Test
    public void compareTimeWithDenseGraph() {
        compareTimeNrFixedNodesAndProbability(7_000, 0.6);

    }

    //@Test
    public void compareTimeWithSparseGraph() {//best case for Boruvka
        compareTimeNrFixedNodesAndProbability(20_000, 0.001);

    }

    private void compareTimeNrFixedNodesAndProbability(int nrNodes, double probability) {

        Graph graph = new RandomGnpGraphGenerator(nrNodes, probability).createGraph();
        EdgeWeightsGenerator.randomDoubles(graph, 1_000, 10_000);

        var kruskalMinimumSpanningTree = new KruskalMinimumSpanningTree(graph);
        long start = System.currentTimeMillis();
        System.out.println("kruskalMinimumSpanningTree weight = " + kruskalMinimumSpanningTree.getWeight());
        System.out.println("kruskalMinimumSpanningTree = " + kruskalMinimumSpanningTree.getTree());
        long end = System.currentTimeMillis();

        System.out.println("time = " + (end - start));

        var boruvkaMinimumSpanningTree = new BoruvkaMinimumSpanningTreeDefault(graph);
        start = System.currentTimeMillis();
        System.out.println("boruvkaMinimumSpanningTree weight = " + boruvkaMinimumSpanningTree.getWeight());
        System.out.println("boruvkaMinimumSpanningTree = " + boruvkaMinimumSpanningTree.getTree());
        end = System.currentTimeMillis();
        System.out.println("time = " + (end - start));

        var parallelBoruvka = new BoruvkaMinimumSpanningTreeParallel(graph, 50);
        start = System.currentTimeMillis();
        System.out.println("parallelBoruvka weight = " + parallelBoruvka.getWeight());
        System.out.println("parallelBoruvka = " + parallelBoruvka.getTree());
        end = System.currentTimeMillis();
        System.out.println("time = " + (end - start));

        var primMinimumSpanningTree = new PrimMinimumSpanningTreeDefault(graph);
        start = System.currentTimeMillis();
        System.out.println("primMinimumSpanningTree weight = " + primMinimumSpanningTree.getWeight());
        System.out.println("primMinimumSpanningTree = " + primMinimumSpanningTree.getTree());
        end = System.currentTimeMillis();
        System.out.println("time = " + (end - start));

        var parallelFilterKruskal = new ParallelFilterKruskal(graph);
        start = System.currentTimeMillis();
        System.out.println("parallelKruskalFilter weight = " + parallelFilterKruskal.getWeight());
        System.out.println("parallelKruskalFilter = " + parallelFilterKruskal.getTree());
        end = System.currentTimeMillis();
        System.out.println("time = " + (end - start));

    }

    private void analiseTimeNrVarNodes(int minNrOfVertices, int maxNrOfVertices) {
        long kruskalAvg = 0;
        long kruskalParallelAvg = 0;
        long primAvg = 0;
        long primHeapAvg = 0;
        long boruvkaAvg = 0;
        long boruvkaParallelAvg = 0;

        double avgDiffWeightBoruvka = 0;

        for (int i = 0; i < NUM_TESTS_FOR_ANALISE; ++i) {
            //a random edge probability
            Graph graph = new RandomGnpGraphGenerator(
                    minNrOfVertices + ((int) (Math.random() * (maxNrOfVertices - minNrOfVertices))),
                    Math.random()).createGraph();

            EdgeWeightsGenerator.randomDoubles(graph, 1, 100);

            long start = System.currentTimeMillis();
            var mstPrim = new PrimMinimumSpanningTreeDefault(graph);
            double weightPrim = mstPrim.getWeight();
            primAvg += (System.currentTimeMillis() - start);

            start = System.currentTimeMillis();
            var mstPrimHeap = new PrimMinimumSpanningTreeDefault(graph);
            double weightPrimHeap = mstPrimHeap.getWeight();
            primHeapAvg += (System.currentTimeMillis() - start);

            start = System.currentTimeMillis();
            var mstKruskal = new KruskalMinimumSpanningTree(graph);
            double weightKruskal = mstKruskal.getWeight();
            kruskalAvg += (System.currentTimeMillis() - start);

            start = System.currentTimeMillis();
            var mstBoruvka = new BoruvkaMinimumSpanningTreeDefault(graph);
            double weightBoruvka = mstBoruvka.getWeight();
            boruvkaAvg += (System.currentTimeMillis() - start);

            start = System.currentTimeMillis();
            var mstBoruvkaParallel = new BoruvkaMinimumSpanningTreeParallel(graph, 50);
            double weightBoruvkaParallel = mstBoruvkaParallel.getWeight();
            boruvkaParallelAvg += (System.currentTimeMillis() - start);

            start = System.currentTimeMillis();
            var mstKruskalParallel = new KruskalMinimumSpanningTree(graph);
            double weightKruskalParallel = mstKruskalParallel.getWeight();
            kruskalParallelAvg += (System.currentTimeMillis() - start);

            avgDiffWeightBoruvka = Math.abs(//average difference between the weight of the MSTs and Boruvka's MST
                    (weightPrim + weightPrimHeap + weightKruskal + weightKruskalParallel + weightBoruvkaParallel) / 5.0 - weightBoruvka);
        }

        System.out.println("AvgTime - Prim : " + (double) primAvg / NUM_TESTS_FOR_ANALISE);
        System.out.println("AvgTime - PrimHeap : " + (double) primHeapAvg / NUM_TESTS_FOR_ANALISE);
        System.out.println("AvgTime - Kruskal : " + (double) kruskalAvg / NUM_TESTS_FOR_ANALISE);
        System.out.println("AvgTime - Boruvka : " + (double) boruvkaAvg / NUM_TESTS_FOR_ANALISE);
        System.out.println("AvgTime - KruskalParallel : " + (double) kruskalParallelAvg / NUM_TESTS_FOR_ANALISE);
        System.out.println("AvgTime - BoruvkaParallel : " + (double) boruvkaParallelAvg / NUM_TESTS_FOR_ANALISE);

        System.out.println("\nAvgDiffWeightBoruvka : " + avgDiffWeightBoruvka / NUM_TESTS_FOR_ANALISE);

    }

}
