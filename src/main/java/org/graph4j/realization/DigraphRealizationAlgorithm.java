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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import org.graph4j.Digraph;
import org.graph4j.util.IntPair;

/**
 * The <em>digraph realization</em> problem is to determine whether there exists
 * a directed graph having a specified sequence of in-degrees and out-degrees
 * for each vertex.
 *
 * @see GraphRealizationAlgorithm
 * @author Cristian Frăsinaru
 */
public interface DigraphRealizationAlgorithm {

    /**
     * Creates a directed graph with the specified in-degrees and out-degrees.
     *
     * @return a digraph with the specified degree sequence.
     * @throws IllegalArgumentException if the sequence is not digraphic.
     */
    Digraph getDigraph();

    /**
     * Checks if the degree sequence is digraphic.
     *
     * @return {@code true} if the sequence is digraphic, {@code false}
     * otherwise.
     */
    boolean isDigraphic();

    /**
     * Checks if the in-degree, out-degree sequence is digraphic, using the
     * Fulkerson–Chen–Anstee theorem.
     *
     * @param indegrees the in-degrees of the vertices.
     * @param outdegrees the out-degrees of the vertices.
     * @return {@code true} if the sequence is digraphic, {@code false}
     * otherwise.
     */
    static boolean checkFulkersonChenAnsteeCondition(int[] indegrees, int[] outdegrees) {
        //https://en.wikipedia.org/wiki/Fulkerson%E2%80%93Chen%E2%80%93Anstee_theorem
        Objects.nonNull(indegrees);
        Objects.nonNull(outdegrees);
        if (indegrees.length != outdegrees.length) {
            return false;
        }
        int n = indegrees.length;
        for (int i = 0; i < n; i++) {
            if (indegrees[i] < 0 || indegrees[i] >= n || outdegrees[i] < 0 || outdegrees[i] >= n) {
                return false;
            }
        }
        if (Arrays.stream(indegrees).sum() != Arrays.stream(outdegrees).sum()) {
            return false;
        }

        List<IntPair> pairs = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            pairs.add(new IntPair(indegrees[i], outdegrees[i]));
        }
        //decreasing lexicographic order (wikipedia is not correct)
        //see 2014-beger-A note on the characterization of digraphic sequences
        pairs.sort((p1, p2) -> p1.first() - p2.first() != 0 ? p2.first() - p1.first() : p2.second() - p1.second());
        pairs.add(0, null);

        for (int j = 1; j <= n; j++) {
            final int k = j;
            long left = IntStream.rangeClosed(1, k).map(i -> pairs.get(i).first()).sum();
            long right
                    = IntStream.rangeClosed(1, k).map(i -> Math.min(pairs.get(i).second(), k - 1)).sum()
                    + IntStream.rangeClosed(k + 1, n).map(i -> Math.min(pairs.get(i).second(), k)).sum();
            if (left > right) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the default implementation of the algorithm.
     *
     * @param indegrees the in-degrees of the vertices.
     * @param outdegrees the out-degrees of the vertices.
     * @return the default implementation of the algorithm.
     */
    static DigraphRealizationAlgorithm getInstance(int[] indegrees, int[] outdegrees) {
        return new KleitmanWangDigraphRealization(indegrees, outdegrees);
    }
}
