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
package org.graph4j.alg.coloring.exact;

import java.util.Arrays;
import org.graph4j.util.IntArrays;

/**
 * The domain of available values for a vertex.
 *
 * @see BacktrackColoring
 * @author Cristian Frăsinaru
 */
class Domain {

    int vertex;
    int[] values;
    int[] positions; //position of a value in the values array
    int size;

    public Domain(Domain parent) {
        this.vertex = parent.vertex;
        this.size = parent.size;
        this.values = Arrays.copyOf(parent.values, parent.values.length);
        this.positions = Arrays.copyOf(parent.positions, parent.positions.length);
    }

    //singleton
    public Domain(int vertex, int value) {
        this.vertex = vertex;
        this.size = 1;
        this.values = new int[]{value};
        positions = new int[value + 1];
        positions[value] = 0;
    }

    public Domain(int vertex, int[] values) {
        this.vertex = vertex;
        this.size = values.length;
        this.values = IntArrays.copyOf(values);
        positions = new int[size];
        for (int i = 0; i < size; i++) {
            positions[values[i]] = i;
        }
    }

    public int vertex() {
        return vertex;
    }

    public int size() {
        return size;
    }

    //removes and returns a value in the domain
    public int poll() {
        int value = values[size - 1];
        removeAtPos(size - 1);
        return value;
    }

    public boolean remove(int value) {
        int pos = indexOf(value);
        if (pos < 0) {
            return false;
        }
        removeAtPos(pos);
        return true;
    }

    public int indexOf(int value) {
        return positions[value];
    }

    public void removeAtPos(int pos) {
        positions[values[pos]] = -1;
        if (pos != size - 1) {
            values[pos] = values[size - 1];
            positions[values[pos]] = pos;
        }
        size--;
    }

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
