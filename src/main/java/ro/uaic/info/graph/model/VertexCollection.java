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
import java.util.stream.IntStream;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.util.CheckArguments;
import ro.uaic.info.graph.util.IntIterator;

/**
 * A set of vertices of a graph. No duplicates are allowed.
 *
 * @see VertexList
 * @see VertexSet
 * @see VertexStack
 * @author Cristian Frăsinaru
 */
abstract class VertexCollection {

    protected final Graph graph;
    protected int[] vertices;
    protected int numVertices;
    protected BitSet bitset; //which vertices of the graph are in this collection

    /**
     * The initial capacity of the collection equals the number of vertices in
     * the graph.
     *
     * @param graph the graph the vertices belong to
     */
    public VertexCollection(Graph graph) {
        //this(graph, Math.max(2, graph.numVertices() / 4));
        this(graph, Math.max(2, 10));
    }

    /**
     *
     * @param graph the graph the vertices belong to
     * @param initialCapacity the initial capacity of this collection
     */
    public VertexCollection(Graph graph, int initialCapacity) {
        this.graph = graph;
        this.vertices = new int[initialCapacity];
        this.numVertices = 0;
        initBitSet();
    }

    /**
     *
     * @param graph the graph the vertices belong to
     * @param vertices the initial set of vertices
     */
    public VertexCollection(Graph graph, int[] vertices) {
        //CheckArguments.graphContainsVertices(graph, vertices);
        this.graph = graph;
        this.vertices = vertices;
        this.numVertices = vertices.length;
        initBitSet();
    }

    private void initBitSet() {
        this.bitset = new BitSet(1 + IntStream.of(graph.vertices()).max().orElse(0));
    }

    /**
     *
     * @return {@code true} if this collection has no vertices
     */
    public boolean isEmpty() {
        return numVertices == 0;
    }

    /**
     * Same as {@code size()}.
     *
     * @return the number of vertices in the collection
     */
    public int numVertices() {
        return numVertices;
    }

    /**
     * Same as {@code numVertices()}.
     *
     * @return the number of vertices in the collection
     */
    public int size() {
        return numVertices;
    }

    /**
     *
     * @return an iterator for the vertices in the collection
     */
    public IntIterator iterator() {
        return new VertexCollectionIterator();
    }

    /**
     * For performance reasons, the returned array represents the actual data
     * structure where vertices of the collection are stored, so it must not be
     * modified.
     *
     * @return the vertices in the collection
     */
    public int[] vertices() {
        if (numVertices != vertices.length) {
            vertices = Arrays.copyOf(vertices, numVertices);
        }
        return vertices;
    }

    protected int indexOf(int v) {
        return indexOf(v, 0);
    }

    protected int indexOf(int v, int startPos) {
        for (int i = 0; i < numVertices; i++) {
            if (vertices[i] == v) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Adds the vertex in the collection.
     *
     * @param v a vertex number
     * @return true, if the collection changed as a result of this call
     */
    protected boolean add(int v) {
        if (numVertices == vertices.length) {
            grow();
        }
        vertices[numVertices++] = v;
        bitset.set(v, true);
        return true;
    }

    /**
     *
     * @param vertices some vertices
     */
    protected void addAll(int... vertices) {
        for (int v : vertices) {
            add(v);
        }
    }

    /**
     * Removes a vertex from the collection.
     *
     * @param v a vertex number
     * @return true, if the collection changed as a result of this call
     */
    protected boolean remove(int v) {
        int pos = indexOf(v);
        if (pos < 0) {
            return false;
        }
        removeFromPos(pos);
        return true;
    }

    //the order is maintained by default
    protected void removeFromPos(int pos) {
        for (int i = pos; i < numVertices - 1; i++) {
            vertices[i] = vertices[i + 1];
        }
        numVertices--;
        bitset.set(vertices[pos], false);
    }

    /**
     *
     * @param v
     * @return true, if this collection contains the vertex v
     */
    public boolean contains(int v) {
        return bitset.get(v);
    }

    /**
     *
     * @return the sum of all weights of the vertices in the collection,
     * including duplicates.
     */
    public double computeVerticesWeight() {
        double weight = 0;
        for (int i = 0; i < numVertices; i++) {
            weight += graph.getVertexWeight(vertices[i]);
        }
        return weight;
    }

    protected void grow() {
        int oldLen = vertices.length;
        int newLen = Math.max(2, oldLen + (oldLen >> 1));
        vertices = Arrays.copyOf(vertices, newLen);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Arrays.hashCode(this.vertices());
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
        final VertexCollection other = (VertexCollection) obj;
        for (int i = 0; i < numVertices; i++) {
            if (this.vertices[i] != other.vertices[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        var sb = new StringJoiner(",", "[", "]");
        for (int i = 0; i < numVertices; i++) {
            sb.add(String.valueOf(vertices[i]));
        }
        return sb.toString();
    }

    private class VertexCollectionIterator implements IntIterator {

        private int pos = -1;

        @Override
        public int next() {
            if (pos < 0 || pos >= numVertices) {
                throw new IllegalStateException(
                        "There are no more values to return.");
            }
            return vertices[++pos];
        }

        @Override
        public boolean hasNext() {
            return pos < numVertices;
        }

        @Override
        public void remove() {
            removeFromPos(pos);
        }
    }
}
