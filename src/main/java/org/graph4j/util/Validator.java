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

import java.util.Objects;
import org.graph4j.Digraph;
import org.graph4j.Edge;
import org.graph4j.Graph;
import org.graph4j.Multigraph;
import org.graph4j.Pseudograph;
import org.graph4j.ordering.InvalidVertexOrdering;

/**
 * Utility class for performing various checks related to graphs. This class is
 * designed primarily for doing parameter validation in methods and
 * constructors.
 *
 * @author Cristian Frăsinaru
 */
public class Validator {

    /**
     * Checks that the specified graph is not empty.
     *
     * @param graph the graph this check is performed on.
     * @throws IllegalArgumentException if {@code graph} is empty.
     */
    public static void requireNonEmpty(Graph graph) {
        if (graph.isEmpty()) {
            throw new IllegalArgumentException("Graph must not be empty.");
        }
    }

    /**
     * Checks if a graph is undirected.
     *
     * @param graph the graph this check is performed on.
     * @throws IllegalArgumentException if {@code graph} is directed.
     */
    public static void requireUndirected(Graph graph) {
        Objects.requireNonNull(graph);
        if (graph instanceof Digraph) {
            throw new IllegalArgumentException("Graph must be undirected.");
        }
    }

    /**
     * Checks if a graph is simple, i.e. undirected, does not allow multiple
     * edges and does not allow self loops.
     *
     * @param graph the graph this check is performed on.
     * @throws IllegalArgumentException if {@code graph} is not simple.
     */
    public static void requireSimple(Graph graph) {
        Objects.requireNonNull(graph);
        if (graph instanceof Digraph) {
            throw new IllegalArgumentException("Graph must be undirected.");
        }
        if (graph instanceof Multigraph) {
            throw new IllegalArgumentException("Graph must not contain multiple edges.");
        }
        if (graph instanceof Pseudograph) {
            throw new IllegalArgumentException("Graph must not contain multiple edges or self loops.");
        }
    }

