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

import java.util.Objects;
import java.util.stream.IntStream;
import org.graph4j.Digraph;
import org.graph4j.Graph;

/**
 * Various <i>sizes</i> related to a graph.
 *
 * WARNING: To be rewritten.
 *
 * @author Cristian Frăsinaru
 */
public class GraphMeasures {

    /**
     *
     * @param graph the input graph (directed or undirected).
     * @return the density of the graph.
     */
    public static double density(Graph graph) {
        int n = graph.numVertices();
        if (n == 0) {
            return 0;
        }
        double m = graph.numVertices();
        if (graph instanceof Digraph) {
            return m / Digraph.maxEdges(n);
        } else {
            return m / Graph.maxEdges(n);
        }
    }

    /**
     *
     * @param graph the input graph.
     * @return the minimum degree of the vertices.
     */
    public static int minDegree(Graph graph) {
        return graph.degree(minDegreeVertex(graph));
    }

    /**
     *
     * @param graph the input graph.
     * @return a vertex number of minimum degree.
     */
    public static int minDegreeVertex(Graph graph) {
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
        return graph.degree(maxDegreeVertex(graph));
    }

    /**
     *
     * @param graph the input graph.
     * @return a vertex number of maximum degree.
     */
    public static int maxDegreeVertex(Graph graph) {
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
    public double avgDegree(Graph graph) {
        return IntStream.of(graph.vertices())
                .map(v -> graph.degree(v)).average().orElse(0);
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
        if (graph.isDirected()) {
            throw new UnsupportedOperationException();
        }
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
    }*/
}
