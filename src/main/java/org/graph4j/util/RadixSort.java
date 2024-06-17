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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

/**
 * Radix sort is a non-comparative integer sorting algorithm that sorts data
 * with integer keys by grouping keys by the individual digits which share the
 * same significant position and value.
 *
 *
 * @author Ignat Gabriel-Andrei
 */
public class RadixSort extends SortingAlgorithm {

    @Override
    public void sort(int[] arr) {
        Objects.requireNonNull(arr);
        // Find the maximum number to know number of digits
        int max = Arrays.stream(arr).max().orElse(0);

        for (int digit = 1; max / digit > 0; digit *= 10) {
            // Initialize 10 buckets
            List<Queue<Integer>> buckets = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                buckets.add(new LinkedList<>());
            }
            // Place each number in the corresponding bucket
            for (int num : arr) {
                int bucketIndex = (num / digit) % 10;
                buckets.get(bucketIndex).add(num);
            }
            // Reconstruct the array
            int index = 0;
            for (Queue<Integer> bucket : buckets) {
                while (!bucket.isEmpty()) {
                    arr[index++] = bucket.poll();
                }
            }
        }
    }

}
