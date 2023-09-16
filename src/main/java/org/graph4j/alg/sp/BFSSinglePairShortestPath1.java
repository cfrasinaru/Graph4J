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
//package org.graph4j.alg.sp;
//
//import org.graph4j.Digraph;
//import org.graph4j.Graph;
//import org.graph4j.Graphs;
//import org.graph4j.alg.GraphAlgorithm;
//import org.graph4j.traverse.BFSIterator;
//import org.graph4j.util.CheckArguments;
//import org.graph4j.util.Path;
//
///**
// * For unweighted graphs, breadth-first search can be used to find the shortest
// * path from two vertices.
// *
// * PARALLEL VERSION - NOT GOOD.
// *
// * @author Cristian Frăsinaru
// */
//@Deprecated
//class BFSSinglePairShortestPath1 extends GraphAlgorithm
//        implements SinglePairShortestPath {
//
//    private final int source;
//    private final int target;
//    private final Graph transpose;
//    private boolean[] visited;
//    private int meeting;
//    private Path bestPath;
//    private double bestWeight;
//
//    /**
//     * Creates an algorithm to find the shortest path between source and target.
//     *
//     * @param graph the input graph.
//     * @param source the source vertex number.
//     * @param target the target vertex number.
//     */
//    public BFSSinglePairShortestPath1(Graph graph, int source, int target) {
//        super(graph);
//        if (graph.isEdgeWeighted()) {
//            throw new IllegalArgumentException(
//                    "BFSSinglePairShortestPath should be used only for graphs with unweighted edges.");
//        }
//        CheckArguments.graphContainsVertex(graph, source);
//        CheckArguments.graphContainsVertex(graph, target);
//        this.source = source;
//        this.target = target;
//        if (graph instanceof Digraph) {
//            this.transpose = Graphs.transpose((Digraph) graph);
//        } else {
//            this.transpose = graph;
//        }
//    }
//
//    @Override
//    public int getSource() {
//        return source;
//    }
//
//    @Override
//    public int getTarget() {
//        return target;
//    }
//
//    @Override
//    public Path findPath() {
//        if (source == target) {
//            return new Path(graph, new int[]{source});
//        }
//        if (bestPath == null) {
//            compute();
//        }
//        return bestPath;
//    }
//
//    @Override
//    public double getPathWeight() {
//        if (source == target) {
//            return 0;
//        }
//        if (bestPath == null) {
//            compute();
//        }
//        return bestWeight;
//    }
//
//    private void compute() {
//        visited = new boolean[graph.numVertices()];
//        meeting = -1;
//        var sourceThread = new BFSThread(graph, source);
//        var targetThread = new BFSThread(transpose, target);
//        sourceThread.start();
//        targetThread.start();
//        try {
//            sourceThread.join();
//            targetThread.join();
//        } catch (InterruptedException e) {
//        }
//        bestPath = sourceThread.createPath(meeting);
//        bestPath.reverse();
//        System.out.println("From source: " + bestPath);
//        Path pathToTarget = targetThread.createPath(meeting);
//        System.out.println("To targer: " + pathToTarget);
//        for (int i = 1, k = pathToTarget.size(); i < k; i++) {
//            bestPath.add(pathToTarget.get(i));
//        }
//        System.out.println(bestPath);
//        bestWeight = bestPath.computeEdgesWeight();
//    }
//
//    private class BFSThread extends Thread {
//
//        Graph g;
//        int[] before;
//        int startVertex;
//        int size;
//
//        BFSThread(Graph g, int startVertex) {
//            this.g = g;
//            this.startVertex = startVertex;
//            before = new int[graph.numVertices()];
//            before[graph.indexOf(startVertex)] = -1;
//        }
//
//        @Override
//        public void run() {
//            var bfs = new BFSIterator(g, startVertex);
//            while (bfs.hasNext()) {
//                if (meeting > 0) {
//                    break;
//                }
//                var node = bfs.next();
//                System.out.println(this.getName() + ": " + node);
//                int vi = g.indexOf(node.vertex());
//                if (node.parent() != null) {
//                    before[vi] = node.parent().vertex();
//                }
//                synchronized (graph) {
//                    if (visited[vi]) {
//                        if (meeting < 0) {
//                            meeting = vi;
//                            System.out.println(this.getName() + " Meeting: " + vi);
//                        }
//                        break;
//                    }
//                    visited[vi] = true;
//                }
//                size++;
//            }
//        }
//
//        Path createPath(int vi) {
//            Path path = new Path(g, size + 1);
//            while (vi >= 0) {
//                path.add(g.vertexAt(vi));
//                vi = before[vi];
//            }
//            return path;
//        }
//    }
//}
