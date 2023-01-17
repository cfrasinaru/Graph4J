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
 * Contract for single-source shortest path algorithms. The graph and the source
 * will be specifed by the implementation constructors.
 *
 * @author Cristian Frăsinaru
 */
public interface SingleSourceShortestPath {

    /**
     *
     * @return the source of the paths
     */
    int getSource();

    /**
     * Attempts at finding the shortest path from the source to the target
     * without computing all the paths. It may be implemented by starting the
     * computation for all the shortest paths and stopping the algorithm when
     * the shortest path to the target is found. If a implementation does not
     * have this ability, it simply invokes <code>getPath</code> instead.
     *
     * @param target
     * @return the shortest path from the source to the target
     */
    default Path computePath(int target) {
        return getPath(target);
    }

    /**
     * Returns the shortest path between source and target. On the first
     * invocation of this method, it computes all the shortest paths starting in
     * source and then it returns the requested one. All the shortest paths are
     * stored for later retrieval, so subsequent invocations will return the
     * already computed paths.
     *
     * @param target
     * @return the shortest path from the source to the target, or null if no
     * path exists.
     */
    Path getPath(int target);

    /**
     * Returns the weight of the shortest path from the source to the target.
     *
     * @param target the number of the target vertex
     * @return the weight of the shortest path from the source to the target, or
     * <code>Double.POSTIVE_INFINITY</code> if no path exist.
     */
    default double getPathWeight(int target) {
        return getPath(target).computeEdgesWeight();
    }

}
