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

import org.graph4j.generate.RandomGnpGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
class ExactColoringDemo extends PerformanceDemo {

    private final double probability = 0.2;

    public ExactColoringDemo() {
        numVertices = 50;
        runJGraphT = true; //very slow
        //runOther = true;
    }

    @Override
    protected void createGraph() {
        graph = new RandomGnpGraphGenerator(numVertices, probability).createGraph();
        //graph = GraphGenerator.wheel(numVertices);
        //graph = GraphGenerator.cycle(numVertices);
        //graph = GraphGenerator.complete(numVertices);
        //graph = new RandomGnmGraphGenerator(numVertices, 5 * numVertices).createGraph();
        //graph = GraphGenerator.completeBipartite(numVertices, numVertices);
        //graph = new RandomTreeGenerator(numVertices).create();
    }

    @Override
    protected void testGraph4J() {
        var alg = new org.graph4j.alg.coloring.BacktrackColoring(graph);
        var col = alg.findColoring();
        System.out.println(col.numUsedColors());

    }

    @Override
    protected void testJGraphT() {
        var alg = new org.jgrapht.alg.color.BrownBacktrackColoring(jgrapht);
        var col = alg.getColoring();
        System.out.println(col.getNumberColors());

    }

    @Override
    protected void testOther() {
        /*
        var alg = new org.graph4j.alg.coloring.BacktrackColoring2(graph);
        var col = alg.findColoring();
        System.out.println(col.numUsedColors());
        */
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
