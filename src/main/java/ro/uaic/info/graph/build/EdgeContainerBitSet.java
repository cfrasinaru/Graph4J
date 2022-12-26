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
package ro.uaic.info.graph.build;

import java.util.BitSet;
import ro.uaic.info.graph.Digraph;

/**
 *
 * @author Cristian Frăsinaru
 */
class EdgeContainerBitSet implements EdgeContainer {

    private BitSet bitSet;
    private final int maxVertices;
    private static final int MAX_BITS = (int) Digraph.maxEdges(10_000);

    public EdgeContainerBitSet(int maxVertices) {
        this.maxVertices = maxVertices;
        long nbits = maxVertices * (maxVertices - 1);
        if (nbits > MAX_BITS) {
            throw new IllegalArgumentException("Too many vertices: " + maxVertices);
        }
        this.bitSet = new BitSet((int) nbits);
    }

    @Override
    public EdgeContainerBitSet copy() {
        var copy = new EdgeContainerBitSet(maxVertices);
        copy.bitSet = (BitSet) bitSet.clone();
        return copy;
    }

    private int bitIndex(int v, int u) {
        return v * maxVertices + u;
    }

    @Override
    public void add(int v, int u) {
        bitSet.set(bitIndex(v, u), true);
    }

    @Override
    public void remove(int v, int u) {
        bitSet.set(bitIndex(v, u), false);
    }

    @Override
    public boolean contains(int v, int u) {
        return bitSet.get(bitIndex(v, u));
    }
}
