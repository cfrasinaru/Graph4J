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
 * A <i>clique</i> is a set of vertices of a graph such that any two of them are
 * adjacent.
 *
 *
 * @author Cristian Frăsinaru
 */
public class Clique extends VertexSet {

    /**
     *
     * @param graph the graph this clique belongs to
     * @param vertices the vertices of the clique
     */
    public Clique(Graph graph, int... vertices) {
        super(graph, vertices);
        checkEdges(vertices);
    }

    protected void checkEdge(int v, int u) {
        if (v == u) {
            return;
        }
        if (!graph.containsEdge(v, u) && !graph.containsEdge(u, v)) {
            throw new IllegalArgumentException(
                    "Vertices do not form a clique, "
                    + "there is no edge connecting " + v + " and " + u);
        }
    }

    protected final void checkEdges(int[] vertices) {
        for (int i = 0; i < numVertices - 1; i++) {
            for (int j = i + 1; j < numVertices; j++) {
                checkEdge(vertices[i], vertices[j]);
            }
        }
    }

    @Override
    public boolean add(int v) {
        for (int i = 0; i < numVertices; i++) {
            checkEdge(v, vertices[i]);
        }
        return super.add(v);
    }

}
