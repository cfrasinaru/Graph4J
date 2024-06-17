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
import java.util.List;
import java.util.Random;
import org.graph4j.Edge;
import org.graph4j.Graph;
import org.graph4j.GraphTests;

/**
 * Generates a random forest. A <em>forest</em> is a collection of disjoint
 * trees. A forest can also be defined as an acyclic graph, either connected (in
 * which case it is a tree) or disconnected. A forest with k components and n
 * nodes has n-k edges.
 *
 * @author Cristian Frăsinaru
 */
public class RandomForestGenerator extends AbstractGraphGenerator {

    private final int numTrees;

    /**
     * Creates a generator for a forest with vertices ranging from 0 to
     * {@code numVertices - 1}, containing a random number of disjoint trees.
     *
     * @param numVertices the number of vertices of the generated forest.
     */
    public RandomForestGenerator(int numVertices) {
        super(numVertices);
        this.numTrees = 2 + new Random().nextInt(numVertices - 1);
    }

    /**
     * Creates a generator for a forest with vertices ranging from 0 to
     * {@code numVertices - 1}, containing a specified number of disjoint trees.
     *
     * @param numVertices the number of vertices of the generated forest.
     * @param numTrees the number of disjoint trees.
     */
    public RandomForestGenerator(int numVertices, int numTrees) {
        super(numVertices);
        validateNumTrees(numTrees);
        this.numTrees = numTrees;
    }

    /**
     * Creates a generator for a forest with vertices ranging from firstVertex
     * to lastVertex, containing a specified number of disjoint trees.
     *
     * @param firstVertex the number of the first vertex in the generated
     * forest.
     * @param lastVertex the number of the last vertex in the generated forest.
     * @param numTrees the number of disjoint trees.
     */
    public RandomForestGenerator(int firstVertex, int lastVertex, int numTrees) {
        super(firstVertex, lastVertex);
        validateNumTrees(numTrees);
        this.numTrees = numTrees;
    }

    private void validateNumTrees(int numTrees) {
        if (numTrees < 1 || numTrees > vertices.length) {
            throw new IllegalArgumentException("Invalid number of trees: " + numTrees);
        }
    }

    /**
     * Creates a random forest.
     *
     * @return a random forest.
     */
    public Graph createForest() {
        var g = new RandomTreeGenerator(vertices).createTree();
        var random = new Random();
        g.setSafeMode(false);
        List<Edge> edges = Arrays.asList(g.edges());
        int numEdges = edges.size();
        for (int i = 0; i < numTrees - 1; i++) {
            int j = random.nextInt(numEdges);
            g.removeEdge(edges.get(j));
            edges.set(j, edges.get(numEdges - 1));
            numEdges--;
        }
        g.setSafeMode(true);
        assert GraphTests.isForest(g);
        return g;
    }

}
