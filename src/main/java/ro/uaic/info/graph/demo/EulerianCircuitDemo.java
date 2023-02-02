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

import edu.princeton.cs.algs4.EulerianCycle;
import ro.uaic.info.graph.alg.eulerian.HierholzerEulerianCircuit;
import ro.uaic.info.graph.gen.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class EulerianCircuitDemo extends PerformanceDemo {

    public EulerianCircuitDemo() {
        runJGraphT = true;
        runAlgs4 = true;
    }

    @Override
    protected void createGraph() {
        graph = GraphGenerator.complete(3003);
    }

    @Override
    protected void testGraph4J() {
        new HierholzerEulerianCircuit(graph).findCircuit();
    }

    @Override
    protected void testJGraphT() {
        new org.jgrapht.alg.cycle.HierholzerEulerianCycle().getEulerianCycle(jgraph);
    }

    @Override
    protected void testAlgs4() {
        new EulerianCycle(algs4Graph).cycle();
    }

    public static void main(String args[]) {
        var app = new EulerianCircuitDemo();
        app.demo();
    }
}
