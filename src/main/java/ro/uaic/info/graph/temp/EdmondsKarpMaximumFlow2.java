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
//package ro.uaic.info.graph.temp;
//
//import java.util.Arrays;
//import java.util.Map;
//import ro.uaic.info.graph.Digraph;
//import ro.uaic.info.graph.Edge;
//import ro.uaic.info.graph.alg.DirectedGraphAlgorithm;
//import ro.uaic.info.graph.alg.flow.MaximumFlowAlgorithm;
//import ro.uaic.info.graph.model.VertexQueue;
//
///**
// *
// * @author Cristian Frăsinaru
// */
//@Deprecated
//class EdmondsKarpMaximumFlow2 extends DirectedGraphAlgorithm
//        implements MaximumFlowAlgorithm {
//
//    protected int source, sink;
//    protected boolean[] visited;
//    protected boolean[] forward;
//    protected int[] parent;
//    protected double[] residual;
//    //protected boolean[] activeVertices;
//    private VertexQueue queue;
//    //protected Map<IntPair, Double> flowMap;
//    Edge2DoubleMap flowMap;
//
//    /**
//     *
//     * @param graph
//     * @param source
//     * @param sink
//     */
//    public EdmondsKarpMaximumFlow2(Digraph graph, int source, int sink) {
//        super(graph);
//        this.source = source;
//        this.sink = sink;
//    }
//    
//    private int pos(int v, int u) {
//        int pos = 0;
//        for (var it = graph.neighborIterator(v); it.hasNext();) {
//            if (it.next() == u) {
//                return pos;
//            }
//            pos++;
//        }
//        return -1;
//    }
//
//    @Override
//    public double getValue(int v, int u) {
//        if (flowMap == null) {
//            compute();
//        }
//        int vi = graph.indexOf(v);
//        int ui = graph.indexOf(u);
//        return flowMap.getOrDefault(vi, ui, 0.0);
//    }
//
//    @Override
//    public double getValue() {
//        if (flowMap == null) {
//            compute();
//        }
//        int si = graph.indexOf(source);
//        double value = 0.0;
//        for (var it = graph.succesorIterator(source); it.hasNext();) {
//            int u = it.next();
//            int ui = graph.indexOf(u);
//            value += flowMap.getOrDefault(si, ui, 0.0);
//        }
//        return value;
//    }
//
//    private boolean scan(int v) {
//        //System.out.println("scan " + v);
//        //forward edges
//        //boolean active = false;
//        int vi = graph.indexOf(v);
//        for (var it = graph.succesorIterator(v); it.hasNext();) {
//            int u = it.next(); //v -> u
//            int ui = graph.indexOf(u);
//            if (visited[ui]) {
//                //active = true;
//                continue;
//            }
//            double flow = flowMap.getOrDefault(vi, ui, 0.0);
//            double capacity = it.getEdgeWeight();
//            if (capacity > flow) {
//                visited[ui] = true;
//                forward[ui] = true;
//                parent[ui] = vi;
//                residual[ui] = Math.min(capacity - flow, residual[vi]);
//                queue.offer(u);
//                if (u == sink) {
//                    return true;
//                }
//                //active = true;
//                //System.out.println("forward: " + v + " -> " + u);
//            }
//        }
//        //backward edges
//        for (var it = graph.predecesorIterator(v); it.hasNext();) {
//            int u = it.next(); //v <- u
//            int ui = graph.indexOf(u);
//            if (visited[ui]) {
//                //active = true;
//                continue;
//            }
//            double flow = flowMap.getOrDefault(ui, vi, 0.0);
//            if (flow > 0) {
//                //System.out.println("backward: " + v + " -> " + u);
//                visited[ui] = true;
//                forward[ui] = false;
//                parent[ui] = vi;
//                residual[ui] = Math.min(flow, residual[vi]);
//                queue.offer(u);
//                //active = true;
//            }
//        }
//        //activeVertices[vi] = active;
//        return false;
//    }
//
//    protected void compute() {
//        int n = graph.numVertices();
//        visited = new boolean[n];
//        forward = new boolean[n];
//        parent = new int[n];
//        residual = new double[n];
//        //activeVertices = new boolean[n];
//        //Arrays.fill(activeVertices, true);
//        queue = new VertexQueue(graph, n);
//        /*
//        try {
//            flowMap = new Edge2DoubleArrayMap(graph);
//        } catch (IllegalArgumentException e) {
//            flowMap = new Edge2DoubleHashMap(graph);
//        }*/
//        //flowMap = new Edge2DoubleAdjListMap(graph);
//        //flowMap = new Edge2DoubleHashMap(graph);
//        //flowMap = new Edge2DoubleArrayMap(graph);
//
//        int si = graph.indexOf(source);
//        int ti = graph.indexOf(sink);
//        boolean found;
//        do {
//            visited[si] = true;
//            residual[si] = Double.POSITIVE_INFINITY;
//            parent[si] = -1;
//            queue.offer(source);
//            found = false;
//            while (!queue.isEmpty()) {
//                int v = queue.poll();
//                //if (!activeVertices[graph.indexOf(v)]) {
//                //    continue;
//                //}
//                if (scan(v)) {
//                    //found an augmenting path
//                    found = true;
//                    break;
//                }
//            }
//            if (found) {
//                //increase flow with the residual capacity in the sink
//                double r = residual[ti];
//                int ui = ti;
//                do {
//                    int vi = parent[ui]; //v -> u or v <- u
//                    flowMap.inc(vi, ui, forward[ui] ? r : -r);
//                    //var e = new IntPair(vi, ui);
//                    //double flow = flowMap.getOrDefault(e, 0.0);
//                    //flowMap.put(e, forward[ui] ? flow + r : flow - r);
//                    //if (!forward[ui]) {
//                    //    activeVertices[vi] = true;
//                    //    activeVertices[ui] = true;
//                    //}
//                    ui = vi;
//                } while (ui != si);
//                Arrays.fill(visited, false);
//                queue.clear();
//                //System.out.println("augmented: " + flowMap);
//            }
//        } while (found);
//    }
//
//    @Override
//    public Map<Edge, Double> getFlowMap() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//}
