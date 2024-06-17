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
import org.graph4j.GraphAlgorithm;

/**
 * 
 * @author Cristian Frăsinaru
 */
public abstract class EulerianCircuitAlgorithmBase
        extends GraphAlgorithm implements EulerianCircuitAlgorithm {
   
    /**
     *
     * @param graph the input graph
     */
    public EulerianCircuitAlgorithmBase(Graph graph) {
        super(graph);
    }

    @Override
    public boolean isEulerian() {
        if (graph.isEmpty()) {
            return false;
        }
        if (!directed) {
            for (int v : graph.vertices()) {
                if (graph.degree(v) % 2 != 0) {
                    return false;
                }
            }
        } else {
            Digraph d = (Digraph) graph;
            for (int v : d.vertices()) {
                if (d.indegree(v) != d.outdegree(v)) {
                    return false;
                }
            }
        }
        return true;
    }
}
