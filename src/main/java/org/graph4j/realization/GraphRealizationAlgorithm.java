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
package org.graph4j.realization;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import org.graph4j.Graph;
import org.graph4j.util.IntArrays;

/**
 * The <em>graph realization</em> problem is to determine whether a given degree
 * sequence can be represented by a simple, undirected graph. A <em>degree
 * sequence</em> is a list of non-negative integers representing the degrees of
 * the vertices in a graph. Such a sequence is called "graphic".
 *
 * @author Cristian Frăsinaru
 */
public interface GraphRealizationAlgorithm {

    /**
     * Creates a graph with the specified degree sequence. The vertices of the
     * graph are numbered from 0 to n - 1, where n is the length of the
     * sequence.
     *
     * @return a graph with the specified degree sequence.
     * @throws IllegalArgumentException if the sequence is not graphic.
     */
    Graph getGraph();

    /**
     * Checks if the degree sequence is graphic.
     *
     * @return {@code true} if the sequence is graphic, {@code false} otherwise.
     */
    boolean isGraphic();

    /**
     * An alternative method to check if a degree sequence is graphic, using the
     * Erdos-Gallai theorem.
     *
     * @param degreeSequence a degree sequence.
     * @return {@code true} if the sequence is graphic, {@code false} otherwise.
     */
    static boolean checkErdosGallaiCondition(int[] degreeSequence) {
        //https://en.wikipedia.org/wiki/Erd%C5%91s%E2%80%93Gallai_theorem
        Objects.nonNull(degreeSequence);
        int n = degreeSequence.length;
        for (int i = 0; i < n; i++) {
            if (degreeSequence[i] < 0 || degreeSequence[i] >= n) {
                return false;
            }
        }
        int sum = Arrays.stream(degreeSequence).sum();
        if (sum % 2 != 0) {
            return false;
        }
        int[] d = IntArrays.sortDesc(degreeSequence);
        for (int j = 0; j < n; j++) {
            final int k = j;
            long left = IntStream.rangeClosed(0, k).map(i -> d[i]).sum();
            long right = (long) k * (k + 1) + IntStream.range(k + 1, n).map(i -> Math.min(d[i], k + 1)).sum();
            if (left > right) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the default implementation of the algorithm.
     *
     * @param degreeSequence a degree sequence.
     * @return the default implementation of the algorithm.
     */
    static GraphRealizationAlgorithm getInstance(int[] degreeSequence) {
        return new HavelHakimiGraphRealization(degreeSequence);
    }
}
