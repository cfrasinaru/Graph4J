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

import java.util.HashSet;
import ro.uaic.info.graph.gen.GnmRandomGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class RemoveNodesDemo extends PerformanceDemo {

    public RemoveNodesDemo() {
        numVertices = 5_000_000;
        runGuava = true;
        runJung = true;
        runJGraphT = true;
        runAlgs4 = false;
    }

    @Override
    protected void createGraph() {
        graph = new GnmRandomGenerator(numVertices, numVertices).createGraph();
    }

    @Override
    protected void testGraph4J() {
        for (var it = graph.vertexIterator(); it.hasNext();) {
            it.next();
            it.remove();
        }
        System.out.println(graph.numEdges());
    }

    @Override
    protected void testJGraphT() {
        var set = new HashSet<>(jgraph.vertexSet());
        for (var v : set) {
            jgraph.removeVertex(v);
        }
        System.out.println(jgraph.vertexSet().size());
    }

    @Override
    protected void testGuava() {
        var set = new HashSet(guavaGraph.nodes());
        for (var v : set) {
            guavaGraph.removeNode(v);
        }
        System.out.println(guavaGraph.nodes().size());
    }

    @Override
    protected void testJung() {
        var set = new HashSet<>(jungGraph.getVertices());
        for (var v : set) {
            jungGraph.removeVertex(v);
        }
        System.out.println(jungGraph.getVertexCount());
    }

    @Override
    protected void prepareArgs() {
        int steps = 10;
        args = new int[steps];
        for (int i = 0; i < steps; i++) {
            args[i] = 1_000_000 * (i + 1);
        }
    }
    
}
