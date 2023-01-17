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

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import ro.uaic.info.graph.Graphs;
import ro.uaic.info.graph.alg.connectivity.GraphConnectivity;
import ro.uaic.info.graph.gen.GnpRandomGenerator;
import ro.uaic.info.graph.gen.GraphGenerator;
import ro.uaic.info.graph.util.Tools;

/**
 *
 * @author Cristian Frăsinaru
 */
public class ConnectivityDemo extends PerformanceDemo {

    @Override
    protected void prepare() {
        //graph = new GnpRandomGenerator(50_000, 0.0002).createGraph();
        int n = 1000;
        var g1 = GraphGenerator.complete(1000);
        var g2 = g1.copy();
        g2.renumberAdding(n);
        var g3 = g1.copy();
        g3.renumberAdding(2*n);
        graph = Graphs.disjointUnion(g1, g2, g3);
        jgraph = Tools.createJGraph(graph);
    }

    @Override
    protected void testGraph4J() {
        var result = new GraphConnectivity(graph).getComponents();
        //System.out.println(new GraphConnectivity(graph).isConnected());
        System.out.println(result.size());
    }

    @Override
    protected void testJGraphT() {
        var result = new ConnectivityInspector(jgraph).connectedSets();
        //System.out.println(new ConnectivityInspector(jgraph).isConnected());
        System.out.println(result.size());
    }

    public static void main(String args[]) {
        var app = new ConnectivityDemo();
        app.demo();
    }
}
