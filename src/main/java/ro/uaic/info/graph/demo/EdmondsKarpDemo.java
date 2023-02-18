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

import edu.princeton.cs.algs4.FordFulkerson;
import edu.uci.ics.jung.algorithms.flows.EdmondsKarpMaxFlow;
import java.util.HashMap;
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.alg.flow.*;
import ro.uaic.info.graph.generate.EdgeWeightsGenerator;
import ro.uaic.info.graph.generate.GnpGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class EdmondsKarpDemo extends PerformanceDemo {

    private final double probability = 0.2;

    public EdmondsKarpDemo() {
        numVertices = 5000;
        runJGraphT = true;
        //runAlgs4 = true;
        //runJung = true; //very slow
    }

    @Override
    protected void createGraph() {
        graph = new GnpGraphGenerator(numVertices, probability).createDigraph();
        //graph = new GnmRandomGenerator(numVertices, 3*numVertices).createDigraph();
        //graph = new CompleteGenerator(numVertices).createDigraph();
        EdgeWeightsGenerator.randomIntegers(graph, 0, numVertices - 1);
    }

    @Override
    protected void testGraph4J() {
        //((GraphImpl)graph).enableAdjListMatrix(); //not worth it
        var alg = new EdmondsKarpMaximumFlow((Digraph) graph, 0, numVertices - 1);
        //var alg = new FordFulkersonMaximumFlow((Digraph) graph, 0, numVertices - 1);
        System.out.println(alg.getValue());
    }

    @Override
    protected void testJGraphT() {
        var alg = new EdmondsKarpMFImpl(jgrapht);
        System.out.println(alg.calculateMaximumFlow(0, numVertices - 1));
    }

    @Override
    protected void testAlgs4() {
        var alg = new FordFulkerson(algs4Net, 0, numVertices - 1);
        System.out.println(alg.value());
    }

    @Override
    protected void testJung() {
        var edgeFlowMap = new HashMap<Edge, Double>();
        var alg = new EdmondsKarpMaxFlow((edu.uci.ics.jung.graph.DirectedSparseGraph) jungGraph, 0, numVertices - 1,
                e -> ((Edge) e).weight(),
                edgeFlowMap,
                (() -> new Edge(0, 0)));
        alg.evaluate();
        System.out.println(alg.getMaxFlow());
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
