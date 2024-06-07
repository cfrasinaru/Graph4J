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

import java.util.Arrays;
import org.graph4j.Graph;

/**
 * A <em>matching</em> or <em>independent edge set</em> is a set of edges
 * without common vertices.
 *
 * @author Cristian Frăsinaru
 */
public class Matching extends EdgeSet {

    private int mates[];
    //if vu in matching, mates[vi]=ui, mates[ui]=vi

    public Matching(Graph graph) {
        super(graph);
        createMates();
    }

    public Matching(Graph graph, int initialCapacity) {
        super(graph, initialCapacity);
        createMates();
    }

    private void createMates() {
        mates = new int[graph.numVertices()];
        Arrays.fill(mates, -1);
    }

    @Override
    public boolean add(int v, int u) {
        int vi = graph.indexOf(v);
        int ui = graph.indexOf(u);
        if (mates[vi] == ui) {
            return false;
        }
        if (!super.add(v, u)) {
            return false;
        }
        mates[vi] = ui;
        mates[ui] = vi;
        return true;
    }

    @Override
    protected void removeFromPos(int pos) {
        int vi = edges[pos][0];
        int ui = edges[pos][1];
        mates[vi] = -1;
        mates[ui] = -1;
        super.removeFromPos(pos);
    }

    @Override
    public boolean contains(int v, int u) {
        int vi = graph.indexOf(v);
        int ui = graph.indexOf(u);
        return mates[vi] == ui;
    }

    /**
     * Returns {@code true} if there is an edge in the matching incident to the
     * given vertex.
     *
     * @param v a vertex number.
     * @return {@code true} if the matching covers the given vertex.
     */
    public boolean covers(int v) {
        return mates[graph.indexOf(v)] >= 0;
    }

    /**
     * The <em>mate</em> of a vertex v is a vertex u such that the edge vu
     * belongs to the matching.
     *
     * @param v a vertex number.
     * @return the mate of v in the matching, or {@code -1} if it has no mate.
     */
    public int mate(int v) {
        int mateIdx = mates[graph.indexOf(v)];
        return mateIdx < 0 ? -1 : graph.vertexAt(mateIdx);
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
        for (int k = 0; k < numEdges; k++) {
            int vi = graph.indexOf(edges[k][0]);
            int ui = graph.indexOf(edges[k][1]);
            count[vi]++;
            count[ui]++;
            if (count[vi] > 1 || count[ui] > 1) {
                return false;
            }
        }
        return true;
    }
}
