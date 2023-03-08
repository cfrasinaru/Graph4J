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
import org.graph4j.Edge;
import org.graph4j.alg.DirectedGraphAlgorithm;
import org.graph4j.util.EdgeSet;
import org.graph4j.util.VertexSet;
import org.graph4j.util.VertexStack;

/**
 * DFS - Much slower most of the times.
 *
 * @author Cristian Frăsinaru
 */
@Deprecated
public class FordFulkersonMaximumFlow extends DirectedGraphAlgorithm
        implements MaximumFlowAlgorithm {

    protected int source, sink;
    protected boolean[] visited;
    protected boolean[] forward;
    protected int[] parent;
    protected double[] residual;
    protected VertexStack stack;
    protected int[] nextSuccPos, nextPredPos;
    protected double[][] flowData;
    //
    protected VertexSet sourcePartition;
    protected VertexSet sinkPartition;
    protected EdgeSet cutEdges;

    /**
     *
     * @param graph
     * @param source
     * @param sink
     */
    public FordFulkersonMaximumFlow(Digraph graph, int source, int sink) {
        super(graph);
        if (graph.isAllowingMultipleEdges() || graph.isAllowingSelfLoops()) {
            throw new IllegalArgumentException("Multigraphs and pseudographs are not supported.");
        }
        this.source = source;
        this.sink = sink;
    }

    @Override
    public double getValue() {
        if (flowData == null) {
            compute();
        }
        int si = graph.indexOf(source);
        double value = 0.0;
        for (var it = graph.successorIterator(source); it.hasNext();) {
            int u = it.next();
            value += flowData[si][adjListPos(source, u)];
        }
        return value;
    }

    @Override
    public double getValue(int v, int u) {
        if (flowData == null) {
            compute();
        }
        int pos = adjListPos(v, u);
        if (pos < 0) {
            return 0;
        }
        return flowData[graph.indexOf(v)][pos];
    }

    @Override
    public NetworkFlow getFlow() {
        NetworkFlow flow = new NetworkFlow(graph, source, sink);
        for (var it = graph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            flow.put(e, getValue(e));
        }
        return flow;
    }

    @Override
    public VertexSet getSourcePart() {
        if (sourcePartition != null) {
            return sourcePartition;
        }
        if (flowData == null) {
            compute();
        }
        sourcePartition = new VertexSet(graph);
        for (int v : graph.vertices()) {
            if (visited[graph.indexOf(v)]) {
                sourcePartition.add(v);
            }
        }
        return sourcePartition;
    }

    @Override
    public VertexSet getSinkPart() {
        if (sinkPartition != null) {
            return sinkPartition;
        }
        if (flowData == null) {
            compute();
        }
        sinkPartition = new VertexSet(graph);
        for (int v : graph.vertices()) {
            if (!visited[graph.indexOf(v)]) {
                sinkPartition.add(v);
            }
        }
        return sinkPartition;
    }

    @Override
    public EdgeSet getCutEdges() {
        if (cutEdges != null) {
            return cutEdges;
        }
        getSourcePart();
        getSinkPart();
        cutEdges = new EdgeSet(graph);
        for (int v : sourcePartition) {
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                if (sinkPartition.contains(u)) {
                    cutEdges.add(v, u);
                }
            }
        }
        return cutEdges;
    }

    private boolean scan(int v) {
        //forward edges
        int vi = graph.indexOf(v);
        for (var it = graph.successorIterator(v, nextSuccPos[vi]); it.hasNext();) {
            int u = it.next(); //v -> u
            int ui = graph.indexOf(u);
            nextSuccPos[vi]++;
            if (visited[ui]) {
                continue;
            }
            double flow = flowData[vi][it.adjListPos()];//pos in the adjList of v
            double capacity = it.getEdgeWeight();
            if (capacity > flow) {
                visited[ui] = true;
                forward[ui] = true;
                parent[ui] = vi;
                residual[ui] = Math.min(capacity - flow, residual[vi]);
                stack.push(u);
                return u == sink;
            }
        }
        //backward edges
        for (var it = graph.predecesorIterator(v, nextPredPos[vi]); it.hasNext();) {
            int u = it.next(); //v <- u
            int ui = graph.indexOf(u);
            nextPredPos[vi]++;
            if (visited[ui]) {
                continue;
            }
            double flow = flowData[ui][it.adjListPos()]; //pos in the adjList of u
            if (flow > 0) {
                visited[ui] = true;
                forward[ui] = false;
                parent[ui] = vi;
                residual[ui] = Math.min(flow, residual[vi]);
                stack.push(u);
                return false;
            }
        }
        stack.pop();
        return false;
    }

    protected void reset() {
        Arrays.fill(visited, false);
        Arrays.fill(nextSuccPos, -1);
        Arrays.fill(nextPredPos, -1);
        stack.clear();
    }

    protected void compute() {
        //int aug = 0;
        int n = graph.numVertices();
        visited = new boolean[n];
        forward = new boolean[n];
        parent = new int[n];
        residual = new double[n];
        stack = new VertexStack(graph, n);
        nextSuccPos = new int[n];
        nextPredPos = new int[n];
        flowData = new double[n][];
        for (int v : graph.vertices()) {
            flowData[graph.indexOf(v)] = new double[graph.degree(v)];
        }

        int si = graph.indexOf(source);
        int ti = graph.indexOf(sink);
        boolean found;
        do {
            reset();
            visited[si] = true;
            residual[si] = Double.POSITIVE_INFINITY;
            parent[si] = -1;
            stack.push(source);
            found = false;
            while (!stack.isEmpty()) {
                int v = stack.peek();
                if (scan(v)) {
                    found = true; // an augmenting path
                    break;
                }
            }
            if (found) {
                //increase flow with the residual capacity in the sink
                double r = residual[ti];
                //aug++;
                int ui = ti;
                do {
                    int vi = parent[ui];
                    if (forward[ui]) {
                        //v -> u
                        int v = graph.vertexAt(vi);
                        int pos = adjListPos(v, graph.vertexAt(ui));
                        flowData[vi][pos] += r;
                    } else {
                        //v <- u
                        int u = graph.vertexAt(ui);
                        int pos = adjListPos(u, graph.vertexAt(vi));
                        flowData[ui][pos] -= r;
                    }
                    ui = vi;
                } while (ui != si);
            }
        } while (found);
        //System.out.println("FF aug=" + aug);
    }

    //Returns the first position of u in the neighbor list of v.
    private int adjListPos(int v, int u) {
        int pos = 0;
        for (var it = graph.neighborIterator(v); it.hasNext();) {
            if (it.next() == u) {
                return pos;
            }
            pos++;
        }
        return -1;
    }

}
