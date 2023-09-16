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
import java.util.Arrays;
import org.graph4j.Graphs;
import org.graph4j.alg.coloring.BacktrackColoring;
import org.graph4j.alg.coloring.GurobiAssignmentColoring;
import org.graph4j.alg.coloring.bw.BacktrackBandwithColoring;
import org.graph4j.alg.coloring.bw.GurobiBandwithColoring;
import org.graph4j.alg.coloring.bw.GurobiOptBandwithColoring;
import org.graph4j.alg.coloring.eq.GurobiAssignmentEquitableColoring;
import org.graph4j.alg.coloring.eq.GurobiStableModelEquitableColoring;
import org.graph4j.alg.cut.GreedyVertexSeparator;
import org.graph4j.alg.cut.GurobiVertexSeparator;
import org.graph4j.generate.EdgeWeightsGenerator;
import org.graph4j.generate.GraphGenerator;
import org.graph4j.io.DimacsIO;

/**
 * Driver class for running the comparisons with other libraries.
 *
 * @author Cristian Frăsinaru
 */
public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        var app = new Main();
        //var app = new ExactBandwithColoringDemo();
        //var app = new ExactEquitableColoringDemo();
        //var app = new ExactColoringDemo();        
        //var app = new GraphMetricsDemo();
        //var app = new TriangleCounterDemo();
        //var app = new GreedyColoringDemo();
        //var app = new BronKerboschDemo();
        //var app = new MaximalMatchingDemo();
        //var app = new HopcroftKarpDemo();
        //var app = new PushRelabelDemo();
        //var app = new EdmondsKarpDemo();
        //var app = new CycleDetectionDemo();
        //var app = new BoruvkaMSTDemo();
        //var app = new KruskalMSTDemo();
        //var app = new PrimMSTDemo();
        //var app = new LineGraphDemo();
        //var app = new BipartiteDemo();
        //var app = new StrongConnectivityDemo();
        //var app = new BiconnectivityDemo();
        //var app = new ConnectivityDemo();
        //var app = new EulerianCircuitDemo();
        //var app = new BellmanFordDemo();
        //var app = new JohnsonDemo();
        //var app = new FloydWarshallDemo();
        //var app = new BidirectionalDijkstraDemo();
        //var app = new DijkstraDemo1();
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
        //app.benchmark();
        app.demo();
    }

    private void demo() {
        run(this::test);
    }

    private void test() {
        int n = 100;
        double p = 0.1;
        
        var g = GraphGenerator.randomGnp(n, p);
        var alg1 = new GreedyVertexSeparator(g);
        var sep1 = alg1.getSeparator();
        System.out.println("Greedy: separator size=" + sep1.separator().size() + "\n" + sep1);
        
        var alg2 = new GurobiVertexSeparator(g);
        var sep2 = alg2.getSeparator();
        System.out.println("Gurobi: separator size=" + sep2.separator().size() + "\n" + sep2);
    }

    private void test0() {

        /*
        var g = GraphBuilder.numVertices(7).addCycle(0, 1, 2, 3, 4)
                .addEdge(4, 5)
                .addEdge(4, 6)
                .addEdge(3, 6)
                .buildGraph();
         */
        //var g = GraphGenerator.randomGnp(100, 0.9);
        //var g = GraphGenerator.complete(5);
        //var g = GraphGenerator.cycle(15);
        //var g = new DimacsIO().read("d:/datasets/coloring/instances/DSJC1000.9.col");
        var g = new DimacsIO().read("d:/datasets/coloring/instances/queen9_9.col");
        var alg = new GurobiStableModelEquitableColoring(g, 1 * 60 * 1000);
        //var alg = new GurobiAssignmentEquitableColoring(g, 1 * 60 * 1000);
        //var alg = new BacktrackEquitableColoring(g, 2 * 60 * 1000);

        //var alg = new BacktrackColoring(g, 1 * 60 * 1000);
        //var alg = new ChocoColoring(g, 1 * 60 * 1000);
        //var alg = new GurobiAssignmentColoring(g, 1 * 60 * 1000);
        var col = alg.findColoring();
        if (col != null) {
            System.out.println(col.numUsedColors());
            System.out.println(col);
        }

        //System.out.println(Arrays.toString(g.degrees()));
        //new BacktrackColoring(g).findColoring();
        //var alg = new MaximumInducedPath(g);
        //var path = alg.findPath();
        //System.out.println("size=" + path.numVertices());
        //var alg = new MaximumInducedCycle(g);
        //System.out.println(alg.findCycle());
    }

    private void test1() {
        //for (int i = 0; i < 1000; i++) {
        int n = 20;
        try {
            //var g = GraphGenerator.randomGnp(n, 0.5);
            //EdgeWeightsGenerator.randomIntegers(g, 1, 5);
            var g = new DimacsIO().read("d:/datasets/coloring/instances/geom20.col");
            //var g = GraphGenerator.empty(1);
            System.out.println(g);
            System.out.println("connected=" + Graphs.isConnected(g));
            //var alg = new BacktrackBandwithColoring(g, 60 * 1000);
            var alg = new GurobiOptBandwithColoring(g, 1 * 60 * 1000);
            //var alg = new GreedyBandwithColoring(g);
            //var alg = new ParallelBacktrackColoring(g);
            //var alg = new GurobiAssignmentEquitableColoring(g);

            var col = alg.findColoring();
            if (col != null) {
                System.out.println("max color=" + col.maxColorNumber());
                System.out.println(col.getColorClasses());
            }

            /*
            var cols = alg.findAllColorings(21, 0);
            for (var col : cols) {
                System.out.println(col);
            }*/
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }
        //}
        //var g = GraphGenerator.cycle(n);
        //var g = new DimacsIO().read("d:/datasets/coloring/instances/mug100_1.col");
        //var g = new DimacsIO().read("d:/datasets/coloring/instances/mug88_1.col");
        //var g = new DimacsIO().read("d:/datasets/coloring/instances/1-Insertions_4.col");
        //var g = new DimacsIO().read("d:/datasets/coloring/instances/1-FullIns_4.col");        
        //var g = new DimacsIO().read("d:/datasets/coloring/instances/queen5_5.col");        
        //System.out.println(Graphs.isConnected(g));
        //var alg = new ZykovColoring(g);        
        //var alg = new SimpleBacktrackColoring(g);
        //var alg = new ParallelBacktrackColoring(g);
        //System.out.println(alg.findColoring().numUsedColors()); 
    }

    private void test2() {
        int n = 30;
        for (int i = 0; i < 100; i++) {
            var g = GraphGenerator.randomGnp(n, Math.random());
            try {
                var alg1 = new BacktrackColoring(g);
                var col1 = alg1.findColoring();
                assert alg1.isValid(col1);

                var alg2 = new GurobiAssignmentColoring(g);
                var col2 = alg2.findColoring();
                assert alg2.isValid(col2);

                if (col1.maxColorNumber() != col2.maxColorNumber()) {
                    System.out.println("OOPS!\n");
                    System.out.println(col1.maxColorNumber() + ", " + col2.maxColorNumber());
                    System.out.println(col1);
                    System.out.println(col2);
                    System.out.println(g);
                    break;
                }
            } catch (Exception e) {
                System.err.println(e);
                e.printStackTrace();
                break;
            }
        }
    }

    private void test3() {
        int n = 20;
        for (int i = 0; i < 10; i++) {
            var g = GraphGenerator.randomGnp(n, Math.random());
            EdgeWeightsGenerator.randomIntegers(g, 1, 5);

            try {
                var alg1 = new BacktrackBandwithColoring(g);
                var col1 = alg1.findColoring();
                assert alg1.isValid(col1);

                var alg2 = new GurobiBandwithColoring(g);
                var col2 = alg2.findColoring();
                assert alg2.isValid(col2);

                if (col1.maxColorNumber() != col2.maxColorNumber()) {
                    System.out.println("OOPS!\n");
                    System.out.println(col1.maxColorNumber() + ", " + col2.maxColorNumber());
                    System.out.println(col1);
                    System.out.println(col2);
                    System.out.println(g);
                    break;
                }
            } catch (Exception e) {
                System.err.println(e);
                e.printStackTrace();
                break;
            }
        }
    }

    private void test4() {
        int n = 30;
        for (int i = 0; i < 100; i++) {
            var g = GraphGenerator.randomGnp(n, 0.9);
            try {
                //var alg1 = new BacktrackEquitableColoring(g);
                var alg1 = new GurobiStableModelEquitableColoring(g);
                var col1 = alg1.findColoring();
                assert col1.isEquitable();

                var alg2 = new GurobiAssignmentEquitableColoring(g);
                var col2 = alg2.findColoring();
                assert col2.isEquitable();

                if (col1.numUsedColors() != col2.numUsedColors()) {
                    System.out.println("OOPS!\n");
                    System.out.println(col1.numUsedColors() + ", " + col2.numUsedColors());
                    System.out.println("col1\n\t" + col1);
                    System.out.println("col2\n\t" + col2);
                    break;
                }
            } catch (Exception e) {
                System.err.println(e);
                e.printStackTrace();
                break;
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
