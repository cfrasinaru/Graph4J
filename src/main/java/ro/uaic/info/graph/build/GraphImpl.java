/*
 * Copyright (C) 2022 Faculty of Computer Science Iasi, Romania
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
package ro.uaic.info.graph.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.InvalidEdgeException;
import ro.uaic.info.graph.InvalidVertexException;
import ro.uaic.info.graph.util.Tools;

/**
 * A generic, array based implementation of a graph. The graph may be simple,
 * directed, weighted. Self loops or multiple edges are not supported.
 *
 * This class avoids the overhead of using too many objects by representing the
 * data in the graph as primitive structures.
 *
 * @author Cristian Frăsinaru
 */
class GraphImpl<V, E> implements Graph<V, E> {

    protected int maxVertices; //maximum number of vertices
    protected int numVertices; //number of vertices
    protected int numEdges;  //number of edges
    protected int[] vertices; //vertices[i] is the vertex with the index i
    protected int[] index; //index[v] is the index of the vertex v
    protected int[][] adjList; //adjList[i] is the adjacency list of the vertex with index i
    protected int[] degree; //degree[i] is the degree of the vertex with index i
    protected double[] vertexWeight;
    protected double[][] edgeWeight;
    protected Object[] vertexLabel;
    protected Object[][] edgeLabel;
    protected EdgeBitSet edgeBitSet;//for fast check if an edge is present in the graph

    //
    protected boolean sorted; //true if the adjacency lists are maintained sorted
    protected boolean directed;
    protected boolean allowsMultiEdges;
    protected boolean allowsSelfLoops;
    //
    protected int avgDegree; //this may improve the memory allocation
    protected static final int DEFAULT_NUM_VERTICES = 100;
    protected static final int DEFAULT_AVG_DEGREE = 10;

    protected GraphImpl() {
    }

    /**
     *
     * @param vertices
     * @param avgDegree
     * @param sorted
     * @param directed
     * @param weighted
     */
    protected GraphImpl(int[] vertices, int maxVertices, int avgDegree,
            boolean sorted, boolean directed, boolean allowsMultiEdges, boolean allowsSelfLoops) {
        if (maxVertices < numVertices) {
            throw new IllegalArgumentException("Invalid maximum number of vertices: " + maxVertices);
        }
        if (avgDegree == 0) {
            avgDegree = DEFAULT_AVG_DEGREE;
        } else {
            if (avgDegree < 0) {
                throw new IllegalArgumentException("Invalid vertex average degree: " + avgDegree);
            }
        }
        this.maxVertices = maxVertices;
        this.numVertices = vertices.length;
        this.avgDegree = avgDegree;
        this.sorted = sorted;
        this.directed = directed;
        this.allowsMultiEdges = allowsMultiEdges;
        this.allowsSelfLoops = allowsSelfLoops;
        //
        this.index = new int[1 + IntStream.of(vertices).max().orElse(maxVertices)];
        Arrays.fill(index, -1);
        this.vertices = new int[maxVertices];
        for (int i = 0; i < numVertices; i++) {
            this.vertices[i] = vertices[i];
            index[vertices[i]] = i;
        }
        this.degree = new int[maxVertices];
        this.adjList = new int[maxVertices][];
        this.numEdges = 0;
        if (maxVertices > 0) {
            this.edgeBitSet = new EdgeBitSet(maxVertices);
        }
    }

    protected GraphImpl newInstance() {
        return new GraphImpl();
    }

    protected GraphImpl newInstance(int[] vertices, int maxVertices, int avgDegree,
            boolean sorted, boolean directed, boolean allowsMultiEdges, boolean allowsSelfLoops) {
        return new GraphImpl(vertices, maxVertices, avgDegree, sorted, directed, allowsMultiEdges, allowsSelfLoops);
    }

