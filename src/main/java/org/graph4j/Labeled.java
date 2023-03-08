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
package org.graph4j;

import org.graph4j.util.EdgeSet;
import org.graph4j.util.VertexSet;

/**
 *
 * @author Cristian Frăsinaru
 */
interface Labeled<V, E> {

    /**
     *
     * @param v a vertex number.
     * @param label a vertex label.
     * @return the index of the added vertex.
     */
    int addVertex(int v, V label);

    /**
     *
     * @param label a vertex label.
     * @return the number of the added vertex.
     */
    int addVertex(V label);

    /**
     *
     * @param v a vertex number
     * @param u a vertex number
     * @param label an edge label.
     * @return the position of u in the adjacency list of v.
     */
    int addEdge(int v, int u, E label);

    /**
     *
     * @param vLabel a vertex label.
     * @param uLabel a vertex label.
     * @param edgeLabel an edge label.
     * @return the position of u in the adjacency list of v.
     */
    default int addEdge(V vLabel, V uLabel, E edgeLabel) {
        int v = findVertex(vLabel);
        int u = findVertex(uLabel);
        return Labeled.this.addEdge(v, u, edgeLabel);
    }

    /**
     *
     * @param v a vertex number.
     * @param label a vertex label.
     */
    void setVertexLabel(int v, V label);

    /**
     *
     * @param v a vertex number.
     * @return the label of the specified vertex.
     */
    V getVertexLabel(int v);

    /**
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @param label an edge label.
     */
    void setEdgeLabel(int v, int u, E label);

    /**
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return the label of the edge vu.
     */
    E getEdgeLabel(int v, int u);

    /**
     *
     * @return {@code true}, if labels have been set on edges.
     */
    boolean isEdgeLabeled();

    /**
     *
     * @return {@code true}, if labels have been set on vertices.
     */
    boolean isVertexLabeled();

    /**
     *
     * @param label a vertex label.
     * @return the number of the (first) vertex which has the specified label,
     * or {@code -1} if no such vertex exists.
     */
    int findVertex(V label);

    /**
     *
     * @param label a vertex label.
     * @return all the vertices of the graph which have been assigned the
     * specified label.
     */
    VertexSet findAllVertices(V label);

    /**
     * If there are more edges with the given label, it returns the last one
     * added in the graph.
     *
     * @param label an edge label.
     * @return the edge with the given label.
     */
    Edge findEdge(E label);

    /**
     *
     * @param label an edge label.
     * @return all the edges with the given label.
     */
    EdgeSet findAllEdges(E label);
}
