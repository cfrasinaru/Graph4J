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

import java.util.Arrays;
import java.util.Comparator;
import org.graph4j.Edge;
import org.graph4j.Graph;
import org.graph4j.GraphAlgorithm;
import org.graph4j.util.Matching;

/**
 * Greedy algorithm to create a weighted matching. The algorithm sorts
 * descending the edges of the graph either by their weight or by their
 * normalized weight (weigth / sum of endpoints degree) and then adds them to
 * the matching as long as it is possible. Non-positive weighted edges are
 * ignored so the matching is maximal with respect to the subgraph formed only
 * by positive weighted edges.
 *
 * If the edges are sorted by their weight, the matching is guaranteed to be a
 * 1/2-approximation of the meximum weighted matching.
 *
 * @author Cristian Frăsinaru
 */
public class GreedyWeightedMatching extends GraphAlgorithm
        implements MatchingAlgorithm {

    private Comparator<Edge> comparator;
    private Matching matching;

    /**
     * Creates a maximal weighted matching algorithm that iterates over the
     * edges of the graph in the descending order given by their normalized
     * weight. This method has O(m + m log n) time complexity.
     *
     *
     * @param graph the input graph.
     */
    public GreedyWeightedMatching(Graph graph) {
        this(graph, true);
    }

    /**
     * Creates a maximal weighted matching algorithm that iterates over the
     * edges of the graph in the descending order given by theier wight or
     * normalized weight. This method has O(m + m log n) time complexity.
     *
     *
     * @param graph the input graph.
     * @param normalized if {@code true}, the edges are sorted by their
     * normalized weight, otherwise they are sorted by their weight.
     */
    public GreedyWeightedMatching(Graph graph, boolean normalized) {
        super(graph);
        if (normalized) {
            comparator = (Edge e1, Edge e2) -> {
                double w1 = e1.weight() / (graph.degree(e1.source()) + graph.degree(e1.target()));
                double w2 = e2.weight() / (graph.degree(e2.source()) + graph.degree(e2.target()));
                return (int) Math.signum(w2 - w1);
            };
        } else {
            comparator = (Edge e1, Edge e2) -> (int) Math.signum(e2.weight() - e1.weight());
        }
    }

    /**
     * Creates a maximal matching algorithm that iterates over the edges of the
     * graph in the order given by a specified comparator. This method has O(m +
     * m log n) time complexity.
     *
     * @param graph the input graph.
     * @param comparator a comparator for sorting the edges.
     */
    public GreedyWeightedMatching(Graph graph, Comparator<Edge> comparator) {
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
        int n = graph.numVertices();
        matching = new Matching(graph);
        Edge[] edges = graph.edges();
        Arrays.sort(edges, comparator); //expensive
        for (Edge e : edges) {
            if (e.weight() <= 0) {
                break;
            }
            int v = e.source();
            int u = e.target();
            if (u != v && !matching.covers(v) && !matching.covers(u)) {
                matching.add(v, u);
                if (matching.size() == n / 2) {
                    break;
                }
            }
        }
        assert matching.isValid();
        return matching;
    }
    /*
    public Matching getMatching1() {
        if (matching != null) {
            return matching;
        }
        int n = graph.numVertices();
        matching = new Matching(graph, n / 2);
        Edge[] edges = graph.edges();
        //Arrays.sort(edges, comparator);
        var pq = new PriorityQueue<Edge>(edges.length, comparator);
        for (Edge e : edges) {
            if (e.weight() > 0) {
                pq.add(e);
            }
        }
        while (!pq.isEmpty() && matching.size() < n / 2) {
            Edge e = pq.poll();
            int v = e.source();
            int u = e.target();
            if (u != v && !matching.covers(v) && !matching.covers(u)) {
                matching.add(v, u);
            }
        }
        assert matching.isValid();
        return matching;
    }*/

}
