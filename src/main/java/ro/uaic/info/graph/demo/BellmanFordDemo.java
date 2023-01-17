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
import ro.uaic.info.graph.util.Tools;

/**
 *
 * @author Cristian Frăsinaru
 */
public class BellmanFordDemo extends PerformanceDemo {

    @Override
    protected void prepare() {
        //graph = new GnpRandomGenerator(500, 0.5).createGraph();
        //EdgeWeightsGenerator.randomIntegers(graph, 1, 1000);
        graph = new CompleteGenerator(1500).createDigraph();
        EdgeWeightsGenerator.consecutiveIntegers(graph);
        jgraph = Tools.createJGraph(graph);
        algs4Ewd = Tools.createAlgs4EdgeWeightedDigraph(graph);
    }

    @Override
    protected void testGraph4J() {
        for (int i = 1; i < graph.numVertices(); i++) {
            new BellmanFordShortestPath(graph, 0).getPath(i);
        }
    }

    @Override
    protected void testJGraphT() {
        for (int i = 1; i < graph.numVertices(); i++) {
            //new org.jgrapht.alg.shortestpath.BellmanFordShortestPath<>(jgraph).getPath(0, i);
        }
    }

    @Override
    protected void testAlgs4() {
        for (int i = 1; i < graph.numVertices(); i++) {
            new BellmanFordSP(algs4Ewd, 0).pathTo(i);
        }
    }
    
    public static void main(String args[]) {
        var app = new BellmanFordDemo();
        app.demo();
    }
}
