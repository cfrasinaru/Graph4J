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
package org.graph4j.traverse;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import org.graph4j.Graph;
import org.graph4j.util.CheckArguments;

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
    private int maxLevel = -1;

    /**
     *
     * @param graph the input graph.
     */
    public BFSIterator(Graph graph) {
        this.graph = graph;
        this.startVertex = graph.isEmpty() ? -1 : graph.vertexAt(0);
        init();
    }

    /**
     *
     * @param graph the input graph.
     * @param start the start vertex number.
     */
    public BFSIterator(Graph graph, int start) {
        CheckArguments.graphContainsVertex(graph, start);
        this.graph = graph;
        this.startVertex = start;
        init();
    }

    private void init() {
        this.numVertices = graph.numVertices();
        this.visited = new boolean[numVertices];
        this.queue = new LinkedList<>();
        if (startVertex >= 0) {
            queue.offer(new SearchNode(0, startVertex, 0, orderNumber++, null));
            visited[graph.indexOf(startVertex)] = true;
        }
    }

    @Override
    public boolean hasNext() {
        return numIterations < numVertices;
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
        //
        for (var it = graph.neighborIterator(v); it.hasNext();) {
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
     *
     * @return the number of connected components identified so far by the
     * iterator
     */
    public int numComponents() {
        return compIndex;
    }

    /**
     *
     * @return the maximum level in the search tree, root is at level 0.
     */
    public int maxLevel() {
        return maxLevel;
    }
}
