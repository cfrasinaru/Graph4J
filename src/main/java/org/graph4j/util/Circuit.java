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
 * A <i>circuit</i> is a trail whose last vertex is connected to the first one.
 *
 * Vertices can repeat. Edges can not repeat.
 *
 * The length of a circuit is the number of edges in the trail plus the one
 * between the endpoints.
 *
 * @see Walk
 * @see Trail
 * @see Path
 * @see Cycle
 * @author Cristian Frăsinaru
 */
public class Circuit extends Trail {

    public Circuit(Graph graph) {
        super(graph);
    }

    public Circuit(Graph graph, int initialCapacity) {
        super(graph, initialCapacity);
    }

    public Circuit(Graph graph, int[] vertices) {
        super(graph, vertices);
    }

    @Override
    public boolean isValid() {
        if (!super.isValid()) {
            return false;
        }
        Edge e = new Edge(vertices[numVertices - 1], vertices[0]);
        if (!graph.containsEdge(e)) {
            System.err.println("Vertices do not form a circuit, there is no edge: " + e);
            return false;
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

    /**
     * Joins this circuit with another which has a common vertex.
     *
     * @param other another circuit of the graph, having a common vertex with
     * this
     * @return a circuit join this circuit and the other
     */
    public Circuit join(Circuit other) {
        if (this.isEmpty()) {
            return new Circuit(graph, other.vertices());
        }
        if (other.isEmpty()) {
            return new Circuit(graph, this.vertices());
        }
        //join this=C1 and other=C2 
        //pick any vertex v from the first circuit
        //start from v and walk on the first circuit
        //until you reach a common vertex u (may be v)
        //walk on the second circuit all the way until you return in u
        //continue from u the first circuit back to v
        Circuit joined = new Circuit(graph);
        int i = 0;
        int j = -1;
        while (i < this.numVertices) {
            j = other.indexOf(vertices[i]);
            if (j >= 0) {
                break;
            }
            joined.add(vertices[i++]);
        }
        if (j < 0) {
            throw new IllegalArgumentException("Cannot join the two circuits");
        }
        for (int k = j; k < j + other.numVertices; k++) {
            joined.add(other.vertices[k % other.numVertices]);
        }
        while (i < this.numVertices) {
            joined.add(this.vertices[i++]);
        }
        return joined;
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
        final Circuit other = (Circuit) obj;
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
