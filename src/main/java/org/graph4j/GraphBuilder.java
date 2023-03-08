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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.graph4j.util.IntArrays;

/**
 * Support class for creating a graph, directed or not, weighted or not, holding
 * data or not.
 *
 * @author Cristian Frăsinaru
 */
public class GraphBuilder {

    private int[] vertices;
    private final List<Integer> dynamicVertices = new ArrayList<>();
    private final Map<Object, Integer> labelMap = new HashMap<>();
    private final Map<Integer, Double> weightMap = new HashMap<>();
    private Integer maxVertices;
    private Long numEdges;
    private Integer avgDegree;
    private Double density;
    private boolean directed;
    private boolean allowingSelfLoops;
    private boolean allowingMultiEdges;
    private String name;
    //
    private final List<Edge> edges = new ArrayList();
    private final List<int[]> paths = new ArrayList();
    private final List<int[]> cycles = new ArrayList();
    private final List<int[]> cliques = new ArrayList();

    private GraphBuilder() {
    }

    private GraphImpl newInstance() {
        GraphImpl graph;
        if (allowingMultiEdges) {
            if (allowingSelfLoops) {
                if (directed) {
                    graph = new DirectedPseudographImpl<>(vertices, maxVertices, avgDegree(),
                            directed, allowingMultiEdges, allowingSelfLoops);
                } else {
                    graph = new PseudographImpl<>(vertices, maxVertices, avgDegree(),
                            directed, allowingMultiEdges, allowingSelfLoops);
                }
            } else {
                if (directed) {
                    graph = new DirectedMultigraphImpl<>(vertices, maxVertices, avgDegree(),
                            directed, allowingMultiEdges, allowingSelfLoops);

                } else {
                    graph = new MultigraphImpl<>(vertices, maxVertices, avgDegree(),
                            directed, allowingMultiEdges, allowingSelfLoops);
                }
            }
        } else {
            if (directed) {
                graph = new DigraphImpl<>(vertices, maxVertices, avgDegree(),
                        directed, allowingMultiEdges, allowingSelfLoops);
            } else {
                graph = new GraphImpl<>(vertices, maxVertices, avgDegree(),
                        directed, allowingMultiEdges, allowingSelfLoops);
            }
        }
        return graph;
    }

    /**
     * The created graph will be empty (no vertices).
     *
     * @return a new a {@link GraphBuilder}.
     */
    public static GraphBuilder empty() {
        var builder = new GraphBuilder();
        builder.vertices = new int[0];
        return builder;
    }

    /**
     * The created graph will contain all the vertices numbered from
     * <code>0</code> to <code>numVertices - 1</code>.
     *
     * @param numVertices the actual number of vertices in the graph.
     * @return a new a {@link GraphBuilder}.
     */
    public static GraphBuilder numVertices(int numVertices) {
        if (numVertices < 0) {
            throw new IllegalArgumentException("Number of vertices must not be negative.");
        }
        var builder = new GraphBuilder();
        builder.vertices = new int[numVertices];
        for (int i = 0; i < numVertices; i++) {
            builder.vertices[i] = i;
        }
        return builder;
    }

    /**
     *
     * @param firstVertex The number of the first vertex.
     * @param lastVertex The number of the last vertex.
     * @return a new a {@link GraphBuilder}.
     */
    public static GraphBuilder vertexRange(int firstVertex, int lastVertex) {
        var builder = new GraphBuilder();
        builder.vertices = IntStream.rangeClosed(firstVertex, lastVertex).toArray();
        return builder;
    }

    /**
     *
     * @param vertices the vertices of the graph.
     * @return a new a {@link GraphBuilder}.
     */
    public static GraphBuilder vertices(int... vertices) {
        var builder = new GraphBuilder();
        builder.vertices = vertices;
        return builder;
    }

    /**
     *
     * @param <V> the type of vertex labels.
     * @param vertexObjects an array of objects, representing the labeled
     * vertices of the graph.
     * @return a new a {@link GraphBuilder}.
     */
    public static <V> GraphBuilder labeledVertices(Collection vertexObjects) {
        var builder = new GraphBuilder();
        int n = vertexObjects.size();
        builder.vertices = IntStream.range(0, n).toArray();
        int v = 0;
        for (var label : vertexObjects) {
            builder.labelMap.put(label, v++);
        }
        return builder;
    }

