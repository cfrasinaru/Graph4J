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

import org.jgrapht.alg.cycle.CycleDetector;
import ro.uaic.info.graph.Graphs;
import ro.uaic.info.graph.generate.GnmGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class CycleDetectionDemo extends PerformanceDemo {

    public CycleDetectionDemo() {
        numVertices = 1_000_000;
        runGuava = true;
        //runJGraphT = true;
    }

    @Override
    protected void createGraph() {
        //graph = new CycleGenerator(numVertices).createDigraph(true);
        graph = new GnmGraphGenerator(numVertices, numVertices).createGraph();
    }

    @Override
    protected void testGraph4J() {
        System.out.println(Graphs.containsCycle(graph));
    }

    @Override
    protected void testJGraphT() {
        var alg = new CycleDetector(jgrapht);
        System.out.println(alg.detectCycles());

    }

    @Override
    protected void testGuava() {
        System.out.println(com.google.common.graph.Graphs.hasCycle(guavaGraph));
    }

    @Override
    protected void prepareArgs() {
        int steps = 10;
        args = new int[steps];
        for (int i = 0; i < steps; i++) {
            args[i] = 100 * (i + 1);
        }
    }
}
