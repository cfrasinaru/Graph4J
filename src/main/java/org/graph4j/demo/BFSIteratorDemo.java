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

import com.google.common.graph.Traverser;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.graph4j.generate.RandomGnpGraphGenerator;
import org.graph4j.traverse.BFSIterator;

/**
 *
 * @author Cristian Frăsinaru
 */
class BFSIteratorDemo extends PerformanceDemo {

    private final double edgeProbability = 0.2;

    public BFSIteratorDemo() {
        numVertices = 1000;
        runGuava = true;
        runJGraphT = true;
    }

    @Override
    protected void createGraph() {
        graph = new RandomGnpGraphGenerator(numVertices, edgeProbability).createGraph();
    }

    @Override
    protected void testGraph4J() {
        int k = 0;
        for (int v : graph.vertices()) {
            var it = new BFSIterator(graph);
            while (it.hasNext()) {
                it.next();
                k++;
            }
        }
        System.out.println(k);
    }

    @Override
    protected void testJGraphT() {
        int k = 0;
        for (var v : jgrapht.vertexSet()) {
            var it = new BreadthFirstIterator<>(jgrapht, v);
            while (it.hasNext()) {
                it.next();
                k++;
            }
        }
        System.out.println(k);
    }

    private int gk;

    @Override
    protected void testGuava() {
        gk = 0;
        for (var v : guavaGraph.nodes()) {
            Traverser.forGraph(guavaGraph).depthFirstPostOrder(v)
                    .forEach(x -> gk++);
        }
        System.out.println(gk);
    }

    @Override
    protected void prepareArgs() {
        int steps = 10;
        args = new int[steps];
        for (int i = 0; i < steps; i++) {
            args[i] = 100 * (i + 1);
        }
    }
}
