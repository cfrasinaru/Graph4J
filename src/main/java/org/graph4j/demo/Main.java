/*
 * Copyright (C) 2022 Cristian Frăsinaru and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graph4j.demo;

import java.io.FileNotFoundException;
import org.graph4j.Digraph;
import org.graph4j.GraphBuilder;
import org.graph4j.Graphs;
import org.graph4j.alg.connectivity.BridgeDetectionAlgorithm;
import org.graph4j.generate.RandomGnpGraphGenerator;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;

/**
 * Driver class for running the comparisons with other libraries.
 *
 * @author Cristian Frăsinaru
 */
class Main {

    public static void main(String[] args) throws FileNotFoundException {
        //var app = new BronKerboschDemo();
        //var app = new HopcroftKarpDemo();
        //var app = new PushRelabelDemo();
        //var app = new EdmondsKarpDemo();
        //var app = new CycleDetectionDemo();
        //var app = new KruskalMSTDemo();
        //var app = new PrimMSTDemo();
        //var app = new LineGraphDemo();
        //var app = new GreedyColoringDemo();
        //var app = new BipartiteDemo();
        //var app = new StrongConnectivityDemo();
        //var app = new BiconnectivityDemo();
        //var app = new ConnectivityDemo();
        //var app = new EulerianCircuitDemo();
        //var app = new BellmanFordDemo();
        //var app = new FloydWarshallDemo();
        //var app = new BidirectionalDijkstraDemo();
        //var app = new DijkstraDemo1();

        //var app = new DFSVisitorDemo();
        //var app = new BFSVisitorDemo();
        //var app = new BFSIteratorDemo();
        //var app = new DFSIteratorDemo();
        //var app = new RemoveNodesDemo();
        //var app = new RemoveEdgesDemo();
        //var app = new ContainsEdgeDemo();
        //var app = new IterateSuccessorsDemo();
        //var app = new IteratePredecessorsDemo();
        //var app = new WeightedGraphDemo();
        //var app = new LabeledGraphDemo();
        //var app = new CopyGraphDemo();
        //var app = new RandomGraphDemo();
        //var app = new SparseGraphDemo();
        //var app = new CompleteGraphDemo();
        //var app = new EmptyGraphDemo();
        var app = new Main();
        //app.benchmark();
        app.demo();
    }

    private void demo() {
        run(this::test);
    }

    private void test() {
        var g = GraphBuilder.empty().buildGraph();
        int v = g.addVertex();
        System.out.println(v);
        v = g.addVertex();
        System.out.println(v);
        
        //var g = GraphBuilder.numVertices(6).addEdges("0-1,1-2,2-0,1-3,3-4,4-5,5-3").buildGraph();
        //var g = GraphGenerator.complete(6);
        //var g = GraphGenerator.path(6);
        /*
        var g = GraphGenerator.cycle(6);
        var alg = new BridgeDetectionAlgorithm(g);
        System.out.println(alg.getBridges());
         */

 /*
        int n = 10;
        for (int i = 0; i < 100; i++) {
            Digraph g = new RandomGnpGraphGenerator(n, 0.5).createDigraph();
            EdgeWeightsGenerator.randomIntegers(g, 0, 9);
            //System.out.println(g);
            //Graph g = GraphGenerator.path(n);           
            //EdgeWeightsGenerator.fill(g, 1);
            var alg1 = new DijkstraShortestPathHeap(g, 0);
            var alg2 = new BidirectionalDijkstra(g, 0, n - 1);
            double x1 = alg1.getPathWeight(n - 1);
            double x2 = alg2.getPathWeight();
            if (x1 != x2) {
                System.out.println(g);
                System.out.println(Graphs.transpose(g));
                System.out.println(x1);
                System.out.println(x2);
                break;
            }
        }*/
    }

    protected void run(Runnable snippet) {
        System.gc();
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        long t0 = System.currentTimeMillis();
        snippet.run();
        long t1 = System.currentTimeMillis();
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = usedMemoryAfter - usedMemoryBefore;
        System.out.println((t1 - t0) + " ms");
        System.out.println(memoryIncrease / (1024 * 1024) + " MB");
        System.out.println("------------------------------------------------");
    }

}
