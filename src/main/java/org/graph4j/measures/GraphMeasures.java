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
package org.graph4j.measures;

import java.util.stream.IntStream;
import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.util.Validator;

/**
 * Various <em>sizes</em> related to graphs.
 *
 * @author Cristian Frăsinaru
 */
public class GraphMeasures {

    /**
     *
     * @param graph the input graph.
     * @return the density of the graph.
     */
    public static double density(Graph graph) {
        Validator.requireUndirected(graph);
        int n = graph.numVertices();
        if (n == 0) {
            return 0;
        }
        return (double) graph.numEdges() / Graph.maxEdges(n);
    }

    /**
     * Determines the minimum degree of the vertices.
     *
     * @param graph the input graph.
     * @return the minimum degree of the vertices.
     */
    public static int minDegree(Graph graph) {
        Validator.requireUndirected(graph);
        return graph.degree(minDegreeVertex(graph));
    }

    /**
     * Determines a vertex with minimum degree.
     *
     * @param graph the input graph.
     * @return a vertex number of minimum degree.
     */
    public static int minDegreeVertex(Graph graph) {
        Validator.requireUndirected(graph);
        int minVertex = -1;
        int minDeg = Integer.MAX_VALUE;
        for (int v : graph.vertices()) {
            int deg = graph.degree(v);
            if (deg < minDeg) {
                minDeg = deg;
                minVertex = v;
            }
        }
        return minVertex;
    }

    /**
     *
     * @param graph the input graph.
     * @return the maximum degree of the vertices.
     */
    public static int maxDegree(Graph graph) {
        Validator.requireUndirected(graph);
        return graph.degree(maxDegreeVertex(graph));
    }

    /**
     *
     * @param graph the input graph.
     * @return a vertex number of maximum degree.
     */
    public static int maxDegreeVertex(Graph graph) {
        Validator.requireUndirected(graph);
        int maxVertex = -1;
        int maxDeg = -1;
        for (int v : graph.vertices()) {
            int deg = graph.degree(v);
            if (deg > maxDeg) {
                maxDeg = deg;
                maxVertex = v;
            }
        }
        return maxVertex;
    }

    /**
     *
     * @param graph the input graph.
     * @return the average degree of the vertices.
     */
    public static double avgDegree(Graph graph) {
        Validator.requireUndirected(graph);
        return IntStream.of(graph.vertices())
                .map(v -> graph.degree(v)).average().orElse(0);
    }

    /**
     * The <em>degree histogram</em> counts how many vertices have a specific
     * degree in the graph. If {@code degreeHistogram(g)[i]=k} then there are k
     * vertices having the degree i.
     *
     * @param graph the input graph.
     * @return the degree histogram of the graph.
     */
    public static int[] degreeHistogram(Graph graph) {
        Validator.requireUndirected(graph);
        int n = graph.numVertices();
        int[] count = new int[n];
        for (int v : graph.vertices()) {
            count[graph.degree(v)]++;
        }
        return count;
    }

    /**
     * The <em>degree distribution</em> is the probability distribution P of the
     * graph degrees over the whole graph. If there are n vertices in the graph
     * and n<sub>k</sub> of them have degree k, we have P(k)=n<sub>k</sub>/n.
     *
     * @param graph the input graph.
     * @return the degree distribution of the graph.
     */
    public static double[] degreeDistribution(Graph graph) {
        Validator.requireUndirected(graph);
        int n = graph.numVertices();
        double[] p = new double[n];
        if (n == 0) {
            return p;
        }
        for (int v : graph.vertices()) {
            p[graph.degree(v)]++;
        }
        for (int i = 0; i < n; i++) {
            p[i] = p[i] / n;
        }
        return p;
    }

    /**
     * A <em>triangle</em> is formed by three distinct vertices connected all
     * with each other.
     *
     * @param graph the input graph.
     * @return the number of triangles in the graph.
     * @see TriangleCounter
     */
    public static long numberOfTriangles(Graph graph) {        
        return new TriangleCounter(graph).count();
    }

    /**
     * A <em>triplet</em> is formed by three distinct vertices that are
     * connected by either two (open triplet) or three (closed triplet)
     * undirected edges. A triangle graph therefore includes three closed
     * triplets.
     *
     * @param graph the input graph.
     * @return the number of triplets in an undirected graph.
     */
    public static long numberOfTriplets(Graph graph) {
        Validator.requireUndirected(graph);
        long count = 0;
        for (int v : graph.vertices()) {
            int deg = graph.degree(v);
            count += deg * (deg - 1) / 2;
        }
        return count;
    }

    /**
     * A <em>triplet</em> is formed by three distinct vertices that are
     * connected by either two (open triplet) or three (closed triplet)
     * undirected edges. A triangle graph therefore includes three closed
     * triplets.
     *
     * @param graph
     * @return the number of triplets in a directed graph.
     */
    /*
    public long numberOfTriplets(Digraph graph) {
        long count = 0;
        for (int v : graph.vertices()) {
            int deg = graph.indegree(v) + graph.outdegree(v);
            count += deg;
        }
        return count;
    }
     */
    
    
    /**
     *
     * @param digraph the input digraph.
     * @return the density of the digraph.
     */
    public static double density(Digraph digraph) {
        int n = digraph.numVertices();
        if (n == 0) {
            return 0;
        }
        return (double) digraph.numEdges() / Digraph.maxEdges(n);
    }

