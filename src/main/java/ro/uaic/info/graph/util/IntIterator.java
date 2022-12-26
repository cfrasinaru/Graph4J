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
package ro.uaic.info.graph.util;

/**
 * Iterator for a collection of primitive integers.
 *
 * @author Cristian Frăsinaru
 */
public interface IntIterator {

    /**
     * Returns {@code true} if there are more values to iterate through.
     *
     * @return {@code true} if there are more values to iterate through
     */
    boolean hasNext();

    /**
     * Returns the next value. If there are no more values it throws an
     * exception.
     *
     * @return the next value
     */
    int next();

    /**
     * Removes the current value.
     */
    void remove();
}
