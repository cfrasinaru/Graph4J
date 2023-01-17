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

import org.jgrapht.generate.EmptyGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.util.SupplierUtil;
import ro.uaic.info.graph.gen.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class EmptyGraphDemo extends PerformanceDemo {

    private int n = 5_000_000;

    public EmptyGraphDemo() {
        runGuava = true;
        runJung = true;
        runJGraphT = true;
        runAlgs4 = true;
    }

    @Override
    protected void testGraph4J() {
        var g = GraphGenerator.empty(n);
    }

    @Override
    protected void testJGraphT() {
        var jg = new org.jgrapht.graph.SimpleGraph<Integer, DefaultEdge>(
                SupplierUtil.createIntegerSupplier(), SupplierUtil.createDefaultEdgeSupplier(), false);
        new EmptyGraphGenerator(n).generateGraph(jg);
    }

    @Override
    protected void testAlgs4() {
        var g = new edu.princeton.cs.algs4.Graph(n);
    }

    @Override
    protected void testGuava() {
        var g = com.google.common.graph.GraphBuilder.undirected().expectedNodeCount(n).build();
        for (int v = 0; v < n; v++) {
            g.addNode(v);
        }
    }

    @Override
    protected void testJung() {
        var g = new edu.uci.ics.jung.graph.SparseGraph<Integer, Object>();
        for (int v = 0; v < n; v++) {
            g.addVertex(v);
        }
    }

    public static void main(String args[]) {
        var app = new EmptyGraphDemo();
        app.demo();
    }

}
