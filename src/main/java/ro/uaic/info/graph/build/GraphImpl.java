/*
 * Copyright (C) 2022 Cristian Frăsinaru and contributors
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.InvalidEdgeException;
import ro.uaic.info.graph.InvalidVertexException;
import ro.uaic.info.graph.util.CheckArguments;
import ro.uaic.info.graph.util.IntArrays;
import ro.uaic.info.graph.NeighborIterator;

/**
 * A generic, array based implementation of a graph. The graph may be simple,
 * directed, weighted. Self loops or multiple edges are not supported.
 *
 * This class avoids the overhead of using too many objects by representing the
 * data in the graph as primitive structures.
 *
 * @author Cristian Frăsinaru
 */
public class GraphImpl<V, E> implements Graph<V, E> {

    protected String name;
    protected int maxVertices; //maximum number of vertices
    protected int numVertices; //number of vertices
    protected int numEdges;  //number of edges
    protected int[] vertices; //vertices[i] is the vertex with the index i
    protected int[][] adjList; //adjList[i] is the adjacency list of the vertex with index i
    protected int[][] adjPos; //adjPos[i][j]=the position of v=vertices[i] in the adjacency list of u=adjList[i][j]
    protected int[] degree; //degree[i] is the (out)degree of the vertex with index i
    //protected int[] indegree;
    protected Map<Integer, Integer> selfLoops;
    protected double[] vertexWeight;
    protected double[][] edgeWeight;
    protected Object[] vertexLabel;
    protected Object[][] edgeLabel;
    private VertexContainer vertexContainer; //to find the index of a vertex
    private EdgeContainer edgeContainer;//for fast check if an edge is present in the graph

    //
    protected boolean sorted; //true if the adjacency lists are maintained sorted
    protected boolean directed;
    protected boolean allowingMultipleEdges;
    protected boolean allowingSelfLoops;
    //
    protected int avgDegree; //this may improve the memory allocation
    protected static final int DEFAULT_NUM_VERTICES = 100;
    protected static final int DEFAULT_AVG_DEGREE = 10;

    protected GraphImpl() {
    }

    protected GraphImpl(int numVertices, int maxVertices, int avgDegree,
            boolean sorted, boolean directed, boolean allowingMultipleEdges, boolean allowingSelfLoops) {
        this(IntStream.range(0, numVertices).toArray(), maxVertices, avgDegree,
                sorted, directed, allowingMultipleEdges, allowingSelfLoops);
    }

    /**
     *
     * @param vertices
     * @param maxVertices
     * @param avgDegree
     * @param sorted
     * @param directed
     * @param allowingMultipleEdges
     * @param allowingSelfLoops
     */
    protected GraphImpl(int[] vertices, int maxVertices, int avgDegree,
            boolean sorted, boolean directed, boolean allowingMultipleEdges, boolean allowingSelfLoops) {
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
        this.allowingMultipleEdges = allowingMultipleEdges;
        this.allowingSelfLoops = allowingSelfLoops;
        if (maxVertices > numVertices) {
            this.vertices = Arrays.copyOf(vertices, maxVertices);
        } else {
            this.vertices = vertices;
        }
        this.degree = new int[maxVertices];
        this.adjList = new int[maxVertices][];
        this.adjPos = new int[maxVertices][];
        this.numEdges = 0;
        //edge container
        if (maxVertices > 0) {
            try {
                this.edgeContainer = new EdgeContainerBitSet(maxVertices);
            } catch (Exception e) {
                //edgeBitSet remains null, containsEdge will have to iterate                
            }
        }
        //vertex container
        int maxVertexNumber = IntStream.of(vertices).max().orElse(maxVertices);
        vertexContainer = new VertexContainerArray(maxVertexNumber);
        for (int i = 0; i < numVertices; i++) {
            vertexContainer.add(vertices[i], i);
        }
        if (allowingSelfLoops) {
            selfLoops = new HashMap<>();
        }
    }

    protected GraphImpl newInstance() {
        return new GraphImpl();
    }

