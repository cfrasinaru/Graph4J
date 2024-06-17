/*
 * Copyright (C) 2024 Cristian Frăsinaru and contributors
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
package org.graph4j.flow;

import java.util.Arrays;
import org.graph4j.Network;
import static org.graph4j.Network.CAPACITY;
import static org.graph4j.Network.FLOW;
import org.graph4j.util.IntArrays;
import org.graph4j.util.VertexQueue;
import org.graph4j.util.VertexStack;

/**
 * /**
 * Implements the Dinic algorithm for finding the maximum flow in a network.
 *
 * The algorithm works by repeatedly finding blocking flows in level graphs and
 * augmenting the flow along these paths.
 *
 *
 * Dinic's algorithm is an efficient maximum flow algorithm with a time
 * complexity of O(n^2 * m) for general graphs and O(m * sqrt(n)) for unit
 * capacity graphs, where n is the number of vertices and m is the number of
 * edges.
 *
 * @author Cristian Frăsinaru
 */
public class DinicMaximumFlow extends MaximumFlowBase {

    private int[] levels;
    private int[] succPos;
    private int[] predPos;

    private VertexStack stack;
    private boolean[] visited;
    private double[] residual;
    private int[] parent;
    boolean[] forward;

    public DinicMaximumFlow(Network graph) {
        super(graph);
    }

    public DinicMaximumFlow(Network graph, FlowData flow) {
        super(graph, flow);
    }

    @Override
    public void computeMaximumFlow() {
        initFlow();
        this.levels = new int[numVertices];
        this.succPos = new int[numVertices];
        this.predPos = new int[numVertices];

        while (true) {
            if (!createLevels()) {
                break;
            }
            if (!createBlockingFlow()) {
                break;
            }
        }
        computed = true;
        assert graph.isFlowValid();
    }

    // Perform a BFS starting in source
    // using only edges with positive residual capacity
    private boolean createLevels() {
        Arrays.fill(levels, -1);
        VertexQueue queue = new VertexQueue(graph, numVertices);
        queue.add(source);
        levels[sourceIndex] = 0;
        while (!queue.isEmpty()) {
            int v = queue.poll();
            int vi = graph.indexOf(v);
            for (var it = graph.successorIterator(v); it.hasNext();) {
                int u = it.next();
                int ui = graph.indexOf(u);
                if (levels[ui] < 0 && it.getEdgeData(CAPACITY) - it.getEdgeData(FLOW) > 0) {
                    levels[ui] = levels[vi] + 1;
                    queue.add(u);
                }
            }
            for (var it = graph.predecessorIterator(v); it.hasNext();) {
                int u = it.next();
                int ui = graph.indexOf(u);
                if (levels[ui] < 0 && it.getEdgeData(FLOW) > 0) {
                    levels[ui] = levels[vi] + 1;
                    queue.add(u);
                }
            }
        }
        return levels[graph.indexOf(sink)] != -1;
    }

    // Use DFS to repeatedly found augmenting paths.
    // A blocking flow blocks all augmenting paths in a level graph such that 
    // no more flow can be sent from the source to the sink 
    // without increasing the levels of some vertices.
    private boolean createBlockingFlow() {
        Arrays.fill(succPos, -1);
        Arrays.fill(predPos, -1);

        residual = new double[numVertices];
        parent = new int[numVertices];
        forward = new boolean[numVertices];
        visited = new boolean[numVertices];
        stack = new VertexStack(graph, numVertices);

        stack.push(source);
        parent[sourceIndex] = -1;
        residual[sourceIndex] = Double.POSITIVE_INFINITY;
        visited[sourceIndex] = true;

        int flowIncreased = 0;
        top:
        while (!stack.isEmpty()) {
            int v = stack.peek();
            int vi = graph.indexOf(v);
            
            //forward
            var succIt = graph.successorIterator(v, succPos[vi]);
            succPos[vi]++;
            while (succIt.hasNext()) {
                int u = succIt.next();
                int ui = graph.indexOf(u);
                if (visited[ui] || levels[ui] != levels[vi] + 1) {
                    continue;
                }
                double edgeResidual = succIt.getEdgeData(CAPACITY) - succIt.getEdgeData(FLOW);
                if (edgeResidual == 0) {
                    continue;
                }
                visited[ui] = true;
                parent[ui] = vi;
                forward[ui] = true;
                residual[ui] = Math.min(edgeResidual, residual[vi]);
                stack.push(u);
                if (u == sink) {
                    //found an augmenting path                    
                    augmentFlow();
                    flowIncreased++;
                }
                continue top;
            }

            //backward
            var predIt = graph.predecessorIterator(v, predPos[vi]);
            predPos[vi]++;
            while (predIt.hasNext()) {
                int u = predIt.next();
                int ui = graph.indexOf(u);
                if (visited[ui] || levels[ui] != levels[vi] + 1) {
                    continue;
                }
                double edgeResidual = predIt.getEdgeData(FLOW);
                if (edgeResidual == 0) {
                    continue;
                }
                visited[ui] = true;
                forward[ui] = false;
                parent[ui] = vi;
                residual[ui] = Math.min(edgeResidual, residual[vi]);
                stack.push(u);
                continue top;
            }
            
            stack.pop();
        }
        return flowIncreased > 0;
    }

    // Alter the flow on the augmenting path.
    // Increase on forward edges, decrease on backward.
    private void augmentFlow() {
        double r = residual[sinkIndex];
        int bi = sinkIndex;
        do {
            residual[bi] -= r;
            if (residual[bi] == 0) {
                stack.pop();
                visited[bi] = false;
            }
            int ai = parent[bi];
            if (forward[bi]) {
                //a -> b
                int a = graph.vertexAt(ai);
                int b = graph.vertexAt(bi);
                graph.incEdgeData(FLOW, a, b, r);
                if (residual[bi] == 0) {
                    succPos[bi] = -1;
                }
            } else {
                //a <- b
                int a = graph.vertexAt(ai);
                int b = graph.vertexAt(bi);
                graph.incEdgeData(FLOW, b, a, -r);
                if (residual[bi] == 0) {
                    predPos[bi] = -1;
                }
            }
            bi = ai;
        } while (bi != sourceIndex);
    }
}
// https://courses.csail.mit.edu/6.854/06/scribe/scribe11.pdf
// https://courses.csail.mit.edu/6.854/16/Notes/n10-blocking_flows.html
// https://cp-algorithms.com/graph/dinic.html
// https://codeforces.com/blog/entry/105658
