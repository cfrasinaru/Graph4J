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

import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.GraphTests;
import org.graph4j.util.IntArrays;

/**
 * Generates a random simple undirected graph that satisfies Ore's condition:
 * <code>deg(v) + deg(u) &gt;= |V(G)|</code>, for every pair of distinct
 * non-adjacent vertices v and u. These graphs are Hamiltonian.
 *
 * @author Cristian Frăsinaru
 */
public class RandomOreGraphGenerator extends AbstractGraphGenerator {

    public RandomOreGraphGenerator(int numVertices) {
        super(numVertices);
    }

    public RandomOreGraphGenerator(int firstVertex, int lastVertex) {
        super(firstVertex, lastVertex);
    }

    /**
     *
     * @return a graph satisfying Ore's property.
     */
    public Graph createGraph() {
        int n = vertices.length;
        var g = GraphBuilder.vertices(vertices)
                .estimatedAvgDegree(n / 2)
                .buildGraph();
        g.setSafeMode(false);
        int[] temp = IntArrays.shuffle(vertices);
        for (int i = 0; i < n - 1; i++) {
            int v = temp[i];
            for (int j = i + 1; j < n; j++) {
                int u = temp[j];
                if (!g.containsEdge(v, u) && g.degree(v) + g.degree(u) < n) {
                    g.addEdge(v, u);
                }
            }
        }
        assert GraphTests.hasOreProperty(g);
        g.setSafeMode(true);
        return g;
    }
}
