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
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;
import org.graph4j.Graph;
import org.graph4j.Edge;

/**
 * A set of edges in a graph. This is actually an extension of
 * {@code HashSet<Edge>}.
 *
 * @author Cristian Frăsinaru
 */
public class EdgeSet extends HashSet<Edge> {

    protected Graph graph;

    /**
     * Constructs a new, empty set of edges.
     *
     * @param graph the graph the edges belong to.
     */
    public EdgeSet(Graph graph) {
        super();
        this.graph = graph;
    }

    /**
     * Constructs a new, empty set of edges, with a specified initial capacity.
     *
     * @param graph the graph the edges belong to.
     * @param initialCapacity the initial capacity of this collection.
     */
    public EdgeSet(Graph graph, int initialCapacity) {
        super(initialCapacity);
        this.graph = graph;
    }

    /**
     * Constructs a new set containing the elements in the specified collection.
     *
     * @param graph the graph the edges belong to.
     * @param edges the collection of edges whose elements are to be placed into
     * this set.
     */
    public EdgeSet(Graph graph, Collection<Edge> edges) {
        super(edges);
        this.graph = graph;
    }

    /**
     * Constructs a new set containing the elements in the specified array.
     *
     * @param graph the graph the edges belong to.
     * @param edges the array of edges whose elements are to be placed into this
     * set.
     */
    public EdgeSet(Graph graph, Edge[] edges) {
        super(Arrays.stream(edges).collect(Collectors.toSet()));
        this.graph = graph;
    }

    /**
     * Returns the vertices representing endpoints of the edges in this
     * collection.
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
     * Adds the edge (v,u) to this set if it is not already present.
     *
     * @param v the source endpoint of the edge.
     * @param u the target endpoint of the edge.
     * @return {@code true} if the set did not already contain the specified
     * edge, {@code false} otherwise.
     */
    public boolean add(int v, int u) {
        return this.add(graph.edge(v, u));
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
