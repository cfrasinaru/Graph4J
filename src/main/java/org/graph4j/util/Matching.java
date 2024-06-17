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
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import org.graph4j.Edge;
import org.graph4j.Graph;

/**
 * A <em>matching</em> or <em>independent edge set</em> is a set of edges
 * without common vertices.
 *
 * @author Cristian Frăsinaru
 */
public class Matching {

    private final Graph graph;
    private final int mates[];
    private int size;
    private Set<Edge> edges;
    //if vu in matching, mates[vi]=ui, mates[ui]=vi

    public Matching(Graph graph) {
        this.graph = graph;
        mates = new int[graph.numVertices()];
        Arrays.fill(mates, -1);
    }

    public boolean add(int v, int u) {
        int vi = graph.indexOf(v);
        int ui = graph.indexOf(u);
        if (mates[vi] == ui) {
            return false;
        }
        mates[vi] = ui;
        mates[ui] = vi;
        size++;
        edges = null;
        return true;
    }

    protected boolean remove(int v, int u) {
        int vi = graph.indexOf(v);
        int ui = graph.indexOf(u);
        if (mates[vi] != ui) {
            return false;
        }
        mates[vi] = -1;
        mates[ui] = -1;
        size--;
        edges = null;
        return true;
    }

    public boolean contains(int v, int u) {
        int vi = graph.indexOf(v);
        int ui = graph.indexOf(u);
        return mates[vi] == ui;
    }

    /**
     * Returns the number of edges in the matching.
     *
     * @return the number of edges in the matching.
     */
    public int size() {
        return size;
    }

    public Set<Edge> edges() {
        if (edges != null) {
            return edges;
        }
        edges = new HashSet<>(size);
        for (int vi = 0, n = mates.length; vi < n; vi++) {
            int ui = mates[vi];
            edges.add(graph.edge(graph.vertexAt(vi), graph.vertexAt(ui)));
        }
        return edges;
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
        return 2 * size == graph.numVertices();
    }

    /**
     * Computes the sum of the weights associated with each edge in the
     * matching.
     *
     * @return the sum of all weights of the edges in the collection, including
     * duplicates.
     */
    public double weight() {
        double weight = 0;
        for (int vi = 0, n = mates.length; vi < n; vi++) {
            int ui = mates[vi];
            if (ui >= 0) {
                weight += graph.getEdgeWeight(graph.vertexAt(vi), graph.vertexAt(ui));
            }
        }
        return weight;
    }

    /**
     * A matching is valid if each vertex of the graph appears in at most one
     * edge of that matching.
     *
     * @return {@code true} if the matching is valid, {@code false} otherwise.
     */
    public boolean isValid() {
        for (int vi = 0, n = mates.length; vi < n; vi++) {
            int ui = mates[vi];
            if (ui >= 0 && vi != mates[ui]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Arrays.hashCode(this.mates);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Matching other = (Matching) obj;
        return Arrays.equals(this.mates, other.mates);
    }

    @Override
    public String toString() {
        var sb = new StringJoiner(", ", "[", "]");
        for (int vi = 0, n = mates.length; vi < n; vi++) {
            int ui = mates[vi];
            if (ui >= 0 && vi < ui) {
                sb.add(vi + "-" + ui);
            }
        }
        return sb.toString();
    }

}
