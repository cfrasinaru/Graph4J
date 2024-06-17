/*
 * Copyright (C) 2024 Cristian Frăsinaru and contributors
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

import java.util.Collection;
import org.graph4j.flow.InvalidFlowException;
import org.graph4j.util.VertexSet;

/**
 * Represents a single commodity transportation (flow) network. In addition to
 * the weight, a network offers the possibility to set the capacity, cost and
 * flow of the edges, using the methods that set the custom edge data of any
 * graph, specifying the following data types: {@code Network.CAPACITY},
 * {@code Network.COST} and {@code NETWORK.FLOW}.
 *
 * @author Cristian Frăsinaru
 * @param <V> the type of vertex labels in this graph.
 * @param <E> the type of edge labels in this graph.
 */
public interface Network<V, E> extends Digraph<V, E> {

    /**
     * 0 (same as Graph.WEIGHT)
     */
    static final int CAPACITY = 1;
    /**
     * 1
     */
    static final int COST = 2;
    /**
     * 2
     */
    static final int FLOW = 3;
    /**
     * 1
     */
    static final double DEFAULT_EDGE_CAPACITY = 0;
    /**
     * 0
     */
    static final double DEFAULT_EDGE_COST = 0;
    /**
     * 0
     */
    static final double DEFAULT_EDGE_FLOW = 0;

    /**
     * Sets the source of the network.
     *
     * @param source the source of the network.
     */
    void setSource(int source);

    /**
     * Returns the source of the network.
     *
     * @return the source of the network.
     */
    int getSource();

    /**
     * Sets the sink of the network.
     *
     * @param sink the sink of the network.
     */
    void setSink(int sink);

    /**
     * Returns the sink of the network.
     *
     * @return the sink of the network.
     */
    int getSink();

    /**
     * Adds a new edge to the network, having the specified capacity. The
     * endpoints of the edge are identified using their vertex numbers. See also
     * {@link #addEdge(int, int)}.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @param capacity the capacity of the edge.
     * @return the position of {@code u} in the adjacency list of {@code v} if
     * {@code (v,u)} edge was added, or {@code -1} if the edge was not added.
     * @throws InvalidVertexException if either of the two vertices is not in
     * the graph.
     */
    @Override
    int addEdge(int v, int u, double capacity);

    /**
     * Adds a new edge to the network, having the specified capacity and cost.
     * The endpoints of the edge are identified using their vertex numbers. See
     * also {@link #addEdge(int, int)}.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @param capacity the capacity of the edge.
     * @param cost the cost of the edge.
     * @return the position of {@code u} in the adjacency list of {@code v} if
     * {@code (v,u)} edge was added, or {@code -1} if the edge was not added.
     * @throws InvalidVertexException if either of the two vertices is not in
     * the graph.
     */
    int addEdge(int v, int u, double capacity, double cost);

    /**
     * Adds a new edge to the network, having the specified label and capacity
     * The endpoints of the edge are identified using their vertex numbers. See
     * also {@link #addEdge(int, int)}.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @param capacity the capacity of the edge.
     * @param label the label of the edge.
     * @return the position of {@code u} in the adjacency list of {@code v} if
     * {@code (v,u)} edge was added, or {@code -1} if the edge was not added.
     * @throws InvalidVertexException if either of the two vertices is not in
     * the graph.
     */
    @Override
    int addLabeledEdge(int v, int u, E label, double capacity);

    /**
     * Adds a new edge to the network, having the specified label, capacity and
     * cost. The endpoints of the edge are identified using their vertex
     * numbers. See also {@link #addEdge(int, int)}.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @param capacity the capacity of the edge.
     * @param cost the cost of the edge.
     * @param label the label of the edge.
     * @return the position of {@code u} in the adjacency list of {@code v} if
     * {@code (v,u)} edge was added, or {@code -1} if the edge was not added.
     * @throws InvalidVertexException if either of the two vertices is not in
     * the graph.
     */
    int addLabeledEdge(int v, int u, E label, double capacity, double cost);

    @Override
    Network<V, E> copy();

    @Override
    Network<V, E> complement();

    @Override
    Network<V, E> subgraph(VertexSet vertexSet);

    @Override
    default Network<V, E> subgraph(int... vertices) {
        return subgraph(new VertexSet(Network.this, vertices));
    }

    @Override
    Network<V, E> subgraph(Collection<Edge> edges);

    /**
     * Checks if the flow values of the network represent a valid flow.
     *
     * @throws InvalidFlowException if the flow is not valid.
     */
    void checkFlow() throws InvalidFlowException;

    /**
     * Checks if the flow values of the network represent a valid flow.
     *
     * @return {@code true} if the flow is valid, {@code false} otherwise.
     */
    default boolean isFlowValid() {
        try {
            checkFlow();
            return true;
        } catch (InvalidFlowException e) {
            return false;
        }
    }
    
    /**
     * Checks if the flow values of the network represent a valid preflow.
     *
     * @throws InvalidFlowException if the preflow is not valid.
     */
    void checkPreflow() throws InvalidFlowException;

    /**
     * Checks if the flow values of the network represent a valid preflow.
     *
     * @return {@code true} if the flow is valid, {@code false} otherwise.
     */
    default boolean isPreflowValid() {
        try {
            checkPreflow();
            return true;
        } catch (InvalidFlowException e) {
            return false;
        }
    }
}
