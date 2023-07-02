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

import java.util.Arrays;

/**
 * Index of vertices.
 *
 * @author Cristian Frăsinaru
 */
class VertexIndexArray implements VertexIndex {

    private int[] index;

    /**
     *
     *
     * @param maxVertexNumber the maximum vertex number that can be stored.
     */
    public VertexIndexArray(int maxVertexNumber) {
        this.index = new int[maxVertexNumber + 1];
        Arrays.fill(index, -1);
    }

    @Override
    public VertexIndexArray copy() {
        var copy = new VertexIndexArray(max());
        copy.index = Arrays.copyOf(index, index.length);
        return copy;
    }

    @Override
    public int max() {
        return index.length - 1;
    }

    @Override
    public void set(int v, int idx) {
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
        if (v < 0 || v >= index.length) {
            return -1;
        }
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
