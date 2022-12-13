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
 * Erdős–Rényi model.
 *
 * @author Cristian Frăsinaru
 */
public class GnpRandomGenerator {

    private final int[] vertices;
    private final double edgeProbability;
    private final Random random;

    /**
     *
     * @param numVertices number of vertices
     * @param edgeProbability probability that two vertices are connected
     */
    public GnpRandomGenerator(int numVertices, double edgeProbability) {
        this(0, numVertices - 1, edgeProbability);
    }

    /**
     *
     * @param firstVertex first vertex number of the graph
     * @param lastVertex last vertex number of the graph
     * @param edgeProbability probability that two vertices are connected
     */
    public GnpRandomGenerator(int firstVertex, int lastVertex, double edgeProbability) {
        CheckArguments.vertexRange(firstVertex, lastVertex);
        CheckArguments.probability(edgeProbability);
        this.vertices = IntStream.rangeClosed(firstVertex, lastVertex).toArray();
        this.edgeProbability = edgeProbability;
        this.random = new Random();
    }

    /**
     *
     * @return
     */
    public Graph createGraph() {
        var g = GraphBuilder.vertices(vertices).density(edgeProbability).buildGraph();
        int n = vertices.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (random.nextDouble() < edgeProbability) {
                    g.addEdge(vertices[i], vertices[j]);
                }
            }
        }
        return g;
    }

}
