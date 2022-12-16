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

/**
 * A <i>walk</i> is a sequence of graph vertices such that any two consecutive
 * vertices form an edge of the graph. A walk is closed if the last vertex
 * equals the first one.
 *
 * Vertices can repeat. Edges can repeat.
 *
 * The length of a walk is its number of edges.
 *
 * @author Cristian Frăsinaru
 */
public class Walk extends VertexSet {

    protected final boolean directed;
    protected int numEdges;

    /**
     *
     * @param graph
     * @param vertices
     */
    public Walk(Graph graph, int... vertices) {
        super(graph, vertices);
        checkAndCountEdges();
        this.directed = (graph instanceof Digraph);
    }

    protected final void checkAndCountEdges() {
        for (int i = 0; i < size - 1; i++) {
            Edge e = new Edge(vertices[i], vertices[i + 1]);
            if (!graph.containsEdge(e)) {
                throw new IllegalArgumentException(
                        "Vertices do not form a "
                        + this.getClass().getSimpleName().toLowerCase()
                        + ", there is no edge: " + e);
            }
            numEdges++;
        }
    }

    /**
     *
     * @return
     */
    public boolean isDirected() {
        return directed;
    }

    /**
     *
     * @return
     */
    public boolean isClosed() {
        return vertices[0] == vertices[size - 1];
    }

    @Override
    public boolean add(int v) {
        throw new UnsupportedOperationException("Cycles are imutable");
    }

    /**
     * The length of the walk, trail, path or cycle (number of edges)
     *
     * @return the number of edges
     */
    public int length() {
        return numEdges;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(directed ? " -> " : " - ");
            }
            sb.append(vertices[i]);
        }
        sb.append("]");
        return sb.toString();
    }

}
