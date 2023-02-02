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
 * Find a shortest path from u to v, for given vertices u and v. If we solve the
 * single-source problem with source vertex u, we solve this problem also.
 * Moreover, all known algorithms for this problem have the same worst-case
 * asymptotic running time as the best single-source algorithms
 *
 * @author Cristian Frăsinaru
 */
public interface SinglePairShortestPath {

    /**
     * Returns the shortest path between source and target.
     *
     * @return the shortest path from the source to the target, or null if no
     * path exists
     */
    Path findPath();

    /**
     *
     * @return the weight of the shortest path from the source to the target, or
     * <code>Double.POSTIVE_INFINITY</code> if no path exist.
     */
    default double getPathWeight() {
        return findPath().computeEdgesWeight();
    }

}
