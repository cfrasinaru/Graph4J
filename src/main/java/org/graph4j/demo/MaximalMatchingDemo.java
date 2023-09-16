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

import org.graph4j.alg.matching.GreedyWeightedMatching;
import org.graph4j.generate.EdgeWeightsGenerator;
import org.graph4j.generate.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
class MaximalMatchingDemo extends PerformanceDemo {

    private final double probability = 0.9;

    public MaximalMatchingDemo() {
        numVertices = 1_000_000;
        runJGraphT = true;
        //runOther = true;
    }

    @Override
    protected void createGraph() {
        //graph = GraphGenerator.randomGnp(numVertices, probability);
        graph = GraphGenerator.randomGnm(numVertices, 2 * numVertices);
        EdgeWeightsGenerator.randomDoubles(graph, -1, 1);
    }

    @Override
    protected void testGraph4J() {
        //var alg = new MaximalCardinalityMatching(graph);
        var alg = new GreedyWeightedMatching(graph, true);
        var m = alg.getMatching();
        System.out.println(m.size() + ": " + m.weight());
    }

    @Override
    protected void testJGraphT() {
        //var alg = new GreedyMaximumCardinalityMatching(jgrapht, true);
        var alg = new org.jgrapht.alg.matching.GreedyWeightedMatching(jgrapht, true);
        var m = alg.getMatching();
        System.out.println(m.getEdges().size() + ": " + m.getWeight());
    }

    @Override
    protected void testOther() {
        //var alg = new MaximalCardinalityMatching(graph);
        var alg = new GreedyWeightedMatching(graph, false);
        var m = alg.getMatching();
        System.out.println(m.size() + ": " + m.weight());
    }
    
    @Override
    protected void prepareArgs() {
        int steps = 10;
        args = new int[steps];
        for (int i = 0; i < steps; i++) {
            args[i] = 1000 * (i + 1);
        }
    }

    public static void main(String args[]) {
        new MaximalMatchingDemo().demo();
    }
}
