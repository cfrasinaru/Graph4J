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

import org.graph4j.alg.sp.FloydWarshallShortestPath;
import org.graph4j.alg.sp.JohnsonShortestPath;
import org.graph4j.generate.EdgeWeightsGenerator;
import org.graph4j.generate.RandomGnpGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
class JohnsonDemo extends PerformanceDemo {

    private final double probability = 0.1;
    private final int v;
    private final int u;

    public JohnsonDemo() {
        numVertices = 2000;
        runJGraphT = true; //8x slower
        runOther = true; //FloydWarshall
        //
        v = random.nextInt(numVertices);
        u = random.nextInt(numVertices);
    }

    @Override
    protected void createGraph() {
        graph = new RandomGnpGraphGenerator(numVertices, probability).createDigraph();
        //graph = GraphGenerator.complete(numVertices);
        EdgeWeightsGenerator.randomDoubles(graph, 0, 1);

    }

    @Override
    protected void testGraph4J() {
        var alg = new JohnsonShortestPath(graph);
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                alg.findPath(i, j);
                //alg.getPathWeight(i, j);
            }
        }
        System.out.println(alg.getPathWeight(v, u));
        System.out.println(alg.findPath(v, u).computeEdgesWeight());
    }

    @Override
    protected void testJGraphT() {
        var alg = new org.jgrapht.alg.shortestpath.JohnsonShortestPaths(jgrapht);
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                alg.getPath(i, j);
                //alg.getPathWeight(i, j);
            }
        }
        System.out.println(alg.getPathWeight(v, u));
    }

    @Override
    protected void testOther() {
        var alg = new FloydWarshallShortestPath(graph);
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                alg.findPath(i, j);
                //alg.getPathWeight(i, j);
            }
        }
        System.out.println(alg.getPathWeight(v, u));
        System.out.println(alg.findPath(v, u).computeEdgesWeight());
    }

    @Override
    protected void prepareArgs() {
        int steps = 10;
        args = new int[steps];
        for (int i = 0; i < steps; i++) {
            args[i] = 500 * (i + 1);
        }
    }

    public static void main(String args[]) {
        new JohnsonDemo().demo();
    }
}
