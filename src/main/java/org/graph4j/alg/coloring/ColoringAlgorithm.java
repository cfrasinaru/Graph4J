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
import org.graph4j.util.Clique;
import org.graph4j.util.Cycle;

/**
 *
 * @author Cristian Frăsinaru
 */
public interface ColoringAlgorithm {

    /**
     *
     * @return the graph to be colored.
     */
    Graph getGraph();

    /**
     *
     * @return a coloring of the graph.
     */
    Coloring findColoring();

    /**
     *
     * @param numColors maximum number of colors to be used.
     * @return a coloring of the graph with the specified number of colors, or
     * {@code null} if no coloring can be found by this algorithm.
     */
    Coloring findColoring(int numColors);

    /**
     *
     * @param coloring a vertex coloring.
     * @return {@code true} if the coloring is proper.
     */
    default boolean isValid(Coloring coloring) {
        return coloring.isProper();
    }

    /**
     *
     * @return a maximal clique of the graph to be colored.
     */
    default Clique getMaximalClique() {
        throw new IllegalArgumentException(
                "Computing a maximal clique is not supported by this implementation.");
    }

    /**
     * 
     * @return a long cycle of the graph to be colored.
     */
    default Cycle getLongCycle() {
        throw new IllegalArgumentException(
                "Computing a long cycle is not supported by this implementation.");
    }
    /**
     *
     * @return a lower bound of the coloring number.
     */
    default int getLowerBound() {
        return getMaximalClique().size();
    }

    /**
     * The heuristic coloring will be used as the starting point in searching
     * the optimum coloring and also to compute the upper bound of the coloring
     * number.
     *
     * @return an easy to compute heuristic coloring.
     */
    default Coloring getHeuristicColoring() {
        return new RecursiveLargestFirstColoring(getGraph()).findColoring();
    }

    /**
     *
     * @return {@code true} if the search for the optimum coloring should stop
     * when the current instance cannot be solved.
     */
    default boolean isStoppingOnFailure() {
        return true;
    }

    /**
     *
     * @return {@code true} for optimisation models.
     */
    default boolean isOptimalityEnsured() {
        return false;
    }

    /**
     *
     * @return {@code true} if, in case of disconnected graphs, the algorithm
     * should solve the connected components and then compose the coloring.
     */
    default boolean isSolvingComponents() {
        return true;
    }

    /**
     * Returns the default implementation of this interface.
     *
     * @param graph the input graph.
     * @return the default implementation of this interface.
     */
    static ColoringAlgorithm getInstance(Graph graph) {
        return new DSaturGreedyColoring(graph);
        //return new RecursiveLargestFirstColoring(graph);
    }

}
