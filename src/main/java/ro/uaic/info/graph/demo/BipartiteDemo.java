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

import ro.uaic.info.graph.alg.bipartite.DFSBipartitionAlgorithm;
import edu.princeton.cs.algs4.Bipartite;
import org.jgrapht.alg.partition.BipartitePartitioning;
import ro.uaic.info.graph.gen.RandomTreeGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class BipartiteDemo extends PerformanceDemo {

    public BipartiteDemo() {
        runJGraphT = true;
        runAlgs4 = true;
    }

    
    @Override
    protected void createGraph() {
        //graph = new GnmRandomGenerator(10_000_000, 1_000_000).createGraph();
        //graph = GraphGenerator.completeBipartite(1500, 1500);
        graph = new RandomTreeGenerator(1_000_000).create();
    }

    @Override
    protected void testGraph4J() {
        var alg = new DFSBipartitionAlgorithm(graph);
        System.out.println(alg.isBipartite());

    }

    @Override
    protected void testJGraphT() {
        var alg = new BipartitePartitioning(jgraph);
        System.out.println(alg.isBipartite());
    }

    @Override
    protected void testAlgs4() {
        var alg = new Bipartite(algs4Graph);
        System.out.println(alg.isBipartite());
    }

    public static void main(String args[]) {
        var app = new BipartiteDemo();
        app.demo();
    }

}
