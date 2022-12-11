///*
// * Copyright (C) 2022 Faculty of Computer Science Iasi, Romania
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
//package ro.uaic.info.graph;
//
//import java.util.List;
//import java.util.function.Supplier;
//
///**
// * An undirected graph, with no loops. Multiple edges are allowed.
// *
// * <br> The number of nodes must be specified at creation time and cannot be
// * changed, therefore it does not support node insertion or removal.
// *
// * <br>Vertices and edges do not have any additional data, such as weights or
// * other content.
// *
// * <br>This class is designed to minimize memory consumption. Once the graph is
// * created, it can be optimized, meaning that its the adjacency lists are
// * trimmed to their actual size and sorted in ascending order. This improves the
// * time complexity of methods that operate on the adjacency lists, such as
// * <i>containsEdge</i>.
// *
// * @author Cristian Frăsinaru
// */
//public class SimpleImmutableGraph extends AbstractGraph {
//
//    /**
//     *
//     * @param numVertices
//     * @param edges
//     */
//    public SimpleImmutableGraph(int numVertices, int[][] edges) {
//        this(numVertices, Math.min(numVertices - 1, DEFAULT_AVG_DEGREE));
//        for (int[] edge : edges) {
//            int v = edge[0];
//            int u = edge[1];
//            this.addEdge(v, u);
//        }
//    }
//
//    /**
//     * 
//     * @param numVertices
//     * @param provider 
//     */
//    public SimpleImmutableGraph(int numVertices, EdgeProvider provider) {
//        this(numVertices, Math.min(numVertices - 1, DEFAULT_AVG_DEGREE));
//        while (provider.hasNext()) {
//            this.addEdge(provider.next());
//        }
//    }
//
//    /**
//     *
//     * @param numVertices
//     * @param avgDegree
//     */
//    protected SimpleImmutableGraph(int numVertices, int avgDegree) {
//        super(numVertices, numVertices, avgDegree);
//    }
//
//    @Override
//    public int vertexAt(int index) {
//        return index;
//    }
//
//    @Override
//    public int indexOf(int vertex) {
//        return vertex;
//    }
//
//    @Override
//    public Graph subgraph(List<Integer> vertices) {
//        throw new UnsupportedOperationException("Not supported.");
//    }
//
//}
