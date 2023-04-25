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
 * Performs well on sparse graphs. Not so good on dense graphs, where
 * {@link PivotBronKerboschCliqueIterator} performs better.
 *
 * @author Cristian Frăsinaru
 */
@Deprecated
class OldBronKerboschCliqueIterator extends SimpleGraphAlgorithm
        implements MaximalCliqueIterator {

    private final Deque<VertexSet> candidatesStack;
    private final Deque<VertexSet> finishedStack;
    private final Clique workingClique;
    private Clique currentClique;

    public OldBronKerboschCliqueIterator(Graph graph) {
        super(graph);
        //
        int n = graph.numVertices();
        workingClique = new Clique(graph);
        candidatesStack = new ArrayDeque<>(n);
        finishedStack = new ArrayDeque<>(n);
        //
        candidatesStack.push(new VertexSet(graph, graph.vertices()));
        finishedStack.push(new VertexSet(graph));
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
        while (!candidatesStack.isEmpty()) {
            var candidates = candidatesStack.peek();
            var finished = finishedStack.peek();
            /*
            if (candidates.isEmpty()) {
                candidatesStack.pop();
                finishedStack.pop();
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
            }*/

            if (candidates.isEmpty()) {
                candidatesStack.pop();
                finishedStack.pop();
                if (!workingClique.isEmpty()) {
                    workingClique.pop();
                }
                continue;
            }

            int v = candidates.peek();
            var neighbors = graph.neighbors(v);
            var newCandidates = candidates.intersection(neighbors);
            var newFinished = finished.intersection(neighbors);

            if (newCandidates.isEmpty()) {
                candidates.pop();
                finished.add(v);
                if (newFinished.isEmpty()) {
                    //solution
                    currentClique = new Clique(workingClique);
                    currentClique.add(v);
                    assert currentClique.isValid();
                    return true;
                } else {
                    //dead-end
                    continue;
                }
            }

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
                candidatesStack.push(newCandidates);
                finishedStack.push(newFinished);
            }
            candidates.pop();
            finished.add(v);
        }
        return false;
    }

}
