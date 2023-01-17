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
package ro.uaic.info.graph.alg.sp;

import ro.uaic.info.graph.model.Path;

/**
 *
 * @author Cristian Frăsinaru
 */
public interface AllPairsShortestPath {

    /**
     * Returns the shortest path between source and target. On the first
     * invocation of this method, it computes shortest paths between any two
     * vertices of the graph, then it returns the requested one. All the
     * shortest paths are stored for later retrieval, so subsequent invocations
     * will return the already computed paths.
     *
     * @param source the number of the source vertex
     * @param target the number of the target vertex
     * @return the shortest path from the source to the target, or null if no
     * path exists
     */
    Path getPath(int source, int target);

    /**
     *
     * @param source the number of the source vertex
     * @param target the number of the target vertex
     * @return the weight of the shortest path from the source to the target, or
     * <code>Double.POSTIVE_INFINITY</code> if no path exist.
     */
    default double getPathWeight(int source, int target) {
        return getPath(source, target).computeEdgesWeight();
    }

}
