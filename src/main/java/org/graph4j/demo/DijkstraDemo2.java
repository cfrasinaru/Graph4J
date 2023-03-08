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
package org.graph4j.demo;

import org.graph4j.alg.sp.DijkstraShortestPathHeap;
import edu.princeton.cs.algs4.DijkstraSP;
import org.graph4j.Edge;
import org.graph4j.generate.EdgeWeightsGenerator;
import org.graph4j.generate.GnpGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
class DijkstraDemo2 extends PerformanceDemo {

    private final double probability = 0.5;

    public DijkstraDemo2() {
        numVertices = 1000;
        runJGraphT = true;
        //runJung = true;
        //runAlgs4 = true;
    }

    @Override
    protected void createGraph() {
        //graph = new CompleteGenerator(numVertices).createGraph();
        graph = new GnpGraphGenerator(numVertices, probability).createDigraph();
        EdgeWeightsGenerator.randomDoubles(graph, 0, 1);
    }

    @Override
    protected void testGraph4J() {
        for (int v : graph.vertices()) {
            var alg = new DijkstraShortestPathHeap(graph, v);
            for (int i = 1; i < graph.numVertices(); i++) {
                var p = alg.findPath(i);
            }
        }
    }

    @Override
    protected void testJGraphT() {
        for (var v : jgrapht.vertexSet()) {
            var alg = new org.jgrapht.alg.shortestpath.DijkstraShortestPath(jgrapht);
            var paths = alg.getPaths(v);
            for (int i = 1; i < graph.numVertices(); i++) {
                paths.getPath(i);
            }
        }
    }

    @Override
    protected void testJung() {
        for (var v : jungGraph.getVertices()) {
            var alg = new edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath(jungGraph, (e -> ((Edge) e).weight()));
            for (int i = 1; i < graph.numVertices(); i++) {
                alg.getPath(v, i);
            }
        }
    }

    @Override
    protected void testAlgs4() {
        for (int v = 0; v < graph.numVertices(); v++) {
        var alg = new DijkstraSP(algs4Ewd, v);
            for (int i = 1; i < graph.numVertices(); i++) {
                var p = alg.pathTo(i);
            }
        }
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
