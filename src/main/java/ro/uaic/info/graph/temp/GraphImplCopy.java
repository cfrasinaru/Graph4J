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
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.IntStream;
//import ro.uaic.info.graph.util.Tools;
//
///**
// * A generic, array based implementation of a graph. The graph may be simple,
// * directed, weighted. Self loops or multiple edges are not supported.
// *
// * This class avoids the overhead of using too many objects by representing the
// * data in the graph as primitive structures.
// *
// * @author Cristian Frăsinaru
// */
//class GraphImpl<V, E> implements Graph<V, E> {
//
//    protected int maxVertices; //maximum number of vertices
//    protected int numVertices; //number of vertices
//    protected int numEdges;  //number of edges
//    protected int[] vertices; //vertices[i] is the vertex with the index i
//    protected int[] index; //index[v] is the index of the vertex v
//    protected int[][] adjList; //adjList[i] is the adjacency list of the vertex with index i
//    protected int[] degree; //degree[i] is the degree of the vertex with index i
//    protected double[] vertexWeight;
//    protected double[][] edgeWeight;
//    protected Object[] vertexLabel;
//    protected Object[][] edgeLabel;
//    protected EdgeBitSet edgeBitSet;//for fast check if an edge is present in the graph
//
//    //
//    protected boolean sorted; //true if the adjacency lists are maintained sorted
//    protected boolean directed;
//    //
//    protected int avgDegree; //this may improve the memory allocation
//    protected static final int DEFAULT_NUM_VERTICES = 100;
//    protected static final int DEFAULT_AVG_DEGREE = 10;
//
//    protected GraphImpl() {
//    }
//
//    /**
//     *
//     * @param vertices
//     * @param avgDegree
//     * @param sorted
//     * @param directed
//     * @param weighted
//     */
//    protected GraphImpl(int[] vertices, int maxVertices, int avgDegree,
//            boolean sorted, boolean directed) {
//        if (maxVertices < numVertices) {
//            throw new IllegalArgumentException("Invalid maximum number of vertices: " + maxVertices);
//        }
//        if (avgDegree == 0) {
//            avgDegree = DEFAULT_AVG_DEGREE;
//        } else {
//            if (avgDegree < 0) {
//                throw new IllegalArgumentException("Invalid vertex average degree: " + avgDegree);
//            }
//        }
//        this.maxVertices = maxVertices;
//        this.numVertices = vertices.length;
//        this.avgDegree = avgDegree;
//        this.sorted = sorted;
//        this.directed = directed;
//        //
//        this.index = new int[1 + IntStream.of(vertices).max().orElse(maxVertices)];
//        Arrays.fill(index, -1);
//        this.vertices = new int[maxVertices];
//        for (int i = 0; i < numVertices; i++) {
//            this.vertices[i] = vertices[i];
//            index[vertices[i]] = i;
//        }
//        this.degree = new int[maxVertices];
//        this.adjList = new int[maxVertices][];
//        this.numEdges = 0;
//        if (maxVertices > 0) {
//            this.edgeBitSet = new EdgeBitSet(maxVertices);
//        }
//    }
//
//    @Override
//    public GraphImpl copy() {
//        var copy = new GraphImpl();
//        copy.numVertices = numVertices;
//        copy.numEdges = numEdges;
//        copy.avgDegree = avgDegree;
//        copy.sorted = sorted;
//        copy.directed = directed;
//        copy.vertices = Arrays.copyOf(vertices, numVertices);
//        copy.index = Arrays.copyOf(index, index.length);
//        copy.degree = Arrays.copyOf(degree, numVertices);
//        if (vertexWeight != null) {
//            copy.vertexWeight = Arrays.copyOf(vertexWeight, numVertices);
//        }
//        if (vertexLabel != null) {
//            copy.vertexLabel = Arrays.copyOf(vertexLabel, numVertices);
//        }
//        copy.adjList = new int[numVertices][];
//        for (int i = 0; i < numVertices; i++) {
//            if (adjList[i] != null) {
//                copy.adjList[i] = Arrays.copyOf(adjList[i], adjList[i].length);
//                if (edgeWeight != null) {
//                    copy.edgeWeight[i] = Arrays.copyOf(edgeWeight[i], edgeWeight[i].length);
//                }
//                if (edgeLabel != null) {
//                    copy.edgeLabel[i] = Arrays.copyOf(edgeLabel[i], edgeLabel[i].length);
//                }
//            }
//        }
//        return copy;
//    }
//
//    @Override
//    public GraphImpl copyAndRenumberAdding(int amount) {
//        var copy = copy();
//        copy.growIndices(index.length + amount);
//        for (int i = 0; i < numVertices; i++) {
//            copy.vertices[i] = vertices[i] + amount;
//            copy.index[vertices[i]] = -1;
//            copy.index[copy.vertices[i]] = i;
//        }
//        for (int i = 0; i < numVertices; i++) {
//            for (int j = 0; j < adjList[i].length; j++) {
//                copy.adjList[i][j] = adjList[i][j] + amount;
//            }
//        }
//        return copy;
//    }
//
//    @Override
//    public int[] vertices() {
//        if (vertices.length != numVertices) {
//            vertices = Arrays.copyOf(vertices, numVertices);
//        }
//        return vertices;
//    }
//
//    @Override
//    public int vertexAt(int index) {
//        return vertices[index];
//    }
//
//    @Override
//    public int indexOf(int v) {
//        if (v < 0 || v > index.length) {
//            throw new InvalidVertexException(v);
//        }
//        return index[v];
//    }
//
//    protected void checkVertex(int v) {
//        if (indexOf(v) < 0) {
//            throw new InvalidVertexException(v);
//        }
//    }
//
//    /**
//     * The vertices are maintained sorted.
//     *
//     * @param v
//     * @return the index of the added vertex
//     */
//    @Override
//    public int addVertex(int v) {
//        if (v < 0) {
//            throw new InvalidVertexException(v, "Vertex numbers must be positive");
//        }
//        if (numVertices == maxVertices) {
//            edgeBitSet = null;
//        }
//        if (numVertices == vertices.length) {
//            growVertices();
//        }
//        if (v >= index.length) {
//            growIndices(v);
//        }
//        int i = Arrays.binarySearch(vertices, 0, numVertices, v);
//        if (i >= 0) {
//            throw new InvalidVertexException(v, "Vertex is already in the graph");
//        }
//        i = -i - 1; //insertion position
//        //shift everything rigth
//        for (int j = i + 1; j < numVertices; j++) {
//            vertices[i] = vertices[i - 1];
//            adjList[i] = adjList[i - 1];
//            degree[i] = degree[i - 1];
//            if (vertexWeight != null) {
//                vertexWeight[i] = vertexWeight[i - 1];
//            }
//            if (edgeWeight != null) {
//                edgeWeight[i] = edgeWeight[i - 1];
//            }
//            if (vertexLabel != null) {
//                vertexLabel[i] = vertexLabel[i - 1];
//            }
//            if (edgeLabel != null) {
//                edgeLabel[i] = edgeLabel[i - 1];
//            }
//        }
//        vertices[i] = v;
//        index[v] = i;
//        return numVertices++;
//    }
//
//    @Override
//    public void removeVertex(int v) {
//        int vi = indexOf(v);
//        if (vi < 0) {
//            throw new InvalidVertexException(v);
//        }
//        //remove all edges incident to v
//        int degv = degree[vi];
//        for (int i = degv - 1; i >= 0; i--) {
//            removeEdge(adjList[vi][i], v);
//        }
//        //shift everyhing left
//        for (int i = vi; i < numVertices - 1; i++) {
//            vertices[i] = vertices[i + 1];
//            degree[i] = degree[i + 1];
//            adjList[i] = adjList[i + 1];
//            if (vertexWeight != null) {
//                vertexWeight[i] = vertexWeight[i + 1];
//            }
//            if (edgeWeight != null) {
//                edgeWeight[i] = edgeWeight[i + 1];
//            }
//            if (vertexLabel != null) {
//                vertexLabel[i] = vertexLabel[i + 1];
//            }
//            if (edgeLabel != null) {
//                edgeLabel[i] = edgeLabel[i + 1];
//            }
//            index[vertices[i]]--;
//        }
//        index[v] = -1;
//        numVertices--;
//    }
//
//    @Override
//    public void addEdge(int v, int u) {
//        if (v == u) {
//            throw new IllegalArgumentException("Loops are not allowed: " + v);
//        }
//        if (containsEdge(v, u)) {
//            throw new IllegalArgumentException("Multiple edges are not allowed: " + v + "-" + u);
//        }
//        addToAdjList(v, u);
//        if (!directed) {
//            addToAdjList(u, v);
//        }
//        numEdges++;
//    }
//
//    //Adds u to the adjacency list of v
//    protected void addToAdjList(int v, int u) {
//        int vi = indexOf(v);
//        if (vi < 0) {
//            throw new InvalidVertexException(v);
//        }
//        if (adjList[vi] == null || degree[vi] == adjList[vi].length) {
//            growAdjList(v);
//        }
//        int k;
//        if (!sorted) {
//            //add the vertex at the end of the list
//            k = degree[vi];
//        } else {
//            //add the vertex at the correct position, to maintain order
//            k = Arrays.binarySearch(adjList[vi], 0, degree[vi], u);
//            if (k < 0) {
//                k = -k - 1;
//            }
//            //shift right from the insertion point
//            for (int j = k; j < degree[vi]; j++) {
//                adjList[vi][j + 1] = adjList[vi][j];
//                if (edgeWeight != null) {
//                    edgeWeight[vi][j + 1] = edgeWeight[vi][j];
//                }
//                if (edgeLabel != null) {
//                    edgeLabel[vi][j + 1] = edgeLabel[vi][j];
//                }
//            }
//        }
//        adjList[vi][k] = u;
//        degree[vi]++;
//        if (edgeBitSet != null) {
//            edgeBitSet.add(v, u);
//        }
//    }
//
//    @Override
//    public void removeEdge(int v, int u) {
//        removeFromAdjList(v, u);
//        if (!directed) {
//            removeFromAdjList(u, v);
//        }
//        numEdges--;
//    }
//
//    //Removes u from the adjacency list of v    
//    protected void removeFromAdjList(int v, int u) {
//        int vi = indexOf(v);
//        if (vi < 0) {
//            throw new InvalidVertexException(v);
//        }
//        int i = adjListPos(v, u);
//        if (i < 0) {
//            throw new InvalidEdgeException(v, u);
//        }
//        if (i < degree[vi] - 1) {
//            if (!sorted) {
//                //swap the vertex to be removed with the last one
//                adjList[vi][i] = adjList[vi][degree[vi] - 1];
//                if (edgeWeight != null) {
//                    edgeWeight[vi][i] = edgeWeight[vi][degree[vi] - 1];;
//                }
//                if (edgeLabel != null) {
//                    edgeLabel[vi][i] = edgeLabel[vi][degree[vi] - 1];;
//                }
//            } else {
//                //shift left
//                for (int j = i; j < degree[vi] - 1; j++) {
//                    adjList[vi][j] = adjList[vi][j + 1];
//                    if (edgeWeight != null) {
//                        edgeWeight[vi][j] = edgeWeight[vi][j + 1];
//                    }
//                    if (edgeLabel != null) {
//                        edgeLabel[vi][j] = edgeLabel[vi][j + 1];
//                    }
//                }
//            }
//        }
//        if (edgeBitSet != null) {
//            edgeBitSet.remove(v, u);
//        }
//        degree[vi]--;
//    }
//
//    /*
//    Returns the position of u in the neighbor list of v.
//     */
//    protected int adjListPos(int v, int u) {
//        int vi = indexOf(v);
//        if (adjList[vi] == null) {
//            return -1;
//        }
//        if (sorted) {
//            return Arrays.binarySearch(adjList[vi], 0, degree[vi], u);
//        }
//        for (int i = 0; i < degree[vi]; i++) {
//            if (adjList[vi][i] == u) {
//                return i;
//            }
//        }
//        return -1;
//    }
//
//    @Override
//    public int numVertices() {
//        return numVertices;
//    }
//
//    @Override
//    public int numEdges() {
//        return numEdges;
//    }
//
//    @Override
//    public int[] neighbors(int v) {
//        int vi = indexOf(v);
//        if (vi < 0) {
//            throw new InvalidVertexException(v);
//        }
//        if (adjList[vi] == null || adjList[vi].length != degree[vi]) {
//            int[] copy = new int[degree[vi]];
//            if (adjList[vi] != null) {
//                System.arraycopy(adjList[vi], 0, copy, 0, degree[vi]);
//            }
//            adjList[vi] = copy;
//        }
//        return adjList[vi];
//    }
//
//    @Override
//    public boolean containsEdge(int v, int u) {
//        if (edgeBitSet != null) {
//            return edgeBitSet.contains(v, u);
//        }
//        return adjListPos(v, u) >= 0;
//    }
//
//    @Override
//    public int degree(int v) {
//        int vi = indexOf(v);
//        if (vi < 0) {
//            throw new InvalidVertexException(v);
//        }
//        return degree[indexOf(v)];
//    }
//
//    private void initVertexWeights() {
//        this.vertexWeight = new double[vertices.length];
//    }
//
//    private void initEdgeWeights() {
//        this.edgeWeight = new double[vertices.length][];
//        for (int i = 0; i < numVertices; i++) {
//            this.edgeWeight[i] = adjList[i] == null ? null : new double[adjList[i].length];
//        }
//    }
//
//    @Override
//    public void addVertex(int v, double weight) {
//        addVertex(v);
//        setVertexWeight(v, weight);
//    }
//
//    @Override
//    public void addEdge(int v, int u, double weight) {
//        addEdge(v, u);
//        setEdgeWeight(v, u, weight);
//    }
//
//    @Override
//    public void setVertexWeight(int v, double weight) {
//        checkVertex(v);
//        if (vertexWeight == null) {
//            initVertexWeights();
//        }
//        vertexWeight[indexOf(v)] = weight;
//    }
//
//    @Override
//    public double getVertexWeight(int v) {
//        checkVertex(v);
//        if (vertexWeight == null) {
//            return 0;
//        }
//        return vertexWeight[indexOf(v)];
//    }
//
//    @Override
//    public void setEdgeWeight(int v, int u, double weight) {
//        if (edgeWeight == null) {
//            initEdgeWeights();
//        }
//        checkVertex(v);
//        edgeWeight[indexOf(v)][adjListPos(v, u)] = weight;
//        if (v != u && !directed) {
//            checkVertex(u);
//            edgeWeight[indexOf(u)][adjListPos(u, v)] = weight;
//        }
//    }
//
//    @Override
//    public double getEdgeWeight(int v, int u) {
//        checkVertex(v);
//        if (edgeWeight == null) {
//            return 0;
//        }
//        return edgeWeight[indexOf(v)][adjListPos(v, u)];
//    }
//
//    //
//    private void initVertexLabels() {
//        this.vertexLabel = new Object[numVertices];
//    }
//
//    private void initEdgeLabels() {
//        this.edgeLabel = new Object[vertices.length][];
//        for (int i = 0; i < numVertices; i++) {
//            this.edgeLabel[i] = adjList[i] == null ? null : new Object[adjList[i].length];
//        }
//    }
//
//    @Override
//    public void addVertex(int v, V label) {
//        addVertex(v);
//        setVertexLabel(v, label);
//    }
//
//    @Override
//    public void addEdge(int v, int u, E label) {
//        addEdge(v, u);
//        setEdgeLabel(v, u, label);
//    }
//
//    @Override
//    public void setVertexLabel(int v, V label) {
//        checkVertex(v);
//        if (vertexLabel == null) {
//            initVertexLabels();
//        }
//        vertexLabel[indexOf(v)] = label;
//    }
//
//    @Override
//    public V getVertexLabel(int v) {
//        checkVertex(v);
//        if (vertexLabel == null) {
//            return null;
//        }
//        return (V) vertexLabel[indexOf(v)];
//    }
//
//    @Override
//    public void setEdgeLabel(int v, int u, E label) {
//        if (edgeLabel == null) {
//            initEdgeLabels();
//        }
//        checkVertex(v);
//        edgeLabel[indexOf(v)][adjListPos(v, u)] = label;
//        if (v != u && !directed) {
//            checkVertex(u);
//            edgeLabel[indexOf(u)][adjListPos(u, v)] = label;
//        }
//    }
//
//    @Override
//    public E getEdgeLabel(int v, int u) {
//        checkVertex(v);
//        if (edgeLabel == null) {
//            return null;
//        }
//        return (E) edgeLabel[indexOf(v)][adjListPos(v, u)];
//    }
//
//    @Override
//    public boolean isSorted() {
//        return sorted;
//    }
//
//    public boolean isDirected() {
//        return directed;
//    }
//
//    //Expands the array holding the vertices
//    //Expands the array holding the vertex degrees
//    //Expands the array holding adjacency lists
//    protected void growVertices() {
//        int oldLen = vertices.length;
//        int newLen = Math.max(DEFAULT_NUM_VERTICES, oldLen + (oldLen >> 1));
//        vertices = Arrays.copyOf(vertices, newLen);
//        degree = Arrays.copyOf(degree, newLen);
//        //
//        int[][] tempAdj = new int[newLen][];
//        System.arraycopy(adjList, 0, tempAdj, 0, oldLen);
//        adjList = tempAdj;
//        //
//        if (vertexWeight != null) {
//            vertexWeight = Arrays.copyOf(vertexWeight, vertices.length);
//        }
//        if (edgeWeight != null) {
//            edgeWeight = Arrays.copyOf(edgeWeight, vertices.length);
//        }
//    }
//
//    //Expands the array holding the adjacency list of v.
//    protected void growAdjList(int v) {
//        int vi = indexOf(v);
//        int oldLen = degree[vi];
//        int newLen = Math.max(avgDegree, oldLen + (oldLen >> 1) + 1);
//        int[] copyAdjList = new int[newLen];
//        if (adjList[vi] != null) {
//            System.arraycopy(adjList[vi], 0, copyAdjList, 0, oldLen);
//        }
//        adjList[vi] = copyAdjList;
//        //
//        if (edgeWeight != null) {
//            double[] copyEdgeWeights = new double[adjList[vi].length];
//            if (edgeWeight[vi] != null) {
//                System.arraycopy(edgeWeight[vi], 0, copyEdgeWeights, 0, edgeWeight[vi].length);
//            }
//            edgeWeight[vi] = copyEdgeWeights;
//        }
//    }
//
//    //Expands the array holding the indices corresponding to vertex numbers.
//    protected void growIndices(int v) {
//        int oldLen = index.length;
//        int newLen = v + (vertices.length >> 1);
//        index = Arrays.copyOf(index, newLen);
//        Arrays.fill(index, oldLen, newLen, -1);
//    }
//
//    @Override
//    public List<Edge> edgeList() {
//        List<Edge> list = new ArrayList<>((int) numEdges());
//        for (int e[] : edges()) {
//            list.add(new Edge(e[0], e[1], directed,
//                    edgeWeight != null ? getEdgeWeight(e[0], e[1]) : null));
//        }
//        return list;
//    }
//
//    @Override
//    public int[][] edges() {
//        int[][] edges = new int[numEdges][2];
//        int i = 0;
//        for (int v : vertices()) {
//            for (int u : neighbors(v)) {
//                if (directed || v <= u) {
//                    edges[i][0] = v;
//                    edges[i][1] = u;
//                    i++;
//                }
//            }
//        }
//        return edges;
//    }
//
//    @Override
//    public GraphImpl<V, E> subgraph(List<Integer> list) {
//        int n = list.size();
//        int deg = (int) list.stream().mapToInt(v -> this.degree(v)).average().orElse(0);
//        var sub = new GraphImpl(Tools.listAsArray(list), n, deg, sorted, directed);
//        for (int v : list) {
//            for (int u : neighbors(v)) {
//                if (v != u && (directed || v < u) && list.contains(u)) {
//                    sub.addEdge(v, u);
//                    if (edgeWeight != null) {
//                        sub.setEdgeWeight(v, u, getEdgeWeight(v, u));
//                    }
//                    if (edgeLabel != null) {
//                        sub.setEdgeLabel(v, u, getEdgeLabel(v, u));
//                    }
//                }
//            }
//        }
//        return sub;
//    }
//
//    @Override
//    public GraphImpl complement() {
//        int avg = (int) IntStream.of(degree).map(deg -> numVertices - deg).average().orElse(0);
//        var complement = new GraphImpl(vertices, numVertices, avg, sorted, directed);
//        for (int i = 0; i < numVertices - 1; i++) {
//            int u = vertices[i];
//            for (int j = i + 1; j < numVertices; j++) {
//                int v = vertices[j];
//                if (!containsEdge(v, u)) {
//                    complement.addEdge(v, u);
//                }
//            }
//        }
//        return complement;
//    }
//
//    @Override
//    public int duplicateVertex(int v) {
//        int newVertex = addVertex();
//        for (int u : neighbors(v)) {
//            addEdge(newVertex, u);
//            if (vertexWeight != null) {
//                setVertexWeight(v, getVertexWeight(v));
//            }
//            if (vertexLabel != null) {
//                setVertexLabel(v, getVertexLabel(v));
//            }
//        }
//        return newVertex;
//    }
//
//    @Override
//    public int contractVertices(int... vertices) {
//        int newVertex = addVertex();
//        for (int v : vertices) {
//            for (int u : neighbors(v)) {
//                if (!Tools.arrayContains(vertices, u) && !containsEdge(newVertex, u)) {
//                    addEdge(newVertex, u);
//                }
//            }
//        }
//        removeVertices(vertices);
//        return newVertex;
//    }
//
//    @Override
//    public int splitEdge(int v, int u) {
//        if (!containsEdge(v, u)) {
//            throw new InvalidEdgeException(v, u);
//        }
//        int newVertex = addVertex();
//        removeEdge(v, u);
//        addEdge(v, newVertex);
//        addEdge(newVertex, u);
//        return newVertex;
//    }
//
//    protected String edgesToString() {
//        var sb = new StringBuilder();
//        sb.append("[");
//        int i = 0;
//        for (int[] e : edges()) {
//            int v = e[0];
//            int u = e[1];
//            if (i++ > 0) {
//                sb.append(", ");
//            }
//            sb.append(v).append(directed ? "->" : "-").append(u);
//            if (edgeLabel != null) {
//                var label = getEdgeLabel(v, u);
//                if (label != null) {
//                    sb.append(":").append(getEdgeLabel(v, u));
//                }
//            }
//            if (edgeWeight != null) {
//                sb.append("=").append(getEdgeWeight(v, u));
//            }
//        }
//        sb.append("]");
//        return sb.toString();
//    }
//
//    protected String verticesToString() {
//        var sb = new StringBuilder();
//        sb.append("[");
//        for (int i = 0; i < numVertices; i++) {
//            if (i > 0) {
//                sb.append(", ");
//            }
//            sb.append(vertices[i]);
//            if (vertexLabel != null) {
//                var label = getVertexLabel(vertices[i]);
//                if (label != null) {
//                    sb.append(":").append(label);
//                }
//            }
//            if (vertexWeight != null) {
//                sb.append("=").append(getVertexWeight(vertices[i]));
//            }
//        }
//        sb.append("]");
//        return sb.toString();
//    }
//
//    @Override
//    public String toString() {
//        var sb = new StringBuilder();
//        if (numVertices() <= 10) {
//            sb.append("V=").append(verticesToString());
//            sb.append(", E=").append(edgesToString());
//        } else {
//            sb.append("|V|=").append(numVertices());
//            sb.append(", |E|=").append(numEdges());
//        }
//        return sb.toString();
//    }
//
//}
