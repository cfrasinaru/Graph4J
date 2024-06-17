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
package org.graph4j.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.graph4j.Graph;
import org.graph4j.Edge;

/**
 * A set of edges in a graph.
 *
 * @author Cristian Frăsinaru
 */
@Deprecated
class EdgeSet2 extends HashSet<Edge> {

    protected Graph graph;

    public EdgeSet2(Graph graph) {
        super();
        this.graph = graph;
    }

    public EdgeSet2(Graph graph, int initialCapacity) {
        super(initialCapacity);
        this.graph = graph;
    }

    public EdgeSet2(Graph graph, Set<Edge> edges) {
        super(edges);
        this.graph = graph;
    }

    public EdgeSet2(Graph graph, Edge[] edges) {
        super(Arrays.stream(edges).collect(Collectors.toSet()));
        this.graph = graph;
    }

    /**
     *
     * @return the vertices representing endpoints of the edges in this
     * collection.
     */
    public VertexSet vertexSet() {
        VertexSet set = new VertexSet(graph, size());
        for (Edge e : this) {
            set.add(e.source());
            set.add(e.target());
        }
        Arrays.sort(set.vertices());
        return set;
    }

    /**
     *
     * @return the vertices representing endpoints of the edges in this
     * collection.
     */
    public int[] vertices() {
        return vertexSet().vertices();
    }

    @Deprecated
    public int[][] edges() {
        int[][] arr = new int[size()][2];
        int i = 0;
        for (var e : this) {
            arr[i][0] = e.source();
            arr[i++][1] = e.target();
        }
        return arr;
    }

    @Deprecated
    public boolean add(int v, int u) {
        return this.add(new Edge(v, u));
    }

    /**
     * Computes the sum of the weights associated with each edge in the
     * collection.
     *
     * @return the sum of all weights of the edges in the collection, including
     * duplicates.
     */
    public double weight() {
        double weight = 0;
        for (var e : this) {
            weight += graph.getEdgeWeight(e);
        }
        return weight;
    }

}
