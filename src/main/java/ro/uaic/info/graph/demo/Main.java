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

import com.google.common.graph.Graphs;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.GraphBuilder;
import ro.uaic.info.graph.alg.eulerian.HierholzerEulerianCircuit;

import ro.uaic.info.graph.alg.mst.KruskalMinimumSpanningTree;
import ro.uaic.info.graph.alg.mst.PrimMinimumSpanningTreeDefault;
import ro.uaic.info.graph.alg.mst.PrimMinimumSpanningTreeHeap;
import ro.uaic.info.graph.gen.CompleteGenerator;
import ro.uaic.info.graph.gen.EdgeWeightsGenerator;
import ro.uaic.info.graph.gen.GnpRandomGenerator;
import ro.uaic.info.graph.gen.GraphGenerator;
import ro.uaic.info.graph.model.EdgeSet;
import ro.uaic.info.graph.util.Tools;

/**
 * TODO: Move this to tests.
 *
 * @author Cristian Frăsinaru
 */
public class Main {

    public static void main(String[] args) {
        var app = new Main();
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
        //var app = new DijkstraDemo();

        //var app = new DFSVisitorDemo();
        //var app = new BFSVisitorDemo();
        //var app = new BFSIteratorDemo();
        //var app = new DFSIteratorDemo();
        //var app = new RemoveNodesDemo();
        //var app = new RemoveEdgesDemo();
        //var app = new ContainsEdgeDemo();
        //var app = new IterateEdgesDemo();
        //var app = new WeightedGraphDemo2();
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
        //run(this::prepare);
    }

    private void test() {
        Graph g = new GnpRandomGenerator(20, 0.5).createGraph();
            EdgeWeightsGenerator.randomDoubles(g, 0, 1);
            double p1 = new PrimMinimumSpanningTreeHeap(g).getWeight();
            double p2 = new PrimMinimumSpanningTreeDefault(g).getWeight();
            double k1 = new KruskalMinimumSpanningTree(g).getWeight();
            System.out.println(p1-p2);
            System.out.println(p1-k1);
    }

    private void testEdgeSet0() {
        Graph g = GraphGenerator.complete(10_000);
        Edge[] edges = g.edges();
    }

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
    }

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
            Graph g = new GnpRandomGenerator(10, 0.5).createGraph();
            EdgeWeightsGenerator.consecutiveIntegers(g);
            var alg1 = new KruskalMinimumSpanningTree(g);
            var alg2 = new org.jgrapht.alg.spanning.PrimMinimumSpanningTree(Tools.createJGraph(g));
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
