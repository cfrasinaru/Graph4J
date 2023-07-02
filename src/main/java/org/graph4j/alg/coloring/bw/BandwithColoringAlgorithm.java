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
package org.graph4j.alg.coloring.bw;

import org.graph4j.Graph;
import org.graph4j.alg.coloring.Coloring;
import org.graph4j.alg.coloring.ColoringAlgorithm;

/**
 *
 * @author Cristian Frăsinaru
 */
public interface BandwithColoringAlgorithm extends ColoringAlgorithm {

    /**
     *
     * @param coloring a vertex coloring.
     * @return {@code true} if the coloring respects the bandwith constraints.
     */
    @Override
    default boolean isValid(Coloring coloring) {
        Graph graph = coloring.getGraph();
        for (int v : graph.vertices()) {
            int c = coloring.getColor(v);
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int e = coloring.getColor(u);
                if (Math.abs(c - e) < it.getEdgeWeight()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public default int getLowerBound() {
        return 1;
    }

    @Override
    public default Coloring getHeuristicColoring() {
        return new GreedyBandwithColoring(getGraph()).findColoring();
    }

    /**
     * Returns the default implementation of this interface.
     *
     * @param graph the input graph.
     * @return the default implementation of this interface.
     */
    static ColoringAlgorithm getInstance(Graph graph) {
        return new BacktrackBandwithColoring(graph);
    }
}
