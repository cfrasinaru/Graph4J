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
package ro.uaic.info.graph.demo;

import java.util.List;
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.GraphBuilder;

import ro.uaic.info.graph.alg.mst.KruskalMinimumSpanningTree;
import ro.uaic.info.graph.generate.EdgeWeightsGenerator;
import ro.uaic.info.graph.generate.GnpGraphGenerator;

/**
 * TODO: Move this to tests.
 *
 * @author Cristian Frăsinaru
 */
public class Main {

    public static void main(String[] args) {
        var app = new HopcroftKarpDemo();
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
        //var app = new Main();
        //app.benchmark();
        app.demo();
    }

    private void demo() {
        run(this::test);
    }

    private void test() {
        /*
        var g = GraphBuilder.vertices(0, 1, 2, 3, 4, 5)
                .addEdges("0-3,0-4,0-5,1-5,2-4").buildGraph();
        System.out.println(g);
        var left = new StableSet(g, new int[]{0, 1, 2});
        var right = new StableSet(g, new int[]{3, 4, 5});
        var alg = new HopkroftKarpBipartiteMatching(g, left, right);
        alg.getMatching();
         */
        //{[0, 1, 2, 3, 4, 5, 6, 7], [0-5, 0-6, 0-7, 1-4, 1-6, 1-7, 2-4, 2-7, 3-7]}
        
        /*
        var g = GraphBuilder.numVertices(8)
                .addEdges("0-5, 0-6, 0-7, 1-4, 1-6, 1-7, 2-4, 2-7, 3-7").buildGraph();
        System.out.println(g);
        var left = new StableSet(g, new int[]{0, 1, 2, 3});
        var right = new StableSet(g, new int[]{4, 5, 6, 7});
        var alg = new HopkroftKarpBipartiteMatching(g, left, right);
        System.out.println(alg.getMatching());
        */

        /*
        for (int i = 0; i < 100; i++) {
            var g = GraphGenerator.randomBipartite(10, 10, 0.1);
            var jg = Tools.createJGraphT(g);
            var gg = Tools.createAlgs4Graph(g);
            var bip = BipartitionAlgorithm.getInstance(g);
            var left = bip.getLeftSide();
            var right = bip.getRightSide();
            var alg1 = new HopkroftKarpBipartiteMatching(g);
            var alg2 = new HopcroftKarp(gg);
            var alg3 = new HopcroftKarpMaximumCardinalityBipartiteMatching(jg,IntArrays.asSet(left.vertices()), IntArrays.asSet(right.vertices()));
            int m1 = alg1.getMatching().size();            
            int m2 = alg2.size();
            int m3 = alg3.getMatching().getEdges().size();
            if (m1 != m2 || m1 != m3) {
                System.out.println("is bipartite??? " + GraphTests.isBipartite(jg));
                System.out.println("Nooooooooooooooo! " + m1 + ", " + m2 + ", " + m3);
                System.out.println(g);
                break;
            }
        }*/
    }


    /*
    private void testEdgeSet1() {
        Graph g = GraphGenerator.complete(10_000);
        EdgeSet set = new EdgeSet(g, g.numEdges());
        for (int v : g.vertices()) {
            for (var it = g.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                if (v < u) {
                    set.add(v, u);
                }
            }
        }
    }

    private void testEdgeSet2() {
        Graph g = GraphGenerator.complete(10_000);
        Set<Edge> set = new HashSet<>();
        for (var it = g.edgeIterator(); it.hasNext();) {
            set.add(it.next());
        }
    }

    private void testEdgeSet3() {
        Graph g = GraphGenerator.complete(10_000);
        Edge[] set = new Edge[g.numEdges()];
        int i = 0;
        for (var it = g.edgeIterator(); it.hasNext();) {
            set[i++] = it.next();
        }
    }*/
    private void testKruskal() {
        /*
        Graph g = GraphBuilder.vertexRange(0, 3).addEdges("0-1,1-2,2-3,3-0,3-1").buildGraph();
        g.setEdgeWeight(0, 1, 3);
        g.setEdgeWeight(1, 2, 1);
        g.setEdgeWeight(2, 3, 4);
        g.setEdgeWeight(3, 1, 1);
        g.setEdgeWeight(3, 0, 1);
        var alg = new KruskalMinimumSpanningTree(g);
        System.out.println(alg.getTree());
         */

        for (int i = 0; i < 1000; i++) {
            Graph g = new GnpGraphGenerator(10, 0.5).createGraph();
            EdgeWeightsGenerator.consecutiveIntegers(g);
            var alg1 = new KruskalMinimumSpanningTree(g);
            var alg2 = new org.jgrapht.alg.spanning.PrimMinimumSpanningTree(Converter.createJGraphT(g));
            //System.out.println(alg1.getTree());
            //System.out.println(alg2.getSpanningTree());
            double x = alg1.getWeight();
            double y = alg2.getSpanningTree().getWeight();
            if (x != y) {
                System.out.println(g);
            }
        }
    }

    private void testLabels() {
        Graph g = GraphBuilder.vertexRange(1, 4).addEdges("1-2,2-3,3-4").buildGraph();
        g.addEdge(new Edge(1, 3, 13.13, "edge13"));
        g.setEdgeLabel(1, 2, "edge12");
        g.setEdgeWeight(1, 2, 12.12);
        g.removeEdge(1, 2);
        g.setEdgeLabel(2, 3, "hello");
        g.setEdgeLabel(3, 4, "hello");
        g.setVertexWeight(1, 1.1);
        g.setVertexLabel(1, "a");
        g.setVertexLabel(2, List.of("b", "c"));
        System.out.println(g.findAllEdges("hello"));
        System.out.println(g);
    }

    private void testAlgs4() {
        var g = new edu.princeton.cs.algs4.Graph(4);
        g.addEdge(0, 1);
        g.addEdge(0, 1);
        System.out.println(g);
    }

    private void testGuava() {
        var g = com.google.common.graph.GraphBuilder.undirected().expectedNodeCount(5).build();
        g.addNode(1);
        g.addNode(2);
        g.addNode(3);
        g.putEdge(1, 2);
        g.putEdge(1, 2);
        System.out.println(g.nodes());
        System.out.println(g.edges());
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
