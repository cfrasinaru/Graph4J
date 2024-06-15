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
import org.graph4j.util.Cycle;

/**
 * A <em>Hamiltonian (or spanning) cycle</em> is a cycle that contains all
 * vertices of the graph.
 *
 * @see Cycle
 * @author Cristian Frăsinaru
 */
public interface HamiltonianCycleAlgorithm {

    /**
     *
     * @return an Hamiltonian cycle, or {@code  null} if the graph does not
     * contain one or the algorithm is unable to find it.
     */
    Cycle findCycle();

    /**
     *
     * @param graph the input graph.
     * @return the default implementation of the algorithm.
     */
    static HamiltonianCycleAlgorithm getInstance(Graph graph) {
        throw new UnsupportedOperationException();
    }
}
