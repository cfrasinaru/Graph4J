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

import java.util.NoSuchElementException;

/**
 * An iterator over all the vertices of a graph.
 *
 * @author Cristian Frăsinaru
 * @param <V> the type of vertex labels.
 */
public interface VertexIterator<V> {

    /**
     * Returns {@code true} if the iteration has more vertices. If it returns
     * {@code true}, the {@link #next} method would return a vertex rather than
     * throwing an exception.
     *
     * @return {@code true} if the iteration has more vertices.
     */
    boolean hasNext();

    /**
     * Returns the next vertex in the iteration.
     *
     * @return the next vertex in the iteration.
     * @throws NoSuchElementException if the iteration has no more vertices.
     */
    int next();

    /**
     *
     * @param weight the weight to be set for the current vertex.
     */
    void setWeight(double weight);

    /**
     *
     * @return the weight of the current vertex.
     */
    double getWeight();

    /**
     *
     * @param label the label to be set for the current vertex.
     */
    void setLabel(V label);

    /**
     *
     * @return the label of the current vertex.
     */
    V getLabel();

    /**
     * Removes the current vertex from the graph.
     */
    void remove();
}
