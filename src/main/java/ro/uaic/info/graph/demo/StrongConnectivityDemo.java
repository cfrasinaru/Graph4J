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

import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.alg.connectivity.TarjanStrongConnectivity;
import ro.uaic.info.graph.gen.GnpRandomGenerator;
import ro.uaic.info.graph.util.Tools;

/**
 *
 * @author Cristian Frăsinaru
 */
public class StrongConnectivityDemo extends PerformanceDemo {

    @Override
    protected void prepare() {
        //graph = GraphGenerator.complete(2000);
        graph = new GnpRandomGenerator(10_000, 0.0001).createDigraph();
        //graph = new CycleGenerator(50_000).createDigraph(true);
        //graph = new RandomTreeGenerator(10_000).create();
        //graph = new GraphBuilder().numVertices(11).addEdges("0-1,1-2,2-0,1-3,3-4,4-5,5-6,6-3,6-4,7-8,9-10").buildGraph();
        jgraph = Tools.createJGraph(graph);
    }

    @Override
    protected void testGraph4J() {
        var result = new TarjanStrongConnectivity((Digraph) graph).getStronglyConnectedSets();
        //var result = new GraphBiconnectivity(graph).getCutVertices();
        //System.out.println(new GraphBiconnectivity(graph).isBiconnected());
        System.out.println(result.size());
        //new DepthFirstSearch(graph).traverse(new DFSVisitor() {});
    }

    @Override
    protected void testJGraphT() {      
        var result = new KosarajuStrongConnectivityInspector(jgraph).stronglyConnectedSets();
        //var result = new BiconnectivityInspector(jgraph).getCutpoints();
        //System.out.println(new BiconnectivityInspector(jgraph).isBiconnected());
        System.out.println(result.size());
    }

    public static void main(String args[]) {
        var app = new StrongConnectivityDemo();
        app.demo();
    }
}
