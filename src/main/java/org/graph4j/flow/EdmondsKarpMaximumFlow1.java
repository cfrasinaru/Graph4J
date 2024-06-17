///*
// * Copyright (C) 2023 Cristian Frăsinaru and contributors
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.graph4j.flow;
//
//import java.util.Arrays;
//import org.graph4j.Network;
//import static org.graph4j.Network.CAPACITY;
//import static org.graph4j.Network.FLOW;
//import org.graph4j.util.VertexQueue;
//import org.graph4j.util.VertexSet;
//
///**
// * The Edmonds–Karp algorithm is an implementation of the Ford–Fulkerson method
// * for computing the maximum flow in a network. The algorithm finds shortest
// * augmenting paths (in terms of the number of edges) using Breadth-First Search
// * (BFS).
// *
// * The time complexity of the algorithm is min(O(nmU),O(n m<sup>2</sup>), where
// * <em>n</em> is the number of vertices, <em>m</em> is the number of edges and
// * <em>U</em> is an upper bound of edge capacities.
// *
// * @author Cristian Frăsinaru
// */
////TEST
//public class EdmondsKarpMaximumFlow1 extends MaximumFlowBase {
//
//    protected boolean[] visited;
//    protected boolean[] forward;
//    protected int[] parent;
//    protected int[] parentPos;
//    protected double[] residual;
//    protected VertexQueue queue;
//
//    public EdmondsKarpMaximumFlow1(Network graph) {
//        super(graph);
//
//    }
//
//    public EdmondsKarpMaximumFlow1(Network graph, FlowData flow) {
//        super(graph, flow);
//        if (graph.hasEdgeData(FLOW)) {
//            graph.checkFlow();
//        }
//    }
//
//    private boolean scan(int v) {
//        int vi = graph.indexOf(v);
//        for (var it = graph.neighborIterator(v, true); it.hasNext();) {
//            int u = it.next(); //v -> u (succ) sau v <- u (pred)
//            int ui = graph.indexOf(u);
//            if (visited[ui]) {
//                continue;
//            }
//            double flow = it.getEdgeData(FLOW);
//            double capacity = it.getEdgeData(CAPACITY);
//            double edgeResidual = it.isSuccessor() ? capacity - flow : flow;
//            if (edgeResidual > 0) {
//                visited[ui] = true;
//                forward[ui] = it.isSuccessor();
//                parent[ui] = vi;
//                parentPos[ui] = it.adjListPos();
//                residual[ui] = Math.min(edgeResidual, residual[vi]);
//                queue.add(u);
//                if (u == sink) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public void computeMaximumFlow() {        
//        initFlow();
//        int n = graph.numVertices();
//        visited = new boolean[n];
//        forward = new boolean[n];
//        parent = new int[n];
//        parentPos = new int[n];
//        residual = new double[n];
//        queue = new VertexQueue(graph, n);
//
//        int si = graph.indexOf(source);
//        int ti = graph.indexOf(sink);
//        boolean hasAugmentingPath;
//        do {
//            visited[si] = true;
//            residual[si] = Double.POSITIVE_INFINITY;
//            parent[si] = -1;
//            queue.add(source);
//            hasAugmentingPath = false;
//            while (!queue.isEmpty()) {
//                int v = queue.poll();
//                if (scan(v)) {
//                    hasAugmentingPath = true;
//                    break;
//                }
//            }
//            if (hasAugmentingPath) {
//                //increase flow with the residual capacity in the sink
//                double r = residual[ti];
//                int ui = ti;
//                do {
//                    int vi = parent[ui];
//                    if (forward[ui]) {
//                        //v -> u
//                        graph.incEdgeDataAt(FLOW, vi, parentPos[ui], r);
//                    } else {
//                        //v <- u
//                        graph.incEdgeDataAt(FLOW, ui, parentPos[ui], -r);
//                    }
//                    ui = vi;
//                } while (ui != si);
//
//                //reset and go again
//                Arrays.fill(visited, false);
//                queue.clear();
//            }
//        } while (hasAugmentingPath);
//        computed = true;
//        assert graph.isFlowValid();
//    }
//
//    @Override
//    public VertexSet getSourcePart() {
//        if (sourcePart != null) {
//            return sourcePart;
//        }
//        if (!computed) {
//            computeMaximumFlow();
//        }
//        sourcePart = new VertexSet(graph);
//        for (int v : graph.vertices()) {
//            if (visited[graph.indexOf(v)]) {
//                sourcePart.add(v);
//            }
//        }
//        return sourcePart;
//    }
//
//    @Override
//    public VertexSet getSinkPart() {
//        if (sinkPart != null) {
//            return sinkPart;
//        }
//        if (!computed) {
//            computeMaximumFlow();
//        }
//        sinkPart = new VertexSet(graph);
//        for (int v : graph.vertices()) {
//            if (!visited[graph.indexOf(v)]) {
//                sinkPart.add(v);
//            }
//        }
//        return sinkPart;
//    }
//
//}
