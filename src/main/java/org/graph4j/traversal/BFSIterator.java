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
package org.graph4j.traversal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.InvalidVertexException;
import org.graph4j.util.Validator;

/**
 * A breadth first search (BFS) iterator over the vertices of the graph.
 *
 * @author Cristian Frăsinaru
 */
public class BFSIterator implements Iterator<SearchNode> {

    private final Graph graph;
    private final int startVertex; //vertex
    private int numVertices;
    private Queue<SearchNode> queue;
    private boolean visited[];
    private int restartIndex; //all the vertices up to restartIndex are visited
    private int numIterations;
    private int compIndex;
    private int orderNumber;
    private int maxLevel;
    private boolean reverse;

    /**
     * Creates an iterator starting with the first vertex of the graph (the one
     * at index 0)
     *
     * @param graph the input graph.
     */
    public BFSIterator(Graph graph) {
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
    public BFSIterator(Graph graph, int start) {
        Objects.requireNonNull(graph);
        Validator.containsVertex(graph, start);
        this.graph = graph;
        this.startVertex = start;
        init();
    }

    /**
     * Creates an iterator starting with the specified vertex, traversing a
     * directed graph either in the direction of the arcs, or in reversed
     * direction. In case of undirected graphs, the {@code reverse} argument is
     * ignored.
     *
     * @param graph the input graph.
     * @param start the start vertex number.
     * @param forbiddenVertices vertices that are not allowed in the path; can
     * be {@code null} if there are no forbidden vertices.
     * @param reverse if {@code true} iteration will pe performed on the
     * reversed graph.
     * @throws InvalidVertexException if the graph does not contain the start
     * vertex.
     */
    public BFSIterator(Graph graph, int start, int[] forbiddenVertices, boolean reverse) {
        Objects.requireNonNull(graph);
        Validator.containsVertex(graph, start);
        this.graph = graph;
        this.startVertex = start;
        this.reverse = reverse;
        if (forbiddenVertices != null) {
            Validator.containsVertices(graph, forbiddenVertices);
            this.visited = new boolean[graph.numVertices()];
            for (int w : forbiddenVertices) {
                visited[graph.indexOf(w)] = true;
            }
        }
        init();
    }

    private void init() {
        this.numVertices = graph.numVertices();
        if (this.visited == null) {
            this.visited = new boolean[numVertices];
        }
        this.queue = new LinkedList<>();
        if (startVertex >= 0) {
            queue.offer(new SearchNode(0, startVertex, 0, orderNumber++, null));
            visited[graph.indexOf(startVertex)] = true;
        }
        this.maxLevel = -1;
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public SearchNode next() {
        if (queue.isEmpty()) {
            throw new NoSuchElementException();
        }
        var current = queue.poll();
        if (current.level() > maxLevel) {
            maxLevel = current.level();
        }
        var v = current.vertex();
        numIterations++;
        //memory aggressive
        for (var it = (reverse && graph.isDirected()
                ? ((Digraph) graph).predecessorIterator(v)
                : graph.neighborIterator(v));
                it.hasNext();) {
            int u = it.next();
            int j = graph.indexOf(u);
            if (!visited[j]) {
                queue.offer(new SearchNode(compIndex, u, current.level() + 1, orderNumber++, current));
                visited[j] = true;
            }
        }
        if (queue.isEmpty()) {
            //try and traverse to another connected component
            for (int i = restartIndex; i < numVertices; i++) {
                restartIndex++;
                if (!visited[i]) {
                    queue.offer(new SearchNode(++compIndex, graph.vertexAt(i), 0, orderNumber++, null));
                    visited[i] = true;
                    break;
                }
            }
        }
        return current;
    }

    /**
     * Returns the number of connected components identified so far by the
     * iterator.
     *
     * @return the number of connected components.
     */
    public int numComponents() {
        return compIndex;
    }

    /**
     * Returns the maximum level in the search tree, identified so far by the
     * iterator. The root of the search tree is considered at level 0.
     *
     * @return the maximum level in the search tree.
     */
    public int maxLevel() {
        return maxLevel;
    }
}
