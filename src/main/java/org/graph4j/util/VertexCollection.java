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
import java.util.BitSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringJoiner;
import org.graph4j.Graph;

/**
 * A collection of vertices in a graph.
 *
 * @see VertexList
 * @see VertexSet
 * @see VertexStack
 * @author Cristian Frăsinaru
 */
abstract class VertexCollection implements Iterable<Integer> {

    protected final Graph graph;
    protected int[] vertices;
    protected int numVertices;
    protected int first = 0;
    protected BitSet bitset; //which vertices of the graph are in this collection
    protected final static int DEFAULT_CAPACITY = 10;

    /**
     *
     * @param graph the graph the vertices belong to.
     */
    public VertexCollection(Graph graph) {
        this(graph, DEFAULT_CAPACITY);
    }

    /**
     *
     * @param graph the graph the vertices belong to.
     * @param initialCapacity the initial capacity of this collection.
     */
    public VertexCollection(Graph graph, int initialCapacity) {
        this.graph = graph;
        this.vertices = new int[initialCapacity];
        this.numVertices = 0;
    }

    /**
     *
     * @param graph the graph the vertices belong to.
     * @param vertices the initial set of vertices.
     */
    public VertexCollection(Graph graph, int[] vertices) {
        //CheckArguments.graphContainsVertices(graph, vertices);
        this.graph = graph;
        this.vertices = vertices;
        this.numVertices = vertices.length;
    }

    //lazy creation
    private void createBitSet() {
        this.bitset = new BitSet();
        for (int v : vertices()) {
            bitset.set(v, true);
        }
    }

    /**
     *
     * @return {@code true} if this collection has no vertices.
     */
    public boolean isEmpty() {
        return numVertices == 0;
    }

    /**
     * Same as {@code size()}.
     *
     * @return the number of vertices in the collection.
     */
    public int numVertices() {
        return numVertices;
    }

    /**
     * Same as {@code numVertices()}.
     *
     * @return the number of vertices in the collection.
     */
    public int size() {
        return numVertices;
    }

    /**
     *
     * @return an iterator over the vertices in the collection.
     */
    @Override
    public Iterator<Integer> iterator() {
        return new VertexCollectionIterator();
    }

    /**
     * For performance reasons, the returned array represents the actual data
     * structure where vertices of the collection are stored, so it must not be
     * modified.
     *
     * @return the vertices in the collection.
     */
    public int[] vertices() {
        if (first + numVertices != vertices.length) {
            int[] temp = new int[numVertices];
            System.arraycopy(vertices, first, temp, 0, numVertices);
            vertices = temp;
            first = 0;
        }
        return vertices;
    }

    protected int indexOf(int v) {
        return indexOf(v, 0);
    }

    protected int indexOf(int v, int startPos) {
        for (int i = 0; i < numVertices; i++) {
            if (vertices[first + i] == v) {
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
        if (first + numVertices >= vertices.length) {
            grow();
        }
        vertices[first + numVertices] = v;
        numVertices++;
        if (bitset != null) {
            bitset.set(v, true);
        }
        return true;
    }

    /**
     *
     * @param vertices an array of vertex numbers.
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
            vertices[first + i] = vertices[first + i + 1];
        }
        numVertices--;
        if (bitset != null) {
            bitset.set(vertices[pos], false);
        }
    }

    /**
     *
     * @param vertices an array of vertex numbers.
     * @return {@code true} if the collection was modified as a result of this
     * invocation.
     */
    public boolean removeAll(int... vertices) {
        boolean modified = false;
        for (int v : vertices) {
            if (remove(v)) {
                modified = true;
            }
        }
        return modified;
    }

    /**
     *
     * @param vertices an array of vertex numbers.
     * @return {@code true} if the collection was modified as a result of this
     * invocation.
     */
    public boolean retainAll(int... vertices) {
        boolean modified = false;
        for (var it = iterator(); it.hasNext();) {
            int v = it.next();
            if (!IntArrays.contains(vertices, v)) {
                remove(v);
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Removes all of the elements from this collection. The collection will be
     * empty after this method returns.
     */
    public void clear() {
        first = 0;
        numVertices = 0;
        if (bitset != null) {
            bitset.clear();
        }
    }

    /**
     *
     * @param v a vertex number.
     * @return {@code true}, if this collection contains the vertex v.
     */
    public boolean contains(int v) {
        //for smaller sets, just iterate
        if (numVertices <= DEFAULT_CAPACITY) {
            return indexOf(v) >= 0;
        }
        //for larger sets, create the bitset and use it
        if (bitset == null) {
            createBitSet();
        }
        return bitset.get(v);
    }

    protected static void union(VertexCollection set, int[] other, VertexCollection result) {
        result.numVertices = set.numVertices;
        for (int i = 0; i < set.numVertices; i++) {
            result.vertices[i] = set.vertices[i];
        }
        for (int v : other) {
            result.add(v);
        }
    }

    /**
     *
     * @return the sum of all weights of the vertices in the collection,
     * including duplicates.
     */
    public double computeVerticesWeight() {
        double weight = 0;
        for (int i = 0; i < numVertices; i++) {
            weight += graph.getVertexWeight(vertices[first + i]);
        }
        return weight;
    }

    protected void grow() {
        int oldLen = numVertices;
        int newLen = Math.max(DEFAULT_CAPACITY, oldLen + (oldLen >> 1));
        int[] temp = new int[newLen];
        System.arraycopy(vertices, first, temp, 0, numVertices);
        vertices = temp;
        first = 0;
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
            if (this.vertices[first + i] != other.vertices[first + i]) {
                return false;
            }
        }
        return true;
    }

    protected String vertexToString(int i) {
        StringBuilder sb = new StringBuilder();
        int v = vertices[first + i];
        sb.append(v);
        if (graph.isVertexLabeled()) {
            var label = graph.getVertexLabel(v);
            if (label != null) {
                sb.append(":").append(label);
            }
        }
        if (graph.isVertexWeighted()) {
            double weight = graph.getVertexWeight(v);
            sb.append("=").append(weight);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        var sb = new StringJoiner(", ", "[", "]");
        for (int i = 0; i < numVertices; i++) {
            sb.add(vertexToString(i));
        }
        return sb.toString();
    }

    private class VertexCollectionIterator implements Iterator<Integer> {

        private int pos = -1;

        @Override
        public Integer next() {
            if (pos >= numVertices) {
                throw new NoSuchElementException();
            }
            return vertices[++pos];
        }

        @Override
        public boolean hasNext() {
            return pos < numVertices - 1;
        }

        @Override
        public void remove() {
            if (pos < 0) {
                throw new NoSuchElementException();
            }
            removeFromPos(pos);
            pos--;
        }
    }
}
