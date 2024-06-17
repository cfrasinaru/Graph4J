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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Cristian Frăsinaru
 */
public class CountingSort extends SortingAlgorithm {

    @Override
    public void sort(int[] arr) {
        Objects.requireNonNull(arr);
        int max = Arrays.stream(arr).max().orElse(0);

        int[] count = new int[max + 1];
        for (int value : arr) {
            count[value]++;
        }
        int pos = 0;
        for (int value = 0; value <= max; value++) {
            for (int i = 0, k = count[value]; i < k; i++) {
                arr[pos++] = value;
            }
        }
    }

    @Override
    public void sort(List<Integer> list) {
        Objects.requireNonNull(list);
        if (list.size() <= 30) {
            list.sort(null);
            return;
        }
        int max = list.stream().max(Integer::compare).orElse(0);

        int[] count = new int[max + 1];
        for (int value : list) {
            count[value]++;
        }
        list.clear();
        for (int value = 0; value <= max; value++) {
            for (int i = 0, k = count[value]; i < k; i++) {
                list.add(value);
            }
        }
    }

}