    @Override
    public Graph<V, E> copy() {
        var copy = newInstance();
        copy.numVertices = numVertices;
        copy.numEdges = numEdges;
        copy.avgDegree = avgDegree;
        copy.sorted = sorted;
        copy.directed = directed;
        copy.allowsMultiEdges = allowsMultiEdges;
        copy.allowsSelfLoops = allowsSelfLoops;
        copy.vertices = Arrays.copyOf(vertices, numVertices);
        copy.index = Arrays.copyOf(index, index.length);
        copy.degree = Arrays.copyOf(degree, numVertices);
        if (vertexWeight != null) {
            copy.vertexWeight = Arrays.copyOf(vertexWeight, numVertices);
        }
        if (vertexLabel != null) {
            copy.vertexLabel = Arrays.copyOf(vertexLabel, numVertices);
        }
        copy.adjList = new int[numVertices][];
        for (int i = 0; i < numVertices; i++) {
            if (adjList[i] != null) {
                copy.adjList[i] = Arrays.copyOf(adjList[i], adjList[i].length);
                if (edgeWeight != null) {
                    copy.edgeWeight[i] = Arrays.copyOf(edgeWeight[i], edgeWeight[i].length);
                }
                if (edgeLabel != null) {
                    copy.edgeLabel[i] = Arrays.copyOf(edgeLabel[i], edgeLabel[i].length);
                }
            }
        }
        return copy;
    }

