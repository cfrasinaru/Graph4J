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
import ro.uaic.info.graph.gen.GraphGenerator;
import ro.uaic.info.graph.util.Tools;

/**
 *
 * @author Cristian Frăsinaru
 */
@Deprecated
public class IterateAllEdgesDemo extends PerformanceDemo {

    @Override
    protected void prepare() {
        graph = GraphGenerator.complete(5000);
        jgraph = Tools.createJGraph(graph);
    }

    @Override
    protected void testGraph4J() {
        int k = 0;
        for (int v : graph.vertices()) {
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                it.next();
                k++;
            }
        }
        System.out.println(k + " = " + 2 * graph.numEdges());
    }

    @Override
    protected void testJGraphT() {
        int k = 0;
        for (var v : jgraph.vertexSet()) {
            for (var e : jgraph.iterables().edgesOf(v)) {
                k++;
            }
        }
        System.out.println(k + " = " + 2 * jgraph.edgeSet().size());
    }

    public static void main(String args[]) {
        var app = new IterateAllEdgesDemo();
        app.demo();
    }
}
