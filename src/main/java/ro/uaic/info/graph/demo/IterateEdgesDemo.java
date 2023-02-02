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

import ro.uaic.info.graph.gen.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class IterateEdgesDemo extends PerformanceDemo {

    public IterateEdgesDemo() {
        numVertices = 2000;
        runJGraphT = true;
        //runJung = true;
        runGuava = true;
    }

    @Override
    protected void createGraph() {
        graph = GraphGenerator.complete(numVertices);
    }

    @Override
    protected void testGraph4J() {
        int k = 0;
        for (int v : graph.vertices()) {
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                it.next();
                k++;
            }
        }
        System.out.println(k + " = " + 2 * graph.numEdges());
    }

    @Override
    protected void testJGraphT() {
        int k = 0;
        for (var v : jgraph.vertexSet()) {
            for (var e : jgraph.iterables().edgesOf(v)) {
                k++;
            }
        }
        System.out.println(k + " = " + 2 * jgraph.edgeSet().size());
    }

    @Override
    protected void testGuava() {
        int k = 0;
        for (var v : guavaGraph.nodes()) {
            for (var u : guavaGraph.adjacentNodes(v)) {
                k++;
            }
        }
        System.out.println(k + " = " + 2 * guavaGraph.edges().size());
    }

    @Override
    protected void testJung() {
        int k = 0;
        for (var v : jungGraph.getVertices()) {
            for (var u : jungGraph.getNeighbors(v)) {
                k++;
            }
        }
        System.out.println(k + " = " + 2 * jungGraph.getEdgeCount());
    }
    
    @Override
    protected void prepareArgs() {
        int steps = 10;
        args = new int[steps];
        for (int i = 0; i < steps; i++) {
            args[i] = 500 * (i + 1);
        }
    }
    
}
