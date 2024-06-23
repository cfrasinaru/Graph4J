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
 * The <em>bipartite graph realization</em> problem is to determine whether
 * there exists a bipartite graph having a specified sequence of degrees for
 * vertices in each partition set.
 *
 * @see GraphRealizationAlgorithm
 * @author Cristian Frăsinaru
 */
public interface BipartiteRealizationAlgorithm {

    /**
     * Creates a bipartite graph with the specified degree sequence.
     *
     * @return a bipartite graph with the specified degree sequence.
     * @throws IllegalArgumentException if the sequence is not valid
     * (bigraphic).
     */
    Graph getGraph();

    /**
     * Checks if the degree sequence is valid (bigraphic).
     *
     * @return {@code true} if the sequence is bigraphic, {@code false}
     * otherwise.
     */
    boolean isBigraphic();

    /**
     * Checks if the degree sequence is valid, using the Gale–Ryser theorem.
     *
     * @param leftDegrees the degrees of the vertices on the left side.
     * @param rightDegrees the degrees of the vertices on the left side.
     * @return {@code true} if the sequence is digraphic, {@code false}
     * otherwise.
     */
    static boolean checkGaleRyserCondition(int[] leftDegrees, int[] rightDegrees) {
        //https://en.wikipedia.org/wiki/Gale%E2%80%93Ryser_theorem
        Objects.nonNull(leftDegrees);
        Objects.nonNull(rightDegrees);
        int n = leftDegrees.length;
        int m = rightDegrees.length;
        for (int i = 0; i < n; i++) {
            if (leftDegrees[i] < 0 || leftDegrees[i] > m) {
                return false;
            }
        }
        for (int i = 0; i < m; i++) {
            if (rightDegrees[i] < 0 || rightDegrees[i] > n) {
                return false;
            }
        }
        if (Arrays.stream(leftDegrees).sum() != Arrays.stream(rightDegrees).sum()) {
            return false;
        }
        int[] a = IntArrays.sortDesc(leftDegrees);
        int[] b = rightDegrees;
        for (int j = 0; j < n; j++) {
            final int k = j;
            long left = IntStream.range(0, k).map(i -> a[i]).sum();
            long right = IntStream.range(0, m).map(i -> Math.min(b[i], k + 1)).sum();
            if (left > right) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the default implementation of the algorithm.
     *
     * @param leftDegrees the degrees of the vertices on the left side.
     * @param rightDegrees the degrees of the vertices on the right side.
     *
     * @return the default implementation of the algorithm.
     */
    static BipartiteRealizationAlgorithm getInstance(int[] leftDegrees, int[] rightDegrees) {
        return new HavelHakimiBipartiteRealization(leftDegrees, rightDegrees);
    }
}
