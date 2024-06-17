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
import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.GraphTests;
import org.graph4j.util.IntArrays;

/**
 * Generates a random tree or arborescence. A <em>tree</em> is a connected
 * acyclic graph. An <em>arborescence</em> is a directed rooted tree where there
 * is exactly one path from the root to every other vertex.
 *
 * @author Cristian Frăsinaru
 */
public class RandomTreeGenerator extends AbstractGraphGenerator {

    public RandomTreeGenerator(int numVertices) {
        super(numVertices);
    }

    public RandomTreeGenerator(int firstVertex, int lastVertex) {
        super(firstVertex, lastVertex);
    }

    public RandomTreeGenerator(int[] vertices) {
        super(vertices);
    }

    /**
     * Creates a random tree.
     *
     * @return a random tree.
     */
    public Graph createTree() {
        var g = GraphBuilder.vertices(vertices)
                .estimatedNumEdges(vertices.length - 1)
                .buildGraph();
        addEdges(g);
        assert GraphTests.isTree(g);
        return g;
    }

    /**
     * Creates a random arborescence.
     *
     * @return a random arborescence.
     */
    public Digraph createArborescence() {
        var g = GraphBuilder.vertices(vertices)
                .estimatedNumEdges(vertices.length - 1)
                .buildDigraph();
        addEdges(g);
        assert GraphTests.isArborescence(g);
        return (Digraph) g;
    }

    private void addEdges(Graph g) {
        g.setSafeMode(false);
        var random = new Random();
        int[] shuffled = IntArrays.shuffle(vertices, random);
        for (int i = 1, n = vertices.length; i < n; i++) {
            //add an edge one of the previously vertices at 0..i-1 to i
            int v = shuffled[random.nextInt(i)];
            int u = shuffled[i];
            g.addEdge(v, u); //works both for directed and undirected
        }
        g.setSafeMode(true);

    }

}
