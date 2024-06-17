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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.graph4j.util.IntArrays;

/**
 * Support class for creating a graph of any type.
 *
 * @author Cristian Frăsinaru
 */
abstract class GraphBuilderBase {

    protected int[] vertices;
    protected final List<Integer> dynamicVertices = new ArrayList<>();
    protected final Map<Object, Integer> vertexLabelMap = new HashMap<>();
    protected final Map<Integer, Double> vertexWeightMap = new HashMap<>();
    protected Integer maxVertices;
    protected Long numEdges;
    protected Integer avgDegree;
    protected Double density;
    protected boolean directed;
    protected boolean allowingSelfLoops;
    protected boolean allowingMultiEdges;
    protected int vertexDataSize = 1;
    protected int edgeDataSize = 1;
    protected String name;
    //
    protected final List<Edge> edges = new ArrayList();
    protected final List<int[]> paths = new ArrayList();
    protected final List<int[]> cycles = new ArrayList();
    protected final List<int[]> cliques = new ArrayList();

    protected GraphBuilderBase() {
    }

    abstract GraphImpl newInstance();

    /**
     * Sets an estimation of the number of vertices in the graph, in order to
     * optimize memory allocation.
     *
     * No vertex will be added to the graph as a result of this invocation.
     *
     * @param numVertices the estimated maximum number of vertices.
     * @return a reference to this object.
     */
    public GraphBuilderBase estimatedNumVertices(int numVertices) {
        if (numVertices < 0) {
            throw new IllegalArgumentException("Maximum number of vertices must be non-negative.");
        }
        this.maxVertices = numVertices;
        return this;
    }

    /**
     * Sets an estimation of the number of edges in the graph, in order to
     * optimize memory allocation.
     *
     * @param numEdges the estimated maximum number of edges.
     * @return a reference to this object.
     */
    public GraphBuilderBase estimatedNumEdges(long numEdges) {
        if (numEdges < 0) {
            throw new IllegalArgumentException("Number of edges must be non-negative.");
        }
        this.numEdges = numEdges;
        return this;
    }

    /**
     * Sets an estimation of the graph average vertex degree, in order to
     * optimize memory allocation.
     *
     * @param avgDegree the estimated average degree of the vertices.
     * @return a reference to this object.
     */
    public GraphBuilderBase estimatedAvgDegree(int avgDegree) {
        if (avgDegree <= 0) {
            throw new IllegalArgumentException("Average degree must be positive.");
        }
        this.avgDegree = avgDegree;
        return this;
    }

    /**
     * Sets an estimation of the graph density, in order to optimize memory
     * allocation.
     *
     * @param density the estimated density of the graph.
     * @return a reference to this object.
     */
    public GraphBuilderBase estimatedDensity(double density) {
        if (density < 0 || density > 1) {
            throw new IllegalArgumentException("Density must be in the range [0,1]");
        }
        this.density = density;
        return this;
    }

    public GraphBuilderBase vertexDataSize(int vertexDataSize) {
        this.vertexDataSize = vertexDataSize;
        return this;
    }

    public GraphBuilderBase edgeDataSize(int edgeDataSize) {
        this.edgeDataSize = edgeDataSize;
        return this;
    }

    /**
     * Adds an edge to the graph, specified using its vertex numbers.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return a reference to this object.
     */
    public GraphBuilderBase addEdge(int v, int u) {
        getVertex(v);
        getVertex(u);
        edges.add(new Edge(v, u));
        return this;
    }

    /**
     * Adds an edge to the graph, specified using its vertex labels.
     *
     * @param <V> the type of vertex labels.
     * @param vLabel a labeled vertex.
     * @param uLabel a labeled vertex.
     * @return a reference to this object.
     */
    public <V> GraphBuilderBase addEdge(V vLabel, V uLabel) {
        int v = vertexLabelMap.getOrDefault(vLabel, -1);
        int u = vertexLabelMap.getOrDefault(uLabel, -1);
        edges.add(new Edge(v, u));
        return this;
    }

    /**
     * Adds an edge to the graph.
     *
     * @param e an object containing the endpoints of the edge, eventually its
     * weight and label.
     * @return a reference to this object.
     */
    public GraphBuilderBase addEdge(Edge e) {
        getVertex(e.source());
        getVertex(e.target());
        edges.add(e);
        return this;
    }

    /**
     * Adds to the graph a set of edges represented as a string, for example:
     * "1-2, 2-3, 3-1", "a-b, b-c, c-d", etc.
     *
     * "0-1,0-2-0-3" may be written as "0-1,2,3".
     *
     * @param edges a text representation of the edges to be added.
     * @return a reference to this object.
     */
    public GraphBuilderBase addEdges(String edges) {
        String[] edgeTokens = edges.split(",");
        int v = -1;
        for (String edgeToken : edgeTokens) {
            String[] edgeVertices = edgeToken.trim().split("-");
            int u;
            try {
                if (edgeVertices.length == 1) {
                    //v is the previous vertex
                    String ustr = edgeVertices[0].trim();
                    try {
                        u = getVertex(Integer.parseInt(ustr));
                    } catch (NumberFormatException e) {
                        u = getVertex(ustr);
                    }
                } else {
                    String vstr = edgeVertices[0].trim();
                    String ustr = edgeVertices[1].trim();
                    try {
                        v = getVertex(Integer.parseInt(vstr));
                        u = getVertex(Integer.parseInt(ustr));
                    } catch (NumberFormatException e) {
                        v = getVertex(vstr);
                        u = getVertex(ustr);
                    }
                }
                addEdge(v, u);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException(
                        "Incorrect format for edges: " + edgeToken);
            }
        }
        return this;
    }

