/*
 * Copyright (C) 2022 Cristian Frăsinaru and contributors
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
package org.graph4j.alg.sp;

import org.graph4j.Graph;
import org.graph4j.util.Path;

/**
 * Contract for single-pair shortest path algorithms, that is finding a shortest
 * path from s to t, for given vertices s and t.
 *
 * @see BidirectionalDijkstra
 * @author Cristian Frăsinaru
 */
public interface SinglePairShortestPath {

    /**
     * Returns the input graph on which the algorithm is executed.
     *
     * @return the input graph.
     */
    Graph getGraph();

    /**
     * Returns the shortest path between source and target.
     *
     * @return the shortest path from the source to the target, or null if no
     * path exists
     */
    Path findPath();

    /**
     * Returns the source vertex number.
     *
     * @return the source vertex number.
     */
    int getSource();

    /**
     * Returns the target vertex number.
     *
     * @return the target vertex number.
     */
    int getTarget();

    /**
     * Returns the weight of the shortest path from the source to the target, or
     * {@link Double#POSITIVE_INFINITY} if no path exists.
     *
     * @return the weight of the shortest path from the source to the target, or
     * {@link Double#POSITIVE_INFINITY} if no path exists.
     */
    default double getPathWeight() {
        Path path = findPath();
        return path == null ? Double.POSITIVE_INFINITY : path.computeEdgesWeight();
    }

    /**
     * Returns the default implementation of this interface.
     *
     * @param graph the input graph.
     * @param source the source vertex.
     * @param target the target vertex.
     * @return the default implementation of this interface.
     */
    static SinglePairShortestPath getInstance(Graph graph, int source, int target) {
        if (!graph.isEdgeWeighted()) {
            return new BFSSinglePairShortestPath(graph, source, target);
        }
        return new BidirectionalDijkstra(graph, source, target);
    }
}
