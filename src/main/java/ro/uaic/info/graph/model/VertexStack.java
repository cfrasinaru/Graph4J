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

import java.util.EmptyStackException;
import java.util.NoSuchElementException;
import ro.uaic.info.graph.Graph;

/**
 * A stack of vertices of a graph.
 *
 * @author Cristian Frăsinaru
 */
public class VertexStack extends VertexCollection {

    public VertexStack(Graph graph) {
        super(graph);
    }

    public VertexStack(Graph graph, int initialCapacity) {
        super(graph, initialCapacity);
    }

    /**
     *
     * @param v a vertex number
     */
    public void push(int v) {
        super.add(v);
    }

    /**
     *
     * @return
     */
    public int peek() {
        if (numVertices == 0) {
            throw new NoSuchElementException();
        }
        return vertices[numVertices - 1];
    }

    /**
     *
     * @return
     */
    public int pop() {
        if (numVertices == 0) {
            throw new NoSuchElementException();
        }
        if (bitset != null) {
            bitset.set(vertices[numVertices - 1], false);
        }
        return vertices[(numVertices--) - 1];
    }

}
