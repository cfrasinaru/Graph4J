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
 * https://mat.gsia.cmu.edu/COLOR/instances.html
 *
 * http://cedric.cnam.fr/~porumbed/graphs/
 *
 *
 * @author Cristian Frăsinaru
 */
class ExactEquitableColoringDemo extends PerformanceDemo {

    private final double probability = 0.5;
    private final int timeLimit = 1 * 60 * 1000;

    public ExactEquitableColoringDemo() {
        numVertices = 70;
        //runJGraphT = false;
        runOther = true; //gurobi
    }

    @Override
    protected void createGraph() {
        graph = new RandomGnpGraphGenerator(numVertices, probability).createGraph();
        //String name = "queen9_9";
        //String name = "anna";
        //String name = "1-fullins_4";
        //String name = "mug100_1";
        //graph = new DimacsIO().read("d:/datasets/coloring/instances/" + name + ".col");
        //var alg = new GreedyVertexSeparator(graph);
        //System.out.println(alg.getSeparator());
    }

    @Override
    protected void testGraph4J() {
        var alg = new org.graph4j.alg.coloring.eq.BacktrackEquitableColoring(graph, timeLimit);
        var col = alg.findColoring();
        System.out.println(col);
        System.out.println(col == null  ? -1 : col.numUsedColors());
    }

    @Override
    protected void testOther() {
        var alg = new org.graph4j.alg.coloring.eq.GurobiAssignmentEquitableColoring(graph, timeLimit);
        var col = alg.findColoring();
        System.out.println(col);
        System.out.println(col == null  ? -1 : col.numUsedColors());
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
        new ExactEquitableColoringDemo().demo();
    }
    
}
