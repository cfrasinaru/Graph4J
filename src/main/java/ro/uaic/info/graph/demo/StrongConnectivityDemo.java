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

import edu.princeton.cs.algs4.TarjanSCC;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.alg.connectivity.TarjanStrongConnectivity;
import ro.uaic.info.graph.gen.GnmRandomGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class StrongConnectivityDemo extends PerformanceDemo {

    public StrongConnectivityDemo() {
        runJGraphT = true;
        //runAlgs4 = true;
    }

    @Override
    protected void createGraph() {
        int n = 100_000;
        graph = new GnmRandomGenerator(n, 5*n).createDigraph();
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
       var result = new KosarajuStrongConnectivityInspector(jgraph).stronglyConnectedSets();
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
    
    

    public static void main(String args[]) {
        var app = new StrongConnectivityDemo();
        app.demo();
    }
}
