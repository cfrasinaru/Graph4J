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

import edu.princeton.cs.algs4.TarjanSCC;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.graph4j.Digraph;
import org.graph4j.alg.connectivity.TarjanStrongConnectivity;
import org.graph4j.generate.RandomGnmGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
class StrongConnectivityDemo extends PerformanceDemo {

    public StrongConnectivityDemo() {
        numVertices = 100_000;
        runJGraphT = true;
        //runAlgs4 = true;
    }

    @Override
    protected void createGraph() {
        graph = new RandomGnmGraphGenerator(numVertices, 5 * numVertices).createDigraph();
        //graph = new CycleGenerator(500_000).createDigraph(true);
    }

    @Override
    protected void testGraph4J() {
        var result = new TarjanStrongConnectivity((Digraph) graph).getStronglyConnectedSets();
        //var result = new GraphBiconnectivity(graph).getCutVertices();
        //System.out.println(new GraphBiconnectivity(graph).isBiconnected());
        System.out.println(result.size());
        //new DFSTraverser(graph).traverse(new DFSVisitor() {});
    }

    @Override
    protected void testJGraphT() {
        var result = new KosarajuStrongConnectivityInspector(jgrapht).stronglyConnectedSets();
        //var result = new GabowStrongConnectivityInspector(jgraph).stronglyConnectedSets();
        //var result = new BiconnectivityInspector(jgraph).getCutpoints();
        //System.out.println(new BiconnectivityInspector(jgraph).isBiconnected());
        System.out.println(result.size());
    }

    @Override
    protected void testAlgs4() {
        var alg = new TarjanSCC(algs4Digraph);
        //var alg = new KosarajuSharirSCC(algs4Digraph);
        //var alg = new GabowSCC(algs4Digraph);

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