    /**
     * Determines the minimum indegree of the vertices.
     *
     * @param digraph the input digraph.
     * @return the minimum indegree of the vertices.
     */
    public static int minIndegree(Digraph digraph) {
        return digraph.indegree(minIndegreeVertex(digraph));
    }

    /**
     * Determines a vertex with minimum indegree.
     *
     * @param digraph the input digraph.
     * @return a vertex number of minimum indegree.
     */
    public static int minIndegreeVertex(Digraph digraph) {
        int minVertex = -1;
        int minDeg = Integer.MAX_VALUE;
        for (int v : digraph.vertices()) {
            int deg = digraph.indegree(v);
            if (deg < minDeg) {
                minDeg = deg;
                minVertex = v;
            }
        }
        return minVertex;
    }

    /**
     * Determines the maximum indegree of the vertices.
     *
     * @param digraph the input digraph.
     * @return the maximum indegree of the vertices.
     */
    public static int maxIndegree(Digraph digraph) {
        return digraph.indegree(maxIndegreeVertex(digraph));
    }

    /**
     * Determines a vertex with maximum indegree.
     *
     * @param digraph the input digraph.
     * @return a vertex number of maximum indegree.
     */
    public static int maxIndegreeVertex(Digraph digraph) {
        int maxVertex = -1;
        int maxDeg = -1;
        for (int v : digraph.vertices()) {
            int deg = digraph.indegree(v);
            if (deg > maxDeg) {
                maxDeg = deg;
                maxVertex = v;
            }
        }
        return maxVertex;
    }

    /**
     * Determines the average indegree of vertices.
     *
     * @param digraph the input digraph.
     * @return the average indegree of the vertices.
     */
    public static double avgIndegree(Digraph digraph) {
        return IntStream.of(digraph.vertices())
                .map(v -> digraph.indegree(v)).average().orElse(0);
    }

    /**
     * The <em>indegree histogram</em> counts how many vertices have a specific
     * indegree in the digraph. If {@code indegreeHistogram(g)[i]=k} then there
     * are k vertices having the indegree i.
     *
     * @param digraph the input digraph.
     * @return the indegree histogram of the digraph.
     */
    public static int[] indegreeHistogram(Digraph digraph) {
        int n = digraph.numVertices();
        int[] count = new int[n];
        for (int v : digraph.vertices()) {
            count[digraph.indegree(v)]++;
        }
        return count;
    }

    /**
     * Determines the minimum outdegree of the vertices.
     *
     * @param digraph the input digraph.
     * @return the minimum outdegree of the vertices.
     */
    public static int minOutdegree(Digraph digraph) {
        return digraph.outdegree(minOutdegreeVertex(digraph));
    }

    /**
     * Determines a vertex with minimum outdegree.
     *
     * @param digraph the input digraph.
     * @return a vertex number of minimum outdegree.
     */
    public static int minOutdegreeVertex(Digraph digraph) {
        int minVertex = -1;
        int minDeg = Integer.MAX_VALUE;
        for (int v : digraph.vertices()) {
            int deg = digraph.outdegree(v);
            if (deg < minDeg) {
                minDeg = deg;
                minVertex = v;
            }
        }
        return minVertex;
    }

    /**
     * Determines the maximum outdegree of the vertices.
     *
     * @param digraph the input digraph.
     * @return the maximum outdegree of the vertices.
     */
    public static int maxOutdegree(Digraph digraph) {
        return digraph.outdegree(maxOutdegreeVertex(digraph));
    }

    /**
     * Determines a vertex with maximum outdegree.
     *
     * @param digraph the input digraph.
     * @return a vertex number of maximum outdegree.
     */
    public static int maxOutdegreeVertex(Digraph digraph) {
        int maxVertex = -1;
        int maxDeg = -1;
        for (int v : digraph.vertices()) {
            int deg = digraph.outdegree(v);
            if (deg > maxDeg) {
                maxDeg = deg;
                maxVertex = v;
            }
        }
        return maxVertex;
    }

    /**
     * Determines the average outdegree of vertices.
     *
     * @param digraph the input digraph.
     * @return the average outdegree of the vertices.
     */
    public static double avgOutdegree(Digraph digraph) {
        return IntStream.of(digraph.vertices())
                .map(v -> digraph.outdegree(v)).average().orElse(0);
    }

    /**
     * The <em>outdegree histogram</em> counts how many vertices have a specific
     * outdegree in the digraph. If {@code outdegreeHistogram(g)[i]=k} then there
     * are k vertices having the outdegree i.
     *
     * @param digraph the input digraph.
     * @return the outdegree histogram of the digraph.
     */
    public static int[] outdegreeHistogram(Digraph digraph) {
        int n = digraph.numVertices();
        int[] count = new int[n];
        for (int v : digraph.vertices()) {
            count[digraph.outdegree(v)]++;
        }
        return count;
    }

}