    protected GraphImpl newInstance(int[] vertices, int maxVertices, int avgDegree,
            boolean sorted, boolean directed, boolean allowingMultipleEdges, boolean allowingSelfLoops) {
        return new GraphImpl(vertices, maxVertices, avgDegree, sorted, directed, allowingMultipleEdges, allowingSelfLoops);
    }

    @Override
    public Graph<V, E> copy() {
        var copy = newInstance();
        copy.numVertices = numVertices;
        copy.numEdges = numEdges;
        copy.avgDegree = avgDegree;
        copy.sorted = sorted;
        copy.directed = directed;
        copy.allowingMultipleEdges = allowingMultipleEdges;
        copy.allowingSelfLoops = allowingSelfLoops;
        if (allowingSelfLoops) {
            copy.selfLoops = new HashMap<>(selfLoops);
        }
        copy.vertices = Arrays.copyOf(vertices, numVertices);
        copy.degree = Arrays.copyOf(degree, numVertices);
        if (vertexWeight != null) {
            copy.vertexWeight = Arrays.copyOf(vertexWeight, numVertices);
        }
        if (vertexLabel != null) {
            copy.vertexLabel = Arrays.copyOf(vertexLabel, numVertices);
        }
        copy.adjList = new int[numVertices][];
        copy.adjPos = new int[numVertices][];
        for (int i = 0; i < numVertices; i++) {
            if (adjList[i] != null) {
                copy.adjList[i] = Arrays.copyOf(adjList[i], adjList[i].length);
                copy.adjPos[i] = Arrays.copyOf(adjPos[i], adjPos[i].length);
                if (edgeWeight != null) {
                    copy.edgeWeight[i] = Arrays.copyOf(edgeWeight[i], edgeWeight[i].length);
                }
                if (edgeLabel != null) {
                    copy.edgeLabel[i] = Arrays.copyOf(edgeLabel[i], edgeLabel[i].length);
                }
            }
        }
        //edge container
        if (edgeContainer != null) {
            copy.edgeContainer = edgeContainer.copy();
        }
        //vertex container
        copy.vertexContainer = vertexContainer.copy();
        return copy;
    }

    @Override
    public void renumberAdding(int amount) {
        vertexContainer.grow(vertexContainer.max() + amount);
        for (int i = 0; i < numVertices; i++) {
            vertexContainer.remove(vertices[i]);
            vertices[i] = vertices[i] + amount;
            vertexContainer.add(vertices[i], i);
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
        CheckArguments.indexInRange(index, numVertices);
        return vertices[index];
    }

    @Override
    public int indexOf(int v) {
        if (v < 0 || v > vertexContainer.max()) {
            throw new InvalidVertexException(v);
        }
        return vertexContainer.indexOf(v);
    }

    protected void checkVertex(int v) {
        if (indexOf(v) < 0) {
            throw new InvalidVertexException(v);
        }
    }

    protected void checkEdge(int v, int u) {
        if (!containsEdge(v, u)) {
            //throw new InvalidEdgeException(v, u);
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
            edgeContainer = null;
        }
        if (numVertices == vertices.length) {
            growVertices();
        }
        int pos = numVertices;
        if (sorted) {
            pos = Arrays.binarySearch(vertices, 0, numVertices, v);
            if (pos >= 0) {
                throw new InvalidVertexException(v, "Vertex is already in the graph");
            }
            pos = -pos - 1; //insertion position
            //shift everything rigth
            for (int j = pos + 1; j < numVertices; j++) {
                vertices[pos] = vertices[pos - 1];
                adjList[pos] = adjList[pos - 1];
                degree[pos] = degree[pos - 1];
                adjPos[pos] = adjPos[pos - 1];
                if (vertexWeight != null) {
                    vertexWeight[pos] = vertexWeight[pos - 1];
                }
                if (edgeWeight != null) {
                    edgeWeight[pos] = edgeWeight[pos - 1];
                }
                if (vertexLabel != null) {
                    vertexLabel[pos] = vertexLabel[pos - 1];
                }
                if (edgeLabel != null) {
                    edgeLabel[pos] = edgeLabel[pos - 1];
                }
            }
        }
        vertices[pos] = v;
        vertexContainer.add(v, pos);
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
            adjPos[i] = adjPos[i + 1];
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
            vertexContainer.shiftLeft(vertices[i]);
        }
        vertexContainer.remove(v);
        numVertices--;
    }

