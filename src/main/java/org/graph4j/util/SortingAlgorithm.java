/*
 * Copyright (C) 2024 Cristian Frăsinaru and contributors
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

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Cristian Frăsinaru
 */
public abstract class SortingAlgorithm {

    /**
     * Sorts an array of integers.
     *
     * @param arr the array to be sorted.
     */
    public abstract void sort(int[] arr);

    /**
     * Sorts a list of integers.
     *
     * @param list the list to be sorted.
     */
    public void sort(List<Integer> list) {
        Objects.requireNonNull(list);
        final int n = list.size();
        // if list is small, use Java's sort
        if (n <= 30) {
            list.sort(null);
            return;
        }
        // transform list to array
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = list.get(i);
        }
        sort(array);
        // transform array to list
        list.clear();
        for (int i = 0; i < n; i++) {
            list.add(array[i]);
        }
    }

}
