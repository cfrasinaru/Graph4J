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
package org.graph4j.alg.cut;

import org.graph4j.Graph;

/**
 *
 * @author Cristian Frăsinaru
 */
public interface VertexSeparatorAlgorithm {

    /**
     * Returns the input graph.
     *
     * @return the input graph.
     */
    Graph getGraph();

    /**
     * Returns a vertex separator separator.
     *
     * @return a vertex separator set.
     */
    VertexSeparator getSeparator();

    /**
     * Returns the default implementation of this interface.
     *
     * @param graph the input graph.
     * @return the default implementation of this interface.
     */
    static VertexSeparatorAlgorithm getInstance(Graph graph) {
        return new GreedyVertexSeparator(graph);
    }
}
