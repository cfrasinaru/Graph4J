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

import com.google.common.graph.EndpointPair;
import java.util.HashSet;
import ro.uaic.info.graph.gen.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class RemoveEdgesDemo extends PerformanceDemo {

    public RemoveEdgesDemo() {
        numVertices = 1000;
        runGuava = true;
        runJung = true;
        runJGraphT = true;
    }

    @Override
    protected void createGraph() {
        graph = GraphGenerator.complete(numVertices);

    }

    @Override
    protected void testGraph4J() {
        for (var it = graph.edgeIterator(); it.hasNext();) {
            it.next();
            it.remove();
        }
        System.out.println(graph.numEdges());
    }

    @Override
    protected void testJGraphT() {
        var set = new HashSet<>(jgraph.edgeSet());
        for (var e : set) {
            jgraph.removeEdge(e);
        }
        System.out.println(jgraph.edgeSet().size());
    }

    @Override
    protected void testGuava() {
        var set = new HashSet(guavaGraph.edges());
        for (var e : set) {
            guavaGraph.removeEdge((EndpointPair) e);
        }
        System.out.println(guavaGraph.edges().size());
    }

    @Override
    protected void testJung() {
        for (var v : jungGraph.getVertices()) {
            for (var e : jungGraph.getIncidentEdges(v)) {
                jungGraph.removeEdge(e);
            }
        }
        System.out.println(jungGraph.getEdgeCount());
    }

    @Override
    protected void prepareArgs() {
        int steps = 10;
        args = new int[steps];
        for (int i = 0; i < steps; i++) {
            args[i] = 100 * (i + 1);
        }
    }
}
