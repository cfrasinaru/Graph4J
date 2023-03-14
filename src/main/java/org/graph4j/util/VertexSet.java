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

import java.util.NoSuchElementException;
import java.util.stream.IntStream;
import org.graph4j.Graph;

/**
 * A set of vertices of a graph. No duplicates are allowed.
 *
 * @author Cristian Frăsinaru
 */
public class VertexSet extends VertexCollection {

    public VertexSet(Graph graph) {
        super(graph);
    }

    public VertexSet(Graph graph, int initialCapacity) {
        super(graph, initialCapacity);
    }

    public VertexSet(Graph graph, int[] vertices) {
        super(graph, vertices.length);
        addAll(vertices);
    }

    public VertexSet(VertexSet other) {
        this(other.graph, other.vertices());
    }

    @Override
    public boolean add(int v) {
        if (contains(v)) {
            return false;
        }
        return super.add(v);
    }

    @Override
    public final void addAll(int... vertices) {
        super.addAll(vertices);
    }

    @Override
    public boolean remove(int v) {
        return super.remove(v);
    }

    //swap with the last element
    @Override
    protected void removeFromPos(int pos) {
        if (bitset != null) {
            bitset.set(vertices[pos], false);
        }
        vertices[pos] = vertices[numVertices - 1];
        numVertices--;
    }

    /**
     * Returns and removes an element from the set, usually the last one added.
     *
     * @return an element from the set.
     */
    public int pop() {
        if (numVertices == 0) {
            throw new NoSuchElementException("The vertex set is empty");
        }
        if (bitset != null) {
            bitset.set(vertices[numVertices - 1], false);
        }
        return vertices[(numVertices--) - 1];
    }

    /**
     * Returns an element from the set, uauslly the last one added.
     *
     * @return an element from the set.
     */
    public int peek() {
        if (numVertices == 0) {
            throw new NoSuchElementException("The vertex set is empty");
        }
        return vertices[numVertices - 1];
    }

    /**
     *
     * @param other another vertex set.
     * @return a new set containing vertices belonging to both this and the
     * other set.
     */
    public VertexSet intersection(VertexSet other) {
        VertexSet set1, set2;
        if (this.size() <= other.size()) {
            set1 = this;
            set2 = other;
        } else {
            set1 = other;
            set2 = this;
        }
        VertexSet result = new VertexSet(graph, set1.size());
        for (int v : set1.vertices) {
            if (set2.contains(v)) {
                result.add(v);
            }
        }
        return result;
    }

    /**
     *
     * @param other an array of vertex numbers.
     * @return a new set containing vertices belonging to this set and the other
     * array.
     */
    public VertexSet intersection(int... other) {
        int min;
        if (this.numVertices <= other.length) {
            min = this.numVertices;
        } else {
            min = other.length;
        }
        VertexSet result = new VertexSet(graph, min);
        for (int v : other) {
            if (this.contains(v)) {
                result.add(v);
            }
        }
        return result;
    }

    /**
     *
     * @param other an array of vertex numbers.
     * @return a new set containing vertices belonging to this set or the other
     * array.
     */
    public VertexSet union(int... other) {
        VertexSet result = new VertexSet(graph, this.size() + other.length);
        union(this, other, result);
        return result;
    }

    @Override
    public int hashCode() {
        return IntStream.of(vertices).sum();
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
        final VertexSet other = (VertexSet) obj;
        return IntArrays.sameValues(this.vertices(), other.vertices());
    }

}
