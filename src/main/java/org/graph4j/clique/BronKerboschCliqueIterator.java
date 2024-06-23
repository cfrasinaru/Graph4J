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
 *
 * Iterates over all the maximal cliques of a graph.
 *
 * Implemented after: Etsuji Tomita, Akira Tanaka, Haruhisa Takahashi, "The
 * worst-case time complexity for generating all maximal cliques and
 * computational experiments".
 *
 * @author Cristian Frăsinaru
 */
public class BronKerboschCliqueIterator extends SimpleGraphAlgorithm
        implements MaximalCliqueIterator {

    private int[][] adjMatrix;
    private final Deque<Node> stack;
    private final Clique workingClique;
    private Clique currentClique;

    /**
     *
     * @param graph the input graph.
     */
    public BronKerboschCliqueIterator(Graph graph) {
        this(graph, false, false);
    }

    /**
     * Using the adjacency matrix allows for a slightly faster execution of the
     * algorithm, at the expense of using more memory.Not recommended for large
     * sparse graphs.
     *
     * @param graph the input graph.
     * @param shuffle if the vertices are shuffled before.
     * @param useAdjacencyMatrix {@code true} if the algorithm will compute and
     * use the adjacency matrix of the graph in order to test if two vertices
     * are adjacent.
     */
    public BronKerboschCliqueIterator(Graph graph, boolean shuffle, boolean useAdjacencyMatrix) {
        super(graph);
        if (useAdjacencyMatrix) {
            adjMatrix = graph.adjacencyMatrix();
        }
        //
        workingClique = new Clique(graph);
        stack = new ArrayDeque<>((int)graph.numEdges());
        //
        var set = new VertexSet(graph, graph.numVertices());
        int[] vertices = shuffle ? IntArrays.shuffle(graph.vertices()) : graph.vertices();
        for (int v : vertices) {
            set.add(v);
        }
        var subg = set;
        var cand = new VertexSet(set);
        stack.push(new Node(subg, cand, createExt(subg, cand)));
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
            var subg = node.subg;
            var cand = node.cand;
            var ext = node.ext;
            if (ext.isEmpty()) {
                stack.pop();
                if (!workingClique.isEmpty()) {
                    workingClique.pop();
                }
                continue;
            }
            int v = ext.pop();
            cand.remove(v);

            var neighbors = graph.neighbors(v);
            var newSubg = subg.intersection(neighbors);
            if (newSubg.isEmpty()) {
                
                currentClique = new Clique(workingClique);
                currentClique.add(v);
                assert currentClique.isValid();
                return true;
            }
            var newCand = cand.intersection(neighbors);
            if (newCand.isEmpty()) {
                continue;
            }
            var newExt = createExt(newSubg, newCand);
            if (newExt.isEmpty()) {
                continue;
            }
            workingClique.add(v);
            stack.push(new Node(newCand, newSubg, newExt));
        }
        return false;
    }

    //pivot is a vertex in subg that has maximum neighbors in cand
    private int choosePivot(VertexSet subg, VertexSet cand) {
        if (subg.size() == 1) {
            return subg.peek();
        }
        int pivot = -1, maxDeg = -1;
        for (int v : subg.vertices()) {
            int deg = countNeighbors(v, cand);
            if (maxDeg < deg) {
                maxDeg = deg;
                pivot = v;
            }
        }
        return pivot;
    }

    private int countNeighbors(int v, VertexSet set) {
        if (adjMatrix != null) {
            return countNeighborsUsingAdjMatrix(v, set);
        }
        int count = 0;
        for (int u : set.vertices()) {
            if (graph.containsEdge(v, u)) {
                count++;
            }
        }
        return count;
    }

    //slightly faster
    private int countNeighborsUsingAdjMatrix(int v, VertexSet set) {
        int count = 0;
        int vi = graph.indexOf(v);
        for (int u : set.vertices()) {
            int ui = graph.indexOf(u);
            if (adjMatrix[vi][ui] == 1) {
                count++;
            }
        }
        return count;
    }

    //ext = cand - neighbors(pivot)
    private VertexSet createExt(VertexSet subg, VertexSet cand) {
        if (cand.isEmpty()) {
            return cand;
        }
        var ext = new VertexSet(cand);
        int pivot = choosePivot(subg, cand);
        ext.removeAll(graph.neighbors(pivot));
        return ext;
    }

    //subg are the vertices of the subgraph where we look for a clique
    //cand are the vertices available to expand the working clique
    //ext = cand - neighbors(pivot)
    private class Node {

        final VertexSet subg;
        final VertexSet cand;
        final VertexSet ext;

        public Node(VertexSet subg, VertexSet cand, VertexSet ext) {
            this.cand = subg;
            this.subg = cand;
            this.ext = ext;
        }
    }
}
