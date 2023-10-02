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

import org.graph4j.Digraph;
import org.graph4j.util.EdgeSet;
import org.graph4j.util.VertexQueue;
import org.graph4j.util.VertexSet;

/**
 *
 * @author Cristian Frăsinaru
 */
public class PushRelabelMaximumFlow extends MaximumFlowBase {

    private double[] excess;
    private int[] vertexHeight;
    private VertexQueue active;
    private int[] heightCount; //not used for anything
    private int relabelCount;
    private int n;

    //
    //private final boolean DEBUG = false;
    public PushRelabelMaximumFlow(Digraph graph, int source, int sink) {
        super(graph, source, sink);
    }

    public PushRelabelMaximumFlow(Digraph graph, int source, int sink, NetworkFlow flow) {
        super(graph, source, sink, flow);
    }

    @Override
    protected void compute() {
        n = graph.numVertices();
        vertexHeight = new int[n];
        heightCount = new int[2 * n + 1];
        excess = new double[n];
        active = new VertexQueue(graph, n);
        if (flowData == null) {
            initFlowData();
        }
        //create the initial preflow and heights
        for (var it = graph.successorIterator(source); it.hasNext();) {
            int u = it.next();
            int ui = graph.indexOf(u);
            double weight = it.getEdgeWeight();
            flowData[sourceIndex][it.adjListPos()] = weight;
            excess[ui] = weight;
            if (excess[ui] > 0 && ui != sinkIndex) {
                active.add(u);
            }
        }
        vertexHeight[sourceIndex] = n;
        heightCount[n]++;
        for (int i = 0; i < n; i++) {
            if (i != sourceIndex && i != sinkIndex) {
                vertexHeight[i] = 1;
                heightCount[1]++;
            }
        }
        globalRelabel();
        relabelCount = 0;
        while (!active.isEmpty()) {
            int v = active.peek();
            discharge(v);
        }
    }

    //Try to get rid of the excess in v
    private void discharge(int v) {
        int vi = graph.indexOf(v);
        boolean pushed = false;
        int minHeight = 2 * n;

        //arcs from v (forward)
        for (var it = graph.successorIterator(v); it.hasNext();) {
            int u = it.next(); // v -> u
            int ui = graph.indexOf(u);
            int pos = it.adjListPos();
            double residual = it.getEdgeWeight() - flowData[vi][pos];
            if (vertexHeight[ui] >= n || residual == 0) {
                continue;
            }
            if (vertexHeight[vi] != vertexHeight[ui] + 1) {
                if (vertexHeight[ui] + 1 < minHeight) {
                    minHeight = vertexHeight[ui] + 1;
                }
                continue;
            }
            double f = residual >= excess[vi] ? excess[vi] : residual;
            flowData[vi][pos] += f;
            excess[ui] += f;
            excess[vi] -= f;
            if (excess[vi] == 0) {
                active.poll();
            }
            if (u != source && u != sink && !active.contains(u)) {
                active.add(u);
            }
            if (excess[vi] == 0) {
                return;
            }
            pushed = true;
        }

        //arcs to v (backward)
        for (var it = graph.predecessorIterator(v); it.hasNext();) {
            int u = it.next(); // u -> v
            int ui = graph.indexOf(u);
            int pos = it.adjListPos(); //of v in the adj list of u
            double residual = flowData[ui][pos];
            if (residual == 0) {
                continue;
            }
            if (vertexHeight[vi] != vertexHeight[ui] + 1) {
                if (vertexHeight[ui] + 1 < minHeight) {
                    minHeight = vertexHeight[ui] + 1;
                }
                continue;
            }
            double f = residual >= excess[vi] ? excess[vi] : residual;
            flowData[ui][pos] -= f;
            excess[ui] += f;
            excess[vi] -= f;
            if (excess[vi] == 0) {
                active.poll();
            }
            if (u != source && u != sink && !active.contains(u)) {
                active.add(u);
            }
            if (excess[vi] == 0) {
                return;
            }
            pushed = true;
        }

        if (!pushed) {
            //relabel v
            int oldHeight = vertexHeight[vi];
            heightCount[oldHeight]--;
            vertexHeight[vi] = minHeight;
            heightCount[minHeight]++;
            relabelCount++;

            //gap
            /*
            if (0 < oldHeight && oldHeight < n && heightCount[oldHeight] == 0) {
                gapRelabel(oldHeight);
            }*/
            //global
            if (relabelCount % graph.numVertices() == 0) {
                globalRelabel();
            }

        }
    }

    /*
     * Detect gaps in the height function.
     * If there are no nodes with the height h (0 < h < n),
     * then any node v with n > height[v] > h is disconnected from sink and can
     * be relabeled to n + 1.
     */
    private void gapRelabel(int h) {

        for (int i = 0; i < n; i++) {
            if (h < vertexHeight[i] && vertexHeight[i] < n) {
                heightCount[vertexHeight[i]]--;
                vertexHeight[i] = Math.max(vertexHeight[i], n + 1);
                heightCount[vertexHeight[i]]++;
                System.out.println("Gap relabeled");
            }
        }
    }

    /*
     * The labels of the nodes are periodically recomputed by finding the
     * distance of each node from the sink in the residual graph. This is done
     * using a backwards breadth-first search.
     */
    private void globalRelabel() {
        boolean[] visited = new boolean[n];
        visited[sinkIndex] = true;
        //all the visited nodes can reach the sink in the residual graph
        VertexQueue queue = new VertexQueue(graph, n);
        queue.add(sink);
        while (!queue.isEmpty()) {
            int v = queue.poll();
            int vi = graph.indexOf(v);
            if (v != sink) {
                for (var it = graph.successorIterator(v); it.hasNext();) {
                    int u = it.next(); //u<-v
                    int ui = graph.indexOf(u);
                    if (visited[ui]) {
                        continue;
                    }
                    double residual = flowData[vi][it.adjListPos()];
                    if (residual > 0) {
                        visited[ui] = true;
                        queue.add(u);
                    }
                }
            }
            for (var it = graph.predecessorIterator(v); it.hasNext();) {
                int u = it.next(); //u->v
                int ui = graph.indexOf(u);
                if (visited[ui]) {
                    continue;
                }
                double residual = it.getEdgeWeight() - flowData[ui][it.adjListPos()];
                if (residual > 0) {
                    visited[ui] = true;
                    queue.add(u);
                }
            }
        }
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                heightCount[vertexHeight[i]]--;
                vertexHeight[i] = Math.max(vertexHeight[i], n + 1);
                heightCount[vertexHeight[i]]++;
                //if (DEBUG) System.out.println("\t" + graph.vertexAt(i) + " cannot reach the sink");                
            }
        }
        vertexHeight[sourceIndex] = n;
    }

    @Override
    public VertexSet getSourcePart() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public VertexSet getSinkPart() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public EdgeSet getCutEdges() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
