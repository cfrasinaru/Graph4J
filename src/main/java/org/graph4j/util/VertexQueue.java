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
import java.util.NoSuchElementException;
import org.graph4j.Graph;

/**
 * A queue of vertices of a graph.
 *
 * @author Cristian Frăsinaru
 */
public class VertexQueue extends VertexCollection {

    public VertexQueue(Graph graph) {
        super(graph);
    }

    public VertexQueue(Graph graph, int initialCapacity) {
        super(graph, initialCapacity);
    }

    /**
     * Adds a new element at the end of the queue.
     *
     * @param v a vertex number.
     */
    @Override
    public boolean add(int v) {
        return super.add(v);
    }

    /**
     * Returns the first element in the queue, without removing it.
     *
     * @return the first element in the queue.
     */
    public int peek() {
        if (numVertices == 0) {
            throw new NoSuchElementException();
        }
        return vertices[first];
    }

    /**
     * Returns and removes the first element in the queue.
     *
     * @return the first element in the queue.
     */
    public int poll() {
        if (numVertices == 0) {
            throw new NoSuchElementException();
        }
        int v = vertices[first];
        if (bitset != null) {
            bitset.set(v, false);
        }
        numVertices--;
        if (numVertices > 0) {
            first++;
        } else {
            first = 0;
        }
        return v;
    }

}
