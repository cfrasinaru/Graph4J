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

import org.jgrapht.traverse.DepthFirstIterator;
import ro.uaic.info.graph.gen.GnpRandomGenerator;
import ro.uaic.info.graph.search.DFSIterator;
import ro.uaic.info.graph.util.Tools;

/**
 *
 * @author Cristian Frăsinaru
 */
public class DFSIteratorDemo extends PerformanceDemo {

    @Override
    protected void prepare() {
        graph = new GnpRandomGenerator(500, 0.2).createGraph();
        jgraph = Tools.createJGraph(graph);
    }

    @Override
    protected void test1() {
        int k = 0;
        for (int v : graph.vertices()) {
            var it = new DFSIterator(graph);
            while (it.hasNext()) {
                it.next();
                k++;
            }
        }
        System.out.println(k);
    }

    @Override
    protected void test2() {
        int k = 0;
        for (var v : jgraph.vertexSet()) {
            var it = new DepthFirstIterator<>(jgraph, v);
            while (it.hasNext()) {
                it.next();
                k++;
            }
        }
        System.out.println(k);
    }

    public static void main(String args[]) {
        var app = new DFSIteratorDemo();
        app.demo();
    }
}
