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
//package ro.uaic.info.graph;
//
//import java.util.LinkedList;
//import java.util.List;
//
///**
// *
// * @author Cristian Frăsinaru
// */
//@Deprecated
//public class EdgeWeightMap {
//
//    private final Graph graph;
//    private List<Node>[] map;
//    private final int numBins;
//
//    public EdgeWeightMap(Graph graph) {
//        this.graph = graph;
//        this.numBins = graph.numEdges();
//        map = new LinkedList[numBins];
//    }
//
//    private int bin(int v, int u) {
//        return (v * graph.numVertices() + u) % numBins;
//    }
//
//    /**
//     *
//     * @param v a vertex number.
//     * @param u a vertex number.
//     * @param weight the weight of the edge vu.
//     */
//    public void put(int v, int u, double weight) {
//        if (!graph.isDirected()) {
//            if (v > u) {
//                int aux = v;
//                v = u;
//                u = aux;
//            }
//        }
//        Node node = find(v, u);
//        if (node != null) {
//            node.weight = weight;
//            return;
//        }
//
//        node = new Node(v, u, weight);
//        int bin = bin(v, u);
//        if (map[bin] == null) {
//            map[bin] = new LinkedList<>();
//        }
//        map[bin].add(node);
//    }
//
//    public double get(int v, int u) {
//        if (!graph.isDirected()) {
//            if (v > u) {
//                int aux = v;
//                v = u;
//                u = aux;
//            }
//        }
//        Node node = find(v, u);
//        if (node == null) {
//            return Double.POSITIVE_INFINITY;
//        }
//        return node.weight;
//    }
//
//    private Node find(int v, int u) {
//        int bin = bin(v, u);
//        if (map[bin] == null) {
//            return null;
//        }
//        for (var node : map[bin]) {
//            if (node.v == v && node.u == u) {
//                return node;
//            }
//        }
//        return null;
//    }
//
//    private class Node {
//
//        int v;
//        int u;
//        double weight;
//
//        public Node(int v, int u, double weight) {
//            this.v = v;
//            this.u = u;
//            this.weight = weight;
//        }
//
//        @Override
//        public int hashCode() {
//            int hash = 3;
//            hash = 17 * hash + this.v;
//            hash = 17 * hash + this.u;
//            return hash;
//        }
//
//        @Override
//        public boolean equals(Object obj) {
//            if (this == obj) {
//                return true;
//            }
//            if (obj == null) {
//                return false;
//            }
//            if (getClass() != obj.getClass()) {
//                return false;
//            }
//            final Node other = (Node) obj;
//            if (this.v != other.v) {
//                return false;
//            }
//            if (this.u != other.u) {
//                return false;
//            }
//            return true;
//        }
//
//    }
//
//}
