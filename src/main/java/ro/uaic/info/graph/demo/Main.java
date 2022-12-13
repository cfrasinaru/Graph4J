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

import java.util.Arrays;
import java.util.function.Supplier;
import org.jgrapht.generate.GnmRandomGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.util.SupplierUtil;
import ro.uaic.info.graph.alg.CycleDetector;
import ro.uaic.info.graph.build.GraphBuilder;
import ro.uaic.info.graph.search.BFSVisitor;
import ro.uaic.info.graph.search.BreadthFirstSearch;
import ro.uaic.info.graph.search.DFSVisitor;
import ro.uaic.info.graph.search.DepthFirstSearch;
import ro.uaic.info.graph.search.SearchNode;

/**
 * TODO: Move this to tests.
 *
 * @author Cristian Frăsinaru
 */
public class Main {

    public static void main(String[] args) {
        var app = new Main();
        app.test();
        //app.demoBFS();
        //app.demoDFS();
        //app.demoContains();
    }

    public static void printObjectSize(Object object) {
        //System.out.println("Object type: " + object.getClass() + ", size: " + InstrumentationAgent.getObjectSize(object) + " bytes");
    }

    protected void demo(Runnable snippet) {
        long m0 = Runtime.getRuntime().freeMemory();
        long t0 = System.currentTimeMillis();
        snippet.run();
        long t1 = System.currentTimeMillis();
        long m1 = Runtime.getRuntime().freeMemory();
        System.out.println((t1 - t0) / 1000 + " s");
        System.out.println((m0 - m1) / (1024 * 1024) + " MB");
    }

    private void test() {
        var g = GraphBuilder.vertexRange(1, 9)
                .addPath(1, 2, 3, 4, 5, 6, 7).addEdge(7, 1)
                .sorted()
                .addClique(1, 8, 9)
                .buildGraph();
        var detector = new CycleDetector(g);
        System.out.println(Arrays.toString(detector.findAnyCycle()));
        System.out.println(Arrays.toString(detector.findShortestCycle()));
    }

    private void demoDFS() {
        /*
        var g = GraphBuilder
                .vertexRange(1, 8)
                .addPath(1, 2, 4, 6).addEdge(6, 2)
                .addPath(1, 3, 5, 7).addEdge(5, 4).addEdge(5, 8).addEdge(1, 8)
                .sorted()
                .buildDigraph();
         */
        var g = GraphBuilder.numVertices(3).addEdges("0-1,0-1").buildMultigraph();
        //var g = GraphBuilder.numVertices(3).addEdges("0-1,1-0").buildDigraph();
        //g.setName("K4");
        System.out.println(g);
        new DepthFirstSearch(g).traverse(new DFSVisitor() {
            @Override
            public void root(SearchNode node) {
                System.out.println("Root: " + node);
            }

            @Override
            public void treeEdge(SearchNode from, SearchNode to) {
                System.out.println("Tree edge: " + from + "->" + to);
            }

            @Override
            public void backEdge(SearchNode from, SearchNode to) {
                System.out.println("Back edge: " + from + "->" + to);
                System.out.println("Cycle detected");
            }

            @Override
            public void forwardEdge(SearchNode from, SearchNode to) {
                System.out.println("Forward edge: " + from + "->" + to);
            }

            @Override
            public void crossEdge(SearchNode from, SearchNode to) {
                System.out.println("Cross edge: " + from + "->" + to);
            }

            @Override
            public void upward(SearchNode from, SearchNode to) {
                System.out.println("Return to parent: " + from + "->" + to);
            }
        });
    }

    private void demoBFS() {
        /*
        var g = GraphBuilder
                .vertexRange(1, 8)
                .addPath(1, 2, 4, 6).addEdge(6, 2)
                .addPath(1, 3, 5, 7).addEdge(5, 4).addEdge(5, 8).addEdge(1, 8)
                .sorted()
                .buildDigraph();
         */
        //var g = GraphBuilder.numVertices(3).addEdges("0-0,1-1,0-1,1-0").buildPseudograph();
        var g = GraphBuilder.vertexRange(1, 5).addEdges("1-2,2-3,3-4,4-5").buildGraph();
        g.addEdge(2, 4);

        //g.setName("K4");
        System.out.println(g);
        new BreadthFirstSearch(g).traverse(new BFSVisitor() {
            @Override
            public void root(SearchNode node) {
                System.out.println("Root: " + node);
            }

            @Override
            public void treeEdge(SearchNode from, SearchNode to) {
                System.out.println("Tree edge: " + from + "->" + to);
            }

            @Override
            public void backEdge(SearchNode from, SearchNode to) {
                System.out.println("Back edge: " + from + "->" + to);
            }

            @Override
            public void crossEdge(SearchNode from, SearchNode to) {
                System.out.println("Cross edge: " + from + "->" + to);
            }
        });
    }

    private void demoMem() {
        int n = 1_000;
        long mem0 = Runtime.getRuntime().freeMemory();
        var g = GraphBuilder.vertices(1, 2, 100_000_000).buildGraph();
        //var g = RandomGenerator.createGraphGnp(n, 0.1);
        System.out.println(g);
        long mem1 = Runtime.getRuntime().freeMemory();
        System.out.println((mem0 - mem1) / (1024 * 1024) + " MB");
        //printObjectSize(g);
    }

    private void demoContains() {
        int n = 10_000;
        var g = GraphBuilder.numVertices(n).complete().buildGraph();
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                g.containsEdge(g.vertexAt(i), g.vertexAt(j));
            }
        }
        long t1 = System.currentTimeMillis();
        System.out.println((t1 - t0) + " ms");
        System.out.println(g);
        System.out.println((int) g.numEdges());
    }

    private void demoRandom() {
        int n = 500;
        double p = 0.3;
        int m = (int) (p * (n * (n - 1) / 2));
        long t0 = System.currentTimeMillis();
        //var g = Graphs.randomGnp(n, p);
        //var g = Graphs.randomGnm(n, m);
        long t1 = System.currentTimeMillis();
        System.out.println((t1 - t0) + "ms");

        t0 = System.currentTimeMillis();
        Supplier<Integer> vSupplier = new Supplier<Integer>() {
            private int id = 0;

            @Override
            public Integer get() {
                return id++;
            }
        };
        var jg = new org.jgrapht.graph.SimpleGraph<>(vSupplier, SupplierUtil.createDefaultEdgeSupplier(), false);
        //var gnp = new GnpRandomGraphGenerator<Integer, DefaultEdge>(n, p);
        var gnp = new GnmRandomGraphGenerator<Integer, DefaultEdge>(n, m);
        gnp.generateGraph(jg);
        //System.out.println(jg);
        t1 = System.currentTimeMillis();
        System.out.println((t1 - t0) + "ms");
    }

}
