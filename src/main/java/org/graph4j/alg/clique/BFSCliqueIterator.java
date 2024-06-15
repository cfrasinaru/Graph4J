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
package org.graph4j.alg.clique;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;
import org.graph4j.Graph;
import org.graph4j.alg.SimpleGraphAlgorithm;
import org.graph4j.util.Clique;
import org.graph4j.util.IntArrays;
import org.graph4j.util.VertexSet;

/**
 * Iterates over all cliques in a graph in a BFS manner. The cliques are ordered
 * first by their size and second by their number.
 *
 * @author Cristian Frăsinaru
 */
public class BFSCliqueIterator extends SimpleGraphAlgorithm
        implements CliqueIterator {

    private final int minSize, maxSize;
    private final Queue<Node> queue;
    private Clique currentClique;

    public BFSCliqueIterator(Graph graph) {
        this(graph, 1, graph.numVertices());
    }

    /**
     *
     * @param graph the input graph.
     * @param minSize the minimum size of a clique.
     * @param maxSize the maximum size of a clique.
     */
    public BFSCliqueIterator(Graph graph, int minSize, int maxSize) {
        super(graph);
        this.minSize = minSize;
        this.maxSize = maxSize;
        queue = new ArrayDeque<>((int) graph.numEdges());

        for (int v : IntArrays.sort(graph.vertices())) {
            var nbrs = neighbors(v, IntArrays.sort(graph.neighbors(v)));
            queue.add(new Node(new Clique(graph, new int[]{v}), nbrs));
        }
    }

    //find the neighbors of v that are also in cand
    //only those with numbers greater than v
    private VertexSet neighbors(int v, int[] cand) {
        var set = new VertexSet(graph, cand.length);
        for (int u : cand) {
            if (u > v && graph.containsEdge(v, u)) {
                set.add(u);
            }
        }
        return set;
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
        while (!queue.isEmpty()) {
            var node = queue.poll();

            if (node.clique.size() >= minSize) {
                currentClique = node.clique;
                assert currentClique.isValid();
            }

            //make new cliques
            if (node.cand != null) {

                for (int v : node.cand.vertices()) {
                    var newClique = new Clique(node.clique);
                    newClique.add(v);
                    var newCand = newClique.size() == maxSize ? null : neighbors(v, node.cand.vertices());
                    queue.add(new Node(newClique, newCand));
                }
            }

            if (currentClique != null) {
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
