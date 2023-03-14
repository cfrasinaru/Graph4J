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

import edu.princeton.cs.algs4.HopcroftKarp;
import org.jgrapht.alg.matching.HopcroftKarpMaximumCardinalityBipartiteMatching;
import org.jgrapht.alg.partition.BipartitePartitioning;
import org.graph4j.generate.RandomGnpBipartiteGenerator;
import org.graph4j.alg.matching.HopcroftKarpMaximumMatching;

/**
 *
 * @author Cristian Frăsinaru
 */
class HopcroftKarpDemo extends PerformanceDemo {

    private final double probability = 0.1;

    public HopcroftKarpDemo() {
        numVertices = 1_000;
        runJGraphT = true;
        runAlgs4 = true;
    }

    @Override
    protected void createGraph() {
        graph = new RandomGnpBipartiteGenerator(numVertices / 2, numVertices / 2, probability).createGraph();
        //graph = new GnmBipartiteGenerator(numVertices, numVertices, 10*numVertices).createGraph();
    }

    @Override
    protected void testGraph4J() {
        var alg = new HopcroftKarpMaximumMatching(graph);
        System.out.println(alg.getMatching().size());
    }

    @Override
    protected void testJGraphT() {
        var bip = new BipartitePartitioning(jgrapht);
        var left = bip.getPartitioning().getPartition(0);
        var right = bip.getPartitioning().getPartition(1);
        var alg = new HopcroftKarpMaximumCardinalityBipartiteMatching(jgrapht, left, right);
        System.out.println(alg.getMatching().getEdges().size());
    }

    @Override
    protected void testAlgs4() {
        var alg = new HopcroftKarp(algs4Graph);
        System.out.println(alg.size());
    }

    @Override
    protected void prepareArgs() {
        int steps = 10;
        args = new int[steps];
        for (int i = 0; i < steps; i++) {
            args[i] = 1000 * (i + 1);
        }
    }
}
