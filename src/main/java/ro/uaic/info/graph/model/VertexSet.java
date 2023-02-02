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

import java.util.NoSuchElementException;
import java.util.StringJoiner;
import java.util.stream.IntStream;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.util.IntArrays;

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

    /**
     * Returns and removes the last element added to the set.
     *
     * @return an element from the set
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

    @Override
    public String toString() {
        var sb = new StringJoiner(",", "{", "}");
        for (int i = 0; i < numVertices; i++) {
            sb.add(String.valueOf(vertices[i]));
        }
        return sb.toString();
    }
}
