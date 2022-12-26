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

import java.util.Arrays;

/**
 *
 * @author Cristian Frăsinaru
 */
public class IntArrayList {

    protected int[] values;
    protected int size;
    protected final static int DEFAULT_INITIAL_CAPACITY = 10;

    /**
     *
     */
    public IntArrayList() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     *
     * @param initialCapacity
     */
    public IntArrayList(int initialCapacity) {
        this.values = new int[initialCapacity];
        this.size = 0;
    }

    /**
     *
     * @param values
     */
    public IntArrayList(int[] values) {
        this.values = values;
        this.size = values.length;
    }

    /**
     *
     * @return
     */
    public int size() {
        return size;
    }

    /**
     *
     * @return
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     *
     * @param value
     */
    public void add(int value) {
        if (values.length == size) {
            grow();
        }
        values[size++] = value;
    }

    /**
     *
     * @return
     */
    public int[] values() {
        if (values.length > size) {
            values = Arrays.copyOf(values, size);
        }
        return values;
    }

    /**
     *
     * @param value
     * @return
     */
    public boolean contains(int value) {
        for (int i = 0; i < size; i++) {
            if (values[i] == value) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param pos
     * @return
     */
    public int get(int pos) {
        return values[pos];
    }

    /**
     *
     * @param pos
     * @param value
     */
    public void set(int pos, int value) {
        values[pos] = value;
    }

    /**
     *
     */
    public void reverse() {
        for (int i = 0; i < size / 2; i++) {
            int temp = values[i];
            values[i] = values[size - i - 1];
            values[size - i - 1] = temp;
        }
    }

    private void grow() {
        int oldLen = values.length;
        int newLen = oldLen + (oldLen >> 1);
        values = Arrays.copyOf(values, newLen);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(values[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Arrays.hashCode(this.values);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IntArrayList other = (IntArrayList) obj;
        if (!Arrays.equals(this.values, other.values)) {
            return false;
        }
        return true;
    }

}
