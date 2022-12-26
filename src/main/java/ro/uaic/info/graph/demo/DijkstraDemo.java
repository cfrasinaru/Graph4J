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

import ro.uaic.info.graph.alg.sp.DijkstraShortestPathHeap;
import ro.uaic.info.graph.gen.EdgeWeightsGenerator;
import ro.uaic.info.graph.gen.GnpRandomGenerator;
import ro.uaic.info.graph.gen.GraphGenerator;
import ro.uaic.info.graph.util.Tools;

/**
 *
 * @author Cristian Frăsinaru
 */
public class DijkstraDemo extends PerformanceDemo {

    @Override
    protected void prepare() {
        //graph = new GnpRandomGenerator(1000, 0.5).createGraph();
        //EdgeWeightsGenerator.randomIntegers(graph, 1, 1000);
        graph = GraphGenerator.complete(1000);
        EdgeWeightsGenerator.consecutiveIntegers(graph);
        jgraph = Tools.createJGraph(graph);
    }

    @Override
    protected void test1() {
        for (int i = 1; i < graph.numVertices(); i++) {
            //var result = new DijkstraShortestPathDefault(graph, 0).getPath(i);
            var result = new DijkstraShortestPathHeap(graph, 0).getPath(i);
        }
    }

    @Override
    protected void test2() {
        for (int i = 1; i < graph.numVertices(); i++) {
            var result = new org.jgrapht.alg.shortestpath.DijkstraShortestPath(jgraph).getPath(0, i);
        }
    }

    public static void main(String args[]) {
        var app = new DijkstraDemo();
        app.demo();
    }
}
