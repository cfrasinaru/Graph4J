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
 * Represents a data structure used for storing positions in the adjacency
 * lists, such that the method {@link Graph#adjListPos(int, int)} is performed
 * in constant time.
 *
 * Normally, for an edge (v,u), determining the position of u in the adjacency
 * list of v has time complexity O(deg(v)).
 *
 * @see AdjacencyBitSet
 * @author Cristian Frăsinaru
 */
interface AdjacencyMap {

    AdjacencyMap copy();

    void add(int u, int pos);

    void remove(int u);

    int position(int u);
}
