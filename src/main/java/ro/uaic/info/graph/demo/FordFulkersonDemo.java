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
import org.jgrapht.alg.flow.EdmondsKarpMFImpl;
import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.alg.flow.*;
import ro.uaic.info.graph.generate.CompleteGraphGenerator;
import ro.uaic.info.graph.generate.EdgeWeightsGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
@Deprecated
public class FordFulkersonDemo extends PerformanceDemo {


    public FordFulkersonDemo() {
        numVertices = 1_000;
        //runJGraphT = true;
        //runAlgs4 = true;
        runJung = true;
    }

    @Override
    protected void createGraph() {
        //graph = new GnmRandomGenerator(numVertices, 5*numVertices).createDigraph();
        graph = new CompleteGraphGenerator(numVertices).createDigraph();
        EdgeWeightsGenerator.randomIntegers(graph, 0, 5);
    }

    @Override
    protected void testGraph4J() {
        var alg = new FordFulkersonMaximumFlow((Digraph) graph, 0, numVertices - 1);
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
        /*
        var edgeFlowMap = new HashMap<Edge, Double>();
        var edgeFactory = new Factory<Edge>() {
            public Edge create() {
                return new Edge(1.0, 1.0);
            }
        };
        var alg = new EdmondsKarpMaxFlow(jungGraph, 0, numVertices - 1,
                e -> ((Edge) e).weight(),
                edgeFlowMap,
                edgeFactory);
         */
        var alg = new EdmondsKarpMaximumFlow((Digraph) graph, 0, numVertices - 1);
        System.out.println(alg.getValue());
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
