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
package org.graph4j.alg;

import java.util.ArrayDeque;
import java.util.Queue;
import org.graph4j.Digraph;

/**
 *
 * @author Cristian Frăsinaru
 */
public class TopologicalSort extends DirectedGraphAlgorithm {

    public TopologicalSort(Digraph digraph) {
        super(digraph);
    }

    /**
     *
     * @return the topological order, or null if the digraph is not acyclic
     */
    public int[] sort() {
        int n = graph.numVertices();
        int[] inDegrees = graph.indegrees();
        Queue<Integer> queue = new ArrayDeque<>(n);
        for (int i = 0; i < n; i++) {
            if (inDegrees[i] == 0) {
                queue.offer(graph.vertexAt(i));
            }
        }
        int[] ordering = new int[n];
        int k = 0;
        while (!queue.isEmpty()) {
            int v = queue.poll();
            ordering[k++] = v;
            for (int u : graph.successors(v)) {
                int ui = graph.indexOf(u);
                inDegrees[ui]--;
                if (inDegrees[ui] == 0) {
                    queue.offer(graph.vertexAt(u));
                }
            }
        }
        if (k < n) {
            return null;
        }
        return ordering;
    }

}
