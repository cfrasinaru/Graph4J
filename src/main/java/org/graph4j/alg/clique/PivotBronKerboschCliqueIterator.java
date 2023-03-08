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
import org.graph4j.util.IntArrays;
import org.graph4j.util.VertexSet;

/**
 *
 *
 * @author Cristian Frăsinaru
 */
public class PivotBronKerboschCliqueIterator extends SimpleGraphAlgorithm
        implements MaximalCliqueIterator {

    private final Deque<Clique> cliqueStack;
    private final Deque<VertexSet> candidatesStack;
    private final Deque<VertexSet> excludedStack;
    private Clique currentClique;

    public PivotBronKerboschCliqueIterator(Graph graph) {
        super(graph);
        //
        int n = graph.numVertices();
        cliqueStack = new ArrayDeque<>(n);
        candidatesStack = new ArrayDeque<>(n);
        excludedStack = new ArrayDeque<>(n);
        //
        cliqueStack.push(new Clique(graph));
        candidatesStack.push(new VertexSet(graph, graph.vertices()));
        excludedStack.push(new VertexSet(graph));
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
        if (cliqueStack.isEmpty()) {
            return false;
        }
        while (true) {
            var clique = cliqueStack.pop();
            var candidates = candidatesStack.pop();
            var excluded = excludedStack.pop();

            if (candidates.isEmpty() && excluded.isEmpty()) {
                currentClique = clique;
                assert currentClique.isValid();
                return true;
            }

            int pivot = choosePivot(candidates, excluded);
            var pivotNeighbors = graph.neighbors(pivot);
            VertexSet reducedCandidates = new VertexSet(candidates);
            reducedCandidates.removeAll(pivotNeighbors);
            for (int v : reducedCandidates.vertices()) {
                var neighbors = graph.neighbors(v);
                cliqueStack.push(clique.union(v));
                candidatesStack.push(candidates.intersection(neighbors));
                excludedStack.push(excluded.intersection(neighbors));
                candidates.remove(v);
                excluded.add(v);
            }
        }
    }

    private int choosePivot(VertexSet candidates, VertexSet excluded) {
        int pivot = -1, maxDeg = -1;
        for (int v : candidates.vertices()) {
            int deg = graph.degree(v);
            if (maxDeg < deg) {
                maxDeg = deg;
                pivot = v;
            }
        }
        for (int v : excluded.vertices()) {
            int deg = graph.degree(v);
            if (maxDeg < deg) {
                maxDeg = deg;
                pivot = v;
            }
        }
        return pivot;
    }

}
