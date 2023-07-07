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
import org.graph4j.util.VertexHeap;

/**
 * Implementation of Prim's algorithm that uses a binary heap.
 *
 * Complexity O(m + m long n)
 *
 * @author Cristian Frăsinaru
 */
public class PrimMinimumSpanningTreeHeap extends PrimMinimumSpanningTreeBase {

    private VertexHeap heap;

    public PrimMinimumSpanningTreeHeap(Graph graph) {
        super(graph);
    }

    @Override
    protected void preCompute() {
        this.heap = new VertexHeap(graph, (i, j) -> (int) Math.signum(cost[i] - cost[j]));
    }

    @Override
    protected void postUpdate(int index) {
        heap.update(index);
    }

    @Override
    protected int findMinIndex() {
        return heap.poll();
    }
}
