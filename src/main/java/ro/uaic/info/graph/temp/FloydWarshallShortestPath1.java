///*
// * Copyright (C) 2022 Cristian Frăsinaru and contributors
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
//package ro.uaic.info.graph.alg.sp;
//
//import ro.uaic.info.graph.Cycle;
//import ro.uaic.info.graph.Digraph;
//import ro.uaic.info.graph.Graph;
//import ro.uaic.info.graph.Path;
//import ro.uaic.info.graph.util.IntArrayList;
//import ro.uaic.info.graph.util.Tools;
//
///**
// *
// * @author Cristian Frăsinaru
// */
//public class FloydWarshallShortestPath1 implements AllPairsShortestPath {
//
//    private final Graph graph;
//    private final boolean directed;
//    private double[][] cost;
//    private int[][] before;
//    //before[i][j] = the vertex before j on the shortest path from i to j
//
//    public FloydWarshallShortestPath1(Graph graph) {
//        this.graph = graph;
//        this.directed = graph instanceof Digraph;
//    }
//
//    @Override
//    public Path getPath(int source, int target) {
//        if (before == null) {
//            compute();
//        }
//        int v, u;
//        if (!directed && source > source) {
//            v = target;
//            u = source;
//        } else {
//            v = source;
//            u = target;
//        }
//        Path path = createPathBetween(graph.indexOf(v), graph.indexOf(u));
//        if (!directed && source > target) {
//            path.reverse();
//        }
//        return path;
//    }
//
//    @Override
//    public double getPathWeight(int source, int target) {
//        if (cost == null) {
//            compute();
//        }
//        return cost[graph.indexOf(source)][graph.indexOf(target)];
//    }
//
//    private void compute() {
//        int n = graph.numVertices();
//        this.cost = graph.costMatrix();
//        this.before = new int[n][n];
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < n; j++) {
//                before[i][j] = (i == j ? -1 : i);
//            }
//        }
//        for (int k = 0; k < n; k++) {
//            int n1 = directed ? n : n - 1;
//            for (int i = 0; i < n1; i++) {
//                if (i == k) {
//                    continue;
//                }
//                int from = directed ? 0 : i;
//                for (int j = from; j < n; j++) {
//                    if (cost[i][j] > cost[i][k] + cost[k][j]) {
//                        cost[i][j] = cost[i][k] + cost[k][j];
//                        if (!directed) {
//                            cost[j][i] = cost[i][j];
//                        }
//                        before[i][j] = before[k][j];
//                        if (i == j && cost[i][j] < 0) {
//                            Cycle cycle = createCycleBetween(i, j);
//                            if (directed || cycle.length() > 2) {
//                                throw new NegativeCycleException(cycle);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private Path createPathBetween(int vi, int ui) {
//        IntArrayList list = new IntArrayList();
//        while (ui != vi) {
//            list.add(graph.vertexAt(ui));
//            ui = before[vi][ui];
//        }
//        list.add(graph.vertexAt(vi));
//        list.reverse();
//        return new Path(graph, list.values());
//    }
//
//    private Cycle createCycleBetween(int vi, int ui) {
//        System.out.println(graph.vertexAt(vi));
//        Tools.printMatrix(before);
//        IntArrayList list = new IntArrayList();
//        while (!list.contains(graph.vertexAt(ui))) {
//            System.out.println(graph.vertexAt(ui));
//            list.add(graph.vertexAt(ui));
//            ui = before[vi][ui];
//        }
//        list.reverse();
//        return new Cycle(graph, list.values());
//    }
//
//}
