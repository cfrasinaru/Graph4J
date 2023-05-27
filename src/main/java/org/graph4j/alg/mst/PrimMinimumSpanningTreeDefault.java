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
package org.graph4j.alg.mst;

import org.graph4j.Graph;

/**
 * Uses a binary heap. Complexity O(m + m long n)
 * 
 * @author Cristian Frăsinaru
 */
public class PrimMinimumSpanningTreeDefault extends PrimMinimumSpanningTreeBase {

    public PrimMinimumSpanningTreeDefault(Graph graph) {
        super(graph);
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
