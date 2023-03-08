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

import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;

/**
 *
 * An undirected graph is  <em>regular</em> if every vertex has the same degree.
 * A directed graph is regular if the vertices indegrees and outdegrees are all
 * equal.
 *
 * @author Cristian Frăsinaru
 */
public class RegularGraphGenerator extends AbstractGraphGenerator {

    private int degree;

    /**
     * 
     * @param numVertices the number of vertices.
     * @param degree the degree of all vertices.
     */
    public RegularGraphGenerator(int numVertices, int degree) {
        this(0, numVertices - 1, degree);
    }

    /**
     * 
     * @param firstVertex the number of the first vertex in the geenrated graph.
     * @param lastVertex the number of the last vertex in the geenrated graph.
     * @param degree the degree of all vertices.
     */
    public RegularGraphGenerator(int firstVertex, int lastVertex, int degree) {
        super(firstVertex, lastVertex);
        int n = vertices.length;
        if (degree < 0) {
            throw new IllegalArgumentException("The degree must be non-negative.");
        }
        if (degree >= n) {
            throw new IllegalArgumentException("The degree must be less than: " + n);
        }
        if (n % 2 == 1 && degree % 2 == 1) {
            throw new IllegalArgumentException("The number of vertices and the degree cannot be both odd.");
        }
        this.degree = degree;
    }

    /**
     *
     * @return a regular graph.
     */
    public Graph createGraph() {
        var g = GraphBuilder.vertices(vertices).estimatedAvgDegree(degree)
                .buildGraph();
        addEdges(g, false);
        return g;
    }

    /**
     *
     * @return a regular directed graph.
     */
    public Digraph createDigraph() {
        var g = GraphBuilder.vertices(vertices).estimatedAvgDegree(degree)
                .buildDigraph();
        addEdges(g, true);
        return g;
    }

    private void addEdges(Graph g, boolean directed) {
        int n = vertices.length;
        int k = directed ? degree : degree / 2;
        for (int i = 0; i < n; i++) {
            int v = vertices[i];
            for (int j = 0; j < k; j++) {
                int u = vertices[(i + j + 1) % n];
                g.addEdge(v, u);
            }
        }
        if (!directed && degree % 2 == 1) {
            //n must be even
            for (int i = 0; i < n / 2; i++) {
                int v = vertices[i];
                int u = vertices[(i + n / 2) % n];
                g.addEdge(v, u);
            }
        }
    }

}