    /**
     *
     * @param vertexObjects a list of objects, representing the labeled vertices
     * of the graph.
     * @return a new a {@link GraphBuilder}.
     */
    public static <V> GraphBuilder labeledVertices(V... vertexObjects) {
        return labeledVertices(List.of(vertexObjects));
    }

    /**
     *
     * @param graph a graph of any type.
     * @return a new a {@link GraphBuilder}.
     */
    public static GraphBuilder verticesFrom(Graph graph) {
        var builder = new GraphBuilder();
        builder.vertices = IntArrays.copyOf(graph.vertices());
        if (graph.isVertexWeighted()) {
            for (int v : builder.vertices) {
                builder.weightMap.put(v, graph.getVertexWeight(v));
            }
        }
        if (graph.isVertexLabeled()) {
            for (int v : builder.vertices) {
                builder.labelMap.put(graph.getVertexLabel(v), v);
            }
        }
        return builder;
    }

    /**
     * This property can be specified in order to optimize the memory
     * allocation. No vertex will be added to the graph as a result of this
     * invocation.
     *
     * @param numVertices the estimated maximum number of vertices.
     * @return a reference to this object.
     */
    public GraphBuilder estimatedNumVertices(int numVertices) {
        if (numVertices <= 0) {
            throw new IllegalArgumentException("Maximum number of vertices must be positive.");
        }
        this.maxVertices = numVertices;
        return this;
    }

    /**
     * This property can be specified in order to determine the average degree
     * of the vertices or the density of the graph, optimizing memory
     * allocation.
     *
     * @param numEdges the estimated maximum number of edges.
     * @return a reference to this object.
     */
    public GraphBuilder estimatedNumEdges(long numEdges) {
        if (numEdges <= 0) {
            throw new IllegalArgumentException("Number of edges must be positive.");
        }
        this.numEdges = numEdges;
        return this;
    }

    /**
     * This property can be specified in order to optimize memory allocation.
     *
     * @param avgDegree the estimated average degree of the vertices.
     * @return a reference to this object.
     */
    public GraphBuilder estimatedAvgDegree(int avgDegree) {
        if (avgDegree <= 0) {
            throw new IllegalArgumentException("Average degree must be positive.");
        }
        this.avgDegree = avgDegree;
        return this;
    }

    /**
     * This property can be specified in order to optimize memory allocation.
     *
     * @param density the estimated density of the graph.
     * @return a reference to this object.
     */
    public GraphBuilder estimatedDensity(double density) {
        if (density < 0 || density > 1) {
            throw new IllegalArgumentException("Density must be in the range [0,1]");
        }
        this.density = density;
        return this;
    }

    /**
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return a reference to this object.
     */
    public GraphBuilder addEdge(int v, int u) {
        getVertex(v);
        getVertex(u);
        edges.add(new Edge(v, u));
        return this;
    }

    /**
     *
     * @param <V> the type of vertex labels.
     * @param vLabel a labeled vertex.
     * @param uLabel a labeled vertex.
     * @return a reference to this object.
     */
    public <V> GraphBuilder addEdge(V vLabel, V uLabel) {
        int v = labelMap.getOrDefault(vLabel, -1);
        int u = labelMap.getOrDefault(uLabel, -1);
        edges.add(new Edge(v, u));
        return this;
    }

    /**
     *
     * @param e an object containing the endpoints of the edge, eventually its
     * weight and label.
     * @return a reference to this object.
     */
    public GraphBuilder addEdge(Edge e) {
        getVertex(e.source());
        getVertex(e.target());
        edges.add(e);
        return this;
    }

    /**
     * Example: "1-2, 2-3, 3-1", "a-b, b-c,c-d", etc.
     *
     * This method is not type-safe.
     *
     * @param edges a text encoding the edges to be added.
     * @return a reference to this object.
     */
    public GraphBuilder addEdges(String edges) {
        String[] edgeTokens = edges.split(",");
        for (String edgeToken : edgeTokens) {
            String[] edgeVertices = edgeToken.trim().split("-");
            try {
                String vstr = edgeVertices[0].trim();
                String ustr = edgeVertices[1].trim();
                int v, u;
                try {
                    v = getVertex(Integer.parseInt(vstr));
                    u = getVertex(Integer.parseInt(ustr));
                } catch (NumberFormatException e) {
                    v = getVertex(vstr);
                    u = getVertex(ustr);
                }
                addEdge(v, u);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException(
                        "Incorrect format for edges: " + edgeToken);
            }
        }
        return this;
    }