    /**
     * Builds the graph from the adjacency list. The number of rows is the
     * number of vertices, for the row with the index i, a[i] represents the
     * neighbors (successors) of the vertex with the index i in the graph.
     *
     * @param a the adjacency list.
     * @return a reference to this object.
     */
    public GraphBuilderBase adjList(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            int v = vertices == null ? i : vertices[i];
            for (int j = 0; j < a[i].length; j++) {
                int u = getVertex(a[i][j]);
                addEdge(v, u);
            }
        }
        return this;
    }

    protected int getVertex(int v) {
        if (vertices.length > 0) {
            if (!IntArrays.contains(vertices, v)) {
                throw new IllegalArgumentException("Invalid vertex: " + v);
            }
            return v;
        }
        if (!dynamicVertices.contains(v)) {
            dynamicVertices.add(v);
        }
        return v;
    }

    protected int getVertex(String vstr) {
        int v = findVertexWithLabel(vstr);
        if (v >= 0) {
            return v;
        }
        if (vertices.length > 0) {
            throw new IllegalArgumentException("Invalid vertex: " + v);
        }
        v = nextVertexNumber();
        dynamicVertices.add(v);
        vertexLabelMap.put(vstr, v);

        return v;
    }

    protected int findVertexWithLabel(String strLabel) {
        for (Object label : vertexLabelMap.keySet()) {
            if (String.valueOf(label).equals(strLabel)) {
                return vertexLabelMap.get(label);
            }
        }
        return -1;
    }

    //for dynamic vertices
    protected int nextVertexNumber() {
        return 1 + dynamicVertices.stream().mapToInt(a -> a).max().orElse(-1);
    }

    /**
     * Adds a path to the graph, specified by its vertex numbers.
     *
     * @param path an array of vertex numbers.
     * @return a reference to this object.
     */
    public GraphBuilderBase addPath(int... path) {
        for (int v : path) {
            getVertex(v);
        }
        paths.add(path);
        return this;
    }

    /**
     * Adds a cycle to the graph, specified by its vertex numbers.
     *
     * @param cycle an array of vertex numbers.
     * @return a reference to this object.
     */
    public GraphBuilderBase addCycle(int... cycle) {
        for (int v : cycle) {
            getVertex(v);
        }
        cycles.add(cycle);
        return this;
    }

    /**
     * Adds a clique to the graph, specified by its vertex numbers.
     *
     * @param clique an array of vertex numbers.
     * @return a reference to this object.
     */
    public GraphBuilderBase addClique(int... clique) {
        for (int v : clique) {
            getVertex(v);
        }
        cliques.add(clique);
        return this;
    }

    /**
     * Sets the name of the graph.
     *
     * @param name the name to be set for the graph.
     * @return a reference to this object.
     */
    public GraphBuilderBase named(String name) {
        this.name = name;
        return this;
    }

    protected void validate() {
        if (!dynamicVertices.isEmpty()) {
            vertices = dynamicVertices.stream().mapToInt(v -> v).toArray();
        }
        int max = IntStream.of(vertices).max().orElse(0);
        int numVertices = vertices.length;
        if (maxVertices == null || maxVertices < max) {
            maxVertices = max + 1;
        }
        if (!edges.isEmpty()) {
            if (numEdges == null || numEdges < edges.size()) {
                numEdges = (long) edges.size();
            }
        }
        //number of edges
        if (numVertices > 0 && numEdges != null && density != null) {
            throw new IllegalArgumentException("Illegal combination of parameters: numEdges and density");
        }
        if (numVertices > 0 && numEdges != null && avgDegree != null) {
            throw new IllegalArgumentException("Illegal combination of parameters: numEdges and avgDegree");
        }
        if (avgDegree != null && density != null) {
            throw new IllegalArgumentException("Illegal combination of parameters: avgDegree and density");
        }
    }

    protected int avgDegree() {
        if (avgDegree != null) {
            return avgDegree;
        }
        int n = vertices.length;
        if (n == 0) {
            return 0;
        }
        if (numEdges != null) {
            return (int) ((directed ? 1 : 2) * numEdges / n);
        }
        if (density != null) {
            return (int) (density * (n - 1));
        }
        return 0;
    }

    GraphImpl build() {
        validate();
        var g = newInstance();
        g.setName(name);

        //weights
        for (int v : vertexWeightMap.keySet()) {
            g.setVertexWeight(v, vertexWeightMap.get(v));
        }
        //labels
        for (var label : vertexLabelMap.keySet()) {
            int v = vertexLabelMap.get(label);
            g.setVertexLabel(v, label);
        }
        //paths
        for (int[] path : paths) {
            for (int i = 0; i < path.length - 1; i++) {
                g.addEdge(path[i], path[i + 1]);
            }
        }
        //cycles
        for (int[] cycle : cycles) {
            int n = cycle.length;
            for (int i = 0; i < n - 1; i++) {
                g.addEdge(cycle[i], cycle[i + 1]);
            }
            g.addEdge(cycle[n - 1], cycle[0]);
        }
        //cliques
        for (int[] clique : cliques) {
            int n = clique.length;
            if (directed) {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (i != j) {
                            g.addEdge(clique[i], clique[j]);
                        }
                    }
                }
            } else {
                for (int i = 0; i < n - 1; i++) {
                    for (int j = i + 1; j < n; j++) {
                        g.addEdge(clique[i], clique[j]);
                    }
                }
            }
        }
        //edges
        for (Edge e : edges) {
            g.addEdge(e);
        }
        return g;
    }

}
