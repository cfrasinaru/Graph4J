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

import java.util.stream.IntStream;
import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.util.CheckArguments;

/**
 *
 *
 * @author Cristian Frăsinaru
 */
public abstract class AbstractBipartiteGenerator extends AbstractGraphGenerator {

    protected final int first1, last1, first2, last2;

    /**
     *
     * @param n1 the number of vertices in the left side.
     * @param n2 the number of vertices in the right side.
     */
    public AbstractBipartiteGenerator(int n1, int n2) {
        CheckArguments.numberOfVertices(n1);
        CheckArguments.numberOfVertices(n2);
        this.first1 = 0;
        this.last1 = n1 - 1;
        this.first2 = n1;
        this.last2 = n1 + n2 - 1;
        this.vertices = IntStream.range(0, n1 + n2).toArray();
    }

    /**
     *
     * @param first1 the number of first vertex in the left side.
     * @param last1 the number of last vertex in the left side.
     * @param first2 the number of first vertex in the right side.
     * @param last2 the number of last vertex in the right side.
     */
    public AbstractBipartiteGenerator(int first1, int last1, int first2, int last2) {
        CheckArguments.vertexRange(first1, last1);
        CheckArguments.vertexRange(first2, last2);
        if ((first1 >= first2 && first1 <= last2) || (first2 >= first1 && first2 <= last1)) {
            throw new IllegalArgumentException("The vertex ranges of the two partition sets intersect");
        }
        this.first1 = first1;
        this.last1 = last1;
        this.first2 = first2;
        this.last2 = last2;
        int n = last1 - first1 + 1 + last2 - first2 + 1;
        this.vertices = new int[n];
        int k = 0;
        for (int i = first1; i <= last1; i++) {
            this.vertices[k++] = i;
        }
        for (int i = first2; i <= last2; i++) {
            this.vertices[k++] = i;
        }
    }

    /**
     *
     * @return a bipartite graph.
     */
    public Graph createGraph() {
        var g = GraphBuilder.vertices(vertices).buildGraph();
        addEdges(g, true);
        return g;
    }

    /**
     *
     * @param leftToRight {@code true} it the edges are to be oriented from left
     * to right, {@code false} for right to left or {@code null} for a random
     * orientation.
     * @return a bipartite digraph, with the given edge orientation.
     */
    public Digraph createDigraph(Boolean leftToRight) {
        var g = GraphBuilder.vertices(vertices).buildDigraph();
        addEdges(g, leftToRight);
        return g;
    }

    protected abstract void addEdges(Graph g, Boolean leftToRight);

}
