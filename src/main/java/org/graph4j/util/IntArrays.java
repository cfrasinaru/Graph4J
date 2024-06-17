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
package org.graph4j.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Utility class for working with arrays of integers. Most methods assume that
 * the values in the arrays are vertex numbers.
 *
 * @author Cristian Frăsinaru
 */
public class IntArrays {

    /**
     * Returns the minimum value in an array.
     *
     * @param array an array of integers.
     * @return the minimum value in the array.
     */
    public static int min(int[] array) {
        return Arrays.stream(array).min().orElseThrow();
    }

    /**
     * Returns the maximum value in an array.
     *
     * @param array an array of integers.
     * @return the maximum value in the array.
     */
    public static int max(int[] array) {
        return Arrays.stream(array).max().orElseThrow();
    }

    /**
     *
     * @param array an array of integers.
     * @return a copy of the array.
     */
    public static int[] copyOf(int[] array) {
        return Arrays.copyOf(array, array.length);
    }

    /**
     *
     * @param array an array of integers.
     * @param value a value.
     * @return {@code true} if the array contains the value.
     */
    public static boolean contains(int[] array, int value) {
        return contains(array, value, 0);
    }

    /**
     *
     * @param array an array of integers.
     * @param value a value.
     * @param fromPos a position in the array.
     * @return {@code true} if the array contains the value, starting from the
     * given position.
     */
    public static boolean contains(int[] array, int value, int fromPos) {
        for (int i = fromPos, n = array.length; i < n; i++) {
            if (array[i] == value) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param array an array of integers.
     * @param values an array of values.
     * @return {@code true} if the array contains all the values.
     */
    public static boolean contains(int[] array, int... values) {
        for (int value : values) {
            if (!contains(array, value)) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param array an array of integers.
     * @return {@code true} if the array contains duplicate values.
     */
    public static boolean containsDuplicates(int[] array) {
        return findDuplicate(array) != null;
    }

    /**
     *
     * @param array an array of integers.
     * @return a duplicate value, or {@code null} if none exists.
     */
    public static Integer findDuplicate(int[] array) {
        if (array == null || array.length < 2) {
            return null;
        }
        /*
        for (int i = 0, n = array.length; i < n - 1; i++) {
            if (contains(array, array[i], i + 1)) {
                return array[i];
            }
        }*/
        /*
        int max = IntArrays.max(array);
        boolean[] exists = new boolean[max + 1];
        for (int a : array) {
            if (exists[a]) {
                return a;
            }
            exists[a] = true;
        }*/
        Set<Integer> values = new HashSet<>(array.length);
        for (int a : array) {
            if (values.contains(a)) {
                return a;
            }
            values.add(a);
        }

        return null;
    }

    /**
     *
     * @param array1 an array of integers.
     * @param array2 an array of integers.
     * @return {@code true} if the arrays contain common values.
     */
    public static boolean intersects(int[] array1, int[] array2) {
        for (int a : array1) {
            if (contains(array2, a)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param array1 an array of integers.
     * @param array2 an array of integers.
     * @return a new array containing the common values.
     */
    public static int[] intersection(int[] array1, int[] array2) {
        int[] inter = new int[Math.min(array1.length, array2.length)];
        int k = 0;
        for (int a : array1) {
            if (contains(array2, a)) {
                inter[k++] = a;
            }
        }
        return k == inter.length ? inter : Arrays.copyOf(inter, k);
    }

    /**
     *
     * @param array an array of integers.
     * @return a List representation of the array.
     */
    public static List<Integer> asList(int[] array) {
        List<Integer> list = new ArrayList<>(array.length);
        for (int a : array) {
            list.add(a);
        }
        return list;
    }

    /**
     *
     * @param array an array of integers.
     * @return a Set representation of the array.
     */
    public static Set<Integer> asSet(int[] array) {
        Set<Integer> set = new HashSet<>();
        for (int a : array) {
            set.add(a);
        }
        return set;
    }

    /**
     *
     * @param list a list of integers.
     * @return an array representation of the list.
     */
    public static int[] fromList(List<Integer> list) {
        int n = list.size();
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    /**
     *
     * @param set a set of integers.
     * @return an array representation of the set.
     */
    public static int[] fromSet(Set<Integer> set) {
        int n = set.size();
        int[] array = new int[n];
        int i = 0;
        for (int a : set) {
            array[i++] = a;
        }
        return array;
    }

    /**
     *
     * @param arrays an array of array of integers.
     * @return the union of all arrays.
     */
    public static int[] union(int[]... arrays) {
        int n = Stream.of(arrays).mapToInt(a -> a.length).sum();
        int[] result = new int[n];
        int i = 0;
        for (int[] array : arrays) {
            for (int a : array) {
                result[i++] = a;
            }
        }
        return result;
    }

    /**
     *
     * @param array1 an array of integers.
     * @param array2 an array of integers.
     * @return {@code true}, if the two arrays have the same values, regardless
     * of order.
     */
    public static boolean haveSameValues(int[] array1, int[] array2) {
        Objects.requireNonNull(array1);
        Objects.requireNonNull(array2);
        if (array1 == array2) {
            return true;
        }
        if (array1.length != array2.length) {
            return false;
        }
        var clone1 = array1.clone();
        var clone2 = array2.clone();
        Arrays.sort(clone1);
        Arrays.sort(clone2);
        return Arrays.equals(clone1, clone2);
        //return contains(array1, array2) && contains(array2, array1);
    }

    /**
     * Creates a new array with the original elements shuffled.
     *
     * @param array an array of integers.
     * @return a new array with the original elements shuffled.
     */
    public static int[] shuffle(int[] array) {
        return shuffle(array, new Random());
    }

    /**
     * Creates a new array with the original elements shuffled.
     *
     * @param array an array of integers.
     * @param random a generator of random numbers.
     * @return a new array with the original elements shuffled.
     */
    public static int[] shuffle(int[] array, Random random) {
        int[] other = copyOf(array);
        for (int i = 0, n = array.length; i < n; i++) {
            int j = i + random.nextInt(n - i);
            int temp = other[i];
            other[i] = other[j];
            other[j] = temp;
        }
        return other;
    }

    /**
     *
     * @param array an array of integers.
     * @return a new array with the original elements sorted ascending.
     */
    public static int[] sort(int[] array) {
        int[] sorted = Arrays.copyOf(array, array.length);
        Arrays.sort(sorted);
        return sorted;
    }

    /**
     *
     * @param array an array of integers.
     * @return a new array with the original elements sorted descending.
     */
    public static int[] sortDesc(int[] array) {
        return sort(array, (a, b) -> b - a);
    }

    /**
     *
     * @param array an array of integers.
     * @param comparator a comparator.
     * @return a new array with the original elements sorted by the comparator.
     */
    public static int[] sort(int[] array, Comparator<Integer> comparator) {
        return IntStream.of(array).boxed()
                .sorted(comparator)
                .mapToInt(Integer::intValue)
                .toArray();
    }

    /**
     * Checks if an array is sorted in ascending order.
     *
     * @param array an array of integers.
     * @return {@code true} if the array is sorted in ascending order,
     * {@code false} otherwise.
     */
    public static boolean isSortedAscending(int[] array) {
        for (int i = 0, n = array.length; i < n - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if an array is sorted in descending order.
     *
     * @param array an array of integers.
     * @return {@code true} if the array is sorted in descending order,
     * {@code false} otherwise.
     */
    public static boolean isSortedDescending(int[] array) {
        for (int i = 0, n = array.length; i < n - 1; i++) {
            if (array[i] < array[i + 1]) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param array an array of integers.
     * @param fromPos a position in the array.
     * @param toPos a position in the array.
     * @return a string representation of the specified part of the array.
     */
    public static String toString(int[] array, int fromPos, int toPos) {
        var sb = new StringBuilder();
        sb.append("[");
        for (int i = fromPos; i <= toPos; i++) {
            if (i > fromPos) {
                sb.append(", ");
            }
            sb.append(array[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Creates a new array with the elements of the specified array reversed.
     *
     * @param array an array of integers.
     * @return the reversed array.
     */
    public static int[] reverse(int[] array) {
        int n = array.length;
        int[] reversed = new int[n];
        for (int i = 0; i < n; i++) {
            reversed[i] = array[n - i - 1];
        }
        return reversed;
    }

    /**
     * Creates a new array containing the positions (indices) of the elements in
     * the specified array. It assumes that the values in the specified array
     * are all distinct.
     *
     * @param array an array of distinct integers.
     * @return the positions of elements in the array.
     */
    public static int[] positions(int[] array) {
        int max = Arrays.stream(array).max().orElse(0);
        int pos[] = new int[max + 1];
        for (int i = 0, n = array.length; i < n; i++) {
            pos[array[i]] = i;
        }
        return pos;
    }

}
