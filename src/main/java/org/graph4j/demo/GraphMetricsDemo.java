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

import org.graph4j.alg.GraphMetrics;
import org.graph4j.generate.GnmGraphGenerator;
import org.graph4j.generate.GraphGenerator;
import org.graph4j.traverse.BFSIterator;

/**
 *
 * @author Cristian Frăsinaru
 */
class GraphMetricsDemo {

    private void test1() {
        int n = 100_000;
        var graph = new GnmGraphGenerator(n, n * 10).createGraph();
        System.out.println("-------------------------------------------------");
        System.out.println("Iterator");
        int maxLevel = -1;
        int v = graph.vertexAt(0);
        var bfs1 = new BFSIterator(graph, v);
        while (bfs1.hasNext()) {
            var node = bfs1.next();
            if (maxLevel < node.level()) {
                maxLevel = node.level();
            }
        }
        System.out.println("maxLevel=" + maxLevel);
        System.out.println("maxLevel=" + bfs1.maxLevel());
        System.out.println("ecc=" + new GraphMetrics(graph).eccentricity(v, true));
    }
    
    private void demoRandom() {
        int n = 50_000;
        double p = 0.0005;
        int m = (int) (p * (n * (n - 1) / 2));
        //var g = Graphs.randomGnp(n, p);
        //var g = new GnmRandomGenerator(n, m).createGraph();
        var g = GraphGenerator.cycle(n);
        //g.addEdge(n - 1000, n - 1);
        g.addEdge(n / 2, n / 2 + 1000);
        long t0 = System.currentTimeMillis();
        System.out.println(new GraphMetrics(g).girth());
        long t1 = System.currentTimeMillis();
        System.out.println((t1 - t0) + "ms");

        var jg = Converter.createJGraphT(g);
        t0 = System.currentTimeMillis();
        //var gnp = new GnpRandomGraphGenerator<Integer, DefaultEdge>(n, p);
        //var gnp = new GnmRandomGraphGenerator<Integer, DefaultEdge>(n, m);
        //gnp.generateGraph(Tools.createJGraph(null));
        //System.out.println(jg);
        System.out.println(org.jgrapht.GraphMetrics.getGirth(jg));
        t1 = System.currentTimeMillis();
        System.out.println((t1 - t0) + "ms");
    }
}
