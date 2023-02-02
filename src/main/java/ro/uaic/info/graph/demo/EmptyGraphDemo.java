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
 * MERCHAN", "ILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
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

    public EmptyGraphDemo() {
        numVertices = 10_000_000;
        runGuava = true;
        runJung = true;
        runJGraphT = true;
        runAlgs4 = true;
    }

    @Override
    protected void testGraph4J() {
        var g = GraphGenerator.empty(numVertices);
    }

    @Override
    protected void testJGraphT() {
        var jg = new org.jgrapht.graph.SimpleGraph<Integer, DefaultEdge>(
                SupplierUtil.createIntegerSupplier(), SupplierUtil.createDefaultEdgeSupplier(), false);
        new EmptyGraphGenerator(numVertices).generateGraph(jg);
    }

    @Override
    protected void testAlgs4() {
        var g = new edu.princeton.cs.algs4.Graph(numVertices);
    }

    @Override
    protected void testGuava() {
        var g = com.google.common.graph.GraphBuilder.undirected().expectedNodeCount(numVertices).build();
        for (int v = 0; v < numVertices; v++) {
            g.addNode(v);
        }
    }

    @Override
    protected void testJung() {
        var g = new edu.uci.ics.jung.graph.SparseGraph<Integer, Object>();
        for (int v = 0; v < numVertices; v++) {
            g.addVertex(v);
        }
    }

    @Override
    protected void prepareArgs() {
        int steps = 10;
        args = new int[steps];
        for (int i = 0; i < steps; i++) {
            args[i] = 5_000_000 * (i + 1);
        }
    }

}
