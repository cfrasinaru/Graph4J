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
package org.graph4j.generate;

import java.util.Random;
import java.util.stream.IntStream;
import org.graph4j.Graph;
import org.graph4j.util.CheckArguments;

/**
 *
 * @author Cristian Frăsinaru
 */
public abstract class AbstractGraphGenerator {

    protected int[] vertices;

    protected AbstractGraphGenerator() {
        vertices = null;
    }

    /**
     *
     * @param numVertices the number of vertices in the geenrated graph.
     */
    public AbstractGraphGenerator(int numVertices) {
        CheckArguments.numberOfVertices(numVertices);
        this.vertices = IntStream.range(0, numVertices).toArray();
    }

    /**
     *
     * @param firstVertex the number of the first vertex in the geenrated graph.
     * @param lastVertex the number of the last vertex in the geenrated graph.
     */
    public AbstractGraphGenerator(int firstVertex, int lastVertex) {
        CheckArguments.vertexRange(firstVertex, lastVertex);
        this.vertices = IntStream.rangeClosed(firstVertex, lastVertex).toArray();
    }

    protected void addRandomEdges(Graph g, double edgeProbability) {
        int n = g.numVertices();
        boolean directed = g.isDirected();
        boolean allowsSelfLoops = g.isAllowingSelfLoops();      
        var random = new Random();
        int n1 = directed ? n : n - 1;
        for (int i = 0; i < n1; i++) {
            int v = vertices[i];
            int from = directed ? 0 : i + 1;
            for (int j = from; j < n; j++) {
                if (!allowsSelfLoops && i == j) {
                    continue;
                }
                int u = vertices[j];
                if (random.nextDouble() < edgeProbability) {
                    g.addEdge(v, u);
                }
            }
        }
    }

}
