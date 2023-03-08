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

import edu.princeton.cs.algs4.CC;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.graph4j.alg.connectivity.ConnectivityAlgorithm;
import org.graph4j.generate.GnmGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
class ConnectivityDemo extends PerformanceDemo {

    public ConnectivityDemo() {
        runJGraphT = true;
        runAlgs4 = true;
    }

    @Override
    protected void createGraph() {
        int n = 1_000_000;
        graph = new GnmGraphGenerator(n, 3 * n).createGraph();
        /*
        int n = 1000;
        var g1 = GraphGenerator.complete(1000);
        var g2 = g1.copy();
        g2.renumberAdding(n);
        var g3 = g1.copy();
        g3.renumberAdding(2 * n);
        graph = Graphs.disjointUnion(g1, g2, g3);
         */
    }

    @Override
    protected void testGraph4J() {
        //var result = new GraphConnectivity(graph).getConnectedSets();
        //System.out.println(result.size());
        System.out.println(new ConnectivityAlgorithm(graph).isConnected());
    }

    @Override
    protected void testJGraphT() {
        //var result = new ConnectivityInspector(jgraph).connectedSets();
        //System.out.println(result.size());
        System.out.println(new ConnectivityInspector(jgrapht).isConnected());
    }

    @Override
    protected void testAlgs4() {
        //var result = new CC(algs4Graph);
        //System.out.println(result.count());
        System.out.println(new CC(algs4Graph).count() == 1);
    }

    public static void main(String args[]) {
        var app = new ConnectivityDemo();
        app.demo();
    }
}
