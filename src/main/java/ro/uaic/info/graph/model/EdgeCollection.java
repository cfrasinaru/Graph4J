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
import java.util.StringJoiner;
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.Graph;

/**
 * A set of edges of a graph.
 *
 * @see EdgeSet
 * @author Cristian Frăsinaru
 */
abstract class EdgeCollection { //WORK IN PROGRESS

    protected final Graph graph;
    protected int[][] edges;
    protected int numEdges;
    //protected BitSet bitset; //which edges of the graph are in this collection
    protected final static int DEFAULT_CAPACITY = 10;

    /**
     *
     * @param graph the graph the edges belong to.
     */
    public EdgeCollection(Graph graph) {
        this(graph, DEFAULT_CAPACITY);
    }

    /**
     *
     * @param graph the graph the edges belong to
     * @param initialCapacity the initial capacity of this collection
     */
    public EdgeCollection(Graph graph, int initialCapacity) {
        this.graph = graph;
        this.edges = new int[initialCapacity][2];
        this.numEdges = 0;
    }

    /**
     *
     * @param graph the graph the edges belong to.
     * @param edges the initial set of edges.
     */
    public EdgeCollection(Graph graph, int[][] edges) {
        this.graph = graph;
        this.edges = edges;
        this.numEdges = edges.length;
    }

    //lazy creation
    /*
    private void createBitSet() {
        this.bitset = new BitSet();
        for (int[] e : edges()) {
            //bitset.set(v, true);
        }
    }*/
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
    public int numVertices() {
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
     * @return the edges in the collection.
     */
    public int[][] edges() {
        if (numEdges != edges.length) {
            edges = Arrays.copyOf(edges, numEdges);
        }
        return edges;
    }

    protected int indexOf(int v, int u) {
        return indexOf(v, u, 0);
    }

    protected int indexOf(int v, int u, int startPos) {
        for (int i = 0; i < numEdges; i++) {
            if ((edges[i][0] == v && edges[i][1] == u)
                    || (!graph.isDirected() && edges[i][0] == u && edges[i][1] == v)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Adds an edge in the collection.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return true, if the collection changed as a result of this call.
     */
    public boolean add(int v, int u) {
        if (numEdges == edges.length) {
            grow();
        }
        edges[numEdges][0] = v;
        edges[numEdges][1] = u;
        numEdges++;
        /*
        if (bitset != null) {
            bitset.set(v, true);
        }*/
        return true;
    }

    /**
     *
     * @param e
     * @return
     */
    public boolean add(Edge e) {
        return add(e.source(), e.target());
    }

    /**
     *
     * @param edges some edges
     */
    protected void addAll(int[]... edges) {
        for (int[] e : edges) {
            add(e[0], e[1]);
        }
    }

    /**
     * Removes a vertex from the collection.
     *
     * @param v a vertex number
     * @return true, if the collection changed as a result of this call
     */
    protected boolean remove(int v, int u) {
        int pos = indexOf(v, u);
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
        /*
        if (bitset != null) {
            bitset.set(edges[pos], false);
        }*/
    }

    /**
     *
     * @param v
     * @return true, if this collection contains the vertex v
     */
    public boolean contains(int v, int u) {
        //for smaller sets, just iterate
        //if (numEdges <= DEFAULT_CAPACITY) {
        return indexOf(v, u) >= 0;
        //}
        //for larger sets, create the bitset and use it
        /*
        if (bitset == null) {
            createBitSet();
        }
        return bitset.get(v);
         */
    }

    /**
     *
     * @return the sum of all weights of the edges in the collection, including
     * duplicates.
     */
    public double computeEdgesWeight() {
        double weight = 0;
        for (int i = 0; i < numEdges; i++) {
            weight += graph.getEdgeWeight(edges[i][0], edges[i][1]);
        }
        return weight;
    }

    protected void grow() {
        int oldLen = edges.length;
        int newLen = Math.max(DEFAULT_CAPACITY, oldLen + (oldLen >> 1));
        int[][] newEdges = new int[newLen][2];
        for (int i = 0; i < oldLen; i++) {
            newEdges[i][0] = edges[i][0];
            newEdges[i][1] = edges[i][1];
        }
        edges = newEdges;
    }

    public VertexSet vertexSet() {
        VertexSet set = new VertexSet(graph, size());
        for (int[] e : edges) {
            set.add(e[0]);
            set.add(e[1]);
        }
        Arrays.sort(set.vertices());
        return set;
    }

    public int[] vertices() {
        return vertexSet().vertices();
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
        final EdgeCollection other = (EdgeCollection) obj;
        for (int i = 0; i < numEdges; i++) {
            if (!(this.edges[i][0] == other.edges[i][0] && this.edges[i][1] == other.edges[i][1])
                    || (!graph.isDirected() && this.edges[i][0] == other.edges[i][1] && this.edges[i][1] == other.edges[i][0])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        var sb = new StringJoiner(", ", "[", "]");
        for (int i = 0; i < numEdges; i++) {
            sb.add(edges[i][0] + "-" + edges[i][1]);
        }
        return sb.toString();
    }

}
