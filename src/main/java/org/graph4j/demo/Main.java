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
import org.graph4j.GraphBuilder;
import org.graph4j.alg.clique.BronKerboschCliqueIterator;
import org.graph4j.alg.clique.MaximalCliqueFinder;
import org.graph4j.alg.coloring.BacktrackColoring;
import org.graph4j.alg.coloring.GurobiColoring;
import org.graph4j.generate.RandomGnpGraphGenerator;
import org.graph4j.util.Clique;

/**
 * Driver class for running the comparisons with other libraries.
 *
 * @author Cristian Frăsinaru
 */
public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        var app = new ExactColoringDemo();
        //var app = new GreedyColoringDemo();
        //var app = new GraphMetricsDemo();
        //var app = new BronKerboschDemo();
        //var app = new HopcroftKarpDemo();
        //var app = new PushRelabelDemo();
        //var app = new EdmondsKarpDemo();
        //var app = new CycleDetectionDemo();
        //var app = new KruskalMSTDemo();
        //var app = new PrimMSTDemo();
        //var app = new LineGraphDemo();
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

    private void testMaxClique() {
        int n = 100;
        var g = new RandomGnpGraphGenerator(n, 0.5).createGraph();
        var alg = new MaximalCliqueFinder(g);
        var q1 = alg.getMaximalClique();
        var q2 = alg.findMaximumClique();
        System.out.println(q1.size() + ": " + q1);
        System.out.println(q2.size() + ": " + q2);        
    }

    private void test1() {
        int n = 20;
        var g = new RandomGnpGraphGenerator(n, 0.5).createGraph();
        //var alg = new BacktrackColoring(g);
        var alg = new GurobiColoring(g);
        var col = alg.findColoring();
        System.out.println(col.numUsedColors() + ": " + col);
    }

    private void test() {
        int n = 20;
        for (int i = 0; i < 1000; i++) {
            var g = new RandomGnpGraphGenerator(n, 0.5).createGraph();

            try {
                var alg1 = new BacktrackColoring(g);
                var col1 = alg1.findColoring();

                var alg2 = new org.jgrapht.alg.color.BrownBacktrackColoring(Converter.createJGraphT(g));
                var col2 = alg2.getColoring();

                var alg3 = new GurobiColoring(g);
                var col3 = alg3.findColoring();

                if (col1.numUsedColors() != col2.getNumberColors() || col2.getNumberColors() != col3.numUsedColors()) {
                    System.out.println(col1.numUsedColors() + ", " + col2.getNumberColors() + ", " + col3.numUsedColors());
                    System.out.println(g);
                    break;
                }
            } catch (Exception e) {
                System.out.println(g);
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
