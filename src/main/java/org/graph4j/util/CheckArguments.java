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
package org.graph4j.util;

import org.graph4j.Digraph;
import org.graph4j.Edge;
import org.graph4j.Graph;

/**
 * Utility class for performing various checks.
 *
 * @author Cristian Frăsinaru
 */
public class CheckArguments {

    /**
     *
     * @param graph the graph this check is performed on.
     */
    public static void graphNotNull(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph reference must not be null.");
        }
    }

    /**
     *
     * @param graph the graph this check is performed on.
     */
    public static void graphNotEmpty(Graph graph) {
        if (graph.isEmpty()) {
            throw new IllegalArgumentException("Graph must not be empty.");
        }
    }

    /**
     *
     * @param graph the graph this check is performed on.
     */
    public static void graphUndirected(Graph graph) {
        if (graph instanceof Digraph) {
            throw new IllegalArgumentException("Graph must be undirected.");
        }
    }
    
    /**
     *
     * @param n a number.
     */
    public static void numberOfVertices(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number of vertices must be non-negative: " + n);
        }
    }

    /**
     *
     * @param first first number in the range.
     * @param last last number in the range.
     */
    public static void vertexRange(int first, int last) {
        if (first < 0) {
            throw new IllegalArgumentException("Vertex numbers must be non-negative: " + first);
        }
        if (last < 0) {
            throw new IllegalArgumentException("Vertex numbers must be non-negative: " + last);
        }
        if (first > last) {
            throw new IllegalArgumentException("Incorrect vertex range: [" + first + "," + last + "]");
        }
    }

    /**
     *
     * @param index an index.
     * @param numVertices a number of vertices.
     */
    public static void indexInRange(int index, int numVertices) {
        if (index < 0 || index >= numVertices) {
            throw new IllegalArgumentException("Index must be in the range [0," + (numVertices - 1) + "]: " + index);
        }
    }

    /**
     *
     * @param m a number of edges.
     */
    public static void numberOfEdges(long m) {
        if (m < 0) {
            throw new IllegalArgumentException("Number of edges must be non-negative: " + m);
        }
    }

    /**
     *
     * @param p a probability.
     */
    public static void probability(double p) {
        if (p < 0 || p > 1) {
            throw new IllegalArgumentException("Probability must be in the range [0,1]: " + p);
        }
    }

    /**
     *
     * @param g1 a graph.
     * @param g2 a graph.
     */
    public static void disjointVertices(Graph g1, Graph g2) {
        if (IntArrays.intersects(g1.vertices(), g2.vertices())) {
            throw new IllegalArgumentException("Graphs must have disjoint vertex sets");
        }
    }

    /**
     *
     * @param graph the graph this check is performed on.
     * @param v a vertex number.
     */
    public static void graphContainsVertex(Graph graph, int v) {
        if (!graph.containsVertex(v)) {
            throw new IllegalArgumentException("Vertex does not belong to the graph: " + v);
        }
    }

    /**
     *
     * @param graph the graph this check is performed on.
     * @param v a vertex number.
     * @param u a vertex number.
     */
    public static void graphContainsEdge(Graph graph, int v, int u) {
        graphContainsEdge(graph, new Edge(v, u));
    }

    /**
     *
     * @param graph the graph this check is performed on.
     * @param e an edge of the graph.
     */
    public static void graphContainsEdge(Graph graph, Edge e) {
        if (!graph.containsEdge(e)) {
            throw new IllegalArgumentException("Edge does not belong to the graph: " + e);
        }
    }

    /**
     *
     * @param graph the graph this check is performed on.
     * @param vertices an array of vertex numbers.
     */
    public static void graphContainsVertices(Graph graph, int... vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("The references to the vertices is null");
        }
        for (int v : vertices) {
            graphContainsVertex(graph, v);
        }
    }

    /**
     *
     * @param values an array of numbers.
     */
    public static void noDuplicates(int... values) {
        Integer value = IntArrays.findDuplicate(values);
        if (value != null) {
            throw new IllegalArgumentException("Duplicates are not allowed: " + value);
        }
    }
}
