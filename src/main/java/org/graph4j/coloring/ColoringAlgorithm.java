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
package org.graph4j.coloring;

import org.graph4j.Graph;
import org.graph4j.util.Clique;

/**
 *
 * @author Cristian Frăsinaru
 */
public interface ColoringAlgorithm {

    /**
     * Returns the input graph.
     *
     * @return the input graph.
     */
    Graph getGraph();

    /**
     * In case of exact algorithms, this method should return the optimum
     * coloring, if one can be computed within the alloted time. Otherwise, in
     * case of heuristics or if the time limit is exceeded, it should return the
     * best valid coloring that could be computed.
     *
     * @return a valid coloring of the graph.
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
     * The validity depends on the type of coloring: simple, equitable,
     * bandwith, etc. By default, this method return {@code true} if the
     * coloring is proper.
     *
     * @param coloring a vertex coloring.
     * @return {@code true} if the coloring is valid.
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
     * This method should return {@code false} for those type of colorings where
     * it may be possible to exist a k-coloring, even if there is no
     * (k+1)-coloring, for example in the case of equitable coloring. By
     * default, it returns {@code true}.
     *
     * @return {@code true} if the search for the optimum coloring should stop
     * when the current instance cannot be solved.
     */
    default boolean isStoppingOnFailure() {
        return true;
    }

    /**
     * This method should return {@code true} if the implementation of the
     * {@link #findColoring(int)} method returns the optimum coloring and not
     * just a valid coloring repsecting the given upper bound. By default, it
     * returns {@code false}.
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
