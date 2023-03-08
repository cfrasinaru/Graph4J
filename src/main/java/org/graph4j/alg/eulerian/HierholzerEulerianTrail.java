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
package org.graph4j.alg.eulerian;

import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.alg.GraphAlgorithm;
import org.graph4j.util.Trail;

/**
 * An <i>Eulerian trail</i> is a trail visits every edge exactly once.
 *
 * @see Trail
 * @see HierholzerEulerianCircuit
 * @author Cristian Frăsinaru
 */
public class HierholzerEulerianTrail extends GraphAlgorithm {

    private int first, last;

    /**
     *
     * @param graph the input graph
     */
    public HierholzerEulerianTrail(Graph graph) {
        super(graph);
    }

    /**
     *
     * @return an Eulerian trail, or {@code  null} if the graph has no Eulerian
     * trail.
     */
    public Trail findTrail() {
        if (!hasEulerianTrail()) {
            return null;
        }
        var g = graph;
        if (first >= 0 && last >= 0) {
            g = graph.copy();
            g.addEdge(last, first);
        }
        var circuit = new HierholzerEulerianCircuit(g).findCircuit();
        int pos = first < 0 ? 0 : circuit.indexOf(first);
        var trail = new Trail(graph);
        for (int i = 0, n = circuit.length(); i < n; i++) {
            trail.add(circuit.get((pos + i) % n));
        }
        assert trail.isValid();
        return trail;
    }

    /**
     *
     * @return {@code true} if the graph has an Eulerian trail
     */
    public boolean hasEulerianTrail() {
        first = -1;
        last = -1;
        if (graph.isEmpty()) {
            return false;
        }
        if (!directed) {
            int count = 0;
            for (int v : graph.vertices()) {
                if (graph.degree(v) % 2 != 0) {
                    count++;
                    if (count > 2) {
                        return false;
                    }
                    if (first < 0) {
                        first = v;
                    } else {
                        last = v;
                    }
                }
            }
        } else {
            Digraph d = (Digraph) graph;
            int count = 0;
            for (int v : d.vertices()) {
                int indeg = d.indegree(v);
                int outdeg = d.outdegree(v);
                if (indeg != outdeg) {
                    count++;
                    if (count > 2) {
                        return false;
                    }
                    if (outdeg > indeg) {
                        first = v;
                    } else {
                        last = v;
                    }
                }
            }
        }
        return true;
    }
}
