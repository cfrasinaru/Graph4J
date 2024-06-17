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
     * After the execution of this method, the graph is considered
     * <em>vertex weighted</em>.
     *
     * @param v a vertex number.
     * @param weight the weight to be set for vertex {@code v}.
     * @return the index of the added vertex.
     * @throws InvalidVertexException if {@code v} is negative or it is already
     * in the graph.
     */
    int addWeightedVertex(int v, double weight);

    /**
     * Adds a new vertex to the graph having the number equal to the maximum
     * vertex number plus one, and the specified weight.
     *
     * After the execution of this method, the graph is considered
     * <em>vertex weighted</em>.
     *
     * @param weight the weight to be set for the added vertex.
     * @return the number of the added vertex.
     */
    int addWeightedVertex(double weight);

    /**
     * Adds a new weighted edge to the graph. The endpoints of the edge are
     * specified using their vertex numbers.
     *
     * After a successful execution of this method, the graph is considered
     * <em>edge weighted</em>. See also {@link Graph#addEdge(int, int)}.
     *
     * @param v a vertex number
     * @param u a vertex number
     * @param weight the weight to be set for the edge {@code (v,u)}.
     * @return the position of {@code u} in the adjacency list of {@code v} if
     * {@code (v,u)} edge was added, or {@code -1} if the edge was not added.
     * @throws InvalidVertexException if either of the two vertices is not in
     * the graph.
     */
    int addEdge(int v, int u, double weight);

    /**
     * Sets the weight of a vertex. If at least one vertex has been assigned a
     * weight, the graph is considered <em>vertex weighted</em>.
     *
     * The time complexity of this method is {@code O(1)}.
     *
     * @param v a vertex number.
     * @param weight the weight to be set for vertex v.
     * @throws InvalidVertexException if {@code v} is not in the graph.
     */
    void setVertexWeight(int v, double weight);

    /**
     * Returns the weight of a vertex. If no weights have been set (the graph is
     * vertex unweighted) it returns {@link Graph#DEFAULT_VERTEX_WEIGHT}, which
     * is 1.
     *
     * The time complexity of this method is {@code O(1)}.
     *
     * @param v a vertex number.
     * @return the weight of the vertex, 1 if the graph is unweighted.
     * @throws InvalidVertexException if {@code v} is not in the graph.
     */
    double getVertexWeight(int v);

    /**
     * Sets the weight of an edge.
     *
     * The time complexity of this method is {@code O(degree(v))}. It is more
     * efficient to set the weights of the edges while iterating over them, see
     * {@link Graph#neighborIterator(int)} or {@link Graph#edgeIterator()}.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @param weight the weight to be set for the edge {@code (v,u)}.
     * @throws InvalidEdgeException if the graph does not contain the edge
     * {@code (v,u)}.
     */
    void setEdgeWeight(int v, int u, double weight);

    /**
     * Returns the weight of an edge. If no weights have been set on edges (the
     * graph is edge unweighted) it returns {@link Graph#DEFAULT_EDGE_WEIGHT},
     * which is 1. If the endpoints v and u do not represent an edge, it returns
     * {@link Double#POSITIVE_INFINITY}.
     *
     * The time complexity of this method is {@code O(degree(v))}. It is more
     * efficient to get the weights of the edges while iterating over them, see
     * {@link Graph#neighborIterator(int)} or {@link Graph#edgeIterator()}.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return the weight of the edge {@code (v,u)}.
     * @throws InvalidEdgeException if the graph does not contain the edge
     * {@code (v,u)}.
     */
    double getEdgeWeight(int v, int u);

    /**
     * Returns the weight of an edge. If no weights have been set on edges (the
     * graph is edge unweighted) it returns {@link Graph#DEFAULT_EDGE_WEIGHT},
     * which is 1. If the argument does not represent an edge, it returns
     * {@link Double#POSITIVE_INFINITY}.
     *
     * The time complexity of this method is {@code O(degree(v))}. It is more
     * efficient to get the weights of the edges while iterating over them, see
     * {@link Graph#neighborIterator(int)} or {@link Graph#edgeIterator()}.
     *
     * @param e an edge.
     * @return the weight of the edge {@code e}.
     * @throws InvalidEdgeException if the graph does not contain the edge
     * {@code e}.
     */
    default double getEdgeWeight(Edge e) {
        return getEdgeWeight(e.source(), e.target());
    }

    /**
     * Checks if the graph is has at least one edge that has been assigned a
     * weight (is edge weighted).
     *
     * @return {@code true} if weights have been set on edges, {@code false}
     * otherwise.
     */
    boolean hasEdgeWeights();

    /**
     * Checks if the graph has at least one vertex that has been assigned a
     * weight (is vertex weighted).
     *
     * @return {@code true} if weights have been set on vertices, {@code false}
     * otherwise.
     */
    boolean hasVertexWeights();

    double getEdgeData(int dataType, int v, int u, double defaultValue);

    default double getEdgeData(int dataType, int v, int u) {
        return getEdgeData(dataType, v, u, 0);
    }

    default double getEdgeData(int dataType, Edge e, double defaultValue) {
        return getEdgeData(dataType, e.source(), e.target(), defaultValue);
    }

    default double getEdgeData(int dataType, Edge e) {
        return getEdgeData(dataType, e, 0);
    }

    void setEdgeData(int dataType, int v, int u, double value);

    void incEdgeData(int dataType, int v, int u, double amount);

    //void incEdgeDataAt(int dataType, int vi, int pos, double amount);

    boolean hasEdgeData(int dataType);

    void resetEdgeData(int dataType, double value);
}
