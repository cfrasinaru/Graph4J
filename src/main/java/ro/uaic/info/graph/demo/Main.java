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

import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.GraphBuilder;
import ro.uaic.info.graph.alg.coloring.GreedyColoring;

/**
 * TODO: Move this to tests.
 *
 * @author Cristian Frăsinaru
 */
public class Main {

    Graph graph;
    org.jgrapht.Graph jgraph;

    public static void main(String[] args) {
        var app = new Main();
        //var app = new LineGraphDemo();
        //var app = new GreedyColoringDemo();
        //var app = new BipartiteDemo();
        //var app = new StrongConnectivityDemo();
        //var app = new BiconnectivityDemo();
        //var app = new ConnectivityDemo();
        //var app = new DFSVisitorDemo();
        //var app = new BFSVisitorDemo();
        //var app = new DFSIteratorDemo();
        //var app = new BFSIteratorDemo();
        //var app = new EulerianCircuitDemo();
        //var app = new DijkstraDemo();
        //var app = new BellmanFordDemo();
        //var app = new FloydWarshallDemo();
        //var app = new IterateAllEdgesDemo();
        //var app = new ContainsEdgeDemo();
        //var app = new LabeledGraphDemo();
        //var app = new RandomGraphDemo();
        //var app = new SparseGraphDemo();
        //var app = new CompleteGraphDemo();
        //var app = new EmptyGraphDemo();
        app.demo();
    }

    private void demo() {
        run(this::testGuava);
        //run(this::prepare);
    }

    private void test() {
        var g = new GraphBuilder().numVertices(4).buildMultigraph();
        g.addEdge(0, 1);
        g.addEdge(0, 1);
        System.out.println(g);
        for (var it = g.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            System.out.println(e);
            it.remove();
            System.out.println(g);
            System.out.println(g.containsEdge(e));
        }
    }

    private void testGuava() {
        var g = com.google.common.graph.GraphBuilder.undirected().expectedNodeCount(5).build();
        g.addNode(3);
        g.addNode(2);
        g.addNode(1);
        g.putEdge(1, 2);
        g.putEdge(1, 3);
        g.putEdge(1, 4);
        g.putEdge(1, 5);
        System.out.println(g.nodes());
        System.out.println(g.edges());
    }

    private void testCol() {
        //System.out.println("StrongConnectivityAlgorithmBase".length());
        var g = new GraphBuilder().addClique(1, 2, 3, 4).buildGraph();
        //var g = new GraphBuilder().vertexRange(1, 5).addCycle(1, 2, 3, 4, 5).buildGraph();
        //var g= GraphGenerator.complete(10_000);
        //var g = GraphGenerator.completeBipartite(1000, 1000);
        System.out.println(g);
        var alg = new GreedyColoring(g);
        var col = alg.findColoring();
        System.out.println(col.numUsedColors());
        System.out.println(col.isProper());

        /*
        var col = new VertexColoring<String>(g);
        col.setColor(1, "red");
        col.setColor(2, "green");
        System.out.println(col.isComplete());
        System.out.println(col.isProper());
        col.setColor(3, "blue");
        System.out.println(col.getColorClasses());
        System.out.println(col.isProper());
        System.out.println(col);
         */
    }

    protected void run(Runnable snippet) {
        long m0 = Runtime.getRuntime().freeMemory();
        long t0 = System.currentTimeMillis();
        snippet.run();
        long t1 = System.currentTimeMillis();
        long m1 = Runtime.getRuntime().freeMemory();
        System.out.println((t1 - t0) + " ms");
        System.out.println((m0 - m1) / (1024 * 1024) + " MB");
        System.out.println("------------------------------------------------");
    }

}
