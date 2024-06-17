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
import org.graph4j.Digraph;
import org.graph4j.util.IntIterator;
import org.graph4j.util.VertexQueue;

/**
 * A topological order iterator for a directed acyclic graph (DAG).
 *
 * In a topological ordering, each vertex appears before all the other vertices
 * it has an outgoing edge to.
 *
 * If the input digraph is not acyclic, the {@link #hasNext()} method will throw
 * an {@code IllegalArgumentException} when the ordering cannot be continued,
 * without being complete.
 *
 * @author Cristian Frăsinaru
 */
public class TopologicalOrderIterator implements IntIterator {

    private final Digraph graph;
    boolean computeLevels;
    private int[] indegrees;
    private int[] levels;
    private VertexQueue queue;
    private int currentVertex;
    private int counter;

    /**
     * Creates a topological order iterator for a directed graph.
     *
     * @param graph the input directed graph.
     */
    public TopologicalOrderIterator(Digraph graph) {
        Objects.requireNonNull(graph);
        this.graph = graph;
        init();
    }

    private void init() {
        int n = graph.numVertices();
        indegrees = graph.indegrees();
        levels = new int[n];
        queue = new VertexQueue(graph, n);
        for (int i = 0; i < n; i++) {
            if (indegrees[i] == 0) {
                queue.add(graph.vertexAt(i));
            }
        }
        currentVertex = -1;
        counter = 0;
        if (computeLevels) {
            levels = new int[n];

        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code true} if the iteration has more elements.
     * @throws IllegalArgumentException if the digraph is not acyclic.
     */
    @Override
    public boolean hasNext() throws IllegalArgumentException {
        if (queue.isEmpty() && counter < graph.numVertices()) {
            throw new IllegalArgumentException("The digraph is not acyclic.");
        }
        return !queue.isEmpty();
    }

    @Override
    public int next() {
        currentVertex = queue.poll();
        counter++;
        int vi = graph.indexOf(currentVertex);
        for (var it = graph.successorIterator(currentVertex); it.hasNext();) {
            int u = it.next();
            int ui = graph.indexOf(u);
            indegrees[ui]--;
            if (indegrees[ui] == 0) {
                queue.add(u);
            }
            levels[ui] = levels[vi] + 1;
        }
        return currentVertex;
    }

    /**
     * Returns the level of the current vertex (the one returned by the
     * {@link #next()} method.
     *
     * @return the level of the current vertex.
     */
    public int level() {
        if (currentVertex == -1) {
            return -1;
        }
        return levels[graph.indexOf(currentVertex)];
    }

}
