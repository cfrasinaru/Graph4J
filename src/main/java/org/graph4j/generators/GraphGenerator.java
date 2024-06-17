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

import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.measures.GraphMeasures;
import org.graph4j.util.Validator;

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
        Validator.checkNumVertices(n);
        return GraphBuilder.numVertices(n).named("N" + n).buildGraph();
    }

    /**
     * Generates a complete graph.
     *
     * @see CompleteGraphGenerator
     * @param n the number of vertices.
     * @return a complete graph with n vertices.
     */
    public static Graph complete(int n) {
        return new CompleteGraphGenerator(n).createGraph();
    }

    /**
     * Generates a random Gnp graph.
     *
     * @see RandomGnpGraphGenerator
     * @param n the number of vertices.
     * @param edgeProbability the probability that an edge belongs to the graph.
     * @return a random graph.
     */
    public static Graph randomGnp(int n, double edgeProbability) {
        return new RandomGnpGraphGenerator(n, edgeProbability).createGraph();
    }

    /**
     * Generates a random Gnm graph.
     *
     * @see RandomGnmGraphGenerator
     * @param n the number of vertices.
     * @param m the number of edges.
     * @return a random graph.
     */
    public static Graph randomGnm(int n, int m) {
        return new RandomGnmGraphGenerator(n, m).createGraph();
    }

    /**
     * Generates a complete bipartite graph.
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
     * Generates a complete multipartite graph.
     *
     * @see CompleteMultipartiteGenerator
     * @param numVertices the number of vertices in each partition set.
     * @return a complete multipartite graph.
     */
    public static Graph completeMultipartite(int... numVertices) {
        return new CompleteMultipartiteGenerator(numVertices).create();
    }

    /**
     * Generates a random Gnp bipartite graph.
     *
     * @see RandomGnpBipartiteGenerator
     * @param n1 the number of vertices in the first partition set.
     * @param n2 the number of vertices in the second partition set.
     * @param edgeProbability the probability that an edge belongs to the graph.
     * @return a random bipartite graph.
     */
    public static Graph randomGnpBipartite(int n1, int n2, double edgeProbability) {
        return new RandomGnpBipartiteGenerator(n1, n2, edgeProbability).createGraph();
    }

    /**
     * Generates a random Gnm bipartite graph.
     *
     * @see RandomGnmBipartiteGenerator
     * @param n1 the number of vertices in the first partition set.
     * @param n2 the number of vertices in the second partition set.
     * @param m the number of edges.
     * @return a random bipartite graph.
     */
    public static Graph randomGnmBipartite(int n1, int n2, int m) {
        return new RandomGnmBipartiteGenerator(n1, n2, m).createGraph();
    }

    /**
     * Generates a random tree.
     *
     * @see RandomTreeGenerator
     * @param n the number of vertices.
     * @return a random tree.
     */
    public static Graph randomTree(int n) {
        return new RandomTreeGenerator(n).createTree();
    }

    /**
     * Generates a random arborescence. The root of the arborescence can be
     * found using the method
     * {@link GraphMeasures#minIndegreeVertex(org.graph4j.Digraph)}.
     *
     * @see RandomTreeGenerator
     * @param n the number of vertices.
     * @return a random arborescence.
     */
    public static Digraph randomArborescence(int n) {
        return new RandomTreeGenerator(n).createArborescence();
    }

    /**
     * Generates a random forest.
     *
     * @see RandomForestGenerator
     * @param n the number of vertices.
     * @return a random forest.
     */
    public static Graph randomForest(int n) {
        return new RandomForestGenerator(n).createForest();
    }

    /**
     * Generates a random chordal graph.
     *
     * @see RandomChordalGraphGenerator
     * @param n the number of vertices.
     * @return a random chordal graph.
     */
    public static Graph randomChordalGraph(int n) {
        return new RandomChordalGraphGenerator(n).create();
    }

    /**
     * Generates a random path graph.
     *
     * @see PathGenerator
     * @param n the number of vertices.
     * @return a path graph.
     */
    public static Graph path(int n) {
        return new PathGenerator(n).createGraph();
    }

    /**
     * Generates a random cycle graph.
     *
     * @see CycleGenerator
     * @param n the number of vertices.
     * @return a cycle graph.
     */
    public static Graph cycle(int n) {
        return new CycleGenerator(n).createGraph();
    }

    /**
     * Generates a random wheel graph.
     *
     * @see WheelGenerator
     * @param n the number of vertices.
     * @return a wheel graph.
     */
    public static Graph wheel(int n) {
        return new WheelGenerator(n).createGraph();
    }

    /**
     * Generates a random star graph.
     *
     * @see StarGenerator
     * @param n the number of vertices
     * @return a star graph.
     */
    public static Graph star(int n) {
        return new StarGenerator(n).createGraph();
    }

    /**
     * Generates a complete tree.
     *
     * @see CompleteTreeGenerator
     * @param numLevels the number of levels of the tree.
     * @param degree the degree of the internal nodes.
     * @return a complete tree.
     */
    public static Graph completeTree(int numLevels, int degree) {
        return new CompleteTreeGenerator(numLevels, degree).create();
    }

    /**
     * Generates a regular graph.
     *
     * @see RegularGraphGenerator
     * @param n the number of vertices.
     * @param degree the degree of all vertices.
     * @return a regular graph.
     */
    public static Graph regular(int n, int degree) {
        return new RegularGraphGenerator(n, degree).createGraph();
    }

    /**
     * Generates a grid graph.
     *
     * @see GridGenerator
     * @param rows number of rows.
     * @param cols number of columns.
     * @return a grid graph.
     */
    public static Graph grid(int rows, int cols) {
        return new GridGenerator(rows, cols).createGraph();
    }

    /**
     * Generates a random directed acyclic graph (DAG).
     *
     * @see RandomDAGGenerator
     * @param n number of vertices.
     * @param edgeProbability the probability that an edge belongs to the graph.
     * @return a random DAG.
     */
    public static Digraph randomDAG(int n, double edgeProbability) {
        return new RandomDAGGenerator(n, edgeProbability).createDAG();
    }

    /**
     * Generates a random tournament graph.
     *
     * @see TournamentGenerator
     * @param n the number of vertices.
     * @return a randomly generated tournament.
     */
    public static Digraph randomTournament(int n) {
        return new TournamentGenerator(n).createRandom();
    }

    /**
     * Generates a fan graph (a join between an empty graph and a path).
     *
     * @see FanGenerator
     * @param m the number of vertices in the empty graph.
     * @param n the number of vertices in the path graph.
     * @return a fan graph.
     */
    public static Graph fan(int m, int n) {
        return new FanGenerator(m, n).create();
    }

    /**
     * Generates a Mycielski graph with the specified chromatic number.
     *
     * @see MycielskiGenerator
     * @param chromaticNumber the number of vertices in the empty graph.
     * @return a Mycielski graph having the specified chromatic number.
     */
    public static Graph mycielski(int chromaticNumber) {
        return new MycielskiGenerator().create(chromaticNumber);
    }

}
