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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator over all the edges of a graph.
 *
 * In order to iterate over the edges incident with a specific vertex use
 * {@link Graph#neighborIterator(int)}.
 *
 * @author Cristian Frăsinaru
 * @param <E> the type of edge labels.
 */
public interface EdgeIterator<E> extends Iterator<Edge<E>> {

    /**
     * Returns {@code true} if the iteration has more edges. If it returns
     * {@code true}, the {@link #next} method would return an edge rather than
     * throwing an exception.
     *
     * @return {@code true} if the iteration has more edges.
     */
    @Override
    boolean hasNext();

    /**
     * Returns the next edge in the iteration.
     *
     * @return the next edge in the iteration.
     * @throws NoSuchElementException if the iteration has no more edges.
     */
    @Override
    Edge<E> next();

    /**
     *
     * @param weight the weight to be set for the current edge.
     */
    void setWeight(double weight);

    /**
     *
     * @return the weight of the current edge.
     */
    double getWeight();

    void setData(int dataType, double value);
    
    void incData(int dataType, double amount);

    double getData(int dataType);

    double getData(int dataType, double defaultValue);

    /**
     *
     * @param label the label to be set for the current edge.
     */
    void setLabel(E label);

    /**
     *
     * @return the label of the current edge.
     */
    E getLabel();

    /**
     * Removes the current edge from the graph.
     */
    @Override
    void remove();
}
