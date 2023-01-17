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
import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.Multigraph;
import ro.uaic.info.graph.util.CheckArguments;

/**
 *
 * @author Cristian Frăsinaru
 */
public class DepthFirstSearch {

    private final Graph graph;
    private final boolean directed;
    private DFSVisitor visitor;
    //
    private int orderIndex;
    private int compIndex;
    private SearchNode visited[];
    private int nextPos[]; //used to iterate through adjancency lists    
    private int restartIndex;
    private Deque<SearchNode> stack;
    private boolean[] instack;
    private boolean interrupted;

    /**
     *
     * @param graph
     */
    public DepthFirstSearch(Graph graph) {
        if (graph instanceof Multigraph) {
            throw new IllegalArgumentException("DFS is not supported for multigraphs");
        }
        this.graph = graph;
        this.directed = (graph instanceof Digraph);
    }

    private void init() {
        int n = graph.numVertices();
        this.visited = new SearchNode[n];
        this.nextPos = new int[n];
        this.stack = new ArrayDeque<>(n);
        this.instack = new boolean[n];
        orderIndex = 0;
        compIndex = 0;
        interrupted = false;
    }

    /**
     *
     * @param visitor
     */
    public void traverse(DFSVisitor visitor) {
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
    public void traverse(DFSVisitor visitor, int start) {
        CheckArguments.graphContainsVertex(graph, start);
        if (visitor == null) {
            throw new IllegalArgumentException("The visitor cannot be null");
        }
        init();
        this.visitor = visitor;
        try {
            var node = new SearchNode(compIndex, start, 0, orderIndex++, null);
            visited[graph.indexOf(start)] = node;
            stack.push(node);
            instack[graph.indexOf(start)] = true;
            visitor.startVertex(node);
            //start traversing the first component, with the initial vertex
            dfs();
            for (int i = restartIndex, n = graph.numVertices(); i < n; i++) {
                restartIndex++;
                if (visited[i] == null) {
                    //start traversing another connected component
                    compIndex++;
                    node = new SearchNode(compIndex, graph.vertexAt(i), 0, orderIndex++, null);
                    visited[i] = node;
                    stack.push(node);
                    instack[i] = true;
                    visitor.startVertex(node);
                    dfs();
                }
            }
        } catch (InterruptedVisitorException e) {
            interrupted = true;
        }
        visited = null;
        nextPos = null;
        stack = null;
    }

    private void dfs() {
        while (!stack.isEmpty()) {
            var node = stack.peek();
            var parent = node.parent();
            int v = node.vertex();
            int vi = graph.indexOf(v);
            int[] neighbors = graph.neighbors(v);
            boolean ok = false;
            while (!ok && nextPos[vi] < neighbors.length) {
                int u = neighbors[nextPos[vi]];
                int ui = graph.indexOf(u);
                nextPos[vi]++;
                if (visited[ui] == null) {
                    var next = new SearchNode(compIndex, u, node.level() + 1, orderIndex++, node);
                    stack.push(next);
                    instack[ui] = true;
                    visited[ui] = next;
                    visitor.treeEdge(node, next);
                    visitor.startVertex(next);
                    ok = true;
                    break;
                }
                //back edge, forward edge or cross edge
                var other = visited[ui]; //already visited
                if (other.equals(parent)) {
                    //if (directed || (multigraph && ((Multigraph) graph).multiplicity(v, u) > 1)) {
                    if (directed) {
                        visitor.backEdge(node, other);
                    }
                } else {
                    if (!directed) {
                        visitor.backEdge(node, other);
                    } else {
                        if (instack[ui] && other.order() < node.order()) {
                            //other.isAncestorOf(node)
                            visitor.backEdge(node, other);
                        } else {
                            if (instack[vi] && node.order() < other.order()) {
                                //node.isAncestorOf(other)
                                visitor.forwardEdge(node, other);
                            } else {
                                visitor.crossEdge(node, other);
                            }
                        }
                    }
                }
            }//while
            if (!ok) {
                stack.pop();
                instack[vi] = false;
                visitor.finishVertex(node);
                if (parent != null) {
                    visitor.upward(node, parent);
                }

            }
        }
    }

    /**
     *
     * @return the number of connected components identified by the traversal
     */
    public int numComponents() {
        return compIndex;
    }

    /**
     *
     * @return
     */
    public boolean isInterrupted() {
        return interrupted;
    }
}
