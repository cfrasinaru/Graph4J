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
import java.util.Iterator;
import java.util.Set;
import org.graph4j.alg.clique.PivotBronKerboschCliqueIterator;
import org.graph4j.generate.RandomGnpGraphGenerator;
import org.jgrapht.alg.clique.BronKerboschCliqueFinder;

/**
 * Driver class for running the comparisons with other libraries.
 *
 * @author Cristian Frăsinaru
 */
public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        var app = new BronKerboschDemo();
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
        //var app = new DijkstraDemo2();

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
        //var app = new Main();
        //app.benchmark();
        app.demo();
    }

    private void demo() {
        run(this::test);
    }

    private void test() {        
        //int n = 10000;
        //var g = new RandomGnpGraphGenerator(n, 0.5).createGraph();
        //var alg = new DijkstraShortestPathHeap(g, 0);
        //alg.findPath(n-1);
        
        //var g = GraphGenerator.complete(10);
        /*
        var g = new RandomGnpGraphGenerator(50, 0.5).createGraph();
        var alg = new BronKerboschCliqueFinder(g);
        alg.compute();
        */
        //var g = GraphGenerator.complete(30);
        /*
        var g = new RandomGnpGraphGenerator(30, 0.1).createGraph();
        
        var alg = new BronKerboschCliqueIterator(g);
        while (alg.hasNext()) {
            System.out.println("-------------------------> " + alg.next());
        }*/
        
        int n = 30;
        for (int i = 0; i < 100; i++) {
            var g = new RandomGnpGraphGenerator(n, Math.random()).createGraph();
            int c1 = 0;
            var alg1 = new PivotBronKerboschCliqueIterator(g);
            while (alg1.hasNext()) {
                alg1.next();
                c1++;
            }
            int c2 = 0;
            var jg = Converter.createJGraphT(g);
            var alg2 = new BronKerboschCliqueFinder(jg);
            Iterator<Set<Integer>> it = alg2.iterator();
            while (it.hasNext()) {
                it.next();
                c2++;
            }
            if (c1 != c2) {
                System.out.println("Ooops!");
            }
        }
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
