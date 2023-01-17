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

import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.GraphBuilder;
import ro.uaic.info.graph.util.CheckArguments;

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
     * @return
     */
    public static Graph empty(int n) {
        CheckArguments.numberOfVertices(n);
        return new GraphBuilder().numVertices(n).named("N" + n).buildGraph();
    }

    /**
     *
     * @see CompleteGenerator
     * @param n the number of vertices
     * @return
     */
    public static Graph complete(int n) {
        return new CompleteGenerator(n).createGraph();
    }

    /**
     *
     * @see CompleteBipartiteGenerator
     * @param n1 the number of vertices in the first partition set
     * @param n2 the number of vertices in the second partition set
     * @return
     */
    public static Graph completeBipartite(int n1, int n2) {
        return new CompleteBipartiteGenerator(n1, n2).createGraph();
    }

    /**
     * @see PathGenerator
     * @param n the number of vertices
     * @return
     */
    public static Graph path(int n) {
        return new PathGenerator(n).createGraph();
    }

    /**
     *
     * @see CycleGenerator
     * @param n the number of vertices
     * @return
     */
    public static Graph cycle(int n) {
        return new CycleGenerator(n).createGraph();
    }

    /**
     * @see WheelGenerator
     * @param n the number of vertices
     * @return
     */
    public static Graph wheel(int n) {
        return new WheelGenerator(n).createGraph();
    }

    /**
     * @see StarGenerator
     * @param n the number of vertices
     * @return
     */
    public static Graph star(int n) {
        return new StarGenerator(n).createGraph();
    }

    /**
     * @see CompleteTreeGenerator
     * @param numLevels the number of levels of the tree
     * @param degree the degree of the internal nodes
     * @return
     */
    public static Graph completeTree(int numLevels, int degree) {
        return new CompleteTreeGenerator(numLevels, degree).create();
    }

}
