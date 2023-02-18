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

import com.google.common.graph.Traverser;
import ro.uaic.info.graph.generate.GnpGraphGenerator;
import ro.uaic.info.graph.traverse.BFSVisitor;
import ro.uaic.info.graph.traverse.BFSTraverser;
import ro.uaic.info.graph.traverse.DFSVisitor;
import ro.uaic.info.graph.traverse.DFSTraverser;
import ro.uaic.info.graph.traverse.SearchNode;

/**
 *
 * @author Cristian Frăsinaru
 */
public class BFSVisitorDemo extends PerformanceDemo {

    public BFSVisitorDemo() {
        runGuava = true;
    }

    @Override
    protected void createGraph() {
        graph = new GnpGraphGenerator(1000, 0.2).createGraph();
    }

    private int k1=0;
    @Override
    protected void testGraph4J() {
        for (int v : graph.vertices()) {
            new BFSTraverser(graph).traverse(v, new BFSVisitor(){
                @Override
                public void startVertex(SearchNode node) {
                    k1++;
                }
            });
        }
        System.out.println(k1);
    }

    
    private int k2 = 0;

    @Override
    protected void testGuava() {
        for (var v : guavaGraph.nodes()) {
            Traverser.forGraph(guavaGraph).breadthFirst(v)
                    .forEach(x -> k2++);
        }
        System.out.println(k2);
    }

    public static void main(String args[]) {
        var app = new BFSVisitorDemo();
        app.demo();
    }
}
