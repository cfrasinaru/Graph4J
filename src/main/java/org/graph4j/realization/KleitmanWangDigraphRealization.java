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
import org.graph4j.Digraph;
import org.graph4j.GraphBuilder;
import org.graph4j.util.IntArrays;
import org.graph4j.util.VertexHeap;
import org.graph4j.util.VertexStack;

/**
 * This class provides a method to generate a directed graph from a given
 * sequence of in-degrees and out-degrees, using the Kleitman-Wang algorithm.
 *
 * The time complexity is O(n^2 log n), where n is the number of vertices.
 *
 * @author Cristian Frăsinaru
 */
public class KleitmanWangDigraphRealization
        implements DigraphRealizationAlgorithm {

    private final int[] indegrees;
    private final int[] outdegrees;
    private Boolean digraphic;
    private Digraph digraph;

    /**
     * Creates an algorithm for the digraph realization problem.
     *
     * @param indegrees the in-degrees of the vertices.
     * @param outdegrees the out-degrees of the vertices.
     */
    public KleitmanWangDigraphRealization(int[] indegrees, int[] outdegrees) {
        Objects.nonNull(indegrees);
        Objects.nonNull(outdegrees);
        if (indegrees.length != outdegrees.length) {
            throw new IllegalArgumentException("The lengths of the in-degrees and out-degrees arrays are not equal: "
                    + indegrees.length + " != " + outdegrees.length);
        }
        int n = indegrees.length;
        for (int i = 0; i < n; i++) {
            if (indegrees[i] < 0 || indegrees[i] >= n || outdegrees[i] < 0 || outdegrees[i] >= n) {
                throw new IllegalArgumentException(
                        "The degrees should be in the range: [0," + (n - 1) + "].");
            }
        }
        int sumIn = Arrays.stream(indegrees).sum();
        int sumOut = Arrays.stream(outdegrees).sum();
        if (sumIn != sumOut) {
            throw new IllegalArgumentException(
                    "The sum of the in-degrees does not equal the sum of the out-degrees: " + sumIn + " != " + sumOut);
        }
        this.indegrees = indegrees;
        this.outdegrees = outdegrees;
    }

    @Override
    public boolean isDigraphic() {
        if (digraphic != null) {
            return digraphic;
        }
        try {
            getDigraph();
            digraphic = true;
        } catch (IllegalArgumentException e) {
            digraphic = false;
        }
        return digraphic;
    }

    @Override
    public Digraph getDigraph() {
        if (digraph != null) {
            return digraph;
        }
        int n = indegrees.length;
        int[] in = IntArrays.copyOf(indegrees); //don't alter the original
        int[] out = IntArrays.copyOf(outdegrees);
        this.digraph = GraphBuilder.numVertices(n).buildDigraph();
        digraph.setSafeMode(false);
        var heapOut = new VertexHeap(digraph,
                (i, j) -> out[j] != out[i] ? out[j] - out[i] : in[j] - in[i]);
        var heapIn = new VertexHeap(digraph,
                (i, j) -> in[j] != in[i] ? in[j] - in[i] : out[j] - out[i]);
        var stack = new VertexStack(digraph);
        while (!heapOut.isEmpty()) {
            int v = heapOut.poll();
            int d = out[v];
            if (d > heapIn.size()) {
                throwUnfeasible();
            }
            out[v] = 0;
            heapIn.remove(v);
            stack.push(v);
            for (int i = 1; i <= d; i++) {
                int u = heapIn.poll();
                stack.push(u);
                in[u]--;
                if (in[u] < 0) {
                    throwUnfeasible();
                }
                digraph.addEdge(v, u);
            }
            while (!stack.isEmpty()) {
                heapIn.add(stack.pop());
            }
        }
        digraph.setSafeMode(true);
        return digraph;
    }

    private void throwUnfeasible() {
        throw new IllegalArgumentException(
                "The degree sequence is not digraphic.");
    }

}
