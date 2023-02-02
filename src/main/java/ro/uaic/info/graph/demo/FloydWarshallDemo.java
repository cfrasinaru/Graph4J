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

import edu.princeton.cs.algs4.FloydWarshall;
import ro.uaic.info.graph.alg.sp.FloydWarshallShortestPath;
import ro.uaic.info.graph.gen.EdgeWeightsGenerator;
import ro.uaic.info.graph.gen.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class FloydWarshallDemo extends PerformanceDemo {

    public FloydWarshallDemo() {
        runJGraphT = true;
        runAlgs4 = true;
    }

    @Override
    protected void createGraph() {
        //graph = new GnpRandomGenerator(1000, 0.3).createGraph();
        //EdgeWeightsGenerator.randomIntegers(graph, 1, 1000);
        graph = GraphGenerator.complete(1000);
        EdgeWeightsGenerator.consecutiveIntegers(graph);

    }

    @Override
    protected void testGraph4J() {
        var alg = new FloydWarshallShortestPath(graph);
        for (int i = 1; i < graph.numVertices(); i++) {
            alg.findPath(0, i);
            //alg.getPathWeight(0, i);
        }
    }

    @Override
    protected void testJGraphT() {
        var alg = new org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths(jgraph);
        for (int i = 1; i < graph.numVertices(); i++) {
            alg.getPath(0, i);
            //alg.getPathWeight(0, i);
        }
    }

    @Override
    protected void testAlgs4() {
        var alg = new FloydWarshall(adjMatrixEwd);
        for (int i = 1; i < graph.numVertices(); i++) {
            alg.path(0, i);
            //alg.dist(0, i);
        }
    }

    public static void main(String args[]) {
        var app = new FloydWarshallDemo();
        app.demo();
    }

}
