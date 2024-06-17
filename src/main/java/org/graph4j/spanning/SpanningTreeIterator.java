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
package org.graph4j.spanning;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.graph4j.Edge;
import org.graph4j.Graph;
import org.graph4j.util.UnionFind;

/**
 * Iterates over all spanning trees of a graph.
 *
 * The iterator returns the collection of edges of a spanning tree, which can be
 * used to create the actual tree with the method
 * {@link Graph#subgraph(java.util.Collection)}.
 *
 * This iterator should be used when the spanning trees are required without
 * taking into account the weight of the edges. If the spanning trees are
 * supposed to be returned in order by their weight, see
 * {@link WeightedSpanningTreeIterator}.
 *
 * @see WeightedSpanningTreeIterator
 * @author Cristian Frăsinaru
 */
public class SpanningTreeIterator implements Iterator<Collection<Edge>> {

    private final Graph graph;
    private final Edge[] edges;
    private Deque<Edge> workTree;
    private Deque<Edge> nextTree;
    private int currentPos;
    private Deque<Node> stack;
    private UnionFindExt unionFind;
    private boolean finished;

    /**
     * Creates an iterator over the spanning trees of a graph.
     *
     * @param graph the input graph.
     */
    public SpanningTreeIterator(Graph graph) {
        Objects.requireNonNull(graph);
        this.graph = graph;
        edges = graph.edges();
        init();
    }

    /**
     * Creates an iterator over the spanning trees of a graph, using a specified
     * comparator in order to sort the edges. The order in which the the trees
     * are created depends on the specified comparator.
     *
     * @param graph the input graph.
     * @param comparator a comparator that should be used for sorting the edges
     * of the graph, before creating the spanning trees.
     */
    /*
    public SpanningTreeIterator(Graph graph, Comparator<Edge> comparator) {
        Objects.requireNonNull(graph);
        this.graph = graph;
        edges = graph.edges();
        if (comparator != null) {
            Arrays.sort(edges, comparator);
        }
        init();
    }*/
    private void init() {
        int n = graph.numVertices();
        workTree = new ArrayDeque<>(n - 1);
        stack = new ArrayDeque<>(n - 1);
        unionFind = new UnionFindExt(n);
    }

    @Override
    public boolean hasNext() {
        if (finished) {
            return false;
        }
        if (nextTree != null) {
            return true;
        }
        createNextTree();
        nextTree = workTree;
        return workTree != null;
    }

    @Override
    public Collection<Edge> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        nextTree = null;
        return workTree;
    }

    private void createNextTree() {
        if (stack.isEmpty()) {
            currentPos = 0;
        } else {
            removeCurrentEdge();
        }
        int n = graph.numVertices();
        int m = (int) graph.numEdges();
        while (true) {
            while (m - currentPos + 1 > n - 1 - workTree.size()) {
                Edge e = edges[currentPos];
                int root1 = unionFind.find(graph.indexOf(e.source()));
                int root2 = unionFind.find(graph.indexOf(e.target()));
                if (root1 != root2) {
                    workTree.push(e);
                    stack.push(new Node(currentPos, root1, root2, unionFind.getParent(root1), unionFind.getParent(root2)));
                    unionFind.union(root1, root2);
                    if (workTree.size() == n - 1) {
                        return;
                    }
                }
                currentPos++;
            }
            if (stack.isEmpty()) {
                break;
            }
            removeCurrentEdge();
        }
        workTree = null;
        finished = true;
    }

    private void removeCurrentEdge() {
        var node = stack.pop();
        workTree.pop();
        unionFind.setParent(node.root1, node.parent1);
        unionFind.setParent(node.root2, node.parent2);
        currentPos = node.currentPos + 1;
    }

    private class Node {

        int currentPos;
        int root1, root2, parent1, parent2;

        public Node(int currentPos, int root1, int root2, int parent1, int parent2) {
            this.currentPos = currentPos;
            this.root1 = root1;
            this.root2 = root2;
            this.parent1 = parent1;
            this.parent2 = parent2;
        }
    }

    private class UnionFindExt extends UnionFind {

        public UnionFindExt(int numVertices) {
            super(numVertices, false);
        }

        @Override
        public void setParent(int vi, int parentId) {
            super.setParent(vi, parentId);
        }
    }

}
