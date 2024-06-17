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
package org.graph4j.spanning;

import org.graph4j.Graph;

/**
 * Iterates over the minimum spanning trees of a weighted graph.
 *
 * The iterator returns the collection of edges of a spanning tree, which can be
 * used to create the actual tree with the method
 * {@link Graph#subgraph(java.util.Collection)}.
 *
 * @see WeightedSpanningTreeIterator
 * @author Cristian Frăsinaru
 */
public class MinimumSpanningTreeIterator extends WeightedSpanningTreeIterator {

    private Double minWeight;

    /**
     * Creates an iterator over the minimum spanning trees of a weighted graph.
     *
     * @param graph the input graph.
     */
    public MinimumSpanningTreeIterator(Graph graph) {
        super(graph);
    }

    @Override
    public boolean hasNext() {
        boolean hasNext = super.hasNext();
        if (!hasNext) {
            return false;
        }
        double weight = queue.peek().mstWeight;
        if (minWeight == null) {
            minWeight = weight;
            return true;
        }
        if (weight > minWeight) {
            queue.clear();
            return false;
        }
        return true;
    }
}
