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
package org.graph4j.coloring.eq;

import org.graph4j.Graph;
import org.graph4j.alg.coloring.Coloring;
import org.graph4j.alg.coloring.ColoringAlgorithm;
import org.graph4j.alg.coloring.RecursiveLargestFirstColoring;

/**
 *
 * @author Cristian Frăsinaru
 */
public interface EquitableColoringAlgorithm extends ColoringAlgorithm {

    /**
     *
     * @param coloring a vertex coloring.
     * @return {@code true} if the coloring is proper and equitable.
     */
    @Override
    default boolean isValid(Coloring coloring) {
        return coloring.isProper() && coloring.isEquitable();
    }

    @Override
    default Coloring getHeuristicColoring() {
        var graph = getGraph();
        var col = new RecursiveLargestFirstColoring(graph).findColoring();
        return new GreedyEquitableColoring(graph, col).findColoring();
    }

    @Override
    default boolean isStoppingOnFailure() {
        return false;
    }

    @Override
    default boolean isSolvingComponents() {
        return false;
    }

    /**
     * Returns the default implementation of this interface.
     *
     * @param graph the input graph.
     * @return the default implementation of this interface.
     */
    static ColoringAlgorithm getInstance(Graph graph) {
        return new BacktrackEquitableColoring(graph);
    }
}
