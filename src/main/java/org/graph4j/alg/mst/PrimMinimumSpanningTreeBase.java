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

import java.util.Arrays;
import org.graph4j.Graph;
import org.graph4j.util.EdgeSet;

/**
 * Uses a binary heap. Complexity O(m + m long n)
 *
 * @author Cristian Frăsinaru
 */
public abstract class PrimMinimumSpanningTreeBase extends MinimumSpanningTreeBase {

    protected final int[] vertices;
    boolean solved[];
    protected double[] cost;
    protected int[] peer;

    public PrimMinimumSpanningTreeBase(Graph graph) {
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

    protected void preCompute() {
    }

    protected void postUpdate(int vi) {
    }

    protected abstract int findMinIndex();

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
        preCompute();
        this.minWeight = 0.0;
        while (numSolved < n) {
            int vi = findMinIndex();
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
                    postUpdate(ui);
                }
            }
        }
    }
}