    private int getVertex(int v) {
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

    private int getVertex(String vstr) {
        int v = findVertexWithLabel(vstr);
        if (v >= 0) {
            return v;
        }
        if (vertices.length > 0) {
            throw new IllegalArgumentException("Invalid vertex: " + v);
        }
        v = nextVertexNumber();
        dynamicVertices.add(v);
        labelMap.put(vstr, v);

        return v;
    }

    private int findVertexWithLabel(String strLabel) {
        for (Object label : labelMap.keySet()) {
            if (String.valueOf(label).equals(strLabel)) {
                return labelMap.get(label);
            }
        }
        return -1;
    }

    //for dynamic vertices
    private int nextVertexNumber() {
        return 1 + dynamicVertices.stream().mapToInt(a -> a).max().orElse(-1);
    }

    /**
     *
     * @param path
     * @return
     */
    public GraphBuilder addPath(int... path) {
        for (int v : path) {
            getVertex(v);
        }
        paths.add(path);
        return this;
    }

    /**
     *
     * @param cycle
     * @return
     */
    public GraphBuilder addCycle(int... cycle) {
        for (int v : cycle) {
            getVertex(v);
        }
        cycles.add(cycle);
        return this;
    }

    /**
     *
     * @param clique
     * @return
     */
    public GraphBuilder addClique(int... clique) {
        for (int v : clique) {
            getVertex(v);
        }
        cliques.add(clique);
        return this;
    }

    /**
     *
     * @param name
     * @return
     */
    public GraphBuilder named(String name) {
        this.name = name;
        return this;
    }

    private void validate() {
        if (!dynamicVertices.isEmpty()) {
            vertices = dynamicVertices.stream().mapToInt(v -> v).toArray();
        }
        int numVertices = vertices.length;
        int max = IntStream.of(vertices).max().orElse(0);
        if (maxVertices == null || maxVertices < max) {
            maxVertices = max + 1;
        }
        if (!edges.isEmpty()) {
            if (numEdges == null || numEdges < edges.size()) {
                numEdges = (long) edges.size();
            }
        }
        //number of edges
        /*
        if (avgDegree != null && avgDegree > numVertices - 1) {
            throw new IllegalArgumentException("Invalid average degree, "
                    + "it must be in the range: [0," + (numVertices - 1) + "]");
        }*/
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

    private int avgDegree() {
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

    private GraphImpl build() {
        validate();
        var g = newInstance();
        g.setName(name);

        //weights
        for (int v : weightMap.keySet()) {
            g.setVertexWeight(v, weightMap.get(v));
        }
        //labels
        for (var label : labelMap.keySet()) {
            int v = labelMap.get(label);
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

    /**
     *
     * @return an undirected simple graph
     */
    public Graph buildGraph() {
        return build();
    }

    /**
     *
     * @return a directed graph
     */
    public Digraph buildDigraph() {
        directed = true;
        return (Digraph) build();
    }

    /**
     *
     * @return an undirected multigraph
     */
    public Multigraph buildMultigraph() {
        allowingMultiEdges = true;
        return (Multigraph) build();
    }

    /**
     *
     * @return a directed multigraph
     */
    public DirectedMultigraph buildDirectedMultigraph() {
        directed = true;
        allowingMultiEdges = true;
        return (DirectedMultigraph) build();
    }

    /**
     *
     * @return an undirected pseudograph
     */
    public Pseudograph buildPseudograph() {
        allowingMultiEdges = true;
        allowingSelfLoops = true;
        return (Pseudograph) build();
    }

    /**
     *
     * @return a directed pseudograph
     */
    public DirectedPseudograph buildDirectedPseudograph() {
        directed = true;
        allowingMultiEdges = true;
        allowingSelfLoops = true;
        return (DirectedPseudograph) build();
    }

}
