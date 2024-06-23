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
 * This class provides a method to generate a graph from a given degree
 * sequence, using the Havel-Hakimi algorithm.
 *
 * The time complexity is O(n^2 log n), where n is the number of vertices.
 *
 * @author Cristian Frăsinaru
 */
public class HavelHakimiGraphRealization
        implements GraphRealizationAlgorithm {

    private final int[] degreeSequence;
    private Boolean graphic;
    private Graph graph;

    /**
     * Creates an algorithm for the graph realization problem.
     *
     * @param degreeSequence a degree sequence.
     */
    public HavelHakimiGraphRealization(int[] degreeSequence) {
        Objects.nonNull(degreeSequence);
        int n = degreeSequence.length;
        for (int deg : degreeSequence) {
            if (deg < 0 || deg >= n) {
                throw new IllegalArgumentException(
                        "The degrees should be in the range: [0," + (n - 1) + "].");
            }
        }
        int sum = Arrays.stream(degreeSequence).sum();
        if (sum % 2 != 0) {
            throw new IllegalArgumentException(
                    "The sum of the degrees should be even: " + sum);
        }
        this.degreeSequence = degreeSequence;
    }

    @Override
    public boolean isGraphic() {
        if (graphic != null) {
            return graphic;
        }
        try {
            getGraph();
            graphic = true;
        } catch (IllegalArgumentException e) {
            graphic = false;
        }
        return graphic;
    }

    @Override
    public Graph getGraph() {
        if (graph != null) {
            return graph;
        }
        int n = degreeSequence.length;
        int[] degrees = IntArrays.copyOf(degreeSequence);
        graph = GraphBuilder.numVertices(n).buildGraph();
        graph.setSafeMode(false);
        var heap = new VertexHeap(graph, (i, j) -> degrees[j] - degrees[i]);
        var stack = new VertexStack(graph);
        while (heap.size() > 1) {
            int v = heap.poll();
            int d = degrees[v];
            if (d > heap.size()) {
                throwUnfeasible();
            }
            degrees[v] = 0;
            for (int i = 1; i <= d; i++) {
                int u = heap.poll();
                stack.push(u);
                degrees[u]--;
                if (degrees[u] < 0) {
                    throwUnfeasible();
                }
                graph.addEdge(v, u);
            }
            while (!stack.isEmpty()) {
                heap.add(stack.pop());
            }
        }
        graph.setSafeMode(true);
        return graph;
    }

    private void throwUnfeasible() {
        throw new IllegalArgumentException(
                "The degree sequence is not graphic.");
    }

}
