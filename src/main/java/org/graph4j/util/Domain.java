/*
 * Copyright (C) 2023 Cristian Frăsinaru and contributors
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
package org.graph4j.util;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * A domain of available non-negative values for a vertex.
 *
 * The values of the domain are indexed, being represented internally using an
 * array.
 *
 * @author Cristian Frăsinaru
 */
public class Domain {

    int vertex;
    int[] values;
    int[] positions; //position of a value in the values array
    int size;

    /**
     * Used for inheriting in a lazy fashion another domain's values.
     *
     * @param parent the parent domain.
     */
    public Domain(Domain parent) {
        this.vertex = parent.vertex;
        this.size = parent.size;
        this.values = Arrays.copyOf(parent.values, parent.values.length);
        this.positions = Arrays.copyOf(parent.positions, parent.positions.length);
    }

    /**
     * Used for creating single-value domains.
     *
     * @param vertex a vertex number.
     * @param value the value assigned for the vertex.
     */
    public Domain(int vertex, int value) {
        this.vertex = vertex;
        this.size = 1;
        this.values = new int[]{value};
        positions = new int[value + 1];
        Arrays.fill(positions, -1);
        positions[value] = 0;
    }

    /**
     *
     * @param vertex a vertex number.
     * @param values an array of available values for the vertex.
     */
    public Domain(int vertex, int[] values) {
        assert values != null;
        this.vertex = vertex;
        this.size = values.length;
        this.values = IntArrays.copyOf(values);
        //
        int max = IntStream.of(values).max().orElse(-1);
        positions = new int[max + 1];
        Arrays.fill(positions, -1);
        for (int i = 0; i < size; i++) {
            positions[values[i]] = i;
        }
    }

    /**
     *
     * @return the vertex number.
     */
    public int vertex() {
        return vertex;
    }

    /**
     *
     * @return the number of values in the domain.
     */
    public int size() {
        return size;
    }

    /**
     *
     * @return the values in the domain.
     */
    public int[] values() {
        if (values.length > size) {
            values = Arrays.copyOf(values, size);
        }
        return values;
    }

    /**
     *
     * @param pos a position in the domain.
     * @return the value at the specified position.
     */
    public int valueAt(int pos) {
        return values[pos];
    }

    /**
     *
     * @return removes and returns a value in the domain.
     */
    public int poll() {
        int value = values[size - 1];
        removeAtPos(size - 1);
        return value;
    }

    /**
     *
     * @param value the value to be removed from the domain.
     * @return {@code true} if the domain has changed as a result of this
     * operation.
     */
    public boolean remove(int value) {
        int pos = indexOf(value);
        if (pos < 0) {
            return false;
        }
        removeAtPos(pos);
        return true;
    }

    /**
     *
     * @param value a value;
     * @return the position (index) of the value in the domain.
     */
    public int indexOf(int value) {
        if (value < 0 || value >= positions.length) {
            return -1;
        }
        return positions[value];
    }

    /**
     *
     * @param value a value.
     * @return {@code true} if the value belongs to the domain.
     */
    public boolean contains(int value) {
        return indexOf(value) >= 0;
    }

    /**
     * Removes the value at the specified position.
     *
     * @param pos a position.
     */
    public void removeAtPos(int pos) {
        positions[values[pos]] = -1;
        if (pos != size - 1) {
            values[pos] = values[size - 1];
            positions[values[pos]] = pos;
        }
        size--;
    }

    /**
     * Swaps the values at the specified positions.
     *
     * @param i a position in the domain.
     * @param j a position in the domain.
     */
    public void swapPos(int i, int j) {
        int aux = values[i];
        values[i] = values[j];
        values[j] = aux;
        positions[values[i]] = i;
        positions[values[j]] = j;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(values[i]);
        }
        sb.append("}");
        return "dom(" + vertex + ")=" + sb;
    }
}
