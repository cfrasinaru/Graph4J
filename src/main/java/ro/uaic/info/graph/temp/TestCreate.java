/*
 * Copyright (C) 2022 Faculty of Computer Science Iasi, Romania
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
package ro.uaic.info.graph.temp;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.DepthFirstIterator;
import ro.uaic.info.graph.build.GraphBuilder;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.Graphs;
import ro.uaic.info.graph.search.DFSIterator;

/**
 * Size of an object: https://www.baeldung.com/java-size-of-object
 *
 * Web Graph: https://webgraph.di.unimi.it/
 *
 * @author Cristian Frăsinaru
 */
public class TestCreate {

    int n;
    int avgDegree;

    public static void main(String[] args) {
        test1();
        testJGraph();
    }

    private void init() {
        n = 1_000_000;
        avgDegree = 20;
    }

    private static void test1() {
        var app = new TestCreate();
        app.init();
        var g = app.createSparse();
        //var g = app.createComplete();
        //app.testAdjacency(g);
        //app.testAdjacency(g);
        //app.testIterator(g);
    }

    private static void testJGraph() {
        var app = new TestCreate();
        app.init();
        var g = app.createSparseJGraph();
        //var g = app.createCompleteJGraph();
        //app.testAdjacency(g);
        //app.testIterator(g);
    }

    private Graph createSparse() {
        long t0 = System.currentTimeMillis();
        var g = GraphBuilder.numVertices(n).avgDegree(avgDegree).buildGraph();
        for (int v = 0; v < n; v++) {
            for (int j = 0; j < avgDegree; j++) {
                int u = (v + j + 1) % n;
                if (u != v) {
                    g.addEdge(v, u);
                }
            }
        }
        int m = g.numEdges();
        double d = 2.0d * m / ((double) n * (n - 1));
        long t1 = System.currentTimeMillis();
        System.out.println(String.format("%,d", m) + " = " + String.format("%,.8f", d) + "%");
        System.out.println((t1 - t0) / 1000 + " s");
        return g;
    }

    private Graph createComplete() {
        long t0 = System.currentTimeMillis();
        var g = Graphs.complete(n);
        int m = g.numEdges();
        long t1 = System.currentTimeMillis();
        System.out.println(String.format("%,d", m));
        System.out.println((t1 - t0) / 1000 + " s");
        return g;
    }

    private void testAdjacency(Graph g) {
        long t0 = System.currentTimeMillis();
        int k = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < (n / 1); j++) {
                if (g.containsEdge(i, j)) {
                    k++;
                }
            }
            //g.neighbors(i);
        }
        long t1 = System.currentTimeMillis();
        System.out.println(k);
        System.out.println((t1 - t0) / 1000 + " s");
    }

    private void testIterator(Graph g) {
        long t0 = System.currentTimeMillis();
        for (int v : g.vertices()) {
            var dfs = new DFSIterator(g, v);
            while (dfs.hasNext()) {
                dfs.next();
            }
        }
        long t1 = System.currentTimeMillis();
        System.out.println((t1 - t0) / 1000 + " s");
    }

    private org.jgrapht.graph.SimpleGraph createSparseJGraph() {
        long t0 = System.currentTimeMillis();
        var g = new org.jgrapht.graph.SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
        for (int v = 0; v < n; v++) {
            g.addVertex(v);
        }
        for (int v = 0; v < n; v++) {
            for (int j = 0; j < avgDegree; j++) {
                int u = (v + j + 1) % n;
                if (u != v) {
                    g.addEdge(v, u);
                }
            }
        }
        long t1 = System.currentTimeMillis();
        int m = g.edgeSet().size();
        double d = 2.0d * m / ((double) n * (n - 1));
        System.out.println(String.format("%,d", m) + " = " + String.format("%,.8f", d) + "%");
        System.out.println((t1 - t0) / 1000 + " s");
        return g;
    }

    private org.jgrapht.graph.SimpleGraph createCompleteJGraph() {
        long t0 = System.currentTimeMillis();
        var g = new org.jgrapht.graph.SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
        for (int v = 0; v < n; v++) {
            g.addVertex(v);
        }
        for (int v = 0; v < n - 1; v++) {
            for (int u = v + 1; u < n; u++) {
                g.addEdge(v, u);
            }
        }
        long t1 = System.currentTimeMillis();
        int m = g.edgeSet().size();
        System.out.println(String.format("%,d", m));
        System.out.println((t1 - t0) / 1000 + " s");
        return g;
    }

    private void testAdjacency(org.jgrapht.graph.SimpleGraph g) {
        long t0 = System.currentTimeMillis();
        int k = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < (n / 1); j++) {
                if (g.containsEdge(i, j)) {
                    k++;
                }
            }
            //g.edgesOf(i).size();
        }
        long t1 = System.currentTimeMillis();
        System.out.println(k);
        System.out.println((t1 - t0) / 1000 + " s");
    }

    private void testIterator(org.jgrapht.graph.SimpleGraph g) {
        long t0 = System.currentTimeMillis();
        for (var v : g.vertexSet()) {
            var iter = new DepthFirstIterator<>(g, v);
            while (iter.hasNext()) {
                iter.next();
            }
        }
        long t1 = System.currentTimeMillis();
        System.out.println((t1 - t0) / 1000 + " s");
    }

}
