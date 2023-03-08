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

import edu.princeton.cs.algs4.FloydWarshall;
import org.graph4j.alg.sp.FloydWarshallShortestPath;
import org.graph4j.generate.EdgeWeightsGenerator;
import org.graph4j.generate.GnpGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
class FloydWarshallDemo extends PerformanceDemo {

    public FloydWarshallDemo() {
        numVertices = 2000;
        runJGraphT = true;
        runAlgs4 = true;
    }

    @Override
    protected void createGraph() {
        graph = new GnpGraphGenerator(numVertices, 0.1).createGraph();
        //graph = GraphGenerator.complete(numVertices);
        EdgeWeightsGenerator.randomDoubles(graph, 0, 1);

    }

    @Override
    protected void testGraph4J() {
        var alg = new FloydWarshallShortestPath(graph);
        for (int i = 1; i < graph.numVertices(); i++) {
            alg.findPath(0, i);
            //alg.getPathWeight(0, i);
        }
        System.out.println(alg.getPathWeight(0, numVertices - 1));
    }

    @Override
    protected void testJGraphT() {
        var alg = new org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths(jgrapht);
        for (int i = 1; i < graph.numVertices(); i++) {
            alg.getPath(0, i);
            //alg.getPathWeight(0, i);
        }
        System.out.println(alg.getPathWeight(0, numVertices - 1));
    }

    @Override
    protected void testAlgs4() {
        var alg = new FloydWarshall(adjMatrixEwd);
        for (int i = 1; i < graph.numVertices(); i++) {
            alg.path(0, i);
            //alg.dist(0, i);
        }
        System.out.println(alg.dist(0, numVertices - 1));
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
