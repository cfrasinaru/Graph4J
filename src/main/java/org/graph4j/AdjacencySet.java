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
 * Represents a data structure used for checking if an edge is present in the
 * graph in constant time.
 *
 * Normally, the {@link Graph#containsEdge(int, int) } method has time
 * complexity O(deg(v)).
 *
 * @see AdjacencyBitSet
 * @author Cristian Frăsinaru
 */
interface AdjacencySet {

    AdjacencySet copy();

    void add(int u);

    void remove(int u);

    boolean contains(int u);
}
