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

import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.generate.TournamentGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class IterateSuccessorsDemo extends PerformanceDemo {

    private final int rep = 10;

    public IterateSuccessorsDemo() {
        numVertices = 1000;
        runJGraphT = true;
        runJung = true;
        runGuava = true;
    }

    @Override
    protected void createGraph() {
        graph = new TournamentGenerator(numVertices).createRandom();
    }

    @Override
    protected void testGraph4J() {
        Digraph g = (Digraph) graph;
        long k = 0;
        for (int i = 0; i < rep; i++) {
            for (int v : g.vertices()) {
                for (var it = g.succesorIterator(v); it.hasNext();) {
                    int u = it.next();
                    k++;
                }
            }
        }
        System.out.println(k + " = " + rep * graph.numEdges());
    }

    @Override
    protected void testJGraphT() {
        int k = 0;
        for (int i = 0; i < rep; i++) {
            for (var v : jgrapht.vertexSet()) {
                for (var e : jgrapht.outgoingEdgesOf(v)) {
                    k++;
                }
            }
        }
        System.out.println(k + " = " + rep * jgrapht.edgeSet().size());
    }

    @Override
    protected void testGuava() {
        long k = 0;
        for (int i = 0; i < rep; i++) {
            for (var v : guavaGraph.nodes()) {
                for (var u : guavaGraph.successors(v)) {
                    k++;
                }
            }
        }
        System.out.println(k + " = " + rep * guavaGraph.edges().size());
    }

    @Override
    protected void testJung() {
        long k = 0;
        for (int i = 0; i < rep; i++) {
            for (var v : jungGraph.getVertices()) {
                for (var u : jungGraph.getSuccessors(v)) {
                    k++;
                }
            }
        }
        System.out.println(k + " = " + rep * jungGraph.getEdgeCount());
    }

    @Override
    protected void prepareArgs() {
        int steps = 10;
        args = new int[steps];
        for (int i = 0; i < steps; i++) {
            args[i] = 500 * (i + 1);
        }
    }

}
