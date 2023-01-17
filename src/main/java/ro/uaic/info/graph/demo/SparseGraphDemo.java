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

import ro.uaic.info.graph.GraphBuilder;
import ro.uaic.info.graph.util.Tools;

/**
 *
 * @author Cristian Frăsinaru
 */
public class SparseGraphDemo extends PerformanceDemo {

    private int n = 1_000_000;
    private final int avgDegree = 100;

    public SparseGraphDemo() {
        runGuava = true;
        runJGraphT = false;
        runJung = false;
        runAlgs4 = true;
    }

    @Override
    protected void testGraph4J() {
        var g = new GraphBuilder().numVertices(n).avgDegree(avgDegree).buildGraph();
        for (int v = 0; v < n; v++) {
            for (int j = 0; j < avgDegree; j++) {
                int u = (v + j + 1) % n;
                if (u != v) {
                    g.addEdge(v, u);
                }
            }
        }
    }

    @Override
    protected void testJGraphT() {
        var g = Tools.createJGraph(null);
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
    }

    @Override
    protected void testJung() {
        var g = new edu.uci.ics.jung.graph.SparseGraph<Integer, Object>();
        for (int v = 0; v < n; v++) {
            g.addVertex(v);
        }
        for (int v = 0; v < n; v++) {
            for (int j = 0; j < avgDegree; j++) {
                int u = (v + j + 1) % n;
                if (u != v) {
                    g.addEdge(v + "-" + u, v, u);
                }
            }
        }
    }

    @Override
    protected void testGuava() {
        var g = com.google.common.graph.GraphBuilder.undirected().expectedNodeCount(n).build();
        for (int v = 0; v < n; v++) {
            g.addNode(v);
        }
        for (int v = 0; v < n; v++) {
            for (int j = 0; j < avgDegree; j++) {
                int u = (v + j + 1) % n;
                if (u != v) {
                    g.putEdge(v, u);
                }
            }
        }
    }

    @Override
    protected void testAlgs4() {
        var g = new edu.princeton.cs.algs4.Graph(n);
        for (int v = 0; v < n; v++) {
            for (int j = 0; j < avgDegree; j++) {
                int u = (v + j + 1) % n;
                if (u != v) {
                    g.addEdge(v, u);
                }
            }
        }
    }

    public static void main(String args[]) {
        var app = new SparseGraphDemo();
        app.demo();
    }

}
