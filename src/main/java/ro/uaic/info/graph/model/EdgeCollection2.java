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
package ro.uaic.info.graph.model;

import java.util.Arrays;
import java.util.BitSet;
import java.util.StringJoiner;
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.Graph;

/**
 * A set of edges of a graph.
 *
 * @see VertexList
 * @see VertexSet
 * @see VertexStack
 * @author Cristian Frăsinaru
 */
abstract class EdgeCollection2 {

    protected final Graph graph;
    protected Edge[] edges;
    protected int numEdges;
    protected BitSet bitset; //which edges of the graph are in this collection
    protected final static int DEFAULT_CAPACITY = 10;

    /**
     *
     * @param graph the graph the edges belong to.
     */
    public EdgeCollection2(Graph graph) {
        this(graph, DEFAULT_CAPACITY);
    }

    /**
     *
     * @param graph the graph the edges belong to.
     * @param initialCapacity the initial capacity of this collection.
     */
    public EdgeCollection2(Graph graph, int initialCapacity) {
        this.graph = graph;
        this.edges = new Edge[initialCapacity];
        this.numEdges = 0;
    }

    /**
     *
     * @param graph the graph the edges belong to.
     * @param edges the initial set of edges.
     */
    public EdgeCollection2(Graph graph, Edge[] edges) {
        //CheckArguments.graphContainsVertices(graph, edges);
        this.graph = graph;
        this.edges = edges;
        this.numEdges = edges.length;
    }

    //lazy creation
    private void createBitSet() {
        this.bitset = new BitSet();
        for (Edge e : edges()) {
            //bitset.set(v, true);
        }
    }

    /**
     *
     * @return {@code true} if this collection has no edges
     */
    public boolean isEmpty() {
        return numEdges == 0;
    }

    /**
     * Same as {@code size()}.
     *
     * @return the number of edges in the collection
     */
    public int numEdges() {
        return numEdges;
    }

    /**
     * Same as {@code numVertices()}.
     *
     * @return the number of edges in the collection
     */
    public int size() {
        return numEdges;
    }

    /**
     * For performance reasons, the returned array represents the actual data
     * structure where edges of the collection are stored, so it must not be
     * modified.
     *
     * @return the edges in the collection
     */
    public Edge[] edges() {
        if (numEdges != edges.length) {
            edges = Arrays.copyOf(edges, numEdges);
        }
        return edges;
    }

    protected int indexOf(Edge e) {
        return indexOf(e, 0);
    }

    protected int indexOf(Edge e, int startPos) {
        for (int i = 0; i < numEdges; i++) {
            if (edges[i].equals(e)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Adds the vertex in the collection.
     *
     * @param e a vertex number
     * @return true, if the collection changed as a result of this call
     */
    protected boolean add(Edge e) {
        if (numEdges == edges.length) {
            grow();
        }
        edges[numEdges++] = e;
        if (bitset != null) {
            //bitset.set(e, true);
        }
        return true;
    }

    /**
     *
     * @param edges some edges
     */
    protected void addAll(Edge... edges) {
        for (Edge e : edges) {
            add(e);
        }
    }

    /**
     * Removes a vertex from the collection.
     *
     * @param v a vertex number
     * @return true, if the collection changed as a result of this call
     */
    protected boolean remove(Edge e) {
        int pos = indexOf(e);
        if (pos < 0) {
            return false;
        }
        removeFromPos(pos);
        return true;
    }

    //the order is maintained by default
    protected void removeFromPos(int pos) {
        for (int i = pos; i < numEdges - 1; i++) {
            edges[i] = edges[i + 1];
        }
        numEdges--;
        if (bitset != null) {
            //bitset.set(edges[pos], false);
        }
    }

    /**
     *
     * @param e
     * @return true, if this collection contains the vertex v
     */
    public boolean contains(Edge e) {
        //for smaller sets, just iterate
        //if (numEdges <= DEFAULT_CAPACITY) {
        return indexOf(e) >= 0;
        //}
        //for larger sets, create the bitset and use it
        //if (bitset == null) {
        //createBitSet();
        //}
        //return bitset.get(e);
        //return false;
    }

    /**
     *
     * @return the sum of all weights of the edges in the collection, including
     * duplicates.
     */
    public double computeEdgesWeight() {
        double weight = 0;
        for (int i = 0; i < numEdges; i++) {
            weight += edges[i].weight();
        }
        return weight;
    }

    protected void grow() {
        int oldLen = edges.length;
        int newLen = Math.max(DEFAULT_CAPACITY, oldLen + (oldLen >> 1));
        edges = Arrays.copyOf(edges, newLen);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Arrays.hashCode(this.edges());
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
        final EdgeCollection2 other = (EdgeCollection2) obj;
        for (int i = 0; i < numEdges; i++) {
            if (this.edges[i] != other.edges[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        var sb = new StringJoiner(",", "[", "]");
        for (int i = 0; i < numEdges; i++) {
            sb.add(String.valueOf(edges[i]));
        }
        return sb.toString();
    }

}
