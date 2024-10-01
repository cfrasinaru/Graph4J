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
import org.graph4j.util.WorkInProgress;

/**
 * Generates random unit disk graphs. A unit disk graph is a type of geometric
 * graph where each vertex is associated with a disk of a fixed radius. Two
 * vertices are connected by an edge if the Euclidean distance between them is
 * less than or equal to the fixed radius of the disks.
 *
 * @author Cristian Frăsinaru
 */
@WorkInProgress
public class RandomUnitDiskGenerator extends AbstractGraphGenerator {

    private final double radius;

    /**
     * Creates a generator for random unit disks graphs having the specified
     * number of vertices and radius.
     *
     * @param numVertices number of vertices.
     * @param radius the radius of the disks must be in the interval
     * <code>(0,1]</code>.
     */
    public RandomUnitDiskGenerator(int numVertices, double radius) {
        super(numVertices);
        if (radius <= 0 || radius > 1) {
            throw new IllegalArgumentException(
                    "Radius must be in the interval (0,1]: " + radius);
        }
        this.radius = radius;
    }

    /**
     * Creates a random unit disk graph.
     *
     * @return a random k-nearest neighbor graph.
     */
    public Graph createGraph() {
        int n = vertices.length;
        var g = GraphBuilder.vertices(vertices).buildGraph();
        g.setSafeMode(false);
        var random = new Random();
        double[] x = new double[n];
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = random.nextDouble();
            y[i] = random.nextDouble();
        }
        for (int i = 0; i < n - 1; i++) {
            int v = g.vertexAt(i);
            for (int j = i + 1; j < n; j++) {
                double dx = x[i] - x[j];
                double dy = y[i] - y[j];
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist <= radius) {
                    g.addEdge(v, g.vertexAt(j), dist);
                }
            }
        }
        g.setSafeMode(true);
        return g;
    }

}
