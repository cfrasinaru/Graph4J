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

import java.util.LinkedList;
import java.util.Queue;
import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.util.CheckArguments;

/**
 *
 * @author Cristian Frăsinaru
 */
public class BreadthFirstSearch {

    private final Graph graph;
    private final boolean directed;
    private BFSVisitor visitor;
    //
    private Queue<SearchNode> queue;
    private int orderIndex;
    private int compIndex;
    private SearchNode visited[];
    private int restartIndex; //all the vertices up to restartIndex are visited
    private boolean interrupted;

    /**
     *
     * @param graph
     */
    public BreadthFirstSearch(Graph graph) {
        CheckArguments.graphNotNull(graph);
        this.graph = graph;
        this.directed = (graph instanceof Digraph);
    }

    private void init() {
        int n = graph.numVertices();
        this.visited = new SearchNode[n];
        this.queue = new LinkedList<>();
        orderIndex = 0;
        compIndex = 0;
        interrupted = false;
    }

    /**
     *
     * @param visitor
     */
    public void traverse(BFSVisitor visitor) {
        if (graph.isEmpty()) {
            return;
        }
        traverse(visitor, graph.vertexAt(0));
    }

    /**
     *
     * @param visitor
     * @param start
     */
    public void traverse(BFSVisitor visitor, int start) {
        CheckArguments.graphContainsVertex(graph, start);
        if (visitor == null) {
            throw new IllegalArgumentException("The visitor cannot be null");
        }
        init();
        this.visitor = visitor;
        try {
            var node = new SearchNode(compIndex, start, 0, orderIndex++, null);
            visited[graph.indexOf(start)] = node;
            queue.add(node);
            visitor.root(node);
            //start traversing the first component, with the initial vertex
            bfs();
            for (int i = restartIndex, n = graph.numVertices(); i < n; i++) {
                restartIndex++;
                if (visited[i] == null) {
                    //start traversing another connected component
                    compIndex++;
                    node = new SearchNode(compIndex, graph.vertexAt(i), 0, orderIndex++, null);
                    visited[i] = node;
                    queue.add(node);
                    visitor.root(node);
                    bfs();
                }
            }
        } catch (InterruptedVisitorException e) {
            interrupted = true;
        }
    }

    private void bfs() {
        while (!queue.isEmpty()) {
            var node = queue.poll();
            var parent = node.parent();
            int v = node.vertex();
            for (int u : graph.neighbors(v)) {
                int ui = graph.indexOf(u);
                if (visited[ui] == null) {
                    var child = new SearchNode(compIndex, u, node.level() + 1, orderIndex++, node);
                    visited[ui] = child;
                    queue.add(child);
                    visitor.treeEdge(node, child);
                } else {
                    //back edge or cross edge
                    var other = visited[ui]; //already visited
                    if (other == node) {
                        visitor.backEdge(node, other);
                    } else if (other.equals(parent)) {
                        if (directed) {
                            visitor.backEdge(node, other);
                        }
                    } else {
                        if (other.isAncestorOf(node)) {
                            visitor.backEdge(node, other);
                        } else {
                            if (!directed || !node.isAncestorOf(other)) {
                                visitor.crossEdge(node, other);
                            }
                            //no forward edges, ignore in case of multigraphs
                        }
                    }
                }
            }
        }
    }

    public boolean isInterrupted() {
        return interrupted;
    }
}
