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
package ro.uaic.info.graph;

import java.util.Arrays;
import ro.uaic.info.graph.util.CheckArguments;

/**
 *
 * @author Cristian Frăsinaru
 */
public abstract class VertexSet {

    protected final Graph graph;
    protected int[] vertices;
    protected int size;
    protected final static int DEFAULT_INITIAL_CAPACITY = 10;

    /**
     *
     * @param graph
     */
    protected VertexSet(Graph graph) {
        this(graph, DEFAULT_INITIAL_CAPACITY);
    }

    /**
     *
     * @param graph
     * @param initialCapacity
     */
    protected VertexSet(Graph graph, int initialCapacity) {
        this.graph = graph;
        this.vertices = new int[initialCapacity];
        this.size = 0;
    }

    /**
     *
     * @param graph
     * @param vertices
     */
    public VertexSet(Graph graph, int... vertices) {
        CheckArguments.graphNotNull(graph);
        CheckArguments.graphContainsVertices(graph, vertices);
        this.graph = graph;
        this.vertices = vertices;
        this.size = vertices.length;
    }

    /**
     *
     * @return the number of vertices
     */
    public int numVertices() {
        return size;
    }

    /**
     *
     * @param v
     * @return
     */
    public boolean add(int v) {
        CheckArguments.graphContainsVertex(graph, v);
        if (contains(v)) {
            return false;
        }
        if (vertices.length == size) {
            grow();
        }
        vertices[size++] = v;
        return true;
    }

    /**
     *
     * @return
     */
    public int[] vertices() {
        if (vertices.length > size) {
            vertices = Arrays.copyOf(vertices, size);
        }
        return vertices;
    }

    /**
     *
     * @param v
     * @return
     */
    public boolean contains(int v) {
        for (int i = 0; i < size; i++) {
            if (vertices[i] == v) {
                return true;
            }
        }
        return false;
    }
    

    private void grow() {
        int oldLen = vertices.length;
        int newLen = oldLen + (oldLen >> 1);
        vertices = Arrays.copyOf(vertices, newLen);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(vertices[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Arrays.hashCode(this.vertices);
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
        final VertexSet other = (VertexSet) obj;
        for (int v : vertices) {
            if (!other.contains(v)) {
                return false;
            }
        }
        return true;
    }

}
