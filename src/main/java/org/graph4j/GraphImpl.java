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
package org.graph4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.IntStream;
import org.graph4j.util.CheckArguments;
import org.graph4j.util.IntArrays;
import org.graph4j.util.EdgeSet;
import org.graph4j.util.VertexSet;

/**
 * An implementation of a graph that uses adjacency lists.
 *
 * This class avoids the overhead of using too many objects by representing the
 * data in the graph as primitive structures.
 *
 * @author Cristian Frăsinaru
 * @param <V> the type of vertex labels in this graph
 * @param <E> the type of edge labels in this graph
 */
class GraphImpl<V, E> implements Graph<V, E> {

    protected String name;
    protected int maxVertices; //estimated maximum number of vertices
    protected int numVertices; //number of vertices
    protected long numEdges;  //number of edges

    protected int[] vertices; //vertices[i] is the vertex with the index i
    protected int[] degree; //degree[i] is the (out)degree of the vertex with index i
    protected int[][] adjList; //adjList[i] is the adjacency list of the vertex with index i
    protected int[][] adjPos; //adjPos[i][j]=the position of v=vertices[i] in the adjacency list of u=adjList[i][j]
    //adjPos should be null if the graph is directed or (unweighted, unlabeled and edge immutable)

    protected double[] vertexWeight;
    protected double[][] edgeWeight;
    protected V[] vertexLabel;
    protected E[][] edgeLabel;

    private VertexIndex vertexIndex; //to find the index of a vertex
    private AdjacencySet[] adjSet;  //for fast check if an edge is present in the graph

    protected Integer maxVertexNumber;
    protected Map<V, Integer> labelVertexMap;
    protected Map<E, Edge> labelEdgeMap;
    //protected Object2LongOpenHashMap<E> labelEdgeIndexMap;

    protected boolean directed;
    protected boolean allowingMultipleEdges;
    protected boolean allowingSelfLoops;
    //
    protected int avgDegree; //this may improve the memory allocation
    protected static final int DEFAULT_NUM_VERTICES = 100;
    protected static final int DEFAULT_AVG_DEGREE = 10;
    //
    protected boolean safeMode = true;

    protected GraphImpl() {
    }

    /**
     *
     * @param vertices the vertices in this graph.
     * @param maxVertices estimated maximum number of vertices.
     * @param avgDegree estimated average degree.
     * @param directed {@code true} if it is directed.
     * @param allowingMultipleEdges {@code true} if it allows multiple edges.
     * @param allowingSelfLoops {@code true} if it allows self loops.
     */
    protected GraphImpl(int[] vertices, int maxVertices, int avgDegree,
            boolean directed, boolean allowingMultipleEdges, boolean allowingSelfLoops) {
        if (maxVertices < numVertices) {
            throw new IllegalArgumentException("Invalid maximum number of vertices: " + maxVertices);
        }

        this.maxVertices = maxVertices;
        this.numVertices = vertices.length;
        this.directed = directed;
        this.allowingMultipleEdges = allowingMultipleEdges;
        this.allowingSelfLoops = allowingSelfLoops;
        if (maxVertices > numVertices) {
            this.vertices = Arrays.copyOf(vertices, maxVertices);
        } else {
            this.vertices = vertices;
        }
        checkDefaultVertices();
        this.degree = new int[maxVertices];
        this.adjList = new int[maxVertices][];
        if (!directed) {
            this.adjPos = new int[maxVertices][]; //don't if unweighted, unlabeled, immutable
        }

        if (avgDegree == 0) {
            avgDegree = DEFAULT_AVG_DEGREE;
        } else {
            if (avgDegree < 0) {
                throw new IllegalArgumentException("Invalid vertex average degree: " + avgDegree);
            }
        }
        this.avgDegree = avgDegree;
        this.numEdges = 0;
    }

    private void checkDefaultVertices() {
        for (int i = 0; i < numVertices; i++) {
            if (vertices[i] != i) {
                initVertexIndex();
                return;
            }
        }
    }

    protected void initVertexIndex() {
        //vertex-to-index mapping        
        int maxNumber = IntStream.of(vertices()).max().orElse(maxVertices);
        vertexIndex = new VertexIndexArray(maxNumber);
        for (int i = 0; i < numVertices; i++) {
            vertexIndex.set(vertices[i], i);
        }
    }

    protected GraphImpl newInstance() {
        return new GraphImpl();
    }

    protected GraphImpl newInstance(int[] vertices, int maxVertices, int avgDegree,
            boolean directed, boolean allowingMultipleEdges, boolean allowingSelfLoops) {
        return new GraphImpl(vertices, maxVertices, avgDegree, directed, allowingMultipleEdges, allowingSelfLoops);
    }

    @Override
    public Graph<V, E> copy() {
        return copy(true, true, true, true, true);
    }

