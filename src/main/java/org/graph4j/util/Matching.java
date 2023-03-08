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
package org.graph4j.util;

import org.graph4j.Graph;

/**
 * A <em>matching</em> or <em>independent edge set</em> is a set of edges
 * without common vertices.
 *
 * @author Cristian Frăsinaru
 */
public class Matching extends EdgeSet {

    public Matching(Graph graph) {
        super(graph);
    }

    public Matching(Graph graph, int initialCapacity) {
        super(graph, initialCapacity);
    }

    /**
     * A perfect matching of a graph is a matching in which every vertex of the
     * graph is incident to exactly one edge of the matching.
     *
     * @return {@code true} if the matching is perfect.
     */
    public boolean isPerfect() {
        return 2 * numEdges == graph.numVertices();
    }

    /**
     * A matching is valid if each vertex of the graph appears in at most one
     * edge of that matching.
     *
     * @return {@code true} if the matching is valid.
     */
    public boolean isValid() {
        int n = graph.numVertices();
        int[] count = new int[n];
        for (int[] e : edges()) {
            int v = e[0];
            int u = e[1];
            int vi = graph.indexOf(v);
            int ui = graph.indexOf(u);
            count[vi]++;
            count[ui]++;
            if (count[vi] > 1 || count[ui] > 1) {
                return false;
            }
        }
        return true;
    }
}
