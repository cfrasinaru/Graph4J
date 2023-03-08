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

import edu.princeton.cs.algs4.EulerianCycle;
import org.graph4j.alg.eulerian.HierholzerEulerianCircuit;
import org.graph4j.generate.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
class EulerianCircuitDemo extends PerformanceDemo {

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
        new org.jgrapht.alg.cycle.HierholzerEulerianCycle().getEulerianCycle(jgrapht);
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
