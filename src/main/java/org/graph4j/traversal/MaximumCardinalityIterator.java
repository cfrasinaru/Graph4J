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
package org.graph4j.traversal;

import java.util.NoSuchElementException;
import org.graph4j.Graph;
import org.graph4j.InvalidVertexException;
import org.graph4j.util.IntIterator;
import org.graph4j.util.Validator;
import org.graph4j.util.VertexHeap;

/**
 * Implements an iterator that performs Maximum Cardinality Search (MCS)
 * algorithm on an undirected graph.
 *
 * MCS is a graph traversal algorithm which orders the vertices based on their
 * cardinality in non-increasing order. The
 * <em>cardinality</em> of a vertex is defined as the number of its neighbors,
 * which have been already visited by this iterator. At each step, the iterator
 * chooses a vertex with maximum cardinality, breaking ties arbitrarily.
 *
 * @author Cristian Frăsinaru
 */
public class MaximumCardinalityIterator implements IntIterator {

    private final Graph graph;
    private final int startVertex;
    private boolean[] visited;
    private VertexHeap heap;
    private int[] count; //the cardinality of vertices
    private int numIterations;

    /**
     * Creates an iterator starting with the first vertex of the graph (the one
     * at index 0)
     *
     * @param graph the input graph.
     */
    public MaximumCardinalityIterator(Graph graph) {
        Validator.requireUndirected(graph);
        this.graph = graph;
        this.startVertex = graph.isEmpty() ? -1 : graph.vertexAt(0);
        init();
    }

    /**
     * Creates an iterator starting with the specified vertex.
     *
     * @param graph the input graph.
     * @param start the start vertex number.
     * @throws InvalidVertexException if the graph does not contain the start
     * vertex.
     */
    public MaximumCardinalityIterator(Graph graph, int start) {
        Validator.requireUndirected(graph);
        Validator.containsVertex(graph, start);
        this.graph = graph;
        this.startVertex = start;
        init();
    }

    private void init() {
        int n = graph.numVertices();
        this.visited = new boolean[n];
        this.count = new int[n]; //all zeros
        //max heap for selecting a vertex with maximum cardinality
        this.heap = new VertexHeap(graph, true,
                (i, j) -> (int) Math.signum(count[j] - count[i]));
    }

    @Override
    public boolean hasNext() {
        return !heap.isEmpty();
    }

    @Override
    public int next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        int v, vi;
        if (numIterations++ == 0) {
            v = startVertex;
            vi = graph.indexOf(v);
            heap.remove(vi);
        } else {
            vi = heap.poll();
            v = graph.vertexAt(vi);
        }
        visited[vi] = true;
        for (var it = graph.neighborIterator(v); it.hasNext();) {
            int u = it.next();
            int ui = graph.indexOf(u);
            if (!visited[ui]) {
                count[ui]++;
                heap.update(ui);
            }
        }
        return v;
    }
}
