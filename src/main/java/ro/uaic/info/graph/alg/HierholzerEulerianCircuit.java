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
package ro.uaic.info.graph.alg;

import ro.uaic.info.graph.Circuit;
import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.Trail;
import ro.uaic.info.graph.VertexSet;

/**
 * An <i>Eulerian circuit</i> is an Eulerian trail that has its endpoints
 * connected.
 *
 * @see Circuit
 * @see Trail
 * @author Cristian Frăsinaru
 */
public class HierholzerEulerianCircuit extends GraphAlgorithm {

    /**
     *
     * @param graph the input graph
     */
    public HierholzerEulerianCircuit(Graph graph) {
        super(graph);
    }

    public Circuit findCircuit() {
        if (!isEulerian()) {
            return null;
        }
        Circuit circuit = new Circuit(graph);
        var g = graph.copy();
        var candidates = new VertexSet(graph);
        candidates.add(g.vertexAt(0));
        while (!candidates.isEmpty()) {
            int start = candidates.getAndRemoveLast();
            if (g.isIsolated(start)) {
                continue;
            }
            Circuit loop = new Circuit(graph);
            int v = start;
            do {
                loop.add(v);
                var it = g.neighborIterator(v);
                int u = it.next();
                /*
                if (u == start && it.hasNext()) {
                    u = it.next();
                }*/
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
        //circuit.validate();
        return circuit;
    }

    public boolean isEulerian() {
        if (graph.isEmpty()) {
            return false;
        }
        for (int v : graph.vertices()) {
            if (graph.degree(v) % 2 != 0) {
                return false;
            }
        }
        if (directed) {
            if (!((Digraph) graph).isSymmetrical()) {
                return false;
            }
        }
        return true;
    }
}
