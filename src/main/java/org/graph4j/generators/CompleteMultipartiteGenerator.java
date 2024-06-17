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

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.util.StableSet;
import org.graph4j.util.Validator;

/**
 *
 * Generator for complete multipartite graphs.
 *
 * A <em>complete k-partite</em> graph is a graph whose vertices can be
 * decomposed into k disjoint stable sets (no two vertices within the same set
 * are adjacent) and every pair of graph vertices in distinct stable sets are
 * adjacent.
 *
 * @author Cristian Frăsinaru
 */
public class CompleteMultipartiteGenerator extends AbstractGraphGenerator {

    private final int[] numVertices;
    private StableSet[] stableSets;

    /**
     * Creates a generator for a complete multipartite graph, where the number
     * of vertices of each stable set is specified.
     *
     * @param numVertices the number of vertices in each stable set.
     */
    public CompleteMultipartiteGenerator(int... numVertices) {
        for (int i = 0; i < numVertices.length; i++) {
            Validator.checkNumVertices(numVertices[i]);
        }
        this.numVertices = numVertices;
        int n = IntStream.of(numVertices).sum();
        this.vertices = IntStream.range(0, n).toArray();
    }

    /**
     * Creates a complete multipartite graph.
     *
     * @return a complete multipartite graph
     */
    public Graph create() {
        var g = GraphBuilder.vertices(vertices).buildGraph();
        g.setSafeMode(false);
        int k = numVertices.length;
        stableSets = new StableSet[k];
        var name = new StringBuilder("K");
        int firstVertex = 0, lastVertex;
        for (int i = 0; i < k; i++) {
            int size = numVertices[i];
            lastVertex = firstVertex + size - 1;
            stableSets[i] = new StableSet(g, IntStream.rangeClosed(firstVertex, lastVertex).toArray());
            firstVertex += size;
            if (i > 0) {
                name.append(",");
            }
            name.append(size);
        }
        g.setName(name.toString());
        for (int i = 0; i < k - 1; i++) {
            for (int j = i + 1; j < k; j++) {
                for (int v : stableSets[i].vertices()) {
                    for (int u : stableSets[j].vertices()) {
                        g.addEdge(v, u);
                    }
                }
            }
        }
        g.setSafeMode(true);
        return g;
    }

    /**
     * Returns the stable sets of the multipartite graph.
     *
     * @return the stable sets of the multipartite graph.
     */
    public StableSet[] getStableSets() {
        return stableSets;
    }
}
