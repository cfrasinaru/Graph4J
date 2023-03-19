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
 * Enables iteration over the edges incident to a target vertex, returning
 * one by one the predecessors of the target along with information regarding
 * the corresponding edge.
 *
 * Used in directed graphs.
 *
 * @author Cristian Frăsinaru
 * @param <E> the type of edge labels.
 */
public interface PredecessorIterator<E> {

    /**
     * Checks if there are more predecessors of the target vertex to iterate
     * through.
     *
     * @return {@code true} if there are more predecessors of the target vertex.
     */
    boolean hasNext();

    /**
     * Returns the next predecessor.
     *
     * @return the next predecessor of the target vertex.
     */
    int next();

    /**
     * Checks if the current predecessor is not the first one in the predecessor
     * list.
     *
     * @return {@code true} if the current predecessor is not the first one in
     * the predecessor list.
     */
    boolean hasPrevious();

    /**
     * Returns the previous predecessor.
     *
     * @return the previous predecessor of the target vertex.
     */
    int previous();

    /**
     * Returns the position of the target in the adjacency list of the current
     * (source) vertex.
     *
     * @return the position of the target in the adjacency list of the current
     * (source) vertex.
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
     * @return the weight of the edge from the current vertex to the target
     * vertex.
     */
    double getEdgeWeight();

    /**
     * Sets the label of the current edge.
     *
     * @param weight the weight to be set for the edge connecting the current
     * vertex to the target vertex
     */
    void setEdgeWeight(double weight);

    /**
     * Sets the label of the current edge.
     *
     * @return the label of the edge from the current vertex to the target
     * vertex.
     */
    E getEdgeLabel();

    /**
     * Sets the label of the current edge.
     *
     * @param label the label to be set for the edge connecting the current
     * vertex to the target vertex.
     */
    void setEdgeLabel(E label);

    /**
     * Removes the current edge, from the current to the target vertex. The
     * current edge becomes the previously one.
     */
    void removeEdge();
}
