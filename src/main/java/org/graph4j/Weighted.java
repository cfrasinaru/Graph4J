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

/**
 *
 * @author Cristian Frăsinaru
 */
interface Weighted {

    /**
     * Adds a new vertex to the graph having the specified number and weight.
     *
     * @param v a vertex number.
     * @param weight the weight to be set for vertex v.
     * @return the index of the added vertex.
     */
    int addWeightedVertex(int v, double weight);

    @Deprecated
    default int addVertex(int v, double weight) {
        return addWeightedVertex(v, weight);
    }

    /**
     * Adds a new vertex to the graph having the number equal to the maximum
     * vertex number plus one and the specified weight.
     *
     * @param weight the weight to be set for the added vertex.
     * @return the number of the added vertex.
     */
    int addWeightedVertex(double weight);

    @Deprecated
    default int addVertex(double weight) {
        return addWeightedVertex(weight);
    }

    /**
     * Adds a new weighted edge to the graph. The endpoints of the edge are
     * identified using their vertex numbers.
     *
     * @param v a vertex number
     * @param u a vertex number
     * @param weight the weigth to be set for the edge vu.
     * @return the position of u in the adjacency list of v.
     */
    int addWeightedEdge(int v, int u, double weight);

    @Deprecated
    default int addEdge(int v, int u, double weight) {
        return addWeightedEdge(v, u, weight);
    }

    /**
     * Sets the weight of a vertex.
     *
     * @param v a vertex number.
     * @param weight the weight to be set for vertex v.
     */
    void setVertexWeight(int v, double weight);

    /**
     * Returns the weight of a vertex. If no weights have been set (the graph is
     * vertex unweighted) it returns {@link Graph#DEFAULT_VERTEX_WEIGHT}, which
     * is 1.
     *
     * @param v a vertex number.
     * @return the weight of the vertex, 1 if the graph is unweighted.
     */
    double getVertexWeight(int v);

    /**
     * Sets the weight of an edge.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @param weight the weigth to be set for the edge vu.
     */
    void setEdgeWeight(int v, int u, double weight);

    /**
     * Returns the weight of an edge. If no weights have been set on edges (the
     * graph is edge unweighted) it returns {@link Graph#DEFAULT_EDGE_WEIGHT},
     * which is 1. If the endpoints v and u do not represent an edge, it returns
     * {@link Double#POSITIVE_INFINITY}.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return the weight of the edge vu.
     */
    double getEdgeWeight(int v, int u);

    /**
     * Returns {@code true} if the graph is edge weighted, that is there is at
     * least one edge that has been assigned a weight.
     *
     * @return {@code true}, if weights have been set on edges.
     */
    boolean isEdgeWeighted();

    /**
     * Returns {@code true} if the graph is vertex weighted, that is there is at
     * least one vertex that has been assigned a weight.
     *
     * @return {@code true}, if weights have been set on vertices.
     */
    boolean isVertexWeighted();
}
