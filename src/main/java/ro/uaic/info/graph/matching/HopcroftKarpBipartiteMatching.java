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
package ro.uaic.info.graph.matching;

import java.util.Arrays;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.alg.SimpleGraphAlgorithm;
import ro.uaic.info.graph.alg.bipartite.BipartitionAlgorithm;
import ro.uaic.info.graph.model.Matching;
import ro.uaic.info.graph.model.StableSet;
import ro.uaic.info.graph.model.VertexQueue;
import ro.uaic.info.graph.model.VertexStack;
import ro.uaic.info.graph.util.IntArrays;

/**
 * Computes the maximum cardinality matching in a bipartite graph.
 *
 * @author Cristian Frăsinaru
 */
public class HopcroftKarpBipartiteMatching extends SimpleGraphAlgorithm {

    private StableSet leftSide;
    private StableSet rightSide;
    private Matching matching;
    //
    private int[] peer;
    private VertexQueue queue;
    private VertexStack stack;
    private StableSet target;
    private int[] dist;
    private final int FREE = -1;

    /**
     * If the graph is not bipartite, an exception is thrown.
     *
     * @param graph the input graph.
     */
    public HopcroftKarpBipartiteMatching(Graph graph) {
        super(graph);
        var alg = BipartitionAlgorithm.getInstance(graph);
        if (!alg.isBipartite()) {
            throw new IllegalArgumentException("The graph is not bipartite");
        }
        this.leftSide = alg.getLeftSide();
        this.rightSide = alg.getRightSide();
    }

    /**
     *
     * @param graph the input bipartite graph.
     * @param leftSide the left side of the bipartite graph.
     * @param rightSide the right side of the bipartite graph.
     */
    public HopcroftKarpBipartiteMatching(Graph graph, StableSet leftSide, StableSet rightSide) {
        super(graph);
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        if (!leftSide.isValid()) {
            throw new IllegalArgumentException("The left side is not a stable set.");
        }
        if (!rightSide.isValid()) {
            throw new IllegalArgumentException("The right side is not a stable set.");
        }
        int[] vertices = IntArrays.union(leftSide.vertices(), rightSide.vertices());
        if (!IntArrays.sameValues(vertices, graph.vertices())) {
            throw new IllegalArgumentException("Invalid bipartition");
        }
    }

    /**
     *
     * @return
     */
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
                queue.offer(v);
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
                        queue.offer(w);
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
