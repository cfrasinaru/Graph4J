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
 * A <i>cycle</i> is a closed path, meaning that the last vertex of the path is
 * connected to the first one.
 *
 * Vertices can not repeat. Edges can not repeat.
 *
 * The length of a cycle is the number of edges in the path plus the one between
 * the endpoints.
 *
 * @author Cristian Frăsinaru
 */
public class Cycle extends Path {

    /**
     *
     * @param graph
     * @param vertices
     */
    public Cycle(Graph graph, int... vertices) {
        super(graph, vertices);
        Edge e = new Edge(vertices[size - 1], vertices[0]);
        if (!graph.containsEdge(e)) {
            throw new IllegalArgumentException(
                    "Vertices do not form a cycle, there is no edge: " + e);
        }
    }

    @Override
    public boolean isClosed() {
        return true;
    }

    @Override
    public int length() {
        return numEdges + 1; //equals to size
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i <= size; i++) {
            if (i > 0) {
                sb.append(directed ? " -> " : " - ");
            }
            sb.append(vertices[i % size]);
        }
        sb.append("]");
        return sb.toString();
    }

}