    @Override
    public Graph<V, E> copy(boolean vertexWeights, boolean vertexLabels,
            boolean edges, boolean edgeWeights, boolean edgeLabels) {
        if (!edges) {
            edgeWeights = false;
            edgeLabels = false;
        }
        var copy = newInstance();
        copy.numVertices = numVertices;
        copy.maxVertices = maxVertices;
        copy.numEdges = edges ? numEdges : 0;
        copy.avgDegree = avgDegree;
        copy.directed = directed;
        copy.allowingMultipleEdges = allowingMultipleEdges;
        copy.allowingSelfLoops = allowingSelfLoops;

        copy.vertices = Arrays.copyOf(vertices, numVertices);
        copy.degree = edges ? Arrays.copyOf(degree, numVertices) : new int[vertices.length];

        if (this.vertexWeight != null && vertexWeights) {
            copy.vertexWeight = Arrays.copyOf(vertexWeight, numVertices);
        }
        if (this.vertexLabel != null && vertexLabels) {
            copy.vertexLabel = Arrays.copyOf(vertexLabel, numVertices);
            copy.labelVertexMap = new HashMap<>(labelVertexMap);
        }
        copy.adjList = new int[numVertices][];
        if (adjPos != null) {
            copy.adjPos = new int[numVertices][];
        }
        if (adjSet != null) {
            copy.adjSet = new AdjacencySet[numVertices];
        }
        if (edgeWeight != null) {
            copy.edgeWeight = new double[numVertices][];
        }
        if (edgeLabel != null) {
            copy.edgeLabel = new Object[numVertices][];
        }
        if (edges) {
            for (int i = 0; i < numVertices; i++) {
                if (adjList[i] != null) {
                    copy.adjList[i] = Arrays.copyOf(adjList[i], adjList[i].length);
                }
                if (adjPos != null && adjPos[i] != null) {
                    copy.adjPos[i] = Arrays.copyOf(adjPos[i], adjPos[i].length);
                }
                if (edgeWeight != null && edgeWeight[i] != null && edgeWeights) {
                    copy.edgeWeight[i] = Arrays.copyOf(edgeWeight[i], edgeWeight[i].length);
                }
                if (edgeLabel != null && edgeLabel[i] != null && edgeLabels) {
                    copy.edgeLabel[i] = Arrays.copyOf(edgeLabel[i], edgeLabel[i].length);
                }
                if (adjSet != null && adjSet[i] != null) {
                    copy.adjSet[i] = adjSet[i].copy();
                }
            }
        }
        if (edgeLabels) {
            if (this.labelEdgeMap != null) {
                copy.labelEdgeMap = new HashMap<>(labelEdgeMap);
            }
        }
        //vertex container
        if (vertexIndex != null) {
            copy.vertexIndex = vertexIndex.copy();
        }
        //stuff
        copy.maxVertexNumber = maxVertexNumber;
        return copy;
    }

