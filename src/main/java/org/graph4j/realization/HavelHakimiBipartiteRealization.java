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
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.util.IntArrays;
import org.graph4j.util.VertexHeap;
import org.graph4j.util.VertexStack;

/**
 * This class provides a method to generate a bipartite graph from a given
 * sequence of degrees, using an adaptation of the Havel-Hakimi algorithm.
 *
 * The time complexity is O(n^2 log n), where n is the number of vertices.
 *
 * @see HavelHakimiGraphRealization
 * @author Cristian Frăsinaru
 */
public class HavelHakimiBipartiteRealization
        implements BipartiteRealizationAlgorithm {

    private final int[] leftDegrees;
    private final int[] rightDegrees;
    private Boolean bigraphic;
    private Graph graph;

    /**
     * Creates an algorithm for the bipartite realization problem.
     *
     * @param leftDegrees the degrees of the vertices on the left side.
     * @param rightDegrees the degrees of the vertices on the right side.
     *
     */
    public HavelHakimiBipartiteRealization(int[] leftDegrees, int[] rightDegrees) {
        Objects.nonNull(leftDegrees);
        Objects.nonNull(rightDegrees);
        int n = leftDegrees.length;
        int m = rightDegrees.length;
        for (int i = 0; i < n; i++) {
            if (leftDegrees[i] < 0 || leftDegrees[i] > m) {
                throw new IllegalArgumentException(
                        "The left side degrees should be in the range: [0," + m + "].");
            }
        }
        for (int i = 0; i < m; i++) {
            if (rightDegrees[i] < 0 || rightDegrees[i] > n) {
                throw new IllegalArgumentException(
                        "The right side degrees should be in the range: [0," + n + "].");
            }
        }
        int sumLeft = Arrays.stream(leftDegrees).sum();
        int sumRight = Arrays.stream(rightDegrees).sum();
        if (sumLeft != sumRight) {
            throw new IllegalArgumentException(
                    "The sum of the left side degrees does not equal the sum of the right side degrees: "
                    + sumLeft + " != " + sumRight);
        }
        this.leftDegrees = leftDegrees;
        this.rightDegrees = rightDegrees;
    }

    @Override
    public boolean isBigraphic() {
        if (bigraphic != null) {
            return bigraphic;
        }
        try {
            getGraph();
            bigraphic = true;
        } catch (IllegalArgumentException e) {
            bigraphic = false;
        }
        return bigraphic;
    }

    @Override
    public Graph getGraph() {
        int n = leftDegrees.length;
        int m = rightDegrees.length;        
        int[] left = IntArrays.sortDesc(leftDegrees);
        int[] right = IntArrays.copyOf(rightDegrees);
        this.graph = GraphBuilder.numVertices(n + m).buildGraph();
        graph.setSafeMode(false);
        var heapLeft = new VertexHeap(graph, false, (i, j) -> left[j] - left[i]);
        for (int i = 0; i < n; i++) {
            heapLeft.add(i);
        }
        var heapRight = new VertexHeap(graph, false, (i, j) -> right[j - n] - right[i - n]);
        for (int i = n; i < n + m; i++) {
            heapRight.add(i);
        }
        var stack = new VertexStack(graph);
        while (!heapLeft.isEmpty()) {
            int v = heapLeft.poll();
            int d = left[v];
            if (d > heapRight.size()) {
                throwUnfeasible();
            }
            left[v] = 0;
            for (int i = 1; i <= d; i++) {
                int u = heapRight.poll();
                stack.push(u);
                right[u - n]--;
                if (right[u - n] < 0) {
                    throwUnfeasible();
                }
                graph.addEdge(v, u);
            }
            while (!stack.isEmpty()) {
                heapRight.add(stack.pop());
            }
        }
        graph.setSafeMode(true);
        return graph;
    }

    private void throwUnfeasible() {
        throw new IllegalArgumentException(
                "The degree sequence is not bigraphic.");
    }

}
