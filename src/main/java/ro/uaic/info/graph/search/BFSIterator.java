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
package ro.uaic.info.graph.search;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.InvalidVertexException;

/**
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
    private int component;
    private int orderNumber;

    /**
     *
     * @param graph
     */
    public BFSIterator(Graph graph) {
        this.graph = graph;
        this.startVertex = graph.isEmpty() ? -1 : graph.vertexAt(0);
        init();
    }

    /**
     *
     * @param graph
     * @param start
     */
    public BFSIterator(Graph graph, int start) {
        this.graph = graph;
        this.startVertex = start;
        if (!graph.containsVertex(start)) {
            throw new InvalidVertexException(start);
        }
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
            throw new IllegalStateException();
        }
        var current = queue.poll();
        var v = current.vertex();
        numIterations++;
        //
        for (int u : graph.neighbors(v)) {
            int j = graph.indexOf(u);
            if (!visited[j]) {
                queue.offer(new SearchNode(component, u, current.level() + 1, orderNumber++, current));
                visited[j] = true;
            }
        }
        if (queue.isEmpty()) {
            //try and traverse to another connected component
            for (int i = restartIndex; i < numVertices; i++) {
                restartIndex++;
                if (!visited[i]) {
                    queue.offer(new SearchNode(++component, graph.vertexAt(i), 0, orderNumber++, null));
                    visited[i] = true;
                    break;
                }
            }
        }
        return current;
    }

}
