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

import org.graph4j.Graph;
import org.graph4j.alg.SimpleGraphAlgorithm;
import org.graph4j.util.VertexSet;

/**
 * Recursive implementation.
 *
 * @author Cristian Frăsinaru
 */
@Deprecated
class BronKerboschCliqueFinder extends SimpleGraphAlgorithm {

    private int count;

    public BronKerboschCliqueFinder(Graph graph) {
        super(graph);
    }

    public int compute() {
        var clique = new VertexSet(graph);
        var candidates = new VertexSet(graph, graph.vertices());
        var excluded = new VertexSet(graph);
        compute(clique, candidates, excluded);
        return count;
    }

    private void compute(VertexSet clique, VertexSet candidates, VertexSet excluded) {
        if (candidates.isEmpty() && excluded.isEmpty()) {
            count++;
            return;
        }
        while (!candidates.isEmpty()) {
            int v = candidates.peek();
            var neighbors = graph.neighbors(v);

            var newClique = clique.union(v);
            var newCandidates = candidates.intersection(neighbors);
            var newExcluded = excluded.intersection(neighbors);

            compute(newClique, newCandidates, newExcluded);

            candidates.pop();
            excluded.add(v);
        }
    }

}
