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
//import java.util.Arrays;
//
///**
// *
// * @author Cristian Frăsinaru
// */
//public class BaseWeightedGraph extends SimpleGraph implements WeightedGraph, WeightedDigraph {
//
//    protected double[] vertexWeights;
//    protected double[][] edgeWeights;
//
//    protected BaseWeightedGraph() {
//    }
//
//    protected BaseWeightedGraph(int[] vertices, int[][] adj,
//            boolean sorted, boolean directed) {
//        super(vertices, adj, sorted, directed);
//    }
//
//    protected BaseWeightedGraph(int numVertices, int maxVertices, int avgDegree,
//            boolean sorted, boolean directed) {
//        super(numVertices, maxVertices, avgDegree, sorted, directed);
//    }
//
//    private void initVertexWeights() {
//        this.vertexWeights = new double[vertices.length];
//    }
//
//    private void initEdgeWeights() {
//        this.edgeWeights = new double[vertices.length][];
//        for (int i = 0; i < numVertices; i++) {
//            this.edgeWeights[i] = adjList[i] == null ? null : new double[adjList[i].length];
//        }
//    }
//
//    @Override
//    public void addVertex(int v, double weight) {
//        super.addVertex(v);
//        setVertexWeight(v, weight);
//    }
//
//    @Override
//    public void addEdge(int v, int u, double weight) {
//        super.addEdge(v, u);
//        setEdgeWeight(v, u, weight);
//    }
//
//    @Override
//    public void setVertexWeight(int v, double weight) {
//        checkVertex(v);
//        if (vertexWeights == null) {
//            initVertexWeights();
//        }
//        vertexWeights[indexOf(v)] = weight;
//    }
//
//    @Override
//    public double getVertexWeight(int v) {
//        checkVertex(v);
//        if (vertexWeights == null) {
//            return 0;
//        }
//        return vertexWeights[indexOf(v)];
//    }
//
//    @Override
//    public void setEdgeWeight(int v, int u, double weight) {
//        if (edgeWeights == null) {
//            initEdgeWeights();
//        }
//        checkVertex(v);
//        edgeWeights[indexOf(v)][adjListPos(v, u)] = weight;
//        if (v != u && !directed) {
//            checkVertex(u);
//            edgeWeights[indexOf(u)][adjListPos(u, v)] = weight;
//        }
//    }
//
//    @Override
//    public double getEdgeWeight(int v, int u) {
//        checkVertex(v);
//        if (edgeWeights == null) {
//            return 0;
//        }
//        return edgeWeights[indexOf(v)][adjListPos(v, u)];
//    }
//
//    @Override
//    protected void growVertices() {
//        super.growVertices();
//        if (vertexWeights != null) {
//            vertexWeights = Arrays.copyOf(vertexWeights, vertices.length);
//        }
//        if (edgeWeights != null) {
//            edgeWeights = Arrays.copyOf(edgeWeights, vertices.length);
//        }
//    }
//
//    @Override
//    protected void growAdjList(int v) {
//        super.growAdjList(v);
//        if (edgeWeights != null) {
//            int vi = indexOf(v);
//            double[] temp = new double[adjList[vi].length];
//            if (edgeWeights[vi] != null) {
//                System.arraycopy(edgeWeights[vi], 0, temp, 0, edgeWeights[vi].length);
//            }
//            edgeWeights[vi] = temp;
//        }
//    }
//
//    //insert vertex
//    @Override
//    protected void shiftVerticesRight(int i) {
//        super.shiftVerticesRight(i);
//        if (vertexWeights != null) {
//            vertexWeights[i] = vertexWeights[i - 1];
//        }
//        if (edgeWeights != null) {
//            edgeWeights[i] = edgeWeights[i - 1];
//        }
//    }
//
//    //remove vertex
//    @Override
//    protected void shiftVerticesLeft(int i) {
//        super.shiftVerticesLeft(i);
//        if (vertexWeights != null) {
//            vertexWeights[i] = vertexWeights[i + 1];
//        }
//        if (edgeWeights != null) {
//            edgeWeights[i] = edgeWeights[i + 1];
//        }
//    }
//
//    @Override
//    public Edge edge(int v, int u) {
//        return new Edge(v, u, directed, getEdgeWeight(v, u));
//    }
//
//    @Override
//    protected String vertexSpecificInfo(int v) {
//        if (vertexWeights == null) {
//            return "";
//        }
//        return "(" + getVertexWeight(v) + ")";
//    }
//
//}
