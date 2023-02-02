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

import org.jgrapht.generate.CompleteGraphGenerator;
import ro.uaic.info.graph.gen.CompleteGenerator;
import ro.uaic.info.graph.util.Tools;

/**
 *
 * @author Cristian Frăsinaru
 */
public class CompleteGraphDemo extends PerformanceDemo {

    public CompleteGraphDemo() {
        numVertices = 5_000;
        runGuava = true;
        runJung = false;
        runJGraphT = false;
        runAlgs4 = false;
    }

    @Override
    protected void testGraph4J() {
        new CompleteGenerator(numVertices).createGraph();
    }

    @Override
    protected void testJGraphT() {
        new CompleteGraphGenerator(numVertices).generateGraph(Tools.createJGraph(null));
    }

    @Override
    protected void testAlgs4() {
        var g = new edu.princeton.cs.algs4.Graph(numVertices);
        for (int v = 0; v < numVertices - 1; v++) {
            for (int u = v + 1; u < numVertices; u++) {
                g.addEdge(v, u);
            }
        }
    }

    @Override
    protected void testGuava() {
        var g = com.google.common.graph.GraphBuilder.undirected().expectedNodeCount(numVertices).build();
        for (int v = 0; v < numVertices; v++) {
            g.addNode(v);
        }
        for (int v = 0; v < numVertices - 1; v++) {
            for (int u = v + 1; u < numVertices; u++) {
                g.putEdge(v, u);
            }
        }
    }

    @Override
    protected void testJung() {
        var g = new edu.uci.ics.jung.graph.SparseGraph<Integer, Object>();
        for (int v = 0; v < numVertices; v++) {
            g.addVertex(v);
        }
        for (int v = 0; v < numVertices - 1; v++) {
            for (int u = v + 1; u < numVertices; u++) {
                g.addEdge(v + "-" + u, v, u);
            }
        }
    }

    @Override
    protected void prepareArgs() {
        int steps = 10;
        args = new int[steps];
        for (int i = 0; i < steps; i++) {
            args[i] = 1000 * (i + 1);
        }
    }

}
