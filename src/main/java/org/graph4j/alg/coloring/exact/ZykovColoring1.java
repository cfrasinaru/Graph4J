/*
 * Copyright (C) 2023 Cristian Frăsinaru and contributors
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
package org.graph4j.alg.coloring.exact;

import java.util.ArrayDeque;
import java.util.Deque;
import org.graph4j.Graph;
import org.graph4j.alg.clique.MaximalCliqueFinder;
import org.graph4j.alg.coloring.RecursiveLargestFirstColoring;
import org.graph4j.alg.coloring.VertexColoring;

/**
 * Useless.
 *
 * @author Cristian Frăsinaru
 */
class ZykovColoring1 extends ExactColoringBase {

    private Deque<Graph> stack;
    private int chi;

    public ZykovColoring1(Graph graph) {
        super(graph);
    }

    public ZykovColoring1(Graph graph, long timeLimit) {
        super(graph, timeLimit);
    }

    @Override
    protected ZykovColoring1 getInstance(Graph graph, long timeLimit) {
        return new ZykovColoring1(graph, timeLimit);
    }

    @Override
    public VertexColoring findColoring() {
        compute();
        System.out.println(chi);
        return null;
    }

    @Override
    public VertexColoring findColoring(int numColors) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void compute() {
        chi = Integer.MAX_VALUE;
        stack = new ArrayDeque<>();
        stack.push(graph);
        while (!stack.isEmpty()) {
            var g = stack.pop();
            System.out.println(g.numVertices() + ", " + g.numEdges() + ", chi=" + chi + ", stack: " + stack.size());
            if (g.isComplete()) {
                if (chi > g.numVertices()) {
                    chi = g.numVertices();
                }
                continue;
            }
            if (g.numVertices() < 90) {
                System.out.println("Backtrack coloring");
                var bt = new ParallelBacktrackColoring(g);
                var col = bt.findColoring();
                if (chi > col.numUsedColors()) {
                    chi = col.numUsedColors();
                }
                continue;
            }
            var clique = new MaximalCliqueFinder(g).getMaximalClique();
            if (clique.numVertices() >= chi) {
                continue;
            }
            var col = new RecursiveLargestFirstColoring(g).findColoring();
            if (chi > col.numUsedColors()) {
                chi = col.numUsedColors();
                if (chi == clique.numVertices()) {
                    continue;
                }
            }
            //choose a pair of vertices that are not adjacent, either:
            //a) one of them is colored and the other's domain contains 2 values
            // and the other is adjacent with as many vertices having 2 values in their domains
            //b) both have the same domain containing 2 values
            // and they are adjacent with an uncolored vertex 
            // which is adjacent with as many vertices having 2 values in their domains
            int n = g.numVertices();
            int maxv = -1, maxu = -1, max = -1;
            for (int v : g.vertices()) {
                if (g.degree(v) == n - 1) {
                    continue;
                }
                for (int u : g.vertices()) {
                    if (u == v || g.containsEdge(v, u)) {
                        continue;
                    }
                    int d = g.degree(v) + g.degree(u);
                    if (max < d) {
                        max = d;
                        maxv = v;
                        maxu = u;
                    }
                }
            }
            var g1 = g.copy();
            g1.addEdge(maxv, maxu);
            stack.push(g1);

            var g2 = g.copy();
            g2.contractVertices(maxv, maxu);
            stack.push(g2);

        }
    }

    private class Node {

        Graph graph;
        VertexColoring coloring;

        public Node(Graph graph, VertexColoring coloring) {
            this.graph = graph;
            this.coloring = coloring;
        }

    }
}
