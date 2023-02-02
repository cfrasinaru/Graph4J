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

import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import ro.uaic.info.graph.alg.connectivity.*;
import ro.uaic.info.graph.gen.GnmRandomGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class BiconnectivityDemo extends PerformanceDemo {

    public BiconnectivityDemo() {
        runJGraphT = true;
    }

    @Override
    protected void createGraph() {
        int n = 50_000;
        graph = new GnmRandomGenerator(n, n).createGraph();
        //graph = GraphGenerator.complete(2000);
        //graph = GraphGenerator.cycle(50_000);
        //graph = new RandomTreeGenerator(10_000).create();
    }

    @Override
    protected void testGraph4J() {
        //var result = new TarjanBiconnectivity(graph).getBlocks();
        //System.out.println(result.size());
        //var result = new GraphBiconnectivity(graph).getCutVertices();
        System.out.println(new TarjanBiconnectivity(graph).isBiconnected());
    }

    @Override
    protected void testJGraphT() {
        //var result = new BiconnectivityInspector(jgraph).getBlocks();
        //System.out.println(result.size());
        //var result = new BiconnectivityInspector(jgraph).getCutpoints();
        System.out.println(new BiconnectivityInspector(jgraph).isBiconnected());
    }
    
    public static void main(String args[]) {
        var app = new BiconnectivityDemo();
        app.demo();
    }
}