    @Override
    public void renumberAdding(int amount) {
        growIndices(index.length + amount);
        for (int i = 0; i < numVertices; i++) {
            index[vertices[i]] = -1;
            vertices[i] = vertices[i] + amount;
            index[vertices[i]] = i;
        }
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < adjList[i].length; j++) {
                adjList[i][j] = adjList[i][j] + amount;
            }
        }
    }

    @Override
    public int[] vertices() {
        if (vertices.length != numVertices) {
            vertices = Arrays.copyOf(vertices, numVertices);
        }
        return vertices;
    }

    @Override
    public int vertexAt(int index) {
        return vertices[index];
    }

    @Override
    public int indexOf(int v) {
        if (v < 0 || v >= index.length) {
            throw new InvalidVertexException(v);
        }
        return index[v];
    }

    protected void checkVertex(int v) {
        if (indexOf(v) < 0) {
            throw new InvalidVertexException(v);
        }
    }

    protected void checkEdge(int v, int u) {
        if (!containsEdge(v, u)) {
            throw new InvalidEdgeException(v, u);
        }
    }

    /**
     * The vertices are maintained sorted.
     *
     * @param v
     * @return the index of the added vertex
     */
    @Override
    public int addVertex(int v) {
        if (v < 0) {
            throw new InvalidVertexException(v, "Vertex numbers must be positive");
        }
        if (numVertices == maxVertices) {
            edgeBitSet = null;
        }
        if (numVertices == vertices.length) {
            growVertices();
        }
        if (v >= index.length) {
            growIndices(v);
        }
        int i = Arrays.binarySearch(vertices, 0, numVertices, v);
        if (i >= 0) {
            throw new InvalidVertexException(v, "Vertex is already in the graph");
        }
        i = -i - 1; //insertion position
        //shift everything rigth
        for (int j = i + 1; j < numVertices; j++) {
            vertices[i] = vertices[i - 1];
            adjList[i] = adjList[i - 1];
            degree[i] = degree[i - 1];
            if (vertexWeight != null) {
                vertexWeight[i] = vertexWeight[i - 1];
            }
            if (edgeWeight != null) {
                edgeWeight[i] = edgeWeight[i - 1];
            }
            if (vertexLabel != null) {
                vertexLabel[i] = vertexLabel[i - 1];
            }
            if (edgeLabel != null) {
                edgeLabel[i] = edgeLabel[i - 1];
            }
        }
        vertices[i] = v;
        index[v] = i;
        return numVertices++;
    }

    @Override
    public void removeVertex(int v) {
        int vi = indexOf(v);
        if (vi < 0) {
            throw new InvalidVertexException(v);
        }
        //remove all edges incident to v
        int degv = degree[vi];
        for (int i = degv - 1; i >= 0; i--) {
            removeEdge(adjList[vi][i], v);
        }
        //shift everyhing left
        for (int i = vi; i < numVertices - 1; i++) {
            vertices[i] = vertices[i + 1];
            degree[i] = degree[i + 1];
            adjList[i] = adjList[i + 1];
            if (vertexWeight != null) {
                vertexWeight[i] = vertexWeight[i + 1];
            }
            if (edgeWeight != null) {
                edgeWeight[i] = edgeWeight[i + 1];
            }
            if (vertexLabel != null) {
                vertexLabel[i] = vertexLabel[i + 1];
            }
            if (edgeLabel != null) {
                edgeLabel[i] = edgeLabel[i + 1];
            }
            index[vertices[i]]--;
        }
        index[v] = -1;
        numVertices--;
    }

    @Override
    public void addEdge(int v, int u) {
        addEdge(v, u, null, null);
    }

    protected void addEdge(int v, int u, Double weight, E label) {
        System.out.println("adding edge " + v + "-" + u);
        if (!allowsSelfLoops && v == u) {
            throw new IllegalArgumentException("Loops are not allowed: " + v);
        }
        if (!allowsMultiEdges && containsEdge(v, u)) {
            throw new IllegalArgumentException("Multiple edges are not allowed: " + v + "-" + u);
        }
        addToAdjList(v, u, weight, label);
        if (v != u && !directed) {
            addToAdjList(u, v, weight, label);
        }
        numEdges++;
    }

    //Adds u to the adjacency list of v
    protected int addToAdjList(int v, int u, Double weight, E label) {
        int vi = indexOf(v);
        if (vi < 0) {
            throw new InvalidVertexException(v);
        }
        if (adjList[vi] == null || degree[vi] == adjList[vi].length) {
            growAdjList(v);
        }
        int pos;
        if (!sorted) {
            //add the vertex at the end of the list
            pos = degree[vi];
        } else {
            //add the vertex at the correct position, to maintain order
            pos = Arrays.binarySearch(adjList[vi], 0, degree[vi], u);
            if (pos < 0) {
                pos = -pos - 1;
            }
            //shift right from the insertion point
            for (int j = pos; j < degree[vi]; j++) {
                adjList[vi][j + 1] = adjList[vi][j];
                if (edgeWeight != null) {
                    edgeWeight[vi][j + 1] = edgeWeight[vi][j];
                }
                if (edgeLabel != null) {
                    edgeLabel[vi][j + 1] = edgeLabel[vi][j];
                }
            }
        }
        adjList[vi][pos] = u;
        degree[vi]++;
        if (edgeBitSet != null) {
            edgeBitSet.add(v, u);
        }
        if (weight != null) {
            if (edgeWeight == null) {
                initEdgeWeights();
            }
            edgeWeight[vi][pos] = weight;
        }
        if (label != null) {
            if (edgeLabel == null) {
                initEdgeLabels();
            }
            edgeLabel[vi][pos] = label;
        }
        return pos;
    }

    @Override
    public void removeEdge(int v, int u) {
        removeFromAdjList(v, u);
        if (v != u && !directed) {
            removeFromAdjList(u, v);
        }
        numEdges--;
    }

    //Removes u from the adjacency list of v    
    protected void removeFromAdjList(int v, int u) {
        int vi = indexOf(v);
        if (vi < 0) {
            throw new InvalidVertexException(v);
        }
        int i = adjListPos(v, u);
        if (i < 0) {
            throw new InvalidEdgeException(v, u);
        }
        if (i < degree[vi] - 1) {
            if (!sorted) {
                //swap the vertex to be removed with the last one
                adjList[vi][i] = adjList[vi][degree[vi] - 1];
                if (edgeWeight != null) {
                    edgeWeight[vi][i] = edgeWeight[vi][degree[vi] - 1];;
                }
                if (edgeLabel != null) {
                    edgeLabel[vi][i] = edgeLabel[vi][degree[vi] - 1];;
                }
            } else {
                //shift left
                for (int j = i; j < degree[vi] - 1; j++) {
                    adjList[vi][j] = adjList[vi][j + 1];
                    if (edgeWeight != null) {
                        edgeWeight[vi][j] = edgeWeight[vi][j + 1];
                    }
                    if (edgeLabel != null) {
                        edgeLabel[vi][j] = edgeLabel[vi][j + 1];
                    }
                }
            }
        }
        if (edgeBitSet != null) {
            edgeBitSet.remove(v, u);
        }
        degree[vi]--;
    }

    /*
    Returns the first position of u in the neighbor list of v.
     */
    protected int adjListPos(int v, int u) {
        int vi = indexOf(v);
        if (adjList[vi] == null) {
            return -1;
        }
        if (sorted) {
            return Arrays.binarySearch(adjList[vi], 0, degree[vi], u);
        }
        for (int i = 0; i < degree[vi]; i++) {
            if (adjList[vi][i] == u) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int numVertices() {
        return numVertices;
    }

    @Override
    public int numEdges() {
        return numEdges;
    }

    @Override
    public int[] neighbors(int v) {
        int vi = indexOf(v);
        if (vi < 0) {
            throw new InvalidVertexException(v);
        }
        if (adjList[vi] == null || adjList[vi].length != degree[vi]) {
            int[] copy = new int[degree[vi]];
            if (adjList[vi] != null) {
                System.arraycopy(adjList[vi], 0, copy, 0, degree[vi]);
            }
            adjList[vi] = copy;
        }
        return adjList[vi];
    }

    @Override
    public boolean containsEdge(int v, int u) {
        checkVertex(v);
        checkVertex(u);
        if (edgeBitSet != null) {
            return edgeBitSet.contains(v, u);
        }
        return adjListPos(v, u) >= 0;
    }

    @Override
    public int degree(int v) {
        int vi = indexOf(v);
        if (vi < 0) {
            throw new InvalidVertexException(v);
        }
        return degree[indexOf(v)];
    }

    private void initVertexWeights() {
        this.vertexWeight = new double[vertices.length];
    }

    private void initEdgeWeights() {
        this.edgeWeight = new double[vertices.length][];
        for (int i = 0; i < numVertices; i++) {
            this.edgeWeight[i] = adjList[i] == null ? null : new double[adjList[i].length];
        }
    }

    @Override
    public int addWeightedVertex(int v, double weight) {
        int vi = addVertex(v);
        setVertexWeight(v, weight);
        return vi;
    }

    @Override
    public int addWeightedVertex(double weight) {
        int v = addVertex();
        setVertexWeight(v, weight);
        return v;
    }

    @Override
    public void addWeightedEdge(int v, int u, double weight) {
        addEdge(v, u);
        setEdgeWeight(v, u, weight);
    }

    @Override
    public void setVertexWeight(int v, double weight) {
        checkVertex(v);
        if (vertexWeight == null) {
            initVertexWeights();
        }
        vertexWeight[indexOf(v)] = weight;
    }

    @Override
    public double getVertexWeight(int v) {
        checkVertex(v);
        if (vertexWeight == null) {
            return 0;
        }
        return vertexWeight[indexOf(v)];
    }

    @Override
    public void setEdgeWeight(int v, int u, double weight) {
        checkEdge(v, u);
        if (edgeWeight == null) {
            initEdgeWeights();
        }
        edgeWeight[indexOf(v)][adjListPos(v, u)] = weight;
        if (v != u && !directed) {
            edgeWeight[indexOf(u)][adjListPos(u, v)] = weight;
        }
    }

    @Override
    public double getEdgeWeight(int v, int u) {
        checkEdge(v, u);
        if (edgeWeight == null) {
            return 0;
        }
        return edgeWeight[indexOf(v)][adjListPos(v, u)];
    }

    //
    private void initVertexLabels() {
        this.vertexLabel = new Object[numVertices];
    }

    private void initEdgeLabels() {
        this.edgeLabel = new Object[vertices.length][];
        for (int i = 0; i < numVertices; i++) {
            this.edgeLabel[i] = adjList[i] == null ? null : new Object[adjList[i].length];
        }
    }

    @Override
    public int addLabeledVertex(int v, V label) {
        int vi = addVertex(v);
        setVertexLabel(v, label);
        return vi;
    }

    @Override
    public int addLabeledVertex(V label) {
        int v = addVertex();
        setVertexLabel(v, label);
        return v;
    }

    @Override
    public void addLabeledEdge(int v, int u, E label) {
        addEdge(v, u, null, label);
    }

    @Override
    public void setVertexLabel(int v, V label) {
        checkVertex(v);
        if (vertexLabel == null) {
            initVertexLabels();
        }
        vertexLabel[indexOf(v)] = label;
    }

    @Override
    public V getVertexLabel(int v) {
        checkVertex(v);
        if (vertexLabel == null) {
            return null;
        }
        return (V) vertexLabel[indexOf(v)];
    }

    @Override
    public void setEdgeLabel(int v, int u, E label) {
        checkEdge(v, u);
        if (edgeLabel == null) {
            initEdgeLabels();
        }
        edgeLabel[indexOf(v)][adjListPos(v, u)] = label;
        if (v != u && !directed) {
            edgeLabel[indexOf(u)][adjListPos(u, v)] = label;
        }
    }

    @Override
    public E getEdgeLabel(int v, int u) {
        checkEdge(v, u);
        if (edgeLabel == null) {
            return null;
        }
        return (E) edgeLabel[indexOf(v)][adjListPos(v, u)];
    }

    @Override
    public boolean isSorted() {
        return sorted;
    }

    public boolean isDirected() {
        return directed;
    }

    //Expands the array holding the vertices
    //Expands the array holding the vertex degrees
    //Expands the array holding adjacency lists
    protected void growVertices() {
        int oldLen = vertices.length;
        int newLen = Math.max(DEFAULT_NUM_VERTICES, oldLen + (oldLen >> 1));
        vertices = Arrays.copyOf(vertices, newLen);
        degree = Arrays.copyOf(degree, newLen);
        //
        int[][] tempAdj = new int[newLen][];
        System.arraycopy(adjList, 0, tempAdj, 0, oldLen);
        adjList = tempAdj;
        //
        if (vertexWeight != null) {
            vertexWeight = Arrays.copyOf(vertexWeight, vertices.length);
        }
        if (edgeWeight != null) {
            edgeWeight = Arrays.copyOf(edgeWeight, vertices.length);
        }
        if (vertexLabel != null) {
            vertexLabel = Arrays.copyOf(vertexLabel, vertices.length);
        }
        if (edgeLabel != null) {
            edgeLabel = Arrays.copyOf(edgeLabel, vertices.length);
        }
    }

    //Expands the array holding the adjacency list of v.
    protected void growAdjList(int v) {
        int vi = indexOf(v);
        int oldLen = degree[vi];
        int newLen = Math.max(avgDegree, oldLen + (oldLen >> 1) + 1);
        int[] copyAdjList = new int[newLen];
        if (adjList[vi] != null) {
            System.arraycopy(adjList[vi], 0, copyAdjList, 0, oldLen);
        }
        adjList[vi] = copyAdjList;
        //
        if (edgeWeight != null) {
            double[] copyEdgeWeights = new double[adjList[vi].length];
            if (edgeWeight[vi] != null) {
                System.arraycopy(edgeWeight[vi], 0, copyEdgeWeights, 0, edgeWeight[vi].length);
            }
            edgeWeight[vi] = copyEdgeWeights;
        }
        if (edgeLabel != null) {
            Object[] copyEdgeLabels = new Object[adjList[vi].length];
            if (edgeLabel[vi] != null) {
                System.arraycopy(edgeLabel[vi], 0, copyEdgeLabels, 0, edgeLabel[vi].length);
            }
            edgeLabel[vi] = copyEdgeLabels;
        }
    }

    //Expands the array holding the indices corresponding to vertex numbers.
    protected void growIndices(int v) {
        int oldLen = index.length;
        int newLen = v + (vertices.length >> 1);
        index = Arrays.copyOf(index, newLen);
        Arrays.fill(index, oldLen, newLen, -1);
    }

    @Override
    public List<Edge> edgeList() {
        List<Edge> list = new ArrayList<>((int) numEdges());
        for (int e[] : edges()) {
            list.add(new Edge(e[0], e[1], directed,
                    edgeWeight != null ? getEdgeWeight(e[0], e[1]) : null));
        }
        return list;
    }

    @Override
    public int[][] edges() {
        int[][] edges = new int[numEdges][2];
        int i = 0;
        for (int v : vertices()) {
            for (int u : neighbors(v)) {
                if (directed || v <= u) {
                    edges[i][0] = v;
                    edges[i][1] = u;
                    i++;
                }
            }
        }
        return edges;
    }

    @Override
    public Graph<V, E> subgraph(int... vertices) {
        int n = vertices.length;
        int deg = (int) IntStream.of(vertices).map(v -> this.degree(v)).average().orElse(0);
        var sub = newInstance(vertices, n, deg, sorted, directed, allowsMultiEdges, allowsSelfLoops);
        for (int v : vertices) {
            int vi = index[v];
            for (int j = 0; j < degree[v]; j++) {
                int u = adjList[vi][j];
                if ((directed || v <= u) && Tools.arrayContains(vertices, u)) {
                    sub.addEdge(v, u,
                            edgeWeight != null ? edgeWeight[vi][j] : null,
                            edgeLabel != null ? edgeLabel[vi][j] : null);
                }
            }
        }
        return sub;
    }

    public Graph<V, E> supportGraph() {
        var copy = newInstance(vertices, maxVertices, avgDegree,
                sorted, false, false, false);
        for (int v : this.vertices()) {
            for (int u : neighbors(v)) {
                if (v < u && !copy.containsEdge(v, u)) {
                    copy.addEdge(v, u);
                }
            }
        }
        return (Graph) copy;
    }

    @Override
    public Graph<V, E> complement() {
        if (allowsMultiEdges || allowsSelfLoops) {
            throw new UnsupportedOperationException("Complement of a multigraph or pseudograph is not defined.");
        }
        int avg = (int) IntStream.of(degree).map(deg -> numVertices - deg).average().orElse(0);
        var complement = newInstance(vertices, numVertices, avg, sorted, directed, allowsMultiEdges, allowsSelfLoops);
        for (int i = 0; i < numVertices - 1; i++) {
            int u = vertices[i];
            for (int j = i + 1; j < numVertices; j++) {
                int v = vertices[j];
                if (!containsEdge(v, u)) {
                    complement.addEdge(v, u);
                }
            }
        }
        return complement;
    }

    @Override
    public int duplicateVertex(int v) {
        int newVertex = addVertex();
        for (int u : neighbors(v)) {
            addEdge(newVertex, u);
            if (vertexWeight != null) {
                setVertexWeight(v, getVertexWeight(v));
            }
            if (vertexLabel != null) {
                setVertexLabel(v, getVertexLabel(v));
            }
        }
        return newVertex;
    }

    @Override
    public int contractVertices(int... vertices) {
        int newVertex = addVertex();
        for (int v : vertices) {
            for (int u : neighbors(v)) {
                if (!Tools.arrayContains(vertices, u) && !containsEdge(newVertex, u)) {
                    addEdge(newVertex, u);
                }
            }
        }
        removeVertices(vertices);
        return newVertex;
    }

    @Override
    public int splitEdge(int v, int u) {
        checkEdge(v, u);
        int newVertex = addVertex();
        removeEdge(v, u);
        addEdge(v, newVertex);
        addEdge(newVertex, u);
        return newVertex;
    }

    protected String edgesToString() {
        var sb = new StringBuilder();
        sb.append("[");
        int i = 0;
        for (int[] e : edges()) {
            int v = e[0];
            int u = e[1];
            if (i++ > 0) {
                sb.append(", ");
            }
            sb.append(v).append(directed ? "->" : "-").append(u);
            if (edgeLabel != null) {
                var label = getEdgeLabel(v, u);
                if (label != null) {
                    sb.append(":").append(getEdgeLabel(v, u));
                }
            }
            if (edgeWeight != null) {
                sb.append("=").append(getEdgeWeight(v, u));
            }
        }
        sb.append("]");
        return sb.toString();
    }

    protected String verticesToString() {
        var sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < numVertices; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(vertices[i]);
            if (vertexLabel != null) {
                var label = getVertexLabel(vertices[i]);
                if (label != null) {
                    sb.append(":").append(label);
                }
            }
            if (vertexWeight != null) {
                sb.append("=").append(getVertexWeight(vertices[i]));
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        if (numVertices() <= 10) {
            sb.append("V=").append(verticesToString());
            sb.append(", E=").append(edgesToString());
        } else {
            sb.append("|V|=").append(numVertices());
            sb.append(", |E|=").append(numEdges());
        }
        return sb.toString();
    }

}
