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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.IntStream;
import static org.graph4j.Graph.DEFAULT_EDGE_WEIGHT;
import static org.graph4j.Graph.WEIGHT;
import org.graph4j.util.Validator;
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
    //adjPos should be null if the graph is directed

    protected double[] vertexWeight;
    protected int vertexDataSize = 1;

    protected double[][][] edgeData; //weight, cost, flow, etc.
    protected int edgeDataSize = 1;
    protected V[] vertexLabel;
    protected E[][] edgeLabel;

    protected VertexIndex vertexIndex; //to find the index of a vertex
    protected AdjacencySet[] adjSet; //for fast check if an edge is present in the graph    
    protected AdjacencyMap[] adjMap;

    protected Integer maxVertexNumber;
    protected Map<V, Integer> labelVertexMap;
    protected Map<E, Edge> labelEdgeMap;

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
            boolean directed, boolean allowingMultipleEdges, boolean allowingSelfLoops,
            int vertexDataSize, int edgeDataSize) {
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
        //
        if (edgeDataSize < 0) {
            throw new IllegalArgumentException("Invalid edge data size: " + edgeDataSize);
        }
        this.edgeDataSize = edgeDataSize;
        //
        if (vertexDataSize < 0) {
            throw new IllegalArgumentException("Invalid vertex data size: " + vertexDataSize);
        }
        this.vertexDataSize = vertexDataSize;
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
            boolean directed, boolean allowingMultipleEdges, boolean allowingSelfLoops,
            int vertexDataSize, int edgeDataSize) {
        return new GraphImpl(vertices, maxVertices, avgDegree, directed,
                allowingMultipleEdges, allowingSelfLoops, vertexDataSize, edgeDataSize);
    }

    @Override
    public Graph<V, E> copy() {
        return copy(true, true, true, true, true);
    }

    @Override
    public Graph<V, E> copy(boolean copyVertexData, boolean copyVertexLabels,
            boolean copyEdges, boolean copyEdgeData, boolean copyEdgeLabels) {
        if (!copyEdges) {
            copyEdgeData = false;
            copyEdgeLabels = false;
        }
        var copy = newInstance();
        copyTo(copy, copyVertexData, copyVertexLabels, copyEdges, copyEdgeData, copyEdgeLabels);
        return copy;
    }

    protected void copyTo(GraphImpl copy, boolean copyVertexData, boolean copyVertexLabels,
            boolean copyEdges, boolean copyEdgeData, boolean copyEdgeLabels) {
        if (!copyEdges) {
            copyEdgeData = false;
            copyEdgeLabels = false;
        }
        copy.numVertices = numVertices;
        copy.maxVertices = maxVertices;
        copy.numEdges = copyEdges ? numEdges : 0;
        copy.avgDegree = avgDegree;
        copy.directed = directed;
        copy.allowingMultipleEdges = allowingMultipleEdges;
        copy.allowingSelfLoops = allowingSelfLoops;

        copy.vertices = Arrays.copyOf(vertices, numVertices);
        copy.degree = copyEdges ? Arrays.copyOf(degree, numVertices) : new int[vertices.length];

        if (this.vertexWeight != null && copyVertexData) {
            copy.vertexWeight = Arrays.copyOf(vertexWeight, numVertices);
        }
        if (this.vertexLabel != null && copyVertexLabels) {
            copy.vertexLabel = Arrays.copyOf(vertexLabel, numVertices);
            if (labelVertexMap != null) {
                copy.labelVertexMap = new HashMap<>(labelVertexMap);
            }
        }
        copy.adjList = new int[numVertices][];
        if (adjPos != null) {
            copy.adjPos = new int[numVertices][];
        }
        if (adjSet != null) {
            copy.adjSet = new AdjacencySet[numVertices];
        }
        if (adjMap != null) {
            copy.adjMap = new AdjacencyMap[numVertices];
        }
        if (edgeData != null && copyEdgeData) {
            copy.edgeData = new double[edgeData.length][][];
            for (int k = 0; k < edgeData.length; k++) {
                if (edgeData[k] != null) {
                    copy.edgeData[k] = new double[numVertices][];
                }
            }
        }
        if (edgeLabel != null) {
            copy.edgeLabel = new Object[numVertices][];
        }
        if (copyEdges) {
            for (int i = 0; i < numVertices; i++) {
                if (adjList[i] != null) {
                    copy.adjList[i] = Arrays.copyOf(adjList[i], adjList[i].length);
                }
                if (adjPos != null && adjPos[i] != null) {
                    copy.adjPos[i] = Arrays.copyOf(adjPos[i], adjPos[i].length);
                }
                if (edgeData != null && copyEdgeData) {
                    for (int k = 0; k < copy.edgeData.length; k++) {
                        if (edgeData[k] != null && edgeData[k][i] != null) {
                            copy.edgeData[k][i] = Arrays.copyOf(edgeData[k][i], edgeData[k][i].length);
                        }
                    }
                }
                if (edgeLabel != null && edgeLabel[i] != null && copyEdgeLabels) {
                    copy.edgeLabel[i] = Arrays.copyOf(edgeLabel[i], edgeLabel[i].length);
                }
                if (adjSet != null && adjSet[i] != null) {
                    copy.adjSet[i] = adjSet[i].copy();
                }
                if (adjMap != null && adjMap[i] != null) {
                    copy.adjMap[i] = adjMap[i].copy();
                }
            }
        }
        if (copyEdgeLabels) {
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
    }

    @Override
    public Multigraph copyAsMultigraph() {
        if (this instanceof Pseudograph) {
            throw new UnsupportedOperationException(
                    "A pseudograph cannot be transformed into a multigraph");
        }
        var copy = directed ? new DirectedMultigraphImpl() : new MultigraphImpl();
        copyTo(copy, true, true, true, true, true);
        copy.allowingMultipleEdges = true;
        return copy;
    }

    @Override
    public Multigraph copyAsPseudograph() {
        var copy = directed ? new DirectedPseudographImpl() : new PseudographImpl();
        copyTo(copy, true, true, true, true, true);
        copy.allowingMultipleEdges = true;
        copy.allowingSelfLoops = true;
        return copy;
    }

    @Override
    public void setEdgeDataSize(int edgeDataSize) {
        if (edgeData == null) {
            this.edgeDataSize = edgeDataSize;
            return;
        }
        if (edgeDataSize < this.edgeDataSize) {
            throw new IllegalArgumentException(
                    "The new edge data size cannot be smaller than the current one: "
                    + edgeDataSize + " < " + this.edgeDataSize);
        }
        var copyEdgeData = new double[edgeDataSize][][];
        for (int k = 0; k < edgeData.length; k++) {
            if (edgeData[k] != null) {
                copyEdgeData[k] = new double[numVertices][];
                for (int i = 0; i < numVertices; i++) {
                    if (edgeData[k][i] != null) {
                        copyEdgeData[k][i] = Arrays.copyOf(edgeData[k][i], edgeData[k][i].length);
                    }
                }
            }
        }
        this.edgeData = copyEdgeData;
    }

    @Override
    public int getEdgeDataSize() {
        return edgeDataSize;
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
            if (adjList[i] != null) {
                for (int j = 0; j < adjList[i].length; j++) {
                    adjList[i][j] = adjList[i][j] + amount;
                }
            }
        }
        resetCache();
    }

    private void resetCache() {
        adjSet = null;
        adjMap = null;
        labelVertexMap = null;
        maxVertexNumber = null;
        labelVertexMap = null;
        labelEdgeMap = null;
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
        if (index < 0 || index >= numVertices) {
            throw new IllegalArgumentException(
                    "Index must be in the range [0," + (numVertices - 1) + "]: " + index);
        }
        return vertices[index];
    }

    @Override
    public boolean isDefaultVertexNumbering() {
        return vertexIndex == null;
    }

    @Override
    public int indexOf(int v) {
        if (vertexIndex == null) {
            //default vertices
            return v < 0 || v >= numVertices ? -1 : v;
        }
        return vertexIndex.indexOf(v);
    }

    protected int checkVertex(int v) {
        int vi = indexOf(v);
        if (vi < 0) {
            throw new InvalidVertexException(v);
        }
        return vi;
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

    //the main addLabeledVertex
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
        //if (!isLastPos) {
        swapVertexWithLast(vi);
        //}
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
        degree[lastPos] = 0;
        //
        if (adjPos != null) {
            adjPos[i] = adjPos[lastPos];
            adjPos[lastPos] = null;
        }
        if (adjSet != null) {
            adjSet[i] = adjSet[lastPos];
            adjSet[lastPos] = null;
        }
        if (adjMap != null) {
            adjMap[i] = adjMap[lastPos];
            adjMap[lastPos] = null;
        }
        if (vertexWeight != null) {
            vertexWeight[i] = vertexWeight[lastPos];
        }
        if (edgeData != null) {
            for (int k = 0; k < edgeData.length; k++) {
                if (edgeData[k] != null) {
                    edgeData[k][i] = edgeData[k][lastPos];
                    edgeData[k][lastPos] = null;
                }
            }
        }
        if (vertexLabel != null) {
            vertexLabel[i] = vertexLabel[lastPos];
        }
        if (edgeLabel != null) {
            edgeLabel[i] = edgeLabel[lastPos];
        }
    }

    @Override
    public int maxVertexNumber() {
        if (maxVertexNumber == null) {
            maxVertexNumber = IntStream.of(vertices()).max().orElse(-1);
        }
        return maxVertexNumber;
    }

    @Override
    public int addEdge(Edge<E> e) {
        int v = e.source();
        int u = e.target();
        int pos = addEdge(v, u);
        if (pos < 0) {
            return pos;
        }
        if (e.data != null) {
            int vi = indexOf(v);
            for (int k = 0; k < e.data.length; k++) {
                Double value = e.data[k];
                if (value != null) {
                    setEdgeDataAt(k, vi, pos, value);
                }
            }
        }
        return pos;
    }

    @Override
    public int addEdge(int v, int u, double weight) {
        int pos = addEdge(v, u);
        if (pos < 0) {
            return pos;
        }
        setEdgeWeightAt(indexOf(v), pos, weight);
        return pos;
    }

    @Override
    public int addLabeledEdge(int v, int u, E label) {
        int pos = addEdge(v, u);
        if (pos < 0) {
            return pos;
        }
        setEdgeLabelAt(indexOf(v), pos, label);
        return pos;
    }

    @Override
    public int addLabeledEdge(int v, int u, E label, double weight) {
        int pos = addEdge(v, u);
        if (pos < 0) {
            return pos;
        }
        setEdgeWeightAt(indexOf(v), pos, weight);
        setEdgeLabelAt(indexOf(v), pos, label);
        return pos;
    }

    //the main addEdge method
    @Override
    public int addEdge(int v, int u) {
        if (safeMode) {
            checkVertex(v);
            checkVertex(u);
            if (!allowingSelfLoops && v == u) {
                return -1;
            }
            if (!allowingMultipleEdges && containsEdge(v, u)) {
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
        if (adjMap != null && adjMap[vi] != null) {
            adjMap[vi].add(u, pos);
        }
        degree[vi]++;
        return pos;
    }

    @Override
    public void removeEdge(int v, int u) {
        Validator.containsEdge(this, v, u);
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
        if (adjMap != null && adjMap[vi] != null) {
            if (!allowingMultipleEdges || multiplicity(v, u) == 0) {
                adjMap[vi].remove(u);
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
        if (edgeData != null) {
            for (int k = 0; k < edgeData.length; k++) {
                if (edgeData[k] != null) {
                    edgeData[k][vi][pos] = edgeData[k][vi][degree[vi] - 1];
                }
            }
        }
        if (edgeLabel != null) {
            edgeLabel[vi][pos] = edgeLabel[vi][degree[vi] - 1];
        }
    }

    //Returns the first position of u in the neighbor list of v.
    public int adjListPos(int v, int u) {
        int vi = indexOf(v);
        int deg = degree[vi];
        if (deg == 0) {
            return -1;
        }
        /*
        if (adjListMatrix != null) {
            return adjListMatrix[vi][indexOf(u)];
        }*/
        //if the degree of v is small enough, just iterate
        if (deg < numVertices / deg) {
            for (int pos = 0; pos < deg; pos++) {
                if (adjList[vi][pos] == u) {
                    return pos;
                }
            }
        }
        return getAdjMap(vi).position(u);
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
        Double[] data = null;
        if (edgeData != null) {
            data = new Double[edgeData.length];
            for (int k = 0; k < edgeData.length; k++) {
                if (edgeData[k] != null) {
                    data[k] = edgeData[k][vi][pos];
                }
            }
        }
        var label = edgeLabel != null ? edgeLabel[vi][pos] : null;
        var e = new Edge(vertices[vi], adjList[vi][pos], label, data);
        e.directed = directed;
        return e;
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
            adjSet = new AdjacencySet[vertices.length];
        }
        if (adjSet[vi] == null) {
            adjSet[vi] = new AdjacencyBitSet();
            for (int pos = 0; pos < degree[vi]; pos++) {
                adjSet[vi].add(adjList[vi][pos]);
            }
        }
        return adjSet[vi];
    }

    private AdjacencyMap getAdjMap(int vi) {
        if (adjMap == null) {
            adjMap = new AdjacencyMap[vertices.length];
        }
        if (adjMap[vi] == null) {
            adjMap[vi] = new AdjacencyIntHashMap();
            for (int pos = 0; pos < degree[vi]; pos++) {
                adjMap[vi].add(adjList[vi][pos], pos);
            }
        }
        return adjMap[vi];
    }

    @Override
    public boolean containsEdge(int v, int u) {
        int vi = checkVertex(v);
        checkVertex(u);
        int deg = degree[vi];
        if (deg == 0) {
            return false;
        }
        //if the degree of v is small enough, just iterate
        if (deg < numVertices / deg) {
            //return adjListPos(v, u) >= 0;
            for (int pos = 0; pos < deg; pos++) {
                if (adjList[vi][pos] == u) {
                    return true;
                }
            }
            return false;
        }
        //switch to adjacency sets (bitsets)
        return getAdjSet(vi).contains(u);
    }

    @Override
    public int degree(int v) {
        int vi = indexOf(v);
        if (vi < 0) {
            throw new InvalidVertexException(v);
        }
        return degree[vi];
    }

    @Override
    public int[] degrees() {
        return Arrays.copyOf(degree, numVertices);
    }

    /**
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return the multiplicity of the edge (v,u).
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

    protected void initEdgeData() {
        this.edgeData = new double[edgeDataSize][][];
    }

    protected void initEdgeData(int dataType) {
        if (edgeData == null) {
            initEdgeData();
        }
        this.edgeData[dataType] = new double[vertices.length][];
        for (int i = 0; i < numVertices; i++) {
            this.edgeData[dataType][i] = adjList[i] == null ? null : new double[adjList[i].length];
        }
    }

    protected void initEdgeWeights() {
        initEdgeData(WEIGHT);
    }

    @Override
    public boolean hasEdgeWeights() {
        return edgeData != null && edgeData[WEIGHT] != null;
    }

    @Override
    public boolean hasEdgeData(int dataType) {
        return edgeData != null && edgeData[dataType] != null;
    }

    @Override
    public void resetEdgeData(int dataType, double value) {
        if (!hasEdgeData(dataType)) {
            return;
        }
        for (int i = 0; i < numVertices; i++) {
            for (int pos = 0; pos < degree[i]; pos++) {
                edgeData[dataType][i][pos] = value;
            }
        }
    }

    @Override
    public boolean hasVertexWeights() {
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
    public void setVertexWeight(int v, double weight) {
        int vi = checkVertex(v);
        if (vertexWeight == null) {
            initVertexWeights();
        }
        vertexWeight[vi] = weight;
    }

    @Override
    public double getVertexWeight(int v) {
        int vi = checkVertex(v);
        if (vertexWeight == null) {
            return DEFAULT_VERTEX_WEIGHT;
        }
        return vertexWeight[vi];
    }

    @Override
    public void setEdgeData(int dataType, int v, int u, double value) {
        checkEdge(v, u);
        int vi = indexOf(v);
        int pos = adjListPos(v, u);
        setEdgeDataAt(dataType, vi, pos, value);
    }

    //the only method where we set edge data
    protected void setEdgeDataAt(int dataType, int vi, int pos, double value) {
        if (!hasEdgeData(dataType)) {
            initEdgeData(dataType);
        }
        int v = vertices[vi];
        int u = adjList[vi][pos];
        int ui = indexOf(u);
        edgeData[dataType][vi][pos] = value;
        if (v != u && !directed) {
            edgeData[dataType][ui][adjPos[vi][pos]] = value;
        }
        if (labelEdgeMap != null) {
            Edge e = labelEdgeMap.get(edgeLabel[vi][pos]);
            if (e != null) {
                e.data[dataType] = value;
            }
        }
    }

    @Override
    public double getEdgeData(int dataType, int v, int u, double defaultValue) {
        if (!containsEdge(v, u)) {
            return Double.POSITIVE_INFINITY;
        }
        return getEdgeDataAt(dataType, indexOf(v), adjListPos(v, u), defaultValue);
    }

    protected double getEdgeDataAt(int dataType, int vi, int pos, double defaultValue) {
        if (!hasEdgeData(dataType)) {
            return defaultValue;
        }
        return edgeData[dataType][vi][pos];
    }

    protected void incEdgeDataAt(int dataType, int vi, int pos, double amount) {
        double value = hasEdgeData(dataType) ? edgeData[dataType][vi][pos] : 0;
        setEdgeDataAt(dataType, vi, pos, value + amount);
    }

    @Override
    public void incEdgeData(int dataType, int v, int u, double amount) {
        checkEdge(v, u);
        int vi = indexOf(v);
        int pos = adjListPos(v, u);
        double value = hasEdgeData(dataType) ? edgeData[dataType][vi][pos] : 0;
        setEdgeDataAt(dataType, vi, pos, value + amount);
    }

    @Override
    public void setEdgeWeight(int v, int u, double weight) {
        setEdgeData(WEIGHT, v, u, weight);
    }

    //the only method where we set edge weight
    protected void setEdgeWeightAt(int vi, int pos, double weight) {
        setEdgeDataAt(WEIGHT, vi, pos, weight);
    }

    @Override
    public double getEdgeWeight(int v, int u) {
        return getEdgeData(WEIGHT, v, u, DEFAULT_EDGE_WEIGHT);
    }

    protected double getEdgeWeightAt(int vi, int pos) {
        return getEdgeDataAt(WEIGHT, vi, pos, DEFAULT_EDGE_WEIGHT);
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
    public void setVertexLabel(int v, V label) {
        int vi = checkVertex(v);
        if (vertexLabel == null) {
            initVertexLabels();
        }
        vertexLabel[vi] = label;
        if (labelVertexMap != null) {
            labelVertexMap.put(label, v);
        }
    }

    @Override
    public V getVertexLabel(int v) {
        int vi = checkVertex(v);
        if (vertexLabel == null) {
            return null;
        }
        return (V) vertexLabel[vi];
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
        if (labelEdgeMap != null) {
            Edge e = labelEdgeMap.get(oldLabel);
            if (e == null) {
                e = edge(v, u);
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
    public boolean hasEdgeLabels() {
        return edgeLabel != null;
    }

    @Override
    public boolean hasVertexLabels() {
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
        if (adjMap != null) {
            adjMap = Arrays.copyOf(adjMap, newLen);
        }
        if (vertexWeight != null) {
            vertexWeight = Arrays.copyOf(vertexWeight, newLen);
        }
        if (edgeData != null) {
            for (int k = 0; k < edgeData.length; k++) {
                if (edgeData[k] != null) {
                    edgeData[k] = Arrays.copyOf(edgeData[k], newLen);
                }
            }
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
        if (edgeData != null) {
            for (int k = 0; k < edgeData.length; k++) {
                if (edgeData[k] != null) {
                    if (edgeData[k][vi] != null) {
                        edgeData[k][vi] = Arrays.copyOf(edgeData[k][vi], newLen);
                    } else {
                        edgeData[k][vi] = new double[newLen];
                    }
                }
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
        int k = 0;
        for (int i = 0; i < numVertices; i++) {
            int v = vertices[i];
            for (int pos = 0, deg = degree[i]; pos < deg; pos++) {
                int u = adjList[i][pos];
                if (directed || v <= u) {
                    edges[k++] = edgeAt(i, pos);
                }
            }
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

    //TODO: how to avoid Edge creation
    @Override
    public Graph<V, E> subgraph(VertexSet vertexSet) {
        int[] vertexArray = vertexSet.vertices();
        int n = vertexArray.length;
        int deg = (int) IntStream.of(vertexArray).map(v -> this.degree(v)).average().orElse(0);
        deg = (deg * n) / numVertices;
        var sub = newInstance(vertexArray, n, deg, directed, allowingMultipleEdges, allowingSelfLoops,
                vertexDataSize, edgeDataSize);
        sub.setSafeMode(false);
        for (int v : vertexArray) {
            int graphIdx = indexOf(v); //in graph
            int subIdx = sub.indexOf(v); //in subgraph
            if (vertexWeight != null) {
                sub.setVertexWeight(v, vertexWeight[graphIdx]);
            }
            if (vertexLabel != null) {
                sub.setVertexLabel(v, vertexLabel[graphIdx]);
            }
            for (int graphPos = 0; graphPos < degree[graphIdx]; graphPos++) {
                int u = adjList[graphIdx][graphPos];
                if ((directed || v <= u) && vertexSet.contains(u)) {
                    int subPos = sub.addEdge(v, u);
                    if (edgeData != null) {
                        for (int k = 0; k < edgeData.length; k++) {
                            if (edgeData[k] != null) {
                                sub.setEdgeDataAt(k, subIdx, subPos, edgeData[k][graphIdx][graphPos]);
                            }
                        }
                    }
                    if (edgeLabel != null) {
                        sub.setEdgeLabelAt(subIdx, subPos, edgeLabel[graphIdx][graphPos]);
                    }
                }
            }
        }
        sub.setSafeMode(true);
        return sub;
    }

    @Override
    public Graph<V, E> subgraph(Collection<Edge> edges) {
        VertexSet vertexSet = GraphUtils.getVertices(this, edges);
        int n = vertexSet.size();
        int deg = 1 + (n > 0 ? edges.size() / n : 0);
        var sub = newInstance(vertexSet.vertices(), n, deg, directed,
                allowingMultipleEdges, allowingSelfLoops, vertexDataSize, edgeDataSize);
        sub.setSafeMode(false);
        for (int v : vertexSet) {
            int vi = indexOf(v);
            if (vertexWeight != null) {
                sub.setVertexWeight(v, vertexWeight[vi]);
            }
            if (vertexLabel != null) {
                sub.setVertexLabel(v, vertexLabel[vi]);
            }
        }
        for (Edge e : edges) {
            sub.addEdge(e);
        }
        sub.setSafeMode(true);
        return sub;
    }

    public Graph<V, E> supportGraph() {
        GraphImpl copy = (GraphImpl) GraphBuilder.verticesFrom(this).buildGraph();
        copy.setSafeMode(false);
        for (var it = edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            if (e.isSelfLoop()) {
                continue;
            }
            if (edgeData == null || directed) {
                if (!copy.containsEdge(e)) {
                    copy.addEdge(e);
                }
            } else {
                //cumulate edge data            
                int v = e.source();
                int u = e.target();
                int pos = copy.adjListPos(v, u);
                if (pos < 0) {
                    pos = copy.addEdge(v, u);
                }
                int vi = indexOf(v); //same as in copy
                for (int k = 0; k < edgeData.length; k++) {
                    if (edgeData[k][vi] != null) {
                        copy.incEdgeDataAt(k, vi, pos, it.getData(k));
                    }
                }
            }
        }
        copy.setSafeMode(true);
        return copy;
    }

    @Override
    public Graph<V, E> complement() {
        if (allowingMultipleEdges || allowingSelfLoops) {
            throw new UnsupportedOperationException(
                    "Complement of a multigraph or pseudograph is not defined.");
        }
        GraphImpl<V, E> complement = (GraphImpl<V, E>) copy(true, true, false, false, false);
        complement.setSafeMode(false);
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
        complement.setSafeMode(true);
        return complement;
    }

    @Override
    public int duplicateVertex(int v) {
        int newVertex = addVertex();
        int newVertexId = indexOf(newVertex);
        int vi = indexOf(v);
        for (int pos = 0, deg = degree[vi]; pos < deg; pos++) {
            int newPos = addEdge(newVertex, adjList[vi][pos]);
            if (edgeData != null) {
                for (int k = 0; k < edgeData.length; k++) {
                    if (edgeData[k][vi] != null) {
                        setEdgeDataAt(k, newVertexId, newPos, edgeData[k][vi][pos]);
                    }
                }
            }
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
        int newVertexId = numVertices - 1;
        for (int v : vertices) {
            int vi = indexOf(v);
            for (int pos = 0, deg = degree[vi]; pos < deg; pos++) {
                int u = adjList[vi][pos];
                if (IntArrays.contains(vertices, u)) {
                    continue;
                }
                if (allowingMultipleEdges) {
                    addEdge(newVertex, u);
                } else if (directed || edgeData == null) {
                    if (!containsEdge(newVertex, u)) {
                        addEdge(newVertex, u);
                    }
                } else {
                    int newPos = adjListPos(newVertex, u);
                    if (newPos == -1) {
                        newPos = addEdge(newVertex, u);
                    }
                    //cumulate edge data                    
                    for (int k = 0; k < edgeData.length; k++) {
                        if (edgeData[k][vi] != null) {
                            incEdgeDataAt(k, newVertexId, newPos, edgeData[k][vi][pos]);
                        }
                    }
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
            for (int pos = 0, deg = degree[vi]; pos < deg; pos++) {
                int u = adjList[vi][pos];
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
    public double[][] weightMatrix() {
        double[][] weight = new double[numVertices][numVertices];
        boolean hasEdgeWeights = hasEdgeWeights();
        for (int vi = 0; vi < numVertices; vi++) {
            Arrays.fill(weight[vi], Double.POSITIVE_INFINITY);
            weight[vi][vi] = 0;
            for (int pos = 0, deg = degree[vi]; pos < deg; pos++) {
                int u = adjList[vi][pos];
                weight[vi][indexOf(u)]
                        = hasEdgeWeights ? edgeData[WEIGHT][vi][pos] : DEFAULT_EDGE_WEIGHT;
            }
        }
        return weight;
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
        hash = 11 * hash + Arrays.deepHashCode(this.edgeData);
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
        if (!Arrays.deepEquals(this.edgeData, other.edgeData)) {
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

        protected final int v;
        protected final int vi;
        protected int pos;
        protected boolean forward = true;

        public NeighborIteratorImpl(int v) {
            this(v, -1);
        }

        public NeighborIteratorImpl(int v, int pos) {
            checkVertex(v);
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
            if (pos >= degree[vi] - 1) {
                throw new NoSuchElementException();
            }
            forward = true;
            return adjList[vi][++pos];
        }

        @Override
        public int previous() {
            if (pos <= 0) {
                throw new NoSuchElementException();
            }
            forward = false;
            return adjList[vi][--pos];
        }

        @Override
        public void setEdgeWeight(double weight) {
            setEdgeData(WEIGHT, weight);
        }

        @Override
        public double getEdgeWeight() {
            return getEdgeData(WEIGHT, DEFAULT_EDGE_WEIGHT);
        }

        @Override
        public void setEdgeData(int edgeType, double value) {
            checkPos();
            setEdgeDataAt(edgeType, vi, pos, value);
        }

        @Override
        public void incEdgeData(int edgeType, double amount) {
            checkPos();
            incEdgeDataAt(edgeType, vi, pos, amount);
        }

        @Override
        public double getEdgeData(int dataType) {
            return getEdgeData(dataType, 0);
        }

        @Override
        public double getEdgeData(int dataType, double defaultValue) {
            checkPos();
            if (hasEdgeData(dataType)) {
                return edgeData[dataType][vi][pos];
            }
            return defaultValue;
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

        protected void checkPos() {
            if (pos < 0) {
                throw new NoSuchElementException();
            }
        }
    }
}
