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
 * Enables iteration over the edges incident with a source vertex, returning one
 * by one the neighbors of the source along with information regarding the
 * corresponding edge.
 *
 * In case of undirected graphs, it iterates over all the neighbors.
 *
 * In case of directed graphs, the default behavior is to iterate over the
 * successors of the source vertex, returning the information regarding the
 * edges outgoing from it.
 *
 *
 * @see SuccessorIterator
 * @see PredecessorIterator
 * @author Cristian Frăsinaru
 * @param <E> the type of edge labels.
 */
public interface NeighborIterator<E> {

    /**
     * Checks if there are more neighbors of the source vertex to iterate
     * through.
     *
     * @return {@code true} if there are more neighbors of the source vertex
     */
    boolean hasNext();

    /**
     * Returns the next neighbor.
     *
     * @return the next neighbor of the source vertex
     */
    int next();

    /**
     * Checks if the current neighbor is not the first one in the adjacency
     * list.
     *
     * @return {@code true} if the current neighbor is not the first one in the
     * adjacency list
     */
    boolean hasPrevious();

    /**
     * Returns the previous neighbor.
     *
     * @return the previous neighbor of the source vertex
     */
    int previous();

    /**
     * Returns the position of the current vertex in the adjacency list of the
     * source.
     *
     * @return the position of the current vertex in the adjacency list of the
     * source.
     */
    int adjListPos();

    /**
     *
     * @return the current edge;
     */
    Edge edge();

    /**
     * Returns the weight of the current edge.
     *
     * @return the weight of the edge from the source vertex to the current
     * vertex.
     */
    double getEdgeWeight();

    /**
     * Sets the label of the current edge.
     *
     * @param weight the weight to be set for the edge connecting the source
     * vertex to the current vertex
     */
    void setEdgeWeight(double weight);

    void setEdgeData(int dataType, double value);

    void incEdgeData(int dataType, double amount);

    double getEdgeData(int dataType);

    double getEdgeData(int dataType, double defaultValue);

    /**
     * Sets the label of the current edge.
     *
     * @return the label of the edge from the source vertex to the current
     * vertex vertex
     */
    E getEdgeLabel();

    /**
     * Sets the label of the current edge.
     *
     * @param label the label to be set for the edge connecting the source
     * vertex to the current vertex
     */
    void setEdgeLabel(E label);

    /**
     * Removes the current edge, from the source to the current vertex. The
     * current edge becomes the previously one.
     */
    void removeEdge();

    default boolean isSuccessor() {
        return true;
    }

    default boolean isPredecessor() {
        return false;
    }
}
