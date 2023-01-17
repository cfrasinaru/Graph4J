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

import java.util.Arrays;
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.Graph;

/**
 * A <i>walk</i> is a sequence of graph vertices such that any two consecutive
 * vertices form an edge of the graph. Vertices can repeat. Edges can repeat.
 *
 * In order to ensure these properties are respected, call {@code validate}.
 *
 * A walk is closed if the last vertex equals the first one.
 *
 *
 *
 * The length of a walk is its number of edges.
 *
 * @see Trail
 * @see Circuit
 * @see Path
 * @see Cycle
 * @author Cristian Frăsinaru
 */
public class Walk extends VertexList {

    protected final boolean directed;
    protected int numEdges;

    public Walk(Graph graph) {
        super(graph);
        this.directed = graph.isDirected();
    }

    /**
     *
     * @param graph the graph this walk belongs to
     * @param vertices the vertices of the walk
     */
    public Walk(Graph graph, int... vertices) {
        super(graph, vertices);
        this.numEdges = numVertices - 1;
        this.directed = graph.isDirected();
    }

    //type of vertex collection: walk, trail, path, cycle, etc.
    protected String type() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    protected void checkEdge(Edge e) {
        if (!graph.containsEdge(e)) {
            throw new IllegalArgumentException(
                    "Vertices do not form a " + type()
                    + ", there is no edge " + e);
        }
    }

    protected final void checkEdges() {
        for (int i = 0; i < numVertices - 1; i++) {
            checkEdge(new Edge(vertices[i], vertices[i + 1]));

        }
    }

    /**
     * @return {@code false} if the vertices do not represent the intendended
     * structure.
     */
    public boolean isValid() {
        try {
            checkEdges();
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Adds the vertex at the end of the walk, trail or path.
     *
     * @param v a vertex number
     */
    @Override
    public boolean add(int v) {
        return super.add(v);
    }

    /**
     *
     * @return true, if it belongs to a directed graph
     */
    public boolean isDirected() {
        return directed;
    }

    /**
     *
     * @return true, if the first vertex equals the last one
     */
    public boolean isClosed() {
        return vertices[0] == vertices[numVertices - 1];
    }

    /**
     * The length of the walk, trail, path or cycle (number of edges)
     *
     * @return the number of edges
     */
    public int length() {
        return numVertices - 1;
    }

    /**
     *
     * @return the sum of the edge weights
     */
    public double computeEdgesWeight() {
        double weight = 0;
        for (int i = 0; i < numVertices - 1; i++) {
            weight += graph.getEdgeWeight(vertices[i], vertices[i + 1]);
        }
        return weight;
    }

    /**
     * Reverses the walk. 1-2-3 becomes 3-2-1. In case of directed graphs we
     * have to check the reversed walk is valid.
     */
    public void reverse() {
        for (int i = 0; i < numVertices / 2; i++) {
            int temp = vertices[i];
            vertices[i] = vertices[numVertices - i - 1];
            vertices[numVertices - i - 1] = temp;
        }
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < numVertices; i++) {
            if (i > 0) {
                sb.append(directed ? " -> " : " - ");
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
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (super.equals(obj)) {
            return true;
        }
        if (!directed) {
            final Walk other = (Walk) obj;
            for (int i = 0; i < numVertices; i++) {
                if (this.vertices[i] != other.vertices[numVertices - i - 1]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
