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
import org.graph4j.Edge;
import org.graph4j.Graph;

/**
 * A <i>cycle</i> is a closed path, meaning that the last vertex of the path is
 * connected to the first one.
 *
 * Vertices can not repeat. Edges can not repeat.
 *
 * The length of a cycle is the number of edges in the path plus the one between
 * the endpoints.
 *
 * @see Walk
 * @see Trail
 * @see Circuit
 * @see Path
 * @author Cristian Frăsinaru
 */
public class Cycle extends Path {

    public Cycle(Graph graph) {
        super(graph);
    }

    public Cycle(Graph graph, int initialCapacity) {
        super(graph, initialCapacity);
    }

    public Cycle(Graph graph, int[] vertices) {
        super(graph, vertices);
    }

    @Override
    public final boolean isValid() {
        if (!super.isValid()) {
            return false;
        }
        Edge e = new Edge(vertices[numVertices - 1], vertices[0]);
        if (!graph.containsEdge(e)) {
            System.err.println("Vertices do not form a cycle, there is no edge: " + e);
            return false;
        }
        return true;
    }

    /**
     *
     * @return {@code true} if the cycle is induced (there is no chord).
     */
    @Override
    public boolean isInduced() {
        for (int i = 0; i < numVertices - 2; i++) {
            int v =  vertices[i];
            for (int j = i + 2; j < numVertices; j++) {
                if (i == 0 && j == numVertices - 1) {
                    continue;
                }
                int u =  vertices[j];
                if (graph.containsEdge(v, u)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean isClosed() {
        return true;
    }

    @Override
    public int length() {
        return numVertices;
    }

    @Override
    public double computeEdgesWeight() {
        return super.computeEdgesWeight()
                + graph.getEdgeWeight(vertices[numVertices - 1], vertices[0]);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Arrays.hashCode(this.vertices);
        return hash;
    }

    @Override
    public boolean equals(Object obj
    ) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Cycle other = (Cycle) obj;
        for (int k = 0; k < numVertices - 1; k++) {
            boolean equals = true;
            for (int i = 0; i < numVertices; i++) {
                if (this.vertices[i] != other.vertices[(i + k) % numVertices]) {
                    equals = false;
                    break;
                }
            }
            if (equals) {
                return true;
            }
        }
        if (!isDirected()) {
            for (int k = 0; k < numVertices - 1; k++) {
                boolean equals = true;
                for (int i = 0; i < numVertices; i++) {
                    if (this.vertices[i] != other.vertices[(numVertices - i + k) % numVertices]) {
                        equals = false;
                        break;
                    }
                }
                if (equals) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (numVertices == 0) {
            return "[]";
        }
        var sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i <= numVertices; i++) {
            if (i > 0) {
                sb.append(isDirected() ? " -> " : " - ");
            }
            sb.append(vertices[i % numVertices]);
        }
        sb.append("]");
        return sb.toString();
    }

}
