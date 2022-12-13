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
package ro.uaic.info.graph.gen;

import java.util.Random;
import java.util.stream.IntStream;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.build.GraphBuilder;
import ro.uaic.info.graph.util.CheckArguments;

/**
 *
 *
 * @author Cristian Frăsinaru
 */
public class GnmRandomGenerator {

    private final int numEdges;
    private final int[] vertices;
    private Random random;
    private int[] edgeValues;

    /**
     *
     * @param numVertices number of vertices
     * @param numEdges number of edges
     */
    public GnmRandomGenerator(int numVertices, int numEdges) {
        this(0, numVertices - 1, numEdges);
    }

    /**
     *
     * @param firstVertex
     * @param lastVertex
     * @param numEdges
     */
    public GnmRandomGenerator(int firstVertex, int lastVertex, int numEdges) {
        CheckArguments.vertexRange(firstVertex, lastVertex);
        CheckArguments.numberOfEdges(numEdges);
        this.vertices = IntStream.rangeClosed(firstVertex, lastVertex).toArray();
        this.numEdges = numEdges;
        int n = vertices.length;
        long max = (long) n * (n - 1) / 2;
        if (numEdges > max) {
            throw new IllegalArgumentException(
                    "The number of edges is greater than the maximum possible: "
                    + numEdges + " > " + max);
        }
        if (max >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "The number of vertices is too large: " + n);
        }
        this.random = new Random();
        this.edgeValues = new int[(int) max];
        int k = 0;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                edgeValues[k++] = i * n + j;
            }
        }
    }

    /**
     * Fisher-Yates algorithm.
     *
     * @return
     */
    public Graph createGraph() {
        var g = GraphBuilder.vertices(vertices).numEdges(numEdges).buildGraph();
        int n = vertices.length;
        for (int e = 0; e < numEdges; e++) {
            int pos = random.nextInt(edgeValues.length - e);
            int v = vertices[edgeValues[pos] / n];
            int u = vertices[edgeValues[pos] % n];
            g.addEdge(v, u);
            edgeValues[pos] = edgeValues[edgeValues.length - 1 - e];
        }
        return g;
    }

}
