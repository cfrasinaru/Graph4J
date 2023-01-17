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
package ro.uaic.info.graph;

import java.util.HashSet;

/**
 *
 * @author Cristian Frăsinaru
 */
@Deprecated
class AdjacencyHashSet implements AdjacencySet {

    private final HashSet<Integer> hashSet;

    public AdjacencyHashSet() {
        hashSet = new HashSet<>();
    }

    public AdjacencyHashSet(HashSet<Integer> hashSet) {
        this.hashSet = new HashSet<>(hashSet);
    }


    @Override
    public AdjacencyHashSet copy() {
        return new AdjacencyHashSet(hashSet);
    }

    @Override
    public void add(int u) {
        hashSet.add(u);
    }

    @Override
    public void remove(int u) {
        hashSet.remove(u);
    }

    @Override
    public boolean contains(int u) {
        return hashSet.contains(u);
    }
}
