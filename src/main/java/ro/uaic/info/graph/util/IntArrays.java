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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 *
 * @author Cristian Frăsinaru
 */
public class IntArrays {

    /**
     *
     * @param array
     * @return
     */
    public static int[] copyOf(int[] array) {
        return Arrays.copyOf(array, array.length);
    }

    /**
     *
     * @param array
     * @param value
     * @return
     */
    public static boolean contains(int[] array, int value) {
        return contains(array, value, 0);
    }

    /**
     *
     * @param array
     * @param value
     * @param fromPos
     * @return
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
     * @param array
     * @param values
     * @return
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
     * @param array
     * @return
     */
    public static boolean containsDuplicates(int[] array) {
        for (int i = 0, n = array.length; i < n - 1; i++) {
            if (contains(array, array[i], i + 1)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param array
     * @return
     */
    public static Integer findDuplicate(int[] array) {
        for (int i = 0, n = array.length; i < n - 1; i++) {
            if (contains(array, array[i], i + 1)) {
                return array[i];
            }
        }
        return null;
    }

    /**
     *
     * @param array1
     * @param array2
     * @return
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
     * @param array
     * @return
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
     * @param list
     * @return
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
     * @param arrays
     * @return
     */
    public static int[] concat(int[]... arrays) {
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
     * @param array1
     * @param array2
     * @return true, if the two arrays have the same values, regardless of
     * order.
     */
    public static boolean sameValues(int[] array1, int[] array2) {
        if (array1 == array2) {
            return true;
        }
        if (array1.length != array2.length) {
            return false;
        }
        return contains(array1, array2) && contains(array2, array1);
    }

    /**
     *
     * @param array
     * @return a new array with the original elements shuffled
     */
    public static int[] shuffle(int[] array) {
        return shuffle(array, new Random());
    }

    /**
     *
     * @param array
     * @param random
     * @return
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
     * @param array
     * @param fromPos
     * @param toPos
     * @return
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
}
