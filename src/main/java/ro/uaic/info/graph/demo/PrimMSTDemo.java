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

import edu.princeton.cs.algs4.PrimMST;
import ro.uaic.info.graph.alg.mst.*;
import ro.uaic.info.graph.gen.*;

/**
 *
 * @author Cristian Frăsinaru
 */
public class PrimMSTDemo extends PerformanceDemo {

    private final double probability = 0.5;

    public PrimMSTDemo() {
        numVertices = 20;
        runJGraphT = true;
        runAlgs4 = true;
        //runJung = true;
    }

    @Override
    protected void createGraph() {
        //graph = new CompleteGenerator(numVertices).createGraph();
        graph = new GnpRandomGenerator(numVertices, probability).createGraph();
        EdgeWeightsGenerator.randomDoubles(graph, 0, 1);
    }

    @Override
    protected void testGraph4J() {
        var alg = new PrimMinimumSpanningTreeHeap(graph);
        //var alg = new PrimMinimumSpanningTreeDefault(graph);
        System.out.println(alg.getWeight());
        alg.getEdges();
    }

    @Override
    protected void testJGraphT() {
        var alg = new org.jgrapht.alg.spanning.PrimMinimumSpanningTree(jgraph);
        System.out.println(alg.getSpanningTree().getWeight());
    }

    @Override
    protected void testJung() {
        //var alg = new edu.uci.ics.jung.algorithms.shortestpath.PrimMinimumSpanningTree(?);
        //How is it supposed to invoke this algorithm?
    }

    @Override
    protected void testAlgs4() {
        var alg = new PrimMST(algs4Ewg);
        System.out.println(alg.weight());
        alg.edges();
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
