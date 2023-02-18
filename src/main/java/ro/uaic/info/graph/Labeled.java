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

import ro.uaic.info.graph.model.EdgeSet;
import ro.uaic.info.graph.model.VertexSet;

/**
 *
 * @author Cristian Frăsinaru
 */
interface Labeled<V, E> {

    /**
     *
     * @param v a vertex number
     * @param label
     * @return
     */
    int addVertex(int v, V label);

    /**
     *
     * @param v a vertex number
     * @param label
     * @return
     */
    int addVertex(V label);

    /**
     *
     * @param v a vertex number
     * @param u a vertex number
     * @param label
     * @return
     */
    int addEdge(int v, int u, E label);

    /**
     *
     * @param vLael
     * @param uLabel
     * @param edgeLabel
     * @return
     */
    default int addEdge(V vLael, V uLabel, E edgeLabel) {
        int v = findVertex(vLael);
        int u = findVertex(uLabel);
        return Labeled.this.addEdge(v, u, edgeLabel);
    }

    /**
     *
     * @param v a vertex number
     * @param label
     */
    void setVertexLabel(int v, V label);

    /**
     *
     * @param v a vertex number
     * @return
     */
    V getVertexLabel(int v);

    /**
     *
     * @param v a vertex number
     * @param u a vertex number
     * @param label
     */
    void setEdgeLabel(int v, int u, E label);

    /**
     *
     * @param v a vertex number
     * @param u a vertex number
     * @return
     */
    E getEdgeLabel(int v, int u);

    /**
     *
     * @return {@code true}, if weights have been set on edges.
     */
    boolean isEdgeLabeled();

    /**
     *
     * @return {@code true}, if weights have been set on vertices.
     */
    boolean isVertexLabeled();

    /**
     *
     * @param label a label.
     * @return the number of the (first) vertex which has the specified label,
     * or {@code -1} if no such vertex exists.
     */
    int findVertex(V label);

    /**
     *
     * @param label a label.
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
