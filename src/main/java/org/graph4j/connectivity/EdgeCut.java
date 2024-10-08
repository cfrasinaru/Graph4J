/*
 * Copyright (C) 2024 Cristian Frăsinaru and contributors
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
package org.graph4j.connectivity;

import java.util.Objects;
import org.graph4j.Edge;
import org.graph4j.Graph;
import org.graph4j.GraphTests;
import org.graph4j.util.EdgeSet;
import org.graph4j.util.IntArrays;

/**
 * Utility class for representing an edge cut. An <em>edge cut</em> is a set of
 * edges that, if removed, would disconnect the graph.
 *
 * @see StoerWagnerMinimumCut
 * @author Cristian Frăsinaru
 */
public class EdgeCut {

    private final Graph graph;
    private final int[] leftSide;
    private final int[] rightSide;
    private Double weight;
    private EdgeSet edges;

    /**
     * Creates an empty edge cut.
     *
     * @param graph the graph the cut belongs to.
     */
    public EdgeCut(Graph graph) {
        this(graph, new int[]{}, 0.0);
    }

    /**
     * Creates a new edge cut.
     *
     * @param graph the graph the cut belongs to.
     * @param leftSide the vertices in one side of the cut.
     * @param weight the precomputed weight of the cut, or {@code null}.
     */
    public EdgeCut(Graph graph, int[] leftSide, Double weight) {
        Objects.requireNonNull(leftSide);
        this.graph = graph;
        this.leftSide = leftSide;
        this.rightSide = IntArrays.difference(graph.vertices(), leftSide);
        this.weight = weight;
    }

    /**
     * Creates a new edge cut.
     *
     * @param graph the graph the cut belongs to.
     * @param edges the edges in the cut.
     * @param weight the precomputed weight of the cut, or {@code null}.
     */
    public EdgeCut(Graph graph, EdgeSet edges, Double weight) {
        Objects.requireNonNull(edges);
        this.graph = graph;
        this.edges = edges;
        int k = edges.size();
        this.leftSide = new int[k];
        this.rightSide = new int[k];
        int i = 0;
        for (var e : edges) {
            leftSide[i] = e.source();
            rightSide[i] = e.target();
            i++;
        }
        this.weight = weight;

    }

    /**
     * Returns the vertices in the left side of the cut.
     *
     * @return the vertices in the left side of the cut.
     */
    public int[] leftSide() {
        return leftSide;
    }

    /**
     * Returns the vertices in the right side of the cut.
     *
     * @return the vertices in the right side of the cut.
     */
    public int[] rightSide() {
        return rightSide;
    }

    /**
     * Returns the weight of the cut.
     *
     * @return the weight of the cut, that is the sum of the weights of the
     * edges in the cut.
     */
    public double weight() {
        if (weight == null) {
            weight = edges().weight();
        }
        return weight;
    }

    /**
     * Returns the number of edges in the cut.
     *
     * @return the number of edges in the cut.
     */
    public int size() {
        return edges().size();
    }

    /**
     * Returns the edges of the cut, whose removal disconnects the graph.
     *
     * @return the edges of the cut.
     */
    public EdgeSet edges() {
        if (edges != null) {
            return edges;
        }
        edges = new EdgeSet(graph, leftSide.length);
        for (int v : leftSide) {
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                if (!IntArrays.contains(leftSide, u)) {
                    edges.add(graph.edge(v, u));
                }
            }
        }
        return edges;
    }

    /**
     * Checks if the cut is valid.
     *
     * @return {@code true} if the cut is valid, {@code false} otherwise.
     */
    public boolean isValid() {
        var g = graph.copy();
        for (Edge e : edges()) {
            g.removeEdge(e);
        }
        boolean connected = GraphTests.isConnected(g);
        return !connected;
    }

    @Override
    public String toString() {
        return edges().toString() + "\n\tleft: " + leftSide + "\n\tright:" + rightSide;
    }

}
