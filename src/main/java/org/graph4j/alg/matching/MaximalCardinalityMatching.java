/*
 * Copyright (C) 2023 Cristian Frăsinaru and contributors
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
package org.graph4j.alg.matching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.graph4j.Edge;
import org.graph4j.Graph;
import org.graph4j.alg.GraphAlgorithm;
import org.graph4j.util.IntArrays;
import org.graph4j.util.Matching;

/**
 * Creates a <em>maximal</em> cardinality matching either randomly or using a
 * simple heuristic.
 *
 * A maximal cardinality matching has the property that no other edge can be
 * added to it. A maximum cardinality matching is the largest possible matching.
 *
 * In any graph, any maximal matching has size at least 1/2 of the maximum
 * matching.
 *
 * @author Cristian Frăsinaru
 */
public class MaximalCardinalityMatching extends GraphAlgorithm
        implements MatchingAlgorithm {

    private Boolean random;
    private Comparator<Edge> comparator;
    private Matching matching;

    /**
     * Creates a maximal matching algorithm that iterates over the edges of the
     * graph in the order given by the sum of degrees of the edges
     * endpoints.This method has O(m + m log n) time complexity and produces, in
     * general, better results than the random version.
     *
     *
     * @param graph the input graph.
     */
    public MaximalCardinalityMatching(Graph graph) {
        super(graph);
    }

    /**
     * Creates a maximal matching algorithm that iterates over the edges of the
     * graph in a random order {@code (random=true)} or in the order in which
     * the edges are internally stored {@code (random=false)}.This method has
     * O(m) time complexity.
     *
     * @param graph the input graph.
     * @param random if {@code true} creates a random matching.
     */
    public MaximalCardinalityMatching(Graph graph, boolean random) {
        super(graph);
        this.random = random;
    }

    /**
     * Creates a maximal matching algorithm that iterates over the edges of the
     * graph in the order given by a specified comparator.This method has O(m +
     * m log n) time complexity.
     *
     * @param graph the input graph.
     * @param comparator a comparator for sorting the edges.
     */
    public MaximalCardinalityMatching(Graph graph, Comparator<Edge> comparator) {
        super(graph);
        if (comparator == null) {
            throw new IllegalArgumentException("The comparator cannot be null.");
        }
        this.comparator = comparator;
    }

    /**
     * Returns a maximal matching.
     *
     * @return a maximal matching.
     */
    @Override
    public Matching getMatching() {
        if (matching != null) {
            return matching;
        }
        if (random != null) {
            createUnsorted(random);
        } else if (comparator != null) {
            Edge[] edges = graph.edges();
            Arrays.sort(edges, comparator);
            createSorted(edges);
        } else {
            createSorted(edgesSortedByDegree());
        }
        assert matching.isValid();
        return matching;

    }

    //fast and memory efficient
    private void createUnsorted(boolean random) {
        int n = graph.numVertices();
        matching = new Matching(graph, n / 2);
        int[] vertices = graph.vertices();
        if (random) {
            vertices = IntArrays.shuffle(vertices);
        }
        over:
        for (var v : vertices) {
            if (matching.covers(v)) {
                continue;
            }
            int[] neighbors = graph.neighbors(v);
            if (random) {
                neighbors = IntArrays.shuffle(neighbors);
            }
            for (var u : neighbors) {
                if (u != v && !matching.covers(u)) {
                    matching.add(v, u);
                    if (matching.size() == n / 2) {
                        break over;
                    }
                    break;
                }
            }
        }
    }

    //better results
    private void createSorted(Edge[] edges) {
        int n = graph.numVertices();
        matching = new Matching(graph, n / 2);
        for (Edge e : edges) {
            int v = e.source();
            int u = e.target();
            if (u != v && !matching.covers(v) && !matching.covers(u)) {
                matching.add(v, u);
                if (matching.size() == n / 2) {
                    break;
                }
            }
        }
    }

    //counting sort
    private Edge[] edgesSortedByDegree() {
        int n = graph.numVertices();
        Edge[] edges = graph.edges();
        List<Edge>[] buckets = new ArrayList[2 * n - 2];
        for (Edge e : edges) {
            int i = graph.degree(e.source()) + graph.degree(e.target());
            if (buckets[i] == null) {
                buckets[i] = new ArrayList<>();
            }
            buckets[i].add(e);
        }
        int k = 0;
        for (List<Edge> bucket : buckets) {
            if (bucket != null) {
                for (Edge e : bucket) {
                    edges[k++] = e;
                }
            }
        }
        return edges;
    }

}
