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
import org.graph4j.Graph;

/**
 * A <i>path</i> is a trail with no duplicate vertices.
 *
 * Vertices can not repeat. Edges can not repeat.
 *
 * The length of a walk is its number of edges.
 *
 * @see Walk
 * @see Trail
 * @see Circuit
 * @see Cycle
 * @author Cristian Frăsinaru
 */
public class Path extends Trail {

    public Path(Graph graph) {
        super(graph);
    }

    public Path(Graph graph, int initialCapacity) {
        super(graph, initialCapacity);
    }

    public Path(Graph graph, int[] vertices) {
        super(graph, vertices);
    }

    public int firstVertex() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return vertices[0];
    }

    public int lastVertex() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return vertices[numVertices - 1];
    }

    @Override
    public boolean isValid() {
        if (!super.isValid()) {
            return false;
        }
        try {
            CheckArguments.noDuplicates(vertices());
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     *
     * @return {@code true} if the path is induced (there is no chord).
     */
    public boolean isInduced() {
        for (int i = 0; i < numVertices - 2; i++) {
            int v = vertices[i];
            for (int j = i + 2; j < numVertices; j++) {
                int u = vertices[j];
                if (graph.containsEdge(v, u)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * A hamiltonian path contains all vertices in the graph.
     *
     * @return {@code true} if the path is hamiltonian.
     */
    public boolean isHamiltonian() {
        return numVertices == graph.numVertices() && !IntArrays.containsDuplicates(vertices());
    }

}
