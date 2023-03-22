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
package org.graph4j.alg.coloring;

import org.graph4j.Graph;

/**
 *
 * @author Cristian Frăsinaru
 */
public interface VertexColoringAlgorithm {

    /**
     *
     * @return a coloring of the graph.
     */
    public VertexColoring findColoring();

    /**
     *
     * @param numColors maximum number of colors to be used.
     * @return a coloring of the graph with the specified number of colors, or
     * {@code null} if no coloring can be found by this algorithm.
     */
    public VertexColoring findColoring(int numColors);

    /**
     * Returns the default implementation of this interface.
     *
     * @param graph the input graph.
     * @return the default implementation of this interface.
     */
    static VertexColoringAlgorithm getInstance(Graph graph) {
        return new GreedyColoring(graph);
    }

}
