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
package org.graph4j.util;

import java.util.NoSuchElementException;

/**
 * An iterator over a collection of primitive integers.
 *
 * @author Cristian Frăsinaru
 */
public interface IntIterator {

    /**
     * Returns {@code true} if the iteration has more elements.
     *
     * @return {@code true} if the iteration has more elements, {@code false}
     * otherwise.
     */
    boolean hasNext();

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @throws NoSuchElementException if the iteration has no more elements.
     */
    int next();

    /**
     * Removes from the underlying collection the last element returned by this
     * iterator (optional operation). This method can be called only once per
     * call to {@link #next}.
     *
     * @throws UnsupportedOperationException if the {@code remove} operation is
     * not supported by this iterator.
     */
    default void remove() {
        throw new UnsupportedOperationException();
    }
}
