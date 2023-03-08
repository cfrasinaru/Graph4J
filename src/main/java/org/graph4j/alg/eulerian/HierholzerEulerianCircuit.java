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

import org.graph4j.util.Circuit;
import org.graph4j.Graph;
import org.graph4j.util.VertexSet;

/**
 *
 * @author Cristian Frăsinaru
 */
public class HierholzerEulerianCircuit extends EulerianCircuitAlgorithmBase {

    public HierholzerEulerianCircuit(Graph graph) {
        super(graph);
    }

    @Override
    public Circuit findCircuit() {
        if (!isEulerian()) {
            return null;
        }
        Circuit circuit = new Circuit(graph);
        var g = graph.copy();
        var candidates = new VertexSet(graph);
        candidates.add(g.vertexAt(0));
        while (!candidates.isEmpty()) {
            int start = candidates.pop();
            if (g.isIsolated(start)) {
                continue;
            }
            Circuit loop = new Circuit(graph);
            int v = start;
            do {
                loop.add(v);
                var it = g.neighborIterator(v);                
                int u = it.next();
                it.removeEdge();
                if (u != start && g.degree(u) > 1) {
                    candidates.add(u);
                }
                v = u;
            } while (v != start || g.degree(start) > 0);
            circuit = circuit.join(loop);
        }
        if (g.numEdges() > 0) {
            return null;
        }
        assert circuit.isValid();
        return circuit;
    }
}
