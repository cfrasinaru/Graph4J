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
package org.graph4j.alg.flow;

import java.util.Arrays;
import org.graph4j.Digraph;
import org.graph4j.util.VertexQueue;
import org.graph4j.util.VertexSet;

/**
 * The Edmonds–Karp algorithm is an implementation of the Ford–Fulkerson method
 * for computing the maximum flow in a flow network.
 *
 * It's time complexity is min(O(nmU),O(n m<sup>2</sup>), where <em>n</em> is
 * the number of vertices and <em>m</em> is the number of edges and <em>U</em>
 * is an ubber bound of edge capacities.
 *
 * BFS.
 *
 * @author Cristian Frăsinaru
 */
public class EdmondsKarpMaximumFlow extends MaximumFlowBase {

    protected boolean[] visited;
    protected boolean[] forward;
    protected int[] parent;
    protected double[] residual;
    protected VertexQueue queue;

    public EdmondsKarpMaximumFlow(Digraph graph, int source, int sink) {
        super(graph, source, sink);

    }

    public EdmondsKarpMaximumFlow(Digraph graph, int source, int sink, NetworkFlow flow) {
        super(graph, source, sink, flow);
    }

    @Override
    public VertexSet getSourcePart() {
        if (sourcePart != null) {
            return sourcePart;
        }
        if (flowData == null) {
            compute();
        }
        sourcePart = new VertexSet(graph);
        for (int v : graph.vertices()) {
            if (visited[graph.indexOf(v)]) {
                sourcePart.add(v);
            }
        }
        return sourcePart;
    }

    @Override
    public VertexSet getSinkPart() {
        if (sinkPart != null) {
            return sinkPart;
        }
        if (flowData == null) {
            compute();
        }
        sinkPart = new VertexSet(graph);
        for (int v : graph.vertices()) {
            if (!visited[graph.indexOf(v)]) {
                sinkPart.add(v);
            }
        }
        return sinkPart;
    }

    private boolean scan(int v) {
        //forward edges
        int vi = graph.indexOf(v);
        for (var it = graph.successorIterator(v); it.hasNext();) {
            int u = it.next(); //v -> u
            int ui = graph.indexOf(u);
            if (visited[ui]) {
                continue;
            }
            double flow = flowData[vi][it.adjListPos()];
            double capacity = it.getEdgeWeight();
            if (capacity > flow) {
                visited[ui] = true;
                forward[ui] = true;
                parent[ui] = vi;
                residual[ui] = Math.min(capacity - flow, residual[vi]);
                queue.add(u);
                if (u == sink) {
                    return true;
                }
            }
        }
        //backward edges
        for (var it = graph.predecessorIterator(v); it.hasNext();) {
            int u = it.next(); //v <- u
            int ui = graph.indexOf(u);
            if (visited[ui]) {
                continue;
            }
            double flow = flowData[ui][it.adjListPos()];
            if (flow > 0) {
                visited[ui] = true;
                forward[ui] = false;
                parent[ui] = vi;
                residual[ui] = Math.min(flow, residual[vi]);
                queue.add(u);
            }
        }
        return false;
    }

    private void reset() {
        Arrays.fill(visited, false);
        queue.clear();
    }

    @Override
    protected void compute() {
        int n = graph.numVertices();
        visited = new boolean[n];
        forward = new boolean[n];
        parent = new int[n];
        residual = new double[n];
        queue = new VertexQueue(graph, n);
        if (flowData == null) {
            initFlowData();
        }

        int si = graph.indexOf(source);
        int ti = graph.indexOf(sink);
        boolean hasAugmentingPath;
        do {
            visited[si] = true;
            residual[si] = Double.POSITIVE_INFINITY;
            parent[si] = -1;
            queue.add(source);
            hasAugmentingPath = false;
            while (!queue.isEmpty()) {
                int v = queue.poll();
                if (scan(v)) {
                    hasAugmentingPath = true;
                    break;
                }
            }
            if (hasAugmentingPath) {
                //increase flow with the residual capacity in the sink
                double r = residual[ti];
                int ui = ti;
                do {
                    int vi = parent[ui];
                    if (forward[ui]) {
                        //v -> u
                        int v = graph.vertexAt(vi);
                        int pos = graph.adjListPos(v, graph.vertexAt(ui));
                        flowData[vi][pos] += r;
                    } else {
                        //v <- u
                        int u = graph.vertexAt(ui);
                        int pos = graph.adjListPos(u, graph.vertexAt(vi));
                        flowData[ui][pos] -= r;
                    }
                    ui = vi;
                } while (ui != si);
                reset();
            }
        } while (hasAugmentingPath);
    }

}
