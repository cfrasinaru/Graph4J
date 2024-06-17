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
import org.graph4j.Graph;
import org.graph4j.SimpleGraphAlgorithm;
import org.graph4j.support.BipartiteGraphSupport;
import org.graph4j.exceptions.NotBipartiteException;
import org.graph4j.util.Matching;
import org.graph4j.util.StableSet;
import org.graph4j.util.VertexQueue;
import org.graph4j.util.VertexSet;
import org.graph4j.util.VertexStack;

/**
 * Computes the maximum cardinality matching in a bipartite graph.
 *
 * It also determines the maximum stable set and the minimum vertex cover in a
 * bipartite graph.
 *
 * @author Cristian Frăsinaru
 */
public class HopcroftKarpMaximumMatching extends SimpleGraphAlgorithm
        implements MatchingAlgorithm {

    private StableSet leftSide;
    private StableSet rightSide;
    private Matching matching;
    private StableSet maxStable;
    private VertexSet minCover;
    //
    private int[] peer;
    private VertexQueue queue;
    private VertexStack stack;
    private StableSet target;
    private int[] dist;
    private static final int FREE = -1;

    /**
     * Creates an algorithm for determining a maximum matching in a bipartite
     * graph. If the graph is not bipartite, an exception is thrown.
     *
     * @param graph the input graph.
     * @throws NotBipartiteException if the graph is not bipartite.
     */
    public HopcroftKarpMaximumMatching(Graph graph) {
        super(graph);
        var alg = new BipartiteGraphSupport(graph);
        if (!alg.isBipartite()) {
            throw new NotBipartiteException();
        }
        this.leftSide = alg.getLeftSide();
        this.rightSide = alg.getRightSide();
    }

    /**
     * Creates an algorithm for determining a maximum matching in a bipartite
     * graph. The bipartition is assumed to be valid.
     *
     * @param graph the input bipartite graph.
     * @param leftSide the left side of the bipartite graph.
     * @param rightSide the right side of the bipartite graph.
     */
    public HopcroftKarpMaximumMatching(Graph graph, StableSet leftSide, StableSet rightSide) {
        super(graph);
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        /*
        if (!leftSide.isValid()) {
            throw new IllegalArgumentException("The left side is not a stable set.");
        }
        if (!rightSide.isValid()) {
            throw new IllegalArgumentException("The right side is not a stable set.");
        }
        int[] vertices = IntArrays.union(leftSide.vertices(), rightSide.vertices());
        if (!IntArrays.haveSameValues(vertices, graph.vertices())) {
            throw new IllegalArgumentException("Invalid bipartition");
        }*/
    }

    /**
     *
     * @return the maximum cardinality matching.
     */
    @Override
    public Matching getMatching() {
        if (matching != null) {
            return matching;
        }
        compute();
        matching = new Matching(graph);
        for (int v : graph.vertices()) {
            int u = peer[graph.indexOf(v)];
            if (v < u) {
                matching.add(v, u);
            }
        }
        assert matching.isValid();
        return matching;
    }

    /**
     * niu(G) + alpha(G) = n.
     *
     * @return the maximum stable set.
     */
    public StableSet getMaximumStableSet() {
        if (maxStable != null) {
            return maxStable;
        }
        if (matching == null) {
            compute();
        }
        int n = graph.numVertices();
        maxStable = new StableSet(graph, n);
        for (int v : leftSide.vertices()) {
            if (dist[graph.indexOf(v)] >= 0) {
                maxStable.add(v);
            }
        }
        for (int v : rightSide.vertices()) {
            if (dist[graph.indexOf(v)] < 0) {
                maxStable.add(v);
            }
        }
        assert maxStable.isValid();
        return maxStable;
    }

    /**
     * The minimum vertex cover set and the maximum matching set have the same
     * size.
     *
     * @return the minimum vertex cover.
     */
    public VertexSet getMinimumVertexCover() {
        if (minCover != null) {
            return minCover;
        }
        if (matching == null) {
            compute();
        }
        int n = graph.numVertices();
        minCover = new StableSet(graph, n);
        for (int v : leftSide.vertices()) {
            if (dist[graph.indexOf(v)] < 0) {
                minCover.add(v);
            }
        }
        for (int v : rightSide.vertices()) {
            if (dist[graph.indexOf(v)] >= 0) {
                minCover.add(v);
            }
        }
        return minCover;
    }

    private void compute() {
        if (leftSide.size() > rightSide.size()) {
            var aux = leftSide;
            leftSide = rightSide;
            rightSide = aux;
        }
        int n = graph.numVertices();
        peer = new int[n];
        dist = new int[n];
        Arrays.fill(peer, -1);
        queue = new VertexQueue(graph, n);
        stack = new VertexStack(graph, n);
        target = new StableSet(graph, rightSide.size());
        //
        boolean hasAugPath;
        do {
            hasAugPath = bfs();
            if (hasAugPath) {
                dfs();
            }
        } while (hasAugPath);
    }

    private boolean bfs() {
        Arrays.fill(dist, -1);
        queue.clear();
        target.clear();
        for (int v : leftSide.vertices()) {
            int vi = graph.indexOf(v);
            if (peer[vi] == FREE) {
                queue.add(v);
                dist[vi] = 0;
            }
        }
        boolean foundAugPath = false;
        while (!queue.isEmpty()) {
            int v = queue.poll();
            int vi = graph.indexOf(v);
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int ui = graph.indexOf(u);
                if (dist[ui] >= 0) {
                    continue;
                }
                if (peer[ui] == FREE) {
                    //u is a free vertex, we have an augmenting path
                    //saturated/none == v (sat) -- u (free)
                    foundAugPath = true;
                    dist[ui] = dist[vi] + 1;
                    target.add(u);
                } else {
                    //u is saturated: -- u (sat) == w (sat) --
                    if (!foundAugPath) {
                        int w = peer[ui];
                        int wi = graph.indexOf(w);
                        queue.add(w);
                        dist[ui] = dist[vi] + 1;
                        dist[wi] = dist[ui] + 1;
                    }
                }
            }
        }
        return foundAugPath;
    }

    private void dfs() {
        stack.clear();
        for (int v : target.vertices()) {
            stack.push(v);
        }
        while (!stack.isEmpty()) {
            int v = stack.pop();
            int vi = graph.indexOf(v);
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int ui = graph.indexOf(u);
                if (dist[ui] < 0) {
                    continue;
                }
                if (dist[vi] == dist[ui] + 1) {
                    if (peer[ui] != FREE) {
                        int w = peer[ui];
                        stack.push(w);
                        peer[graph.indexOf(w)] = FREE;
                    }
                    peer[ui] = v;
                    peer[vi] = u;
                    dist[ui] = -1;
                    dist[vi] = -1;
                    break;
                }
            }
        }
    }

}
