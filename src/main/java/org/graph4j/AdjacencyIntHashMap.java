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

import org.graph4j.util.IntHashMap;

/**
 *
 * @author Cristian Frăsinaru
 */
class AdjacencyIntHashMap implements AdjacencyMap {

    private final IntHashMap hashMap;

    public AdjacencyIntHashMap() {
        hashMap = new IntHashMap();
    }

    public AdjacencyIntHashMap(IntHashMap other) {
        this.hashMap = new IntHashMap(other);
    }

    @Override
    public AdjacencyIntHashMap copy() {
        return new AdjacencyIntHashMap(hashMap);
    }

    @Override
    public void add(int u, int pos) {
        hashMap.put(u, pos);
    }

    @Override
    public void remove(int u) {
        hashMap.remove(u);
    }

    @Override
    public int position(int u) {
        return hashMap.getOrDefault(u, -1);
    }
}
