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
package org.graph4j.generators;

import java.util.Random;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.util.IntArrays;
import org.graph4j.util.WorkInProgress;

/**
 * Generates a random k-Nearest Neighbor Graph (KNNG).
 *
 * @author Cristian Frăsinaru
 */
@WorkInProgress
public class RandomKNNGenerator extends AbstractGraphGenerator {

    private final int k;

    /**
     *
     * @param numVertices number of vertices.
     * @param k the number of nearest neighbors.
     */
    public RandomKNNGenerator(int numVertices, int k) {
        super(numVertices);
        this.k = k;
    }

    /**
     *
     * @return a random k-nearest neighbor graph.
     */
    public Graph createGraph() {
        int n = vertices.length;
        var g = GraphBuilder.vertices(vertices).estimatedAvgDegree(k).buildGraph();
        
        var random = new Random();
        double[] x = new double[n];
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = random.nextDouble();
            y[i] = random.nextDouble();
        }
        for (int i = 0; i < n; i++) {
            int v = g.vertexAt(i);
            double[] dist = new double[n];
            for (int j = 0; j < n; j++) {
                double dx = x[i] - x[j];
                double dy = y[i] - y[j];
                dist[j] = Math.sqrt(dx * dx + dy * dy);
            }
            dist[i] = Double.POSITIVE_INFINITY;
            int[] copy = IntArrays.sort(vertices, (a, b) -> (int) Math.signum(dist[a] - dist[b]));
            for (int j = 0; j < k; j++) {
                int u = copy[j];
                g.addEdge(v, u, dist[j]);
            }
        }
        return g;
    }

}
