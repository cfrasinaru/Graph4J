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

/**
 * {@inheritDoc}
 *
 * <p>
 * This implementation iterates through the unsolved vertices in order to select
 * the optimal vertex at each step, having a complexity of O(n^2), where n is
 * the number of vertices.
 *
 * Suitable for dense graphs.
 *
 * @author Cristian Frăsinaru
 */
public class DijkstraShortestPathDefault extends DijkstraShortestPathBase {

    public DijkstraShortestPathDefault(Graph graph, int source) {
        super(graph, source);
    }

    @Override
    protected int findMinIndex() {
        int minIndex = -1;
        double minCost = Double.POSITIVE_INFINITY;
        for (int i = 0, n = vertices.length; i < n; i++) {
            if (solved[i]) {
                continue;
            }
            if (minIndex == -1 || minCost > cost[i]) {
                minIndex = i;
                minCost = cost[i];
            }
        }
        return minIndex;
    }

}
