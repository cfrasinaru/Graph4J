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

import org.graph4j.Graph;

/**
 * A list of vertices of a graph. A vertex can appear multiple times.
 *
 * @see Walk
 * @see Trail
 * @see Path
 * @see Cycle
 * @author Cristian Frăsinaru
 */
public class VertexList extends VertexCollection {

    public VertexList(Graph graph) {
        super(graph);
    }

    public VertexList(Graph graph, int initialCapacity) {
        super(graph, initialCapacity);
    }

    public VertexList(Graph graph, int[] vertices) {
        super(graph, vertices);
    }

    @Override
    public boolean remove(int v) {
        return super.remove(v);
    }

    @Override
    public void removeFromPos(int pos) {
        super.removeFromPos(pos);
    }

    @Override
    public boolean add(int v) {
        return super.add(v);
    }

    @Override
    public void addAll(int... vertices) {
        super.addAll(vertices);
    }

    /**
     *
     * @param v a vertex number
     * @return the position of the first appearance of v in the array, or -1 if
     * v does not belong to it
     */
    @Override
    public int indexOf(int v) {
        return super.indexOf(v);
    }

    /**
     *
     * @param v a vertex number
     * @param startPos the start position
     * @return the position of the first appearance of v in the array, starting
     * with the given position (inclusive), or -1 if v does not belong to it
     */
    @Override
    public int indexOf(int v, int startPos) {
        return super.indexOf(v, startPos);
    }

    /**
     * Returns the vertex at the specified position in this list.
     *
     * @param pos a position in the list
     * @return the vertex at the specified position
     */
    public int get(int pos) {
        return vertices[pos];
    }

    /**
     * Replaces the vertex at the specified position in this list with the
     * specified vertex.
     *
     * @param pos position of the vertex to replace.
     * @param v a vertex number.
     */
    public void set(int pos, int v) {
        int u = vertices[pos];
        vertices[pos] = v;
        if (bitset != null) {
            bitset.set(u, false);
            bitset.set(v, true);
        }
    }

    /**
     * Inserts a vertex at the specified position in this list.
     *
     * @param pos position where the vertex must be inserted.
     * @param v a vertex number.
     */
    public void insert(int pos, int v) {
        if (numVertices >= vertices.length) {
            grow();
        }
        for (int j = numVertices; j > pos; j--) {
            vertices[j] = vertices[j - 1];
        }
        vertices[pos] = v;
        numVertices++;
        if (bitset != null) {
            bitset.set(v, true);
        }
    }

    /**
     *
     * @param other an array of vertex numbers.
     * @return a new list containing vertices belonging to this list or the
     * other array.
     */
    public VertexList union(int... other) {
        VertexList result = new VertexList(graph, this.size() + other.length);
        union(this, other, result);
        return result;
    }

    /**
     *
     * @param other a list of vertex numbers.
     * @return a new list containing vertices belonging to this list or the
     * other.
     */
    public VertexList union(VertexList other) {
        return this.union(other.vertices());
    }

}
