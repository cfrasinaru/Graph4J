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
package org.graph4j.hamiltonian;

import org.graph4j.Graph;
import org.graph4j.alg.SimpleGraphAlgorithm;
import org.graph4j.util.Cycle;
import org.graph4j.util.IntArrays;

/**
 * The algorithm finds a Hamiltonian cycle in an undirected simple graph that
 * satisfies Ore's condition: <code>deg(v) + deg(u) &gt;= |V(G)|</code>, for
 * every pair of distinct non-adjacent vertices v and u.
 *
 * The Ore's condition is not tested explicitly - if the algorithm fails to
 * identify a hamiltonian cycle, using Palmer's alorithm, it returns
 * {@code null}.
 *
 *
 * Reference: Palmer, E. M. (1997), "The hidden algorithm of Ore's theorem on
 * Hamiltonian cycles".
 *
 * @author Cristian Frăsinaru
 */
public class PalmerHamiltonianCycle extends SimpleGraphAlgorithm
        implements HamiltonianCycleAlgorithm {

    public PalmerHamiltonianCycle(Graph graph) {
        super(graph);
    }

    @Override
    public Cycle findCycle() {
        int n = graph.numVertices();
        int vertices[] = IntArrays.copyOf(graph.vertices());
        boolean hasGaps;
        do {
            hasGaps = false;
            for (int i = 0; i < n; i++) {
                int v1 = vertices[i];
                int v2 = vertices[(i + 1) % n];
                if (graph.containsEdge(v1, v2)) {
                    continue;
                }
                hasGaps = true;
                //find crossing
                boolean crossingFound = false;
                for (int j = i + 2; j < i + n; j++) {
                    int w1 = vertices[j % n];
                    int w2 = vertices[(j + 1) % n];
                    if (graph.containsEdge(v1, w1) && graph.containsEdge(v2, w2)) {
                        int a, b;
                        if (i + 1 < j % n) {
                            //...v1[v2...w1]w2... becomes
                            //...v1[w1...v2]w2...
                            a = i + 1; //v2
                            b = j % n; //w1
                        } else {
                            //...w1[w2...v1]v2... becomes
                            //...w1[v1...w2]v2...
                            a = (j + 1) % n; //w2
                            b = i; //v1
                        }
                        for (int k = 0; k < (b - a + 1) / 2; k++) {
                            int aux = vertices[a + k];
                            vertices[a + k] = vertices[b - k];
                            vertices[b - k] = aux;
                        }
                        crossingFound = true;
                        break;
                    }
                }
                if (!crossingFound) {
                    //graph doesn't have Ore's property
                    return null;
                }
            }
        } while (hasGaps);
        Cycle cycle = new Cycle(graph, vertices);
        assert cycle.isValid() && cycle.length() == n;
        return cycle;
    }

}
