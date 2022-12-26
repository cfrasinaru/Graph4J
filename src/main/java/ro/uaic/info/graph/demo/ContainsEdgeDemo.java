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

import ro.uaic.info.graph.gen.GnmRandomGenerator;
import ro.uaic.info.graph.util.Tools;

/**
 *
 * @author Cristian Frăsinaru
 */
public class ContainsEdgeDemo extends PerformanceDemo {

    @Override
    protected void prepare() {
        graph = new GnmRandomGenerator(10_000, 10_000).createGraph();
        jgraph = Tools.createJGraph(graph);
    }

    @Override
    protected void test1() {
        int k = 0;
        for (int i = 0, n = graph.numVertices(); i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (graph.containsEdge(i, j)) {
                    k++;
                }
            }
        }
        System.out.println(k + " = " + graph.numEdges());
    }

    @Override
    protected void test2() {
        int k = 0;
        for (int i = 0, n = jgraph.vertexSet().size(); i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (jgraph.containsEdge(i, j)) {
                    k++;
                }
            }
        }
        System.out.println(k + " = " + jgraph.edgeSet().size());
    }

    public static void main(String args[]) {
        var app = new ContainsEdgeDemo();
        app.demo();
    }
}