    /**
     * Checks if the specified number is valid as the number of vertices for a
     * graph, i.e. non-negative.
     *
     * @param n the number of vertices.
     * @throws IllegalArgumentException if {@code n} is negative.
     */
    public static void checkNumVertices(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number of vertices must be non-negative: " + n);
        }
    }

    /**
     * Checks if the numbers in the specified range are valid as the vertices
     * for a graph.
     *
     * @param first first number in the range.
     * @param last last number in the range.
     * @throws IllegalArgumentException if the range is invalid.
     */
    public static void checkVertexRange(int first, int last) {
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
     * Checks if a specified integer is a valid index in a graph, i.e. it is in
     * the range {@code 0} to {@code graph.numVertices()-1}.
     *
     * @param graph the graph this check is performed on.
     * @param index an integer representing an index.
     * @throws IllegalArgumentException if {@code index} is invalid.
     */
    public static void checkVertexIndex(Graph graph, int index) {
        int n = graph.numVertices();
        if (index < 0 || index >= n) {
            throw new IllegalArgumentException("Index must be in the range [0," + (n - 1) + "]: " + index);
        }
    }

    /**
     * Checks if the specified number is valid as the number of edges for a
     * graph, i.e. non-negative.
     *
     * @param m the number of edges.
     * @throws IllegalArgumentException if {@code m} is negative.
     */
    public static void checkNumEdges(long m) {
        if (m < 0) {
            throw new IllegalArgumentException("Number of edges must be non-negative: " + m);
        }
    }

    /**
     * Checks if the specified number is in the range {@code [0,1]}.
     *
     * @param p the number representing a probability.
     * @throws IllegalArgumentException if {@code p} is not in the range
     * {@code [0,1]}.
     */
    public static void checkProbability(double p) {
        if (p < 0 || p > 1) {
            throw new IllegalArgumentException("Probability must be in the range [0,1]: " + p);
        }
    }

    /**
     * Checks if the range of values is valid.
     *
     * @param first first number in the range.
     * @param last last number in the range.
     * @throws IllegalArgumentException if the range is invalid.
     */
    public static void checkRange(double first, double last) {
        if (first > last) {
            throw new IllegalArgumentException("Incorrect range of values: [" + first + "," + last + "]");
        }
    }

    /**
     * Checks if the vertex sets of two graphs are disjoint.
     *
     * @param g1 a graph.
     * @param g2 a graph.
     * @throws IllegalArgumentException if the two graphs have a common vertex
     * number.
     */
    public static void haveDisjointVertices(Graph g1, Graph g2) {
        if (IntArrays.intersects(g1.vertices(), g2.vertices())) {
            throw new IllegalArgumentException("Graphs must have disjoint vertex sets");
        }
    }

    /**
     * Checks if a graph contains a vertex.
     *
     * @param graph the graph this check is performed on.
     * @param v a vertex number.
     * @throws IllegalArgumentException if {@code graph} does not contain
     * {@code v}.
     */
    public static void containsVertex(Graph graph, int v) {
        if (!graph.containsVertex(v)) {
            throw new IllegalArgumentException("Vertex does not belong to the graph: " + v);
        }
    }

    /**
     * Checks if a graph contains an edge.
     *
     * @param graph the graph this check is performed on.
     * @param v a vertex number in the graph.
     * @param u a vertex number in the graph.
     * @throws IllegalArgumentException if {@code graph} does not contain the
     * edge {@code vu}.
     */
    public static void containsEdge(Graph graph, int v, int u) {
        if (!graph.containsEdge(v, u)) {
            throw new IllegalArgumentException("Edge does not belong to the graph: " + v + "-" + u);
        }
    }

    /**
     * Checks if a graph contains an edge.
     *
     * @param graph the graph this check is performed on.
     * @param e an edge of the graph.
     * @throws IllegalArgumentException if {@code graph} does not contain the
     * edge {@code e}.
     */
    public static void containsEdge(Graph graph, Edge e) {
        if (!graph.containsEdge(e)) {
            throw new IllegalArgumentException("Edge does not belong to the graph: " + e);
        }
    }

    /**
     * Checks if a graph contains an array of vertices.
     *
     * @param graph the graph this check is performed on.
     * @param vertices an array of vertex numbers.
     * @throws IllegalArgumentException if {@code graph} does not contain
     * {@code vertices}.
     */
    public static void containsVertices(Graph graph, int... vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("The references to the vertices is null");
        }
        for (int v : vertices) {
            containsVertex(graph, v);
        }
    }

    /**
     * Checks if an array of integers has no duplicate values.
     *
     * @param values an array of numbers.
     * @throws IllegalArgumentException if there are duplicate values.
     */
    public static void hasNoDuplicates(int... values) {
        Integer value = IntArrays.findDuplicate(values);
        if (value != null) {
            throw new IllegalArgumentException("Duplicates are not allowed: " + value);
        }
    }

    /**
     * Checks if an array of vertex numbers has no duplicates.
     *
     * @param vertices an array of vertex numbers.
     * @throws IllegalArgumentException if there exist a duplicate vertex
     * number.
     */
    public static void hasNoDuplicateVertices(int... vertices) {
        if (vertices == null || vertices.length < 2) {
            return;
        }
        int max = IntArrays.max(vertices);
        boolean[] exists = new boolean[max + 1];
        for (int a : vertices) {
            if (a < 0) {
                throw new IllegalArgumentException("Negative numbers are not allowed: " + a);
            }
            if (exists[a]) {
                throw new IllegalArgumentException("Duplicates are not allowed: " + a);
            }
            exists[a] = true;
        }
    }

    /**
     * Checks if a specified ordering is valid, meaning it is a permutation of
     * the vertices of the graph.
     *
     * @param graph the input graph.
     * @param ordering a vertex ordering.
     */
    public static void checkVertexOrdering(Graph graph, int[] ordering) {
        if (!IntArrays.haveSameValues(graph.vertices(), ordering)) {
            throw new InvalidVertexOrdering();
        }

    }

}
