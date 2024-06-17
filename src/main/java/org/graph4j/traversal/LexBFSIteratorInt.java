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

import java.util.Objects;
import org.graph4j.Graph;
import org.graph4j.InvalidVertexException;
import org.graph4j.util.IntIterator;
import org.graph4j.util.Validator;
import org.graph4j.util.VertexSet;

/**
 *
 * A variant of LexBFS that returns vertex numbers instead of {@link SearchNode}
 * objects.
 *
 * @see LexBFSIterator
 * @author Cristian Frăsinaru
 */
@Deprecated
public class LexBFSIteratorInt implements IntIterator {

    private final Graph graph;
    private final int startVertex;
    private int compIndex;
    private boolean visited[];

    private Slice head;
    private Slice[] vertexSlice;

    // A slice is a set of the partition
    private class Slice extends VertexSet {

        Slice prev, next;
        Slice newSlice; //prior to this
        int pivotParent;
        int level;

        public Slice(int[] vertices) {
            super(LexBFSIteratorInt.this.graph, vertices);
        }
    }

    /**
     * Creates an iterator starting with the first vertex of the graph (the one
     * at index 0)
     *
     * @param graph the input graph.
     */
    public LexBFSIteratorInt(Graph graph) {
        Objects.requireNonNull(graph);
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
    public LexBFSIteratorInt(Graph graph, int start) {
        Objects.requireNonNull(graph);
        Validator.containsVertex(graph, start);
        this.graph = graph;
        this.startVertex = start;
        init();
    }

    private void init() {
        int n = graph.numVertices();
        this.visited = new boolean[n];
        this.head = new Slice(graph.vertices());
        this.vertexSlice = new Slice[n];
        for (int i = 0; i < n; i++) {
            vertexSlice[i] = head;
        }
        this.compIndex = -1;
    }

    @Override
    public boolean hasNext() {
        return head != null;
    }

    @Override
    public int next() {
        int pivotVertex;
        if (compIndex == -1) {
            pivotVertex = startVertex;
            head.remove(pivotVertex);
        } else {
            pivotVertex = head.pop();
        }
        //
        if (head.next == null) {
            compIndex++;
        }
        visited[graph.indexOf(pivotVertex)] = true;
        for (var it = graph.neighborIterator(pivotVertex); it.hasNext();) {
            int u = it.next();
            int ui = graph.indexOf(u);
            if (visited[ui]) {
                continue;
            }
            var slice = vertexSlice[ui];
            slice.remove(u);
            if (slice.newSlice == null || slice.newSlice.pivotParent != pivotVertex) {
                slice.newSlice = new Slice(new int[]{u});
                slice.newSlice.pivotParent = pivotVertex;
                slice.newSlice.level = head.level + 1;
                // prev - newSlice - slice                
                Slice prev = slice.prev;
                slice.newSlice.prev = prev;
                slice.newSlice.next = slice;
                if (prev != null) {
                    prev.next = slice.newSlice;
                } else {
                    head = slice.newSlice;
                }
                slice.prev = slice.newSlice;
            } else {
                slice.newSlice.add(u);
            }
            vertexSlice[ui] = slice.newSlice;
        }
        while (head != null && head.isEmpty()) {
            if (head.next != null) {
                head.next.prev = null;
            }
            head = head.next;
        }
        return pivotVertex;
    }

}
