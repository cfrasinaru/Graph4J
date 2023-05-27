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

import org.graph4j.Graphs;
import org.graph4j.measures.GraphMeasures;
import org.graph4j.alg.connectivity.BiconnectivityAlgorithm;
import org.graph4j.generate.RandomGnpGraphGenerator;
import org.graph4j.io.*;

/**
 *
 * https://mat.gsia.cmu.edu/COLOR/instances.html
 *
 * http://cedric.cnam.fr/~porumbed/graphs/
 *
 *
 * @author Cristian Frăsinaru
 */
class ExactColoringDemo extends PerformanceDemo {

    private final double probability = 0.5;
    private final int timeLimit = 2 * 60 * 1000;

    public ExactColoringDemo() {
        numVertices = 70;
        //runJGraphT = true; //very slow
        runOther = true; //gurobi
    }

    @Override
    protected void createGraph() {
        //graph = new RandomGnpGraphGenerator(numVertices, probability).createGraph();
        //gurobi wins: queen9_9(time), le450_5a(5<8), DSJC125.9(44<46)
        //gurobi wins: mug100_1, 1-FullIns_4, 1-Insertions_4
        //graph4j wins: school1 (time), le450_5b(time), le450_15c(22<23), r1000.5(240<252), queen10_10(11<12)
        //graph4j wins: random(70,0.5)
        //ash608GPIA
        //1-Insertions_4
        //String name = "1-FullIns_4";
        //String name = "myciel6";
        //String name = "1-Insertions_4";
        //String name = "mug100_1";
        String name = "queen9_9";
        graph = new DimacsIO().read("d:/datasets/coloring/instances/" + name + ".col");
        System.out.println("n=" + graph.numVertices());
        System.out.println("m=" + graph.numEdges());
        System.out.println("density=" + GraphMeasures.density(graph));
        System.out.println("minDegree=" + GraphMeasures.minDegree(graph));
        System.out.println("maxDegree=" + GraphMeasures.maxDegree(graph));

        System.out.println("connected: " + Graphs.isConnected(graph));
        System.out.println("2-connected: " + Graphs.isBiconnected(graph));
        var alg = BiconnectivityAlgorithm.getInstance(graph);
        for (var cc : alg.getBlocks()) {
            System.out.println("block:" + cc.size());
        }
        System.out.println("bridgeless: " + Graphs.isBridgeless(graph));

        /*
        var bk = MaximalCliqueIterator.getInstance(graph.complement());
        int x = 0;
        while (bk.hasNext()) {
            var q = bk.next();
            if (q.size() > 2) System.out.println(q);
            x++;
        }
        System.out.println(x);
        */
         
        //graph = GraphGenerator.wheel(numVertices);
        //graph = GraphGenerator.cycle(numVertices);
        //graph = GraphGenerator.complete(numVertices);
        //graph = new RandomGnmGraphGenerator(numVertices, 5 * numVertices).createGraph();
        //graph = GraphGenerator.completeBipartite(numVertices, numVertices);
        //graph = new RandomTreeGenerator(numVertices).create();
    }

    @Override
    protected void testGraph4J() {
        var alg = new org.graph4j.alg.coloring.exact.ParallelBacktrackColoring(graph, timeLimit);
        var col = alg.findColoring();
        System.out.println(col.numUsedColors());
    }

    @Override
    protected void testJGraphT() {
        /*
        var alg = new org.jgrapht.alg.color.BrownBacktrackColoring(jgrapht);
        var col = alg.getColoring();
        System.out.println(col.getNumberColors());
         */
        var alg = new org.graph4j.alg.coloring.exact.SimpleBacktrackColoring(graph, timeLimit);
        var col = alg.findColoring();
        System.out.println(col.numUsedColors());
    }

    @Override
    protected void testOther() {
        var alg = new org.graph4j.alg.coloring.exact.GurobiAssignmentColoring(graph, timeLimit);
        //var alg = new org.graph4j.alg.coloring.BacktrackColoring(graph, timeLimit);
        //var alg = new org.graph4j.alg.coloring.ZykovColoring(graph, timeLimit);
        var col = alg.findColoring();
        System.out.println(col.numUsedColors());
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
