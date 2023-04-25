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

import edu.princeton.cs.algs4.BellmanFordSP;
import org.graph4j.alg.sp.BellmanFordShortestPath;
import org.graph4j.generate.CompleteGraphGenerator;
import org.graph4j.generate.EdgeWeightsGenerator;
import org.graph4j.generate.RandomGnpGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
class BellmanFordDemo extends PerformanceDemo {

    public BellmanFordDemo() {
        numVertices = 500;
        runJGraphT = true;
        runAlgs4 = true;
    }

    @Override
    protected void createGraph() {
        graph = new RandomGnpGraphGenerator(numVertices, 0.2).createDigraph();
        //EdgeWeightsGenerator.randomIntegers(graph, 1, 1000);
        //graph = new CompleteGraphGenerator(numVertices).createDigraph();
        EdgeWeightsGenerator.randomDoubles(graph, 0, 1);
    }

    @Override
    protected void testGraph4J() {
        var alg = new BellmanFordShortestPath(graph, 0);
        for (int i = 1; i < graph.numVertices(); i++) {
            alg.findPath(i);
        }
        System.out.println(alg.getPathWeight(numVertices - 1));
    }

    @Override
    protected void testJGraphT() {
        var alg = new org.jgrapht.alg.shortestpath.BellmanFordShortestPath(jgrapht);
        for (int i = 1; i < graph.numVertices(); i++) {
            alg.getPath(0, i);
        }
        System.out.println(alg.getPathWeight(0, numVertices - 1));
    }

    @Override
    protected void testAlgs4() {
        var alg = new BellmanFordSP(algs4Ewd, 0);
        for (int i = 1; i < graph.numVertices(); i++) {
            alg.pathTo(i);
        }
        System.out.println(alg.distTo(numVertices - 1));
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