    @Override
    public void addEdge(Edge<E> e) {
        addEdge(e.source(), e.target(), e.weight(), e.label());
    }

    @Override
    public void addEdge(int v, int u) {
        addEdge(v, u, null, null);
    }

    protected void addEdge(int v, int u, Double weight, E label) {
        if (!allowingSelfLoops && v == u) {
            throw new IllegalArgumentException("Loops are not allowed: " + v);
        }
        if (!allowingMultipleEdges && containsEdge(v, u)) {
            throw new IllegalArgumentException("Multiple edges are not allowed: " + v + "-" + u);
        }
        int posuv = addToAdjList(v, u, weight, label);
        int posvu = -1;
        if (v == u) {
            posvu = posuv;
        } else {
            if (!directed) {
                posvu = addToAdjList(u, v, weight, label);
            }
        }
        adjPos[indexOf(v)][posuv] = posvu;
        if (posvu >= 0) {
            adjPos[indexOf(u)][posvu] = posuv;
        }
        if (v == u && allowingSelfLoops) {
            selfLoops.put(v, selfLoops.getOrDefault(v, 0) + 1);
        }
        numEdges++;
        if (numEdges < 0) {
            throw new RuntimeException("Too many edges.");
        }
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
                adjPos[vi][j + 1] = adjPos[vi][j];
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
        if (edgeContainer != null) {
            try {
                edgeContainer.add(v, u);
            } catch (Exception e) {
                edgeContainer = null;
            }
        }
        //System.out.println("added " + v + " - " + u + ", pos=" + pos);
        return pos;
    }

    @Override
    public void removeEdge(int v, int u) {
        CheckArguments.graphContainsEdge(this, v, u);
        int multi = 0;
        var it = neighborIterator(v);
        while (it.hasNext()) {
            if (u == it.next()) {
                it.removeEdge();
                multi++;
                if (!isAllowingMultipleEdges()) {
                    break;
                }
            }
        }
    }

    //Removes u from the adjacency list of v    
    @Deprecated
    private void removeFromAdjList(int v, int u) {
        int vi = indexOf(v);
        if (vi < 0) {
            throw new InvalidVertexException(v);
        }
        int pos = adjListPosOf(v, u);
        if (pos < 0) {
            throw new InvalidEdgeException(v, u);
        }
        removeFromAdjListAt(vi, pos);
    }

    protected void removeFromAdjListAt(int vi, int pos) {
        int v = vertices[vi];
        int u = adjList[vi][pos];
        if (pos < degree[vi] - 1) {
            if (!sorted) {
                //swap the vertex to be removed with the last one
                int lastPos = degree[vi] - 1;
                adjList[vi][pos] = adjList[vi][lastPos];
                adjPos[vi][pos] = adjPos[vi][lastPos];
                //inform the vertex who was swapped of its current pos
                int w = adjList[vi][pos];
                int wi = indexOf(w);
                if (w != v) {
                    if (!directed) {
                        adjPos[wi][adjPos[vi][pos]] = pos;
                    }
                } else {
                    adjPos[wi][pos] = pos;
                }
                //
                if (edgeWeight != null) {
                    edgeWeight[vi][pos] = edgeWeight[vi][degree[vi] - 1];;
                }
                if (edgeLabel != null) {
                    edgeLabel[vi][pos] = edgeLabel[vi][degree[vi] - 1];;
                }
            } else {
                //shift left
                for (int j = pos; j < degree[vi] - 1; j++) {
                    adjList[vi][j] = adjList[vi][j + 1];
                    adjPos[vi][j] = adjPos[vi][j + 1];
                    //
                    int w = adjList[vi][pos];
                    int wi = indexOf(w);
                    if (w != v) {
                        if (!directed) {
                            adjPos[wi][adjPos[vi][pos]] = j;
                        }
                    } else {
                        adjPos[wi][j] = j;
                    }
                    //
                    if (edgeWeight != null) {
                        edgeWeight[vi][j] = edgeWeight[vi][j + 1];
                    }
                    if (edgeLabel != null) {
                        edgeLabel[vi][j] = edgeLabel[vi][j + 1];
                    }
                }
            }
        }
        if (edgeContainer != null) {
            edgeContainer.remove(v, u);
        }
        degree[vi]--;

    }

