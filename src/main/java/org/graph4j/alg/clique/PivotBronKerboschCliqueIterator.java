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
import java.util.Deque;
import java.util.NoSuchElementException;
import org.graph4j.Graph;
import org.graph4j.alg.SimpleGraphAlgorithm;
import org.graph4j.util.Clique;
import org.graph4j.util.VertexSet;

/**
 *
 * Iterates over all the maximal cliques of a graph.
 *
 * Performs well on dense graphs. On sparse graphs, it is slightly slower than
 * {@link BronKerboschCliqueIterator}.
 *
 * @author Cristian Frăsinaru
 */
public class PivotBronKerboschCliqueIterator extends SimpleGraphAlgorithm
        implements MaximalCliqueIterator {

    private final Deque<Node> stack;
    private final Clique workingClique;
    private Clique currentClique;

    public PivotBronKerboschCliqueIterator(Graph graph) {
        super(graph);
        //
        workingClique = new Clique(graph);
        stack = new ArrayDeque<>(graph.numVertices());
        var candidates = new VertexSet(graph, graph.vertices());
        var finished = new VertexSet(graph);
        stack.push(new Node(candidates, finished));
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
            var candidates = node.candidates;
            var reduced = node.reduced;
            var finished = node.finished;
            if (candidates.isEmpty()) {
                stack.pop();
                if (finished.isEmpty()) {
                    currentClique = new Clique(workingClique);
                    workingClique.pop();
                    assert currentClique.isValid();
                    return true;
                } else {
                    if (!workingClique.isEmpty()) {
                        workingClique.pop();
                    }
                }
                continue;
            }
            if (reduced.isEmpty()) {
                stack.pop();
                continue;
            }
            int v = reduced.peek();
            var neighbors = graph.neighbors(v);
            var newCandidates = candidates.intersection(neighbors);
            var newFinished = finished.intersection(neighbors);

            //if a finished node is connected to all candidates, cut this branch
            boolean connected = false;
            over:
            for (int f : newFinished.vertices()) {
                connected = true;
                for (int c : newCandidates.vertices()) {
                    if (!graph.containsEdge(f, c)) {
                        connected = false;
                        break;
                    }
                }
                if (connected) {
                    break;
                }
            }
            
            if (!connected) {
                workingClique.add(v);
                stack.push(new Node(newCandidates, newFinished));
            }
            reduced.pop();
            candidates.remove(v);
            finished.add(v);
        }
        return false;
    }

    private int choosePivot2(VertexSet candidates, VertexSet finished) {
        return candidates.isEmpty() ? -1 : candidates.peek();
    }

    //choose the vertex x in P U X with as many neighbors in the candidates set
    //the pivot=-1 when both candidates and finished are empty
    private int choosePivot(VertexSet candidates, VertexSet finished) {
        int pivot = -1, maxDeg = -1;
        //System.out.println("choosing pivot of: " + candidates + ", " + finished);
        for (int v : candidates.vertices()) {
            int deg = countNeighbors(v, candidates);
            if (maxDeg < deg) {
                maxDeg = deg;
                pivot = v;
            }
        }
        for (int v : finished.vertices()) {
            int deg = countNeighbors(v, candidates);
            if (maxDeg < deg) {
                maxDeg = deg;
                pivot = v;
            }
        }
        return pivot;
    }

    private int countNeighbors(int v, VertexSet set) {
        int count = 0;
        for (int u : set.vertices()) {
            if (graph.containsEdge(v, u)) {
                count++;
            }
        }
        return count;
    }

    private class Node {

        final VertexSet candidates;
        final VertexSet finished;
        VertexSet reduced; //candidates minus pivot neighbors

        public Node(VertexSet candidates, VertexSet finished) {
            this.candidates = candidates;
            this.finished = finished;
            //
            if (candidates.size() > 1) {
                int pivot = choosePivot(candidates, finished);
                if (pivot >= 0) {
                    reduced = new VertexSet(candidates);
                    reduced.removeAll(graph.neighbors(pivot));
                }
            } else {
                reduced = candidates;
            }
        }
    }
}
