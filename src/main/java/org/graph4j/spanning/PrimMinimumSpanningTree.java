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
package org.graph4j.spanning;

import java.util.Arrays;
import org.graph4j.Graph;
import org.graph4j.util.EdgeSet;
import org.graph4j.util.VertexHeap;

/**
 * Implementation of Prim's algorithm that uses a binary heap.
 *
 * Complexity O(m + m long n)
 *
 * @author Cristian Frăsinaru
 */
public class PrimMinimumSpanningTree extends MinimumSpanningTreeBase {

    private VertexHeap heap;
    private final int[] vertices;
    private boolean solved[];
    private double[] cost;
    private int[] peer;

    public PrimMinimumSpanningTree(Graph graph) {
        super(graph);
        this.vertices = graph.vertices();
    }

    @Override
    public EdgeSet getEdges() {
        if (treeEdges != null) {
            return treeEdges;
        }
        if (minWeight == null) {
            compute();
        }
        int n = graph.numVertices();
        treeEdges = new EdgeSet(graph, n - 1);
        for (int i = 0; i < n; i++) {
            int u = vertices[i];
            int v = peer[i];
            if (v >= 0) {
                treeEdges.add(v, u);
            }
        }
        return treeEdges;
    }

    @Override
    protected void compute() {
        int n = graph.numVertices();
        this.cost = new double[n];
        this.peer = new int[n];
        this.solved = new boolean[n];
        int numSolved = 0;
        Arrays.fill(cost, Double.POSITIVE_INFINITY);
        Arrays.fill(peer, -1);
        //
        this.heap = new VertexHeap(graph, (i, j) -> (int) Math.signum(cost[i] - cost[j]));
        this.minWeight = 0.0;
        while (numSolved < n) {
            int vi = heap.poll();
            int v = graph.vertexAt(vi);
            if (cost[vi] < Double.POSITIVE_INFINITY) {
                minWeight += cost[vi];
            }
            solved[vi] = true;
            numSolved++;
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int ui = graph.indexOf(it.next());
                if (solved[ui]) {
                    continue;
                }
                double weight = it.getEdgeWeight();
                if (cost[ui] > weight) {
                    cost[ui] = weight;
                    peer[ui] = v;
                    heap.update(ui);
                }
            }
        }
    }

}
