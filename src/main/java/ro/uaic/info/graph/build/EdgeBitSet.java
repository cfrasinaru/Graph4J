/*
 * Copyright (C) 2022 Faculty of Computer Science Iasi, Romania
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
package ro.uaic.info.graph.build;

import java.util.BitSet;

/**
 *
 * @author Cristian Frăsinaru
 */
class EdgeBitSet {

    private final BitSet mem;
    private final int maxVertices;

    public EdgeBitSet(int maxVertices) {
        this.maxVertices = maxVertices;
        this.mem = new BitSet(maxVertices * maxVertices - 1);
    }

    private int bitIndex(int v, int u) {
        return v * maxVertices + u;
    }
    public void add(int v, int u) {
        mem.set(bitIndex(v, u), true);
    }

    public void remove(int v, int u) {
        mem.set(bitIndex(v, u), false);
    }

    public boolean contains(int v, int u) {
        return mem.get(bitIndex(v, u));
    }
}