    //Returns the first position of u in the neighbor list of v.
    protected int adjListPosOf(int v, int u) {
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
    public Edge<E> edge(int v, int u) {
        return new Edge(v, u, directed,
                edgeWeight != null ? getEdgeWeight(v, u) : null,
                getEdgeLabel(v, u));
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
    public long maxEdges() {
        return Graph.maxEdges(numVertices);
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
        if (edgeContainer != null) {
            return edgeContainer.contains(v, u);
        }
        return adjListPosOf(v, u) >= 0;
    }

    @Override
    public int degree(int v) {
        int vi = indexOf(v);
        if (vi < 0) {
            throw new InvalidVertexException(v);
        }
        return degree[indexOf(v)];
    }

    /**
     *
     * @return a copy of the degree sequence of the graph vertices
     */
    @Override
    public int[] degrees() {
        return IntArrays.copyOf(degree);
    }

    /**
     *
     * @param v
     * @param u
     * @return
     */
    public int multiplicity(int v, int u) {
        int multi = 0;
        for (int w : neighbors(v)) {
            if (u == w) {
                multi++;
            }
        }
        return multi;
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
    public boolean isEdgeWeighted() {
        return edgeWeight != null;
    }

    @Override
    public boolean isVertexWeighted() {
        return vertexWeight != null;
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
            return DEFAULT_VERTEX_WEIGHT;
        }
        return vertexWeight[indexOf(v)];
    }

    @Override
    public void setEdgeWeight(int v, int u, double weight) {
        checkEdge(v, u);
        if (edgeWeight == null) {
            initEdgeWeights();
        }
        edgeWeight[indexOf(v)][adjListPosOf(v, u)] = weight;
        if (v != u && !directed) {
            edgeWeight[indexOf(u)][adjListPosOf(u, v)] = weight;
        }
    }

    @Override
    public double getEdgeWeight(int v, int u) {
        if (!containsEdge(v, u)) {
            return Double.POSITIVE_INFINITY;
        }
        if (edgeWeight == null) {
            return DEFAULT_EDGE_WEIGHT;
        }
        return edgeWeight[indexOf(v)][adjListPosOf(v, u)];
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
        edgeLabel[indexOf(v)][adjListPosOf(v, u)] = label;
        if (v != u && !directed) {
            edgeLabel[indexOf(u)][adjListPosOf(u, v)] = label;
        }
    }

    @Override
    public E getEdgeLabel(int v, int u) {
        checkEdge(v, u);
        if (edgeLabel == null) {
            return null;
        }
        return (E) edgeLabel[indexOf(v)][adjListPosOf(v, u)];
    }

    @Override
    public boolean isSorted() {
        return sorted;
    }

    @Override
    public boolean isDirected() {
        return directed;
    }

    @Override
    public boolean isAllowingMultipleEdges() {
        return allowingMultipleEdges;
    }

    @Override
    public boolean isAllowingSelfLoops() {
        return allowingSelfLoops;
    }

    //Expands the array holding the vertices
    //Expands the array holding the vertex degrees
    //Expands the array holding adjacency lists
    protected void growVertices() {
        int oldLen = vertices.length;
        int newLen = Math.max(DEFAULT_NUM_VERTICES, oldLen + (oldLen >> 1));
        vertices = Arrays.copyOf(vertices, newLen);
        degree = Arrays.copyOf(degree, newLen);
        adjList = Arrays.copyOf(adjList, newLen);
        adjPos = Arrays.copyOf(adjPos, newLen);
        if (vertexWeight != null) {
            vertexWeight = Arrays.copyOf(vertexWeight, newLen);
        }
        if (edgeWeight != null) {
            edgeWeight = Arrays.copyOf(edgeWeight, newLen);
        }
        if (vertexLabel != null) {
            vertexLabel = Arrays.copyOf(vertexLabel, newLen);
        }
        if (edgeLabel != null) {
            edgeLabel = Arrays.copyOf(edgeLabel, newLen);
        }
    }

    //Expands the array holding the adjacency list of v.
    protected void growAdjList(int v) {
        int vi = indexOf(v);
        int oldLen = degree[vi];
        int newLen = Math.max(avgDegree, oldLen + (oldLen >> 1) + 1);
        if (adjList[vi] != null) {
            adjList[vi] = Arrays.copyOf(adjList[vi], newLen);
        } else {
            adjList[vi] = new int[newLen];
        }
        if (adjPos[vi] != null) {
            adjPos[vi] = Arrays.copyOf(adjPos[vi], newLen);
        } else {
            adjPos[vi] = new int[newLen];
        }
        if (edgeWeight != null) {
            if (edgeWeight[vi] != null) {
                edgeWeight[vi] = Arrays.copyOf(edgeWeight[vi], newLen);
            } else {
                edgeWeight[vi] = new double[newLen];
            }
        }
        if (edgeLabel != null) {
            if (edgeLabel[vi] != null) {
                edgeLabel[vi] = Arrays.copyOf(edgeLabel, newLen);
            } else {
                edgeLabel[vi] = new Object[newLen];
            }
        }
    }

    @Override
    public Iterator<Edge> edgeIterator() {
        return new EdgeIteratorImpl(this);
    }

    @Override
    public int[][] edges() {
        int[][] edges = new int[numEdges][2];
        int k = 0;
        for (int v : vertices()) {
            for (int u : neighbors(v)) {
                if (directed || v <= u) {
                    edges[k][0] = v;
                    edges[k][1] = u;
                    k++;
                }
            }
        }
        return edges;
    }

    protected int edgeIndex(int[][] edges, int v, int u) {
        for (int k = 0, m = edges.length; k < m; k++) {
            if ((edges[k][0] == v && edges[k][1] == u)
                    || (!directed && edges[k][0] == u && edges[k][1] == v)) {
                return k;
            }
        }
        return -1;
    }

    @Override
    public Graph<V, E> subgraph(int... vertices) {
        int n = vertices.length;
        int deg = (int) IntStream.of(vertices).map(v -> this.degree(v)).average().orElse(0);
        var sub = newInstance(vertices, n, deg, sorted, directed, allowingMultipleEdges, allowingSelfLoops);
        for (int v : vertices) {
            int vi = indexOf(v);
            for (int j = 0; j < degree[v]; j++) {
                int u = adjList[vi][j];
                if ((directed || v <= u) && IntArrays.contains(vertices, u)) {
                    sub.addEdge(v, u,
                            edgeWeight != null ? edgeWeight[vi][j] : null,
                            edgeLabel != null ? edgeLabel[vi][j] : null);
                }
            }
        }
        return sub;
    }

    public Graph<V, E> supportGraph() {
        var copy = new GraphImpl(vertices, maxVertices, avgDegree,
                sorted, false, false, false);
        for (int v : this.vertices()) {
            for (int u : neighbors(v)) {
                if (u != v && !copy.containsEdge(v, u)) {
                    copy.addEdge(v, u);
                }
            }
        }
        return copy;
    }

    @Override
    public Graph<V, E> complement() {
        if (allowingMultipleEdges || allowingSelfLoops) {
            throw new UnsupportedOperationException("Complement of a multigraph or pseudograph is not defined.");
        }
        int avg = (int) IntStream.of(degree).map(deg -> numVertices - deg).average().orElse(0);
        var complement = newInstance(vertices, numVertices, avg, sorted, directed, allowingMultipleEdges, allowingSelfLoops);
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
                if (!IntArrays.contains(vertices, u) && !containsEdge(newVertex, u)) {
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

    @Override
    public int[][] adjacencyMatrix() {
        int[][] adjMatrix = new int[numVertices][numVertices];
        for (int i = 0; i < numVertices; i++) {
            int v = vertices[i];
            for (int u : neighbors(v)) {
                adjMatrix[i][indexOf(u)]++;
            }
        }
        return adjMatrix;
    }

    @Override
    public double[][] costMatrix() {
        double[][] costMatrix = new double[numVertices][numVertices];
        for (int vi = 0; vi < numVertices; vi++) {
            Arrays.fill(costMatrix[vi], Double.POSITIVE_INFINITY);
            costMatrix[vi][vi] = 0;
            for (int pos = 0, deg = degree[vi]; pos < deg; pos++) {
                int u = adjList[vi][pos];
                costMatrix[vi][indexOf(u)] = edgeWeight == null
                        ? DEFAULT_EDGE_WEIGHT : edgeWeight[vi][pos];
            }
        }
        return costMatrix;
    }

    @Override
    public int[][] incidenceMatrix() {
        int[][] edges = edges();
        int[][] incMatrix = new int[numVertices][numEdges];
        for (int vi = 0; vi < numVertices; vi++) {
            int v = vertexAt(vi);
            for (int u : adjList[vi]) {
                int ui = indexOf(u);
                int k = edgeIndex(edges, v, u);
                incMatrix[vi][k] = 1;
                incMatrix[ui][k] = (directed ? -1 : 1);
            }
        }
        return incMatrix;
    }

    protected String edgesToString() {
        var sb = new StringBuilder();
        sb.append("[");
        int i = 0;
        for (var it = edgeIterator(); it.hasNext();) {
            var e = it.next();
            int v = e.source();
            int u = e.target();
            if (i++ > 0) {
                sb.append(", ");
            }
            sb.append(e);
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
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(name != null ? name + "=" : "");
        sb.append("{");
        if (numVertices() <= 100) {
            sb.append(verticesToString());
            sb.append(", ").append(edgesToString());
        } else {
            sb.append("|V|=").append(numVertices());
            sb.append(", |E|=").append(numEdges());
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Iterates through the edges incident from v, returning the neighbors of v
     * along with information regarding their edges.
     *
     * @param v a vertex number
     * @return an iterator for the neigbors of v
     */
    @Override
    public NeighborIterator<E> neighborIterator(int v) {
        return new NeighborIteratorImpl(v);
    }

    private class NeighborIteratorImpl<E> implements NeighborIterator<E> {

        private final int v;
        private final int vi;
        private int pos;

        public NeighborIteratorImpl(int v) {
            this.v = v;
            this.vi = indexOf(v);
            this.pos = -1;
        }

        @Override
        public boolean hasNext() {
            return pos < degree[vi] - 1;
        }

        @Override
        public boolean hasPrevious() {
            return pos > 0;
        }

        @Override
        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return adjList[vi][++pos];
        }

        @Override
        public int previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            return adjList[vi][--pos];
        }

        @Override
        public void setEdgeWeight(double weight) {
            if (edgeWeight == null) {
                initEdgeWeights();
            }
            edgeWeight[vi][pos] = weight;
        }

        @Override
        public double getEdgeWeight() {
            if (edgeWeight == null) {
                return DEFAULT_EDGE_WEIGHT;
            }
            return edgeWeight[vi][pos];
        }

        @Override
        public void setEdgeLabel(E label) {
            if (edgeLabel == null) {
                initEdgeLabels();
            }
            edgeLabel[vi][pos] = label;
        }

        @Override
        public E getEdgeLabel() {
            if (edgeLabel == null) {
                return null;
            }
            return (E) edgeLabel[vi][pos];
        }

        @Override
        public void removeEdge() {
            ListIterator a;
            if (pos < 0) {
                throw new IllegalStateException("There is no current edge. Call next().");
            }
            int u = adjList[vi][pos];
            int ui = indexOf(u);
            int posvu = adjPos[vi][pos];
            removeFromAdjListAt(vi, pos);
            if (u != v && !directed) {
                removeFromAdjListAt(ui, posvu);
            }
            numEdges--;
            if (allowingSelfLoops && v == u) {
                selfLoops.put(v, selfLoops.get(v) - 1);
            }
            pos--;
        }
    }
}
