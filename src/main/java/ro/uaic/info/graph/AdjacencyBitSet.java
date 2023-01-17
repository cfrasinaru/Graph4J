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

import java.util.BitSet;

/**
 * Does not scale.
 *
 * @author Cristian Frăsinaru
 */
class AdjacencyBitSet implements AdjacencySet {
    
    private final BitSet bitSet;
    
    public AdjacencyBitSet() {
        this.bitSet = new BitSet();
    }
    
    public AdjacencyBitSet(BitSet bitSet) {
        this.bitSet = (BitSet) bitSet.clone();
    }
    
    @Override
    public AdjacencyBitSet copy() {
        return new AdjacencyBitSet(bitSet);
    }
    
    @Override
    public void add(int u) {
        bitSet.set(u, true);
    }
    
    @Override
    public void remove(int u) {
        bitSet.set(u, false);
    }
    
    @Override
    public boolean contains(int u) {
        return bitSet.get(u);
    }
}
