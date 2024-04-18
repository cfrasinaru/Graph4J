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
     * Adds a new vertex to the graph having the specified number and label.
     *
     * @param v a vertex number.
     * @param label a vertex label.
     * @return the index of the added vertex.
     */
    int addLabeledVertex(int v, V label);

    @Deprecated
    default int addVertex(int v, V label) {
        return addLabeledVertex(v, label);
    }

    /**
     * Adds a new vertex to the graph having the number equal to the maximum
     * vertex number plus one and the specified label.
     *
     * @param label a vertex label.
     * @return the number of the added vertex.
     */
    int addLabeledVertex(V label);

    @Deprecated
    default int addVertex(V label) {
        return addLabeledVertex(label);
    }

    /**
     * Adds a new labeled edge to the graph. The endpoints of the edge are
     * identified using their vertex numbers.
     *
     * @param v a vertex number
     * @param u a vertex number
     * @param label an edge label.
     * @return the position of u in the adjacency list of v.
     */
    int addLabeledEdge(int v, int u, E label);

    @Deprecated
    default int addEdge(int v, int u, E label) {
        return addLabeledEdge(v, u, label);
    }

    /**
     * Adds a new labeled edge to the graph. The endpoints of the edge are
     * identified using their uniquely identifiable vertex labels.
     *
     * @param vLabel a vertex label.
     * @param uLabel a vertex label.
     * @param edgeLabel an edge label.
     * @return the position of u in the adjacency list of v.
     */
    default int addLabeledEdge(V vLabel, V uLabel, E edgeLabel) {
        int v = findVertex(vLabel);
        int u = findVertex(uLabel);
        return Labeled.this.addLabeledEdge(v, u, edgeLabel);
    }

    @Deprecated
    default int addEdge(V vLabel, V uLabel, E edgeLabel) {
        return addLabeledEdge(vLabel, uLabel, edgeLabel);
    }

    /**
     * Sets the label for the specified vertex.
     *
     * @param v a vertex number.
     * @param label a vertex label.
     */
    void setVertexLabel(int v, V label);

    /**
     * Returns the label of the specified vertex or {@code null} if no label has
     * been set.
     *
     * @param v a vertex number.
     * @return the label of the specified vertex.
     */
    V getVertexLabel(int v);

    /**
     * Sets the label for the specified edge.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @param label an edge label.
     */
    void setEdgeLabel(int v, int u, E label);

    /**
     * Returns the label of the specified edge.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return the label of the edge vu.
     */
    E getEdgeLabel(int v, int u);

    /**
     * Returns {@code true} if the graph is edge labeled, that is there is at
     * least one edge that has been assigned a label.
     *
     * @return {@code true}, if labels have been set on edges.
     */
    boolean isEdgeLabeled();

    /**
     * Returns {@code true} if the graph is vertex labeled, that is there is at
     * least one vertex that has been assigned a label.
     *
     * @return {@code true}, if labels have been set on vertices.
     */
    boolean isVertexLabeled();

    /**
     * Returns the number of the vertex having the specified label. If there are
     * more vertices having the specified label, it returns the last one that
     * has been assigned that label. If no vertex has the specified label, it
     * return {@code -1}.
     *
     * @param label a vertex label.
     * @return the number of the vertex which has the specified label, or
     * {@code -1} if no such vertex exists.
     */
    int findVertex(V label);

    /**
     * Returns a set containing all the numbers of the vertices having the
     * specified label.
     *
     * @param label a vertex label.
     * @return all the vertices of the graph which have been assigned the
     * specified label.
     */
    VertexSet findAllVertices(V label);

    /**
     * Returns the edge having the specified label. If there are more edges with
     * the given label, it returns the last one added in the graph.
     *
     * @param label an edge label.
     * @return the edge with the given label.
     */
    Edge findEdge(E label);

    /**
     * Returns a set containing all the edges having the specified label.
     *
     * @param label an edge label.
     * @return all the edges with the given label.
     */
    EdgeSet findAllEdges(E label);
}