    @Override
    public void renumberAdding(int amount) {
        if (vertexIndex == null) {
            initVertexIndex();
        }
        vertexIndex.grow(vertexIndex.max() + amount);
        for (int i = 0; i < numVertices; i++) {
            vertexIndex.remove(vertices[i]);
            vertices[i] = vertices[i] + amount;
            vertexIndex.set(vertices[i], i);
        }
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < adjList[i].length; j++) {
                adjList[i][j] = adjList[i][j] + amount;
            }
        }
    }

    @Override
    public int numVertices() {
        return numVertices;
    }

    @Override
    public long numEdges() {
        return numEdges;
    }

    @Override
    public long maxEdges() {
        return Graph.maxEdges(numVertices);
    }

    @Override
    public int[] vertices() {
        if (vertices.length != numVertices) {
            vertices = Arrays.copyOf(vertices, numVertices);
        }
        return vertices;
    }

    @Override
    public VertexIterator vertexIterator() {
        return new VertexteratorImpl<V>(this);
    }

    @Override
    public int vertexAt(int index) {
        CheckArguments.indexInRange(index, numVertices);
        return vertices[index];
    }

    @Override
    public int indexOf(int v) {
        if (vertexIndex == null) {
            //default vertices
            return v < 0 || v >= numVertices ? -1 : v;
        }
        return vertexIndex.indexOf(v);
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

    @Override
    public int addVertex() {
        int v = 1 + maxVertexNumber();
        addVertex(v);
        return v;
    }

    //the main addVertex
    @Override
    public int addVertex(int v) {
        if (vertexIndex == null) {
            initVertexIndex();
        }
        if (v < 0) {
            throw new InvalidVertexException(v, "Vertex number must be non-negative");
        }
        if (indexOf(v) >= 0) {
            throw new InvalidVertexException(v, "Vertex is already in the graph");
        }
        if (numVertices == vertices.length) {
            growVertices();
        }
        int pos = numVertices;
        vertices[pos] = v;
        vertexIndex.set(v, pos);
        if (maxVertexNumber != null && v > maxVertexNumber) {
            maxVertexNumber = v;
        }
        //adjListMatrix = null;
        return numVertices++;
    }

    @Override
    public void removeVertex(int v) {
        int vi = indexOf(v);
        if (vi < 0) {
            throw new InvalidVertexException(v);
        }
        removeAllEdgesAt(vi);
        if (labelVertexMap != null) {
            labelVertexMap.remove(vertexLabel[vi]);
        }
        //swap with the last pos
        boolean isLastPos = vi == numVertices - 1;
        if (!isLastPos) {
            swapVertexWithLast(vi);
        }
        numVertices--;
        maxVertexNumber = null;
        //
        if (vertexIndex == null) {
            initVertexIndex();
        } else {
            vertexIndex.remove(v);
            if (!isLastPos) {
                vertexIndex.set(vertices[vi], vi);
            }
        }
    }

    protected void swapVertexWithLast(int i) {
        int lastPos = numVertices - 1;
        vertices[i] = vertices[lastPos];
        degree[i] = degree[lastPos];
        //
        adjList[i] = adjList[lastPos];
        adjList[lastPos] = null;
        //
        if (adjPos != null) {
            adjPos[i] = adjPos[lastPos];
            adjPos[lastPos] = null;
        }
        if (adjSet != null) {
            adjSet[i] = adjSet[lastPos];
            adjSet[lastPos] = null;
        }
        if (vertexWeight != null) {
            vertexWeight[i] = vertexWeight[lastPos];
        }
        if (edgeWeight != null) {
            edgeWeight[i] = edgeWeight[lastPos];
        }
        if (vertexLabel != null) {
            vertexLabel[i] = vertexLabel[lastPos];
        }
        if (edgeLabel != null) {
            edgeLabel[i] = edgeLabel[lastPos];
        }
    }

    /*
    private void shiftVerticesLeft(int vi) {
        //shift everyhing left (expensive - should be an option)
        for (int i = vi; i < numVertices - 1; i++) {
            vertices[i] = vertices[i + 1];
            degree[i] = degree[i + 1];
            adjList[i] = adjList[i + 1];
            adjPos[i] = adjPos[i + 1];
            if (adjSet != null) {
                adjSet[i] = adjSet[i + 1];
            }
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
            vertexIndex.shiftLeft(vertices[i]);
        }         
    }*/
    @Override
    public int maxVertexNumber() {
        if (maxVertexNumber == null) {
            maxVertexNumber = IntStream.of(vertices()).max().orElse(-1);
        }
        return maxVertexNumber;
    }

    @Override
    public int addEdge(Edge<E> e) {
        if (edgeWeight != null || e.weight != null) {
            if (edgeLabel != null || e.label != null) {
                return addEdge(e.source, e.target, e.weight, e.label);
            } else {
                return addEdge(e.source, e.target, e.weight);
            }
        } else {
            if (edgeLabel != null || e.label != null) {
                return addEdge(e.source, e.target, e.label);
            } else {
                return addEdge(e.source, e.target);
            }
        }
    }

    @Override
    public int addEdge(int v, int u, double weight) {
        int pos = addEdge(v, u);
        if (edgeWeight == null) {
            initEdgeWeights();
        }
        int vi = indexOf(v);
        edgeWeight[vi][pos] = weight;
        if (adjPos != null) {
            int ui = indexOf(u);
            edgeWeight[ui][adjPos[vi][pos]] = weight;
        }
        return pos;
    }

    @Override
    public int addEdge(int v, int u, E label) {
        int pos = addEdge(v, u);
        if (edgeLabel == null) {
            initEdgeLabels();
        }
        int vi = indexOf(v);
        edgeLabel[vi][pos] = label;
        if (adjPos != null) {
            int ui = indexOf(u);
            edgeLabel[ui][adjPos[vi][pos]] = label;
        }
        if (labelEdgeMap != null) {
            labelEdgeMap.put(label, new Edge(v, u, null, label));
        }
        return pos;
    }

    @Override
    public int addEdge(int v, int u, double weight, E label) {
        int pos = addEdge(v, u);
        if (edgeWeight == null) {
            initEdgeWeights();
        }
        if (edgeLabel == null) {
            initEdgeLabels();
        }
        int vi = indexOf(v);
        edgeWeight[vi][pos] = weight;
        edgeLabel[vi][pos] = label;
        if (adjPos != null) {
            int ui = indexOf(u);
            edgeWeight[ui][adjPos[vi][pos]] = weight;
            edgeLabel[ui][adjPos[vi][pos]] = label;
        }
        if (labelEdgeMap != null) {
            labelEdgeMap.put(label, new Edge(v, u, weight, label));
        }
        return pos;
    }

    //the main addEdge method
    @Override
    public int addEdge(int v, int u) {
        if (safeMode) {
            checkVertex(v);
            checkVertex(u);
            if (!allowingSelfLoops && v == u) {
                //throw new IllegalArgumentException("Loops are not allowed: " + v);
                return -1;
            }
            if (!allowingMultipleEdges && containsEdge(v, u)) {
                //throw new IllegalArgumentException("Multiple edges are not allowed: " + v + "-" + u);
                return -1;
            }
        }
        //after adding u to the adjacency list of v, 
        //get the the position where it was added
        int posuv = addToAdjList(v, u);
        int posvu = -1;
        if (v == u) {
            posvu = posuv;
        } else {
            if (!directed) {
                posvu = addToAdjList(u, v);
            }
        }
        //store the positions in the adjPos array
        if (adjPos != null) {
            adjPos[indexOf(v)][posuv] = posvu;
            if (posvu >= 0) {
                adjPos[indexOf(u)][posvu] = posuv;
            }
        }
        numEdges++;
        return posuv;
    }

    protected long edgeIndex(int v, int pos) {
        return numVertices * v + pos;
    }

    //Adds u to the adjacency list of v
    protected int addToAdjList(int v, int u) {
        int vi = indexOf(v);
        if (adjList[vi] == null || degree[vi] == adjList[vi].length) {
            growAdjList(v);
        }
        //add the vertex at the end of the list
        int pos = degree[vi];
        adjList[vi][pos] = u;
        if (adjSet != null && adjSet[vi] != null) {
            adjSet[vi].add(u);
        }
        degree[vi]++;
        /*
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
        }*/
        return pos;
    }

    @Override
    public void removeEdge(int v, int u) {
        CheckArguments.graphContainsEdge(this, v, u);
        for (var it = neighborIterator(v); it.hasNext();) {
            if (u == it.next()) {
                it.removeEdge();
                if (!isAllowingMultipleEdges()) {
                    break;
                }
            }
        }
    }

    //the main removeEdge method
    protected void removeEdgeAt(int vi, int pos) {
        if (!directed) {
            int v = vertices[vi];
            int u = adjList[vi][pos];
            if (u != v) {
                int ui = indexOf(u);
                int posvu = adjPos[vi][pos];
                removeFromAdjListAt(ui, posvu);
            }
        }

        removeFromAdjListAt(vi, pos);

        if (edgeLabel != null) {
            E label = edgeLabel[vi][pos];
            /*
            if (labelEdgeIndexMap != null) {
                labelEdgeIndexMap.remove(label);
            } else */
            if (labelEdgeMap != null) {
                labelEdgeMap.remove(label);
            }
        }
        numEdges--;
        //adjListMatrix = null;
    }

    @Override
    public void removeAllEdges(int v) {
        int vi = indexOf(v);
        if (vi < 0) {
            throw new InvalidVertexException(v);
        }
        removeAllEdgesAt(vi);
    }

    protected void removeAllEdgesAt(int vi) {
        for (int pos = 0, deg = degree[vi]; pos < deg; pos++) {
            int u = adjList[vi][pos];
            int ui = indexOf(u);
            if (!directed) {
                //remove also the edge uv, for undirected graph
                removeFromAdjListAt(ui, adjPos[vi][pos]);
            }
            if (labelEdgeMap != null) {
                labelEdgeMap.remove(edgeLabel[vi][pos]);
            }
        }
        numEdges -= degree[vi];
        degree[vi] = 0; //bulk
    }

    //Removes u from the adjacency list of v        
    protected void removeFromAdjListAt(int vi, int pos) {
        int v = vertices[vi];
        int u = adjList[vi][pos];
        if (pos < degree[vi] - 1) {
            //swap the vertex to be removed with the last one           
            swapNeighborWithLast(vi, pos);
        }
        degree[vi]--;
        if (adjSet != null && adjSet[vi] != null) {
            if (!allowingMultipleEdges || multiplicity(v, u) == 0) {
                adjSet[vi].remove(u);
            }
        }
    }

    protected void swapNeighborWithLast(int vi, int pos) {
        int lastPos = degree[vi] - 1;
        adjList[vi][pos] = adjList[vi][lastPos];
        if (adjPos != null) {
            adjPos[vi][pos] = adjPos[vi][lastPos];
            //inform the vertex which was swapped of its current pos
            int w = adjList[vi][pos];
            int wi = indexOf(w);
            if (wi != vi) {
                if (!directed) {
                    adjPos[wi][adjPos[vi][pos]] = pos;
                }
            } else {
                adjPos[wi][pos] = pos;
            }
        }
        if (edgeWeight != null) {
            edgeWeight[vi][pos] = edgeWeight[vi][degree[vi] - 1];
        }
        if (edgeLabel != null) {
            edgeLabel[vi][pos] = edgeLabel[vi][degree[vi] - 1];
        }
    }

    /*    
    protected void shiftNeighbors() {
        //shift neighbors left
        for (int j = pos; j < degree[vi] - 1; j++) {
            adjList[vi][j] = adjList[vi][j + 1];
            adjPos[vi][j] = adjPos[vi][j + 1];
            //
            //inform the vertex which was swapped of its current pos
            int w = adjList[vi][pos];
            int wi = indexOf(w);
            if (w != v) {
                if (!directed) {
                    adjPos[wi][adjPos[vi][j]] = j;
                }
            } else {
                adjPos[wi][j] = j;
            }
            if (edgeWeight != null) {
                edgeWeight[vi][j] = edgeWeight[vi][j + 1];
            }
            if (edgeLabel != null) {
                edgeLabel[vi][j] = edgeLabel[vi][j + 1];
            }
        }        
    }
     */
    //Returns the first position of u in the neighbor list of v.
    @Override
    public int adjListPos(int v, int u) {
        int vi = indexOf(v);
        if (adjList[vi] == null) {
            return -1;
        }
        /*
        if (adjListMatrix != null) {
            return adjListMatrix[vi][indexOf(u)];
        }*/
        for (int pos = 0, deg = degree[vi]; pos < deg; pos++) {
            if (adjList[vi][pos] == u) {
                return pos;
            }
        }
        return -1;
    }

    @Override
    public Edge<E> edge(int v, int u) {
        int vi = indexOf(v);
        int posvu = adjListPos(v, u);
        if (posvu < 0) {
            throw new InvalidEdgeException(v, u);
        }
        return edgeAt(vi, posvu);
    }

    //the main create Edge method
    protected Edge<E> edgeAt(int vi, int pos) {
        return new Edge(vertices[vi], adjList[vi][pos],
                directed,
                edgeWeight != null ? edgeWeight[vi][pos] : null,
                edgeLabel != null ? edgeLabel[vi][pos] : null);
    }

    @Override
    public int findVertex(V label) {
        if (vertexLabel == null) {
            return -1;
        }
        if (labelVertexMap == null) {
            initLabelVertexMap();
        }
        return labelVertexMap.getOrDefault(label, -1);
    }

    @Override
    public VertexSet findAllVertices(V label) {
        var set = new VertexSet(this);
        if (vertexLabel != null) {
            for (int v : vertices()) {
                if (Objects.equals(getVertexLabel(v), label)) {
                    set.add(v);
                }
            }
        }
        return set;
    }

    @Override
    public Edge findEdge(E label) {
        if (edgeLabel == null) {
            return null;
        }
        /*
        if (labelEdgeIndexMap != null) {
            long index = labelEdgeIndexMap.getOrDefault(label, -1);
            int v = (int) (index / numVertices);
            int pos = (int) (index % numVertices);
            return edgeAt(v, pos);
        } else */
        if (labelEdgeMap == null) {
            initLabelEdgeMap();
        }
        return labelEdgeMap.get(label);
    }

    @Override
    public EdgeSet findAllEdges(E label) {
        var set = new EdgeSet(this);
        if (edgeLabel != null) {
            for (var it = edgeIterator(); it.hasNext();) {
                Edge e = it.next();
                if (Objects.equals(it.getLabel(), label)) {
                    set.add(e);
                }
            }
        }
        return set;
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

    private AdjacencySet getAdjSet(int vi) {
        if (adjSet == null) {
            adjSet = new AdjacencySet[numVertices];
        }
        if (adjSet[vi] == null) {
            adjSet[vi] = new AdjacencyBitSet();
            for (int pos = 0; pos < degree[vi]; pos++) {
                adjSet[vi].add(adjList[vi][pos]);
            }
        }
        return adjSet[vi];
    }

    @Override
    public boolean containsEdge(int v, int u) {
        checkVertex(v);
        checkVertex(u);
        //if the degree of v is small enough, just iterate
        int vi = indexOf(v);
        if (degree[vi] * degree[vi] < numVertices) {
            return adjListPos(v, u) >= 0;
        }
        //switch to adjacency sets (bitsets)
        return getAdjSet(vi).contains(u);
    }

    @Override
    public int degree(int v
    ) {
        int vi = indexOf(v);
        if (vi < 0) {
            throw new InvalidVertexException(v);
        }
        return degree[vi];
    }

    /**
     *
     * @return a copy of the degree sequence of the graph vertices.
     */
    @Override
    public int[] degrees() {
        return IntArrays.copyOf(degree);
    }

    /**
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return the multiplicity of the edge vu.
     */
    public int multiplicity(int v, int u) {
        int multi = 0;
        for (var it = neighborIterator(v); it.hasNext();) {
            if (u == it.next()) {
                multi++;
            }
        }
        return multi;
    }

    protected void initVertexWeights() {
        this.vertexWeight = new double[vertices.length];
    }

    protected void initEdgeWeights() {
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
    public int addVertex(int v, double weight) {
        int vi = addVertex(v);
        setVertexWeight(v, weight);
        return vi;
    }

    @Override
    public int addVertex(double weight) {
        int v = addVertex();
        setVertexWeight(v, weight);
        return v;
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
        int vi = indexOf(v);
        int pos = adjListPos(v, u);
        setEdgeWeightAt(vi, pos, weight);
    }

    protected void setEdgeWeightAt(int vi, int pos, double weight) {
        if (edgeWeight == null) {
            initEdgeWeights();
        }
        int v = vertices[vi];
        int u = adjList[vi][pos];
        int ui = indexOf(u);
        edgeWeight[vi][pos] = weight;
        if (v != u && !directed) {
            edgeWeight[ui][adjPos[vi][pos]] = weight;
        }
        if (labelEdgeMap != null) {
            Edge e = labelEdgeMap.get(edgeLabel[vi][pos]);
            if (e != null) {
                e.setWeight(weight);
            }
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
        return edgeWeight[indexOf(v)][adjListPos(v, u)];
    }

    //
    protected void initVertexLabels() {
        this.vertexLabel = (V[]) new Object[vertices.length];
    }

    protected void initLabelVertexMap() {
        this.labelVertexMap = new HashMap<>(vertices.length);
        for (int i = 0; i < numVertices; i++) {
            labelVertexMap.put(vertexLabel[i], vertices[i]);
        }
    }

    protected void initEdgeLabels() {
        this.edgeLabel = (E[][]) new Object[vertices.length][];
        for (int i = 0; i < numVertices; i++) {
            this.edgeLabel[i] = (E[]) (adjList[i] == null ? null : new Object[adjList[i].length]);
        }
    }

    protected void initLabelEdgeMap() {
        this.labelEdgeMap = new HashMap<>(numVertices);
        for (var it = edgeIterator(); it.hasNext();) {
            var e = it.next();
            labelEdgeMap.put(e.label(), e);
        }
    }

    @Override
    public int addVertex(int v, V label) {
        int vi = addVertex(v);
        setVertexLabel(v, label);
        return vi;
    }

    @Override
    public int addVertex(V label) {
        int v = addVertex();
        setVertexLabel(v, label);
        return v;
    }

    @Override
    public void setVertexLabel(int v, V label) {
        checkVertex(v);
        if (vertexLabel == null) {
            initVertexLabels();
        }
        vertexLabel[indexOf(v)] = label;
        if (labelVertexMap != null) {
            labelVertexMap.put(label, v);
        }
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
        int vi = indexOf(v);
        int pos = adjListPos(v, u);
        setEdgeLabelAt(vi, pos, label);
    }

    protected void setEdgeLabelAt(int vi, int pos, E label) {
        if (edgeLabel == null) {
            initEdgeLabels();
        }
        int v = vertices[vi];
        int u = adjList[vi][pos];
        int ui = indexOf(u);
        var oldLabel = edgeLabel[vi][pos];
        edgeLabel[vi][pos] = label;
        if (v != u && !directed) {
            edgeLabel[ui][adjPos[vi][pos]] = label;
        }
        /*
        if (labelEdgeIndexMap != null) {
            labelEdgeIndexMap.remove(oldLabel);
            labelEdgeIndexMap.put(label, edgeIndex(v, pos));
        } else */
        if (labelEdgeMap != null) {
            Edge e = labelEdgeMap.get(oldLabel);
            if (e == null) {
                e = new Edge(v, u, directed, null, label);
            }
            e.setLabel(label);
            labelEdgeMap.remove(oldLabel);
            labelEdgeMap.put(label, e);
        }
    }

    @Override
    public E getEdgeLabel(int v, int u) {
        checkEdge(v, u);
        if (edgeLabel == null) {
            return null;
        }
        return edgeLabel[indexOf(v)][adjListPos(v, u)];
    }

    @Override
    public boolean isEdgeLabeled() {
        return edgeLabel != null;
    }

    @Override
    public boolean isVertexLabeled() {
        return vertexLabel != null;
    }

    @Override
    public boolean isDirected() {
        return directed;
    }

    @Override
    public boolean isComplete() {
        return numEdges == Graph.maxEdges(numVertices);
    }

    @Override
    public boolean isAllowingMultipleEdges() {
        return allowingMultipleEdges;
    }

    @Override
    public boolean isAllowingSelfLoops() {
        return allowingSelfLoops;
    }

    @Override
    public boolean isSafeMode() {
        return safeMode;
    }

    @Override
    public void setSafeMode(boolean safeMode) {
        this.safeMode = safeMode;
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
        if (adjPos != null) {
            adjPos = Arrays.copyOf(adjPos, newLen);
        }
        if (adjSet != null) {
            adjSet = Arrays.copyOf(adjSet, newLen);
        }
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
        if (adjPos != null) {
            if (adjPos[vi] != null) {
                adjPos[vi] = Arrays.copyOf(adjPos[vi], newLen);
            } else {
                adjPos[vi] = new int[newLen];
            }
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
                edgeLabel[vi] = Arrays.copyOf(edgeLabel[vi], newLen);
            } else {
                edgeLabel[vi] = (E[]) new Object[newLen];
            }
        }
    }

    @Override
    public EdgeIterator<E> edgeIterator() {
        return new EdgeIteratorImpl<>(this);
    }

    @Override
    public Edge[] edges() {
        Edge[] edges = new Edge[(int) numEdges];
        int i = 0;
        for (var it = edgeIterator(); it.hasNext();) {
            edges[i++] = it.next();
        }
        return edges;
    }

    @Override
    public Edge[] edgesOf(int v) {
        Edge[] edges = new Edge[degree(v)];
        int k = 0;
        for (var it = neighborIterator(v); it.hasNext();) {
            it.next();
            edges[k++] = edgeAt(indexOf(v), it.adjListPos());
        }
        return edges;
    }

    @Override
    public Graph<V, E> subgraph(int... vertices) {
        int n = vertices.length;
        int deg = (int) IntStream.of(vertices).map(v -> this.degree(v)).average().orElse(0);
        var sub = newInstance(vertices, n, deg, directed, allowingMultipleEdges, allowingSelfLoops);
        for (int v : vertices) {
            int vi = indexOf(v);
            if (vertexWeight != null) {
                sub.setVertexWeight(v, vertexWeight[vi]);
            }
            if (vertexLabel != null) {
                sub.setVertexLabel(v, vertexLabel[vi]);
            }
            for (int j = 0; j < degree[vi]; j++) {
                int u = adjList[vi][j];
                if ((directed || v <= u) && IntArrays.contains(vertices, u)) {
                    if (edgeWeight != null) {
                        if (edgeLabel != null) {
                            sub.addEdge(v, u, edgeWeight[vi][j], edgeLabel[vi][j]);
                        } else {
                            sub.addEdge(v, u, edgeWeight[vi][j]);
                        }
                    } else {
                        if (edgeLabel != null) {
                            sub.addEdge(v, u, edgeLabel[vi][j]);
                        } else {
                            sub.addEdge(v, u);
                        }
                    }
                }
            }
        }
        return sub;
    }

    @Override
    public Graph<V, E> subgraph(EdgeSet edgeSet) {
        int[] subVertices = edgeSet.vertices();
        int n = subVertices.length;
        int deg = 1 + (edgeSet.size() / n);
        var sub = newInstance(subVertices, n, deg, directed, allowingMultipleEdges, allowingSelfLoops);
        for (int v : subVertices) {
            int vi = indexOf(v);
            if (vertexWeight != null) {
                sub.setVertexWeight(v, vertexWeight[vi]);
            }
            if (vertexLabel != null) {
                sub.setVertexLabel(v, vertexLabel[vi]);
            }
        }
        for (int[] e : edgeSet.edges()) {
            int v = e[0];
            int u = e[1];
            int vi = indexOf(v);
            int j = adjListPos(v, u);
            if (edgeWeight != null) {
                if (edgeLabel != null) {
                    sub.addEdge(v, u, edgeWeight[vi][j], edgeLabel[vi][j]);
                } else {
                    sub.addEdge(v, u, edgeWeight[vi][j]);
                }
            } else {
                if (edgeLabel != null) {
                    sub.addEdge(v, u, edgeLabel[vi][j]);
                } else {
                    sub.addEdge(v, u);
                }
            }
        }
        return sub;
    }

    public Graph<V, E> supportGraph() {
        var copy = GraphBuilder.verticesFrom(this).buildGraph();
        for (var it = edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            if (!e.isSelfLoop() && !copy.containsEdge(e)) {
                copy.addEdge(e);
            }
        }
        return copy;
    }

    @Override
    public Graph<V, E> complement() {
        if (allowingMultipleEdges || allowingSelfLoops) {
            throw new UnsupportedOperationException(
                    "Complement of a multigraph or pseudograph is not defined.");
        }
        GraphImpl<V, E> complement = (GraphImpl<V, E>) copy(true, true, false, false, false);
        complement.avgDegree = (int) IntStream.of(degree).map(deg -> numVertices - deg).average().orElse(0);
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
        for (int vi = 0; vi < numVertices; vi++) {
            int v = vertices[vi];
            for (int u : neighbors(v)) {
                adjMatrix[vi][indexOf(u)]++;
            }
        }
        return adjMatrix;
    }

    //internal use only
    /*
    protected void createAdjListMatrix() {
        adjListMatrix = new int[numVertices][numVertices];
        for (int vi = 0; vi < numVertices; vi++) {
            Arrays.fill(adjListMatrix[vi], -1);
            for (int pos = 0, deg = degree[vi]; pos < deg; pos++) {
                int ui = indexOf(adjList[vi][pos]);
                adjListMatrix[vi][ui] = pos;
            }
        }
    }*/
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
        int[][] incMatrix = new int[numVertices][(int) numEdges];
        int edgeIndex = 0;
        for (int vi = 0; vi < numVertices; vi++) {
            int v = vertexAt(vi);
            for (int pos = 0, deg = degree[vi]; pos < deg; pos++) {
                int u = adjList[vi][pos];
                if (directed || v <= u) {
                    int ui = indexOf(u);
                    incMatrix[vi][edgeIndex] = 1;
                    incMatrix[ui][edgeIndex] = (directed ? -1 : 1);
                    edgeIndex++;
                }
            }
        }
        return incMatrix;
    }

    protected String edgesToString() {
        var sb = new StringBuilder();
        sb.append("[");
        int i = 0;
        for (var it = edgeIterator(); it.hasNext();) {
            if (i++ > 0) {
                sb.append(", ");
            }
            sb.append(it.next());
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
            int v = vertices[i];
            sb.append(v);
            if (vertexLabel != null) {
                var label = vertexLabel[indexOf(v)];
                if (label != null) {
                    sb.append(":").append(label);
                }
            }
            if (vertexWeight != null) {
                sb.append("=").append(vertexWeight[indexOf(v)]);
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + this.numVertices;
        hash = 11 * hash + Long.hashCode(this.numEdges);
        hash = 11 * hash + Arrays.deepHashCode(this.adjList);
        hash = 11 * hash + Arrays.hashCode(this.degree);
        hash = 11 * hash + Arrays.hashCode(this.vertexWeight);
        hash = 11 * hash + Arrays.deepHashCode(this.edgeWeight);
        hash = 11 * hash + Arrays.deepHashCode(this.vertexLabel);
        hash = 11 * hash + Arrays.deepHashCode(this.edgeLabel);
        hash = 11 * hash + (this.directed ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GraphImpl<?, ?> other = (GraphImpl<?, ?>) obj;
        if (this.numVertices != other.numVertices) {
            return false;
        }
        if (this.numEdges != other.numEdges) {
            return false;
        }
        if (this.directed != other.directed) {
            return false;
        }
        if (!Arrays.deepEquals(this.adjList, other.adjList)) {
            return false;
        }
        if (!Arrays.equals(this.degree, other.degree)) {
            return false;
        }
        if (!Arrays.equals(this.vertexWeight, other.vertexWeight)) {
            return false;
        }
        if (!Arrays.deepEquals(this.edgeWeight, other.edgeWeight)) {
            return false;
        }
        if (!Arrays.deepEquals(this.vertexLabel, other.vertexLabel)) {
            return false;
        }
        if (!Arrays.deepEquals(this.edgeLabel, other.edgeLabel)) {
            return false;
        }
        return true;
    }

    @Override
    public NeighborIterator<E> neighborIterator(int v, int pos) {
        return new NeighborIteratorImpl(v, pos);

    }

    protected class NeighborIteratorImpl implements NeighborIterator<E> {

        private final int v;
        private final int vi;
        private int pos;
        private boolean forward = true;

        public NeighborIteratorImpl(int v) {
            this(v, -1);
        }

        public NeighborIteratorImpl(int v, int pos) {
            this.v = v;
            this.vi = indexOf(v);
            this.pos = pos;
        }

        @Override
        public int adjListPos() {
            return pos;
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
            forward = true;
            return adjList[vi][++pos];
        }

        @Override
        public int previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            forward = false;
            return adjList[vi][--pos];
        }

        @Override
        public void setEdgeWeight(double weight) {
            checkPos();
            setEdgeWeightAt(vi, pos, weight);
        }

        @Override
        public double getEdgeWeight() {
            checkPos();
            if (edgeWeight == null) {
                return DEFAULT_EDGE_WEIGHT;
            }
            return edgeWeight[vi][pos];
        }

        @Override
        public void setEdgeLabel(E label) {
            checkPos();
            setEdgeLabelAt(vi, pos, label);
        }

        @Override
        public E getEdgeLabel() {
            checkPos();
            if (edgeLabel == null) {
                return null;
            }
            return edgeLabel[vi][pos];
        }

        @Override
        public void removeEdge() {
            checkPos();
            removeEdgeAt(vi, pos);
            if (forward) {
                pos--;
            }
        }

        @Override
        public Edge edge() {
            checkPos();
            return edgeAt(vi, pos);
        }

        private void checkPos() {
            if (pos < 0) {
                throw new NoSuchElementException();
            }
        }
    }
}
