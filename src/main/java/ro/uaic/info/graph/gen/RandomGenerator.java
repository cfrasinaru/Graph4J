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
package ro.uaic.info.graph.gen;

import java.util.Random;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.build.GraphBuilder;
import ro.uaic.info.graph.util.CheckArgument;

/**
 * Methods for generating various random graphs.
 *
 * @author Cristian Frăsinaru
 */
public class RandomGenerator {

    /**
     * G(n,p) Erdős–Rényi model, where n is the number of vertices and p is the
     * density (the probability that two vertices are connected).
     *
     * @param n the number of nodes in the generated graph
     * @param p the probabilty of edge occurance in the generated graph
     * @return
     */
    public static Graph createGraphGnp(int n, double p) {
        CheckArgument.numberOfVertices(n);
        CheckArgument.probability(p);
        var g = GraphBuilder.numVertices(n).density(p).buildGraph();
        var rand = new Random();
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (rand.nextDouble() < p) {
                    g.addEdge(i, j);
                }
            }
        }
        return g;
    }

    /**
     * Fisher-Yates algorithm
     *
     * @param n the number of nodes in the generated graph
     * @param m the number of edges in the generated graph
     * @return
     */
    public static Graph createGraphGnm(int n, int m) {
        CheckArgument.numberOfVertices(n);
        CheckArgument.numberOfEdges(m);
        long max = (long) n * (n - 1) / 2;
        if (m > max) {
            throw new IllegalArgumentException(
                    "The number of edges is greater than the maximum possible: " + m);
        }
        if (max >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Cannot use Fisher-Yates, the number of vertices is too large");
        }
        int[] values = new int[(int) max];

        int k = 0;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                values[k++] = i * n + j;
            }
        }
        var rand = new Random();
        var g = GraphBuilder.numVertices(n).numEdges(m).buildGraph();
        for (int e = 0; e < m; e++) {
            int pos = rand.nextInt(values.length - e);
            int v = values[pos] / n;
            int u = values[pos] % n;
            g.addEdge(v, u);
            values[pos] = values[values.length - 1 - e];
        }
        return g;
    }

}
