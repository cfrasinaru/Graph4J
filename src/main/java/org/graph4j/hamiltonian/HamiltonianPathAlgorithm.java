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
import org.graph4j.util.Path;

/**
 * A <em>Hamiltonian path</em> is a path that contains all vertices of the
 * graph.
 *
 * @see Path
 * @author Cristian Frăsinaru
 */
public interface HamiltonianPathAlgorithm {

    /**
     *
     * @return a Hamiltonian path, or {@code  null} if the graph does not contain
     * one or the algorithm is unable to find it.
     */
    Path findPath();

    /**
     *
     * @param source the first vertex number of the path.
     * @param target the last vertex number of the path.
     * @return a Hamiltonian path starting in {@code source} and ending in
     * {@code path}, or {@code  null} if the graph does not contain one or the
     * algorithm is unable to find it.
     */
    Path findPath(int source, int target);

    /**
     *
     * @param graph the input graph.
     * @return the default implementation of the algorithm.
     */
    static HamiltonianPathAlgorithm getInstance(Graph graph) {
        throw new UnsupportedOperationException();
    }
}
