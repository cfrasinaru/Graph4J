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

}
