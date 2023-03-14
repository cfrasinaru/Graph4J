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

import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.util.CheckArguments;

/**
 * Static methods for generating various standard graphs. Most of the methods
 * are shortcuts to a corresponding class responsible with the actual creation
 * of the graph.
 *
 * @author Cristian Frăsinaru
 */
public class GraphGenerator {

    /**
     * Creates a graph with a specified number of vertices and no edges.
     *
     * @see GraphBuilder
     * @param n the number of vertices
     * @return a graph with n vertices and no edges.
     */
    public static Graph empty(int n) {
        CheckArguments.numberOfVertices(n);
        return GraphBuilder.numVertices(n).named("N" + n).buildGraph();
    }

    /**
     *
     * @see CompleteGraphGenerator
     * @param n the number of vertices.
     * @return a complete graph with n vertices.
     */
    public static Graph complete(int n) {
        return new CompleteGraphGenerator(n).createGraph();
    }

    /**
     * @see RandomGnpGraphGenerator
     * @param n the number of vertices.
     * @param edgeProbability the probability that a given edge belongs to the
     * graph.
     * @return a random graph.
     */
    public static Graph randomGnp(int n, double edgeProbability) {
        return new RandomGnpGraphGenerator(n, edgeProbability).createGraph();
    }

    /**
     * @see RandomGnmGraphGenerator
     * @param n the number of vertices.
     * @param m the number of edges.
     * @return a random graph.
     */
    public static Graph randomGnm(int n, int m) {
        return new RandomGnmGraphGenerator(n, m).createGraph();
    }

    /**
     *
     * @see CompleteBipartiteGenerator
     * @param n1 the number of vertices in the first partition set.
     * @param n2 the number of vertices in the second partition set.
     * @return a complete bipartite graph.
     */
    public static Graph completeBipartite(int n1, int n2) {
        return new CompleteBipartiteGenerator(n1, n2).createGraph();
    }

    /**
     *
     * @param n1 the number of vertices in the first partition set.
     * @param n2 the number of vertices in the second partition set.
     * @param edgeProbability the probability that a given edge belongs to the
     * graph.
     * @return a random bipartite graph.
     */
    public static Graph randomGnpBipartite(int n1, int n2, double edgeProbability) {
        return new RandomGnpBipartiteGenerator(n1, n2, edgeProbability).createGraph();
    }

    /**
     *
     * @param n1 the number of vertices in the first partition set.
     * @param n2 the number of vertices in the second partition set.
     * @param m the number of edges.
     * @return a random bipartite graph.
     */
    public static Graph randomGnmBipartite(int n1, int n2, int m) {
        return new RandomGnmBipartiteGenerator(n1, n2, m).createGraph();
    }

    /**
     *
     * @param n the number of vertices.
     * @return a random tree.
     */
    public static Graph randomTree(int n) {
        return new RandomTreeGenerator(n).create();
    }

    /**
     * @see PathGenerator
     * @param n the number of vertices.
     * @return a path graph.
     */
    public static Graph path(int n) {
        return new PathGenerator(n).createGraph();
    }

    /**
     *
     * @see CycleGenerator
     * @param n the number of vertices.
     * @return a cycle graph.
     */
    public static Graph cycle(int n) {
        return new CycleGenerator(n).createGraph();
    }

    /**
     * @see WheelGenerator
     * @param n the number of vertices.
     * @return a wheel graph.
     */
    public static Graph wheel(int n) {
        return new WheelGenerator(n).createGraph();
    }

    /**
     * @see StarGenerator
     * @param n the number of vertices
     * @return a star graph.
     */
    public static Graph star(int n) {
        return new StarGenerator(n).createGraph();
    }

    /**
     * @see CompleteTreeGenerator
     * @param numLevels the number of levels of the tree.
     * @param degree the degree of the internal nodes.
     * @return a complete tree.
     */
    public static Graph completeTree(int numLevels, int degree) {
        return new CompleteTreeGenerator(numLevels, degree).create();
    }

    /**
     *
     * @param n the number of vertices.
     * @param degree the degree of all vertices.
     * @return a regular graph.
     */
    public static Graph regular(int n, int degree) {
        return new RegularGraphGenerator(n, degree).createGraph();
    }

}
