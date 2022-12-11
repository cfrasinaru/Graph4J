/*
 * Copyright (C) 2022 Faculty of Computer Science Iasi, Romania
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
import java.util.List;
import java.util.stream.Stream;
import ro.uaic.info.graph.Graph;

/**
 *
 * @author Cristian Frăsinaru
 */
public class Tools {

    /**
     *
     * @param array
     * @param value
     * @return
     */
    public static boolean arrayContains(int[] array, int value) {
        for (int a : array) {
            if (a == value) {
                return true;
            }
        }
        return false;
    }

    public static boolean arrayIntersects(int[] array1, int[] array2) {
        for (int a : array1) {
            if (arrayContains(array2, a)) {
                return true;
            }
        }
        return false;
    }

    public static List<Integer> arrayAsList(int[] array) {
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
    public static int[] listAsArray(List<Integer> list) {
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
    public static int[] concatArrays(int[]... arrays) {
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
}
