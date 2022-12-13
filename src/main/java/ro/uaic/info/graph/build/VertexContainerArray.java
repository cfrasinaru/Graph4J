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

import java.util.Arrays;

/**
 *
 * @author Cristian Frăsinaru
 */
class VertexContainerArray implements VertexContainer {

    private final int maxVertexNumber;
    private int[] index;

    /**
     * The maximum vertex number that can be stored in the container.
     *
     * @param maxVertexNumber
     */
    public VertexContainerArray(int maxVertexNumber) {
        this.maxVertexNumber = maxVertexNumber;
        this.index = new int[maxVertexNumber + 1];
        Arrays.fill(index, -1);
    }

    @Override
    public VertexContainerArray copy() {
        var copy = new VertexContainerArray(maxVertexNumber);
        copy.index = Arrays.copyOf(index, index.length);
        return copy;
    }

    @Override    
    public int max() {
        return index.length;
    }

    @Override
    public void add(int v, int idx) {
        if (v >= index.length) {
            grow(v);
        }
        index[v] = idx;
    }

    @Override
    public void remove(int v) {
        index[v] = -1;
    }

    @Override
    public int indexOf(int v) {
        return index[v];
    }

    @Override
    public void shiftLeft(int v) {
        index[v]--;
    }

    @Override
    public void grow(int v) {
        int oldLen = index.length;
        int newLen = v + (v >> 1);
        index = Arrays.copyOf(index, newLen);
        Arrays.fill(index, oldLen, newLen, -1);
    }

    @Override
    public String toString() {
        return Arrays.toString(index);
    }

    
}
