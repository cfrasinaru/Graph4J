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

import ro.uaic.info.graph.alg.coloring.GreedyColoring;
import ro.uaic.info.graph.gen.GraphGenerator;
import ro.uaic.info.graph.util.Tools;

/**
 *
 * @author Cristian Frăsinaru
 */
public class GreedyColoringDemo extends PerformanceDemo {

    @Override
    protected void prepare() {
        graph = GraphGenerator.complete(3_000);
        //graph = new GnmRandomGenerator(10_000_000, 1_000_000).createGraph();
        //graph = GraphGenerator.completeBipartite(100, 100);
        //graph = new RandomTreeGenerator(1_000_000).create();
        jgraph = Tools.createJGraph(graph);

    }

    @Override
    protected void testGraph4J() {
        var alg = new GreedyColoring(graph);
        var col = alg.findColoring();
        System.out.println(col.numUsedColors());

    }

    @Override
    protected void testJGraphT() {
        var alg = new org.jgrapht.alg.color.GreedyColoring(jgraph);
        var col = alg.getColoring();
        System.out.println(col.getNumberColors());
    }

    public static void main(String args[]) {
        var app = new GreedyColoringDemo();
        app.demo();
    }

}
