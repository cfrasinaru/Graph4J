/*
 * Copyright (C) 2023 Cristian Frăsinaru and contributors
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
package org.graph4j.clique;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import org.graph4j.Graph;
import org.graph4j.SimpleGraphAlgorithm;
import org.graph4j.util.Clique;
import org.graph4j.util.IntArrays;
import org.graph4j.util.VertexSet;

/**
 * Iterates over all cliques in a graph in a DFS manner. The cliques are ordered
 * lexicographically by their sequence of numbers.
 *
 * @author Cristian Frăsinaru
 */
public class DFSCliqueIterator extends SimpleGraphAlgorithm
        implements CliqueIterator {

    private final int minSize, maxSize;
    private final Deque<Node> stack;
    private Clique currentClique;

    public DFSCliqueIterator(Graph graph) {
        this(graph, 1, graph.numVertices());
    }

    /**
     *
     * @param graph the input graph.
     * @param minSize the minimum size of a clique.
     * @param maxSize the maximum size of a clique.
     */
    public DFSCliqueIterator(Graph graph, int minSize, int maxSize) {
        super(graph);
        this.minSize = minSize;
        this.maxSize = maxSize;
        stack = new ArrayDeque<>((int) graph.numEdges());
        stack.add(new Node(new Clique(graph),
                new VertexSet(graph, IntArrays.sortDesc(graph.vertices()))));
        //the candidates are sorted descending for polling them easily
    }

    //find the neighbors with higher numbers
    private VertexSet neighbors(int v, int[] cand) {
        var nbrs = new VertexSet(graph, cand.length);
        for (int u : cand) {
            if (u > v && graph.containsEdge(v, u)) {
                nbrs.add(u);
            }
        }
        return nbrs;
    }

    @Override
    public Clique next() {
        if (currentClique != null) {
            var temp = currentClique;
            currentClique = null;
            return temp;
        }
        if (hasNext()) {
            return currentClique;
        }
        throw new NoSuchElementException();
    }

    @Override
    public boolean hasNext() {
        if (currentClique != null) {
            return true;
        }
        while (!stack.isEmpty()) {
            var node = stack.peek();
            if (node.cand == null || node.cand.isEmpty()) {
                stack.pop();
                continue;
            }

            //make a new clique
            int v = node.cand.pop();
            var newClique = new Clique(node.clique);
            newClique.add(v);
            var newCand = newClique.size() == maxSize ? null : neighbors(v, node.cand.vertices());
            stack.push(new Node(newClique, newCand));

            if (newClique.size() >= minSize) {
                currentClique = newClique;
                assert currentClique.isValid();
                return true;
            }

        }
        return false;

    }

    private class Node {

        final Clique clique;
        final VertexSet cand;

        public Node(Clique clique, VertexSet cand) {
            this.clique = clique;
            this.cand = cand;
        }
    }
}
