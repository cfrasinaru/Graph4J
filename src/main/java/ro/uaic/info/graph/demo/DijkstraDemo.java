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

import edu.princeton.cs.algs4.DijkstraSP;
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.alg.sp.*;
import ro.uaic.info.graph.gen.CompleteGenerator;
import ro.uaic.info.graph.gen.EdgeWeightsGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class DijkstraDemo extends PerformanceDemo {

    public DijkstraDemo() {
        numVertices = 15000;
        //runJGraphT = true;
        //runJung = true;
        //runAlgs4 = false; //accepts only digraphs
    }

    @Override
    protected void createGraph() {
        graph = new CompleteGenerator(numVertices).createGraph();
        EdgeWeightsGenerator.randomDoubles(graph, 0, 1);
        //EdgeWeightsGenerator.fill(graph, 0.1);
    }

    @Override
    protected void testGraph4J() {
        //var alg = new DijkstraShortestPathDefault(graph, 0);
        var alg = new DijkstraShortestPathHeap(graph, 0);
        for (int i = 1; i < graph.numVertices(); i++) {
            var p = alg.findPath(i);
        }
        System.out.println(alg.getPathWeight(graph.numVertices() - 1));
    }

    @Override
    protected void testJGraphT() {
        var alg = new org.jgrapht.alg.shortestpath.DijkstraShortestPath(jgraph);
        for (int i = 1; i < graph.numVertices(); i++) {
            alg.getPath(0, i);
        }
        System.out.println(alg.getPathWeight(0, graph.numVertices() - 1));
    }

    @Override
    protected void testJung() {
        var alg = new edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath(jungGraph, (e -> ((Edge) e).weight()));
        for (int i = 1; i < graph.numVertices(); i++) {
            alg.getPath(0, i);
        }
        //no easy method for getting the weight of the path        
        System.out.println(alg.getDistance(0, graph.numVertices() - 1));
    }

    @Override
    protected void testAlgs4() {
        var alg = new DijkstraSP(algs4Ewd, 0);
        for (int i = 1; i < graph.numVertices(); i++) {
            var p = alg.pathTo(i);
        }
        System.out.println(alg.distTo(graph.numVertices() - 1));
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
