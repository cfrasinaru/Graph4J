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

import edu.princeton.cs.algs4.BellmanFordSP;
import ro.uaic.info.graph.alg.sp.BellmanFordShortestPath;
import ro.uaic.info.graph.gen.CompleteGenerator;
import ro.uaic.info.graph.gen.EdgeWeightsGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class BellmanFordDemo extends PerformanceDemo {

    public BellmanFordDemo() {
        //runJGraphT = true;
        runAlgs4 = true;
    }

    
    @Override
    protected void createGraph() {
        //graph = new GnpRandomGenerator(500, 0.5).createGraph();
        //EdgeWeightsGenerator.randomIntegers(graph, 1, 1000);
        graph = new CompleteGenerator(10_000).createDigraph();
        EdgeWeightsGenerator.consecutiveIntegers(graph);
    }

    @Override
    protected void testGraph4J() {
        var alg = new BellmanFordShortestPath(graph, 0);
        for (int i = 1; i < graph.numVertices(); i++) {
            alg.findPath(i);
        }
    }

    @Override
    protected void testJGraphT() {
        var alg = new org.jgrapht.alg.shortestpath.BellmanFordShortestPath<>(jgraph);
        for (int i = 1; i < graph.numVertices(); i++) {
            alg.getPath(0, i);
        }
    }

    @Override
    protected void testAlgs4() {
        var alg = new BellmanFordSP(algs4Ewd, 0);
        for (int i = 1; i < graph.numVertices(); i++) {
            alg.pathTo(i);
        }
    }
    
    public static void main(String args[]) {
        var app = new BellmanFordDemo();
        app.demo();
    }
}
