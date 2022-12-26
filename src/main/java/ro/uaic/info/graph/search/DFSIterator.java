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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.util.CheckArguments;

/**
 *
 * @author Cristian Frăsinaru
 */
public class DFSIterator implements Iterator<SearchNode> {

    private final Graph graph;
    private final int startVertex; //vertex
    private int numVertices;
    private Deque<SearchNode> stack;
    private boolean visited[];
    private int nextPos[]; //used to iterate through adjancency lists
    private int restartIndex; //all the vertices up to restartIndex are visited
    private int numIterations; //current number of iterations
    private int component; //index of current connected component
    private int order; //current order number

    /**
     *
     * @param graph
     */
    public DFSIterator(Graph graph) {
        this.graph = graph;
        this.startVertex = graph.isEmpty() ? -1 : graph.vertexAt(0);
        init();
    }

    /**
     *
     * @param graph
     * @param start
     */
    public DFSIterator(Graph graph, int start) {
        CheckArguments.graphContainsVertex(graph, start);
        this.graph = graph;
        this.startVertex = start;
        init();
    }

    private void init() {
        this.numVertices = graph.numVertices();
        this.visited = new boolean[numVertices];
        this.nextPos = new int[numVertices];
        this.stack = new ArrayDeque<>(numVertices);
        if (startVertex >= 0) {
            stack.push(new SearchNode(0, startVertex, 0, order++, null));
            visited[graph.indexOf(startVertex)] = true;
        }
    }

    @Override
    public boolean hasNext() {
        return numIterations < numVertices;
    }

    @Override
    public SearchNode next() {
        if (stack.isEmpty()) {
            throw new NoSuchElementException();
        }
        var current = stack.peek();
        numIterations++;
        //
        boolean ok = false;
        while (!ok && !stack.isEmpty()) {
            var node = stack.peek();
            int v = node.vertex();
            int i = graph.indexOf(v);
            int[] neighbors = graph.neighbors(v);
            while (!ok && nextPos[i] < neighbors.length) {
                int u = neighbors[nextPos[i]];
                int j = graph.indexOf(u);
                nextPos[i]++;
                if (!visited[j]) {
                    //prepare the next one
                    var next = new SearchNode(component, u, node.level() + 1, order++, current);
                    stack.push(next);
                    visited[j] = true;
                    ok = true;
                }
            }
            if (!ok) {
                stack.pop();
            }
        }
        if (!ok) {
            //find another connected component
            for (int i = restartIndex; i < numVertices; i++) {
                restartIndex++;
                if (!visited[i]) {
                    stack.push(new SearchNode(++component, graph.vertexAt(i), 0, order++, null));
                    visited[i] = true;
                    break;
                }
            }
        }
        return current;
    }

}
