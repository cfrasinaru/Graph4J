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

import ro.uaic.info.graph.alg.HierholzerEulerianCircuit;
import ro.uaic.info.graph.gen.GraphGenerator;
import ro.uaic.info.graph.util.Tools;

/**
 *
 * @author Cristian Frăsinaru
 */
public class EulerianCircuitDemo extends PerformanceDemo {

    @Override
    protected void prepare() {
        //graph = new GnpRandomGenerator(10, 0.5).createGraph();
        graph = GraphGenerator.complete(4003);
        jgraph = Tools.createJGraph(graph);
    }

    @Override
    protected void test1() {
        new HierholzerEulerianCircuit(graph).findCircuit();
    }

    @Override
    protected void test2() {
        new org.jgrapht.alg.cycle.HierholzerEulerianCycle().getEulerianCycle(jgraph);
    }

    public static void main(String args[]) {
        var app = new EulerianCircuitDemo();
        app.demo();
    }
}
