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
package ro.uaic.info.graph.alg.eulerian;

import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.model.Circuit;
import ro.uaic.info.graph.model.Trail;

/**
 * An <em>Eulerian circuit</em> is an Eulerian trail (a trail that contains all
 * the edges of the graph) that has its endpoints connected.
 *
 * A connected graph is Eulerian if and only if the degree of each vertex is
 * even.
 *
 * A connected digraph is Eulerian if and only if the in-degree of each vertex
 * equals the out-degree of each vertex.
 *
 * @see Circuit
 * @see Trail
 * @author Cristian Frăsinaru
 */
public interface EulerianCircuitAlgorithm {

    /**
     *
     * @return an Eulerian circuit, or {@code  null} if the graph is not
     * Eulerian.
     */
    Circuit findCircuit();

    /**
     *
     * @return {@code true} if the graph is Eulerian
     */
    boolean isEulerian();

    /**
     *
     * @param graph the input graph.
     * @return the default implementation of the algorithm.
     */
    static EulerianCircuitAlgorithm getInstance(Graph graph) {
        return new HierholzerEulerianCircuit(graph);
    }
}
