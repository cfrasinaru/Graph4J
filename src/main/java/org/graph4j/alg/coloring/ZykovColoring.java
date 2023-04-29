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
package org.graph4j.alg.coloring;

import java.util.ArrayDeque;
import java.util.Deque;
import org.graph4j.Graph;
import org.graph4j.Graphs;
import org.graph4j.alg.SimpleGraphAlgorithm;
import org.graph4j.alg.clique.BronKerboschCliqueIterator;

/**
 * Useless.
 *
 * @author Cristian Frăsinaru
 */
@Deprecated
public class ZykovColoring extends SimpleGraphAlgorithm
        implements VertexColoringAlgorithm {

    private Deque<Graph> stack;
    private int chi;

    public ZykovColoring(Graph graph) {
        super(graph);
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
            //System.out.println(g.numVertices() + ", " + g.numEdges());
            if (g.isComplete()) {
                if (chi > g.numVertices()) {
                    chi = g.numVertices();
                }
                continue;
            }
            var bk = new BronKerboschCliqueIterator(g);
            if (bk.hasNext()) {
                if (bk.next().size() >= chi) {
                    continue;
                }
            }
            int n = g.numVertices();
            over:
            for (int v : g.vertices()) {
                if (g.degree(v) == n - 1) {
                    continue;
                }
                for (int u : g.vertices()) {
                    if (u == v || g.containsEdge(v, u)) {
                        continue;
                    }
                    var g1 = g.copy();
                    g1.addEdge(v, u);
                    stack.push(g1);

                    var g2 = g.copy();
                    g2.contractVertices(v, u);
                    stack.push(g2);
                    break over;
                }
            }
        }
    }

}
