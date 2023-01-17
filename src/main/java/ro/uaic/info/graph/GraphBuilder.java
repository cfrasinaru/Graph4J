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
package ro.uaic.info.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import ro.uaic.info.graph.util.IntArrays;

/**
 * Support class for creating a graph, directed or not, weighted or not, holding
 * data or not.
 *
 * @author Cristian Frăsinaru
 * @param <V> the type of vertex labels in this graph
 * @param <E> the type of edge labels in this graph
 */
public class GraphBuilder<V, E> {

    private int[] vertices;
    private final List<Integer> dynamicVertices = new ArrayList<>();
    private final Map<V, Integer> labelMap = new HashMap<>();
    private final Map<Integer, Double> weightMap = new HashMap<>();
    private Integer maxVertices;
    private Integer maxEdges;
    private Integer avgDegree;
    private Double density;
    private boolean sorted;
    private boolean directed;
    private boolean allowingSelfLoops;
    private boolean allowingMultiEdges;
    private String name;
    //
    private final List<Edge> edges = new ArrayList();
    private final List<int[]> paths = new ArrayList();
    private final List<int[]> cycles = new ArrayList();
    private final List<int[]> cliques = new ArrayList();

    public GraphBuilder() {
        vertices = new int[0];
    }

    private GraphImpl<V, E> newInstance() {
        GraphImpl<V, E> graph;
        if (allowingMultiEdges) {
            if (allowingSelfLoops) {
                if (directed) {
                    graph = new DirectedPseudographImpl<>(vertices, maxVertices, avgDegree(),
                            sorted, directed, allowingMultiEdges, allowingSelfLoops);
                } else {
                    graph = new PseudographImpl<>(vertices, maxVertices, avgDegree(),
                            sorted, directed, allowingMultiEdges, allowingSelfLoops);
                }
            } else {
                if (directed) {
                    graph = new DirectedMultigraphImpl<>(vertices, maxVertices, avgDegree(),
                            sorted, directed, allowingMultiEdges, allowingSelfLoops);

                } else {
                    graph = new MultigraphImpl<>(vertices, maxVertices, avgDegree(),
                            sorted, directed, allowingMultiEdges, allowingSelfLoops);
                }
            }
        } else {
            if (directed) {
                graph = new DigraphImpl<>(vertices, maxVertices, avgDegree(),
                        sorted, directed, allowingMultiEdges, allowingSelfLoops);
            } else {
                graph = new GraphImpl<>(vertices, maxVertices, avgDegree(),
                        sorted, directed, allowingMultiEdges, allowingSelfLoops);
            }
        }
        return graph;
    }

    /**
     * The created graph will contain all the vertices numbered from
     * <code>0</code> to <code>numVertices - 1</code>.
     *
     * @param numVertices the actual number of vertices in the graph.
     * @return a reference to this object.
     */
    public GraphBuilder<V, E> numVertices(int numVertices) {
        if (numVertices < 0) {
            throw new IllegalArgumentException("Number of vertices must not be negative.");
        }
        vertices = IntStream.range(0, numVertices).toArray();
        return this;
    }

    /**
     *
     * @param firstVertex The number of the first vertex.
     * @param lastVertex The number of the last vertex.
     * @return a reference to this object.
     */
    public GraphBuilder<V, E> vertexRange(int firstVertex, int lastVertex) {
        this.vertices = IntStream.rangeClosed(firstVertex, lastVertex).toArray();
        return this;
    }

    /**
     *
     * @param vertices the vertices of the graph.
     * @return a reference to this object.
     */
    public GraphBuilder<V, E> vertices(int... vertices) {
        this.vertices = vertices;
        return this;
    }

    /**
     *
     * @param vertexObjects an array of objects, representing the labeled
     * vertices of the graph.
     * @return a reference to this object.
     */
    public GraphBuilder<V, E> labeledVertices(Collection<V> vertexObjects) {
        int n = vertexObjects.size();
        this.vertices = IntStream.range(0, n).toArray();
        int v = 0;
        for (V label : vertexObjects) {
            labelMap.put(label, v++);
        }
        return this;
    }

    /**
     *
     * @param vertexObjects a list of objects, representing the labeled vertices
     * of the graph.
     * @return a reference to this object.
     */
    public GraphBuilder<V, E> labeledVertices(V... vertexObjects) {
        return labeledVertices(List.of(vertexObjects));
    }

    /**
     *
     * @param graph a graph of any type.
     * @return a reference to this object.
     */
    public GraphBuilder<V, E> verticesFrom(Graph<V, E> graph) {
        this.vertices = IntArrays.copyOf(graph.vertices());
        if (graph.isVertexWeighted()) {
            for (int v : vertices) {
                weightMap.put(v, graph.getVertexWeight(v));
            }
        }
        if (graph.isVertexLabeled()) {
            for (int v : vertices) {
                labelMap.put(graph.getVertexLabel(v), v);
            }
        }
        return this;
    }

    /**
     * This property can be specified in order to optimize the memory
     * allocation. No vertex will be added to the graph as a result of this
     * invocation.
     *
     * @param maxVertices the estimated maximum number of vertices.
     * @return a reference to this object.
     */
    public GraphBuilder<V, E> estimatedMaxVertices(int maxVertices) {
        if (maxVertices <= 0) {
            throw new IllegalArgumentException("Maximum number of vertices must be positive.");
        }
        this.maxVertices = maxVertices;
        return this;
    }

    /**
     * This property can be specified in order to determine the average degree
     * of the vertices or the density of the graph, optimizing memory
     * allocation.
     *
     * @param maxEdges the estimated maximum number of edges.
     * @return a reference to this object.
     */
    public GraphBuilder<V, E> estimatedMaxEdges(int maxEdges) {
        if (maxEdges <= 0) {
            throw new IllegalArgumentException("Number of edges must be positive.");
        }
        this.maxEdges = maxEdges;
        return this;
    }

    /**
     * This property can be specified in order to optimize memory allocation.
     *
     * @param avgDegree the estimated average degree of the vertices.
     * @return a reference to this object.
     */
    public GraphBuilder<V, E> estimatedAvgDegree(int avgDegree) {
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
    public GraphBuilder<V, E> estimatedDensity(double density) {
        if (density < 0 || density > 1) {
            throw new IllegalArgumentException("Density must be in the range [0,1]");
        }
        this.density = density;
        return this;
    }

    /**
     * Specifies if the graph should maintain its vertices and adjacency lists
     * sorted. The graph creation will take longer. There are no algorithmic
     * benefits for keeping the graph sorted.
     *
     *
     * @return a reference to this object.
     */
    public GraphBuilder<V, E> sorted() {
        this.sorted = true;
        return this;
    }

    /**
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return a reference to this object.
     */
    public GraphBuilder<V, E> addEdge(int v, int u) {
        createIfNotExists(v);
        createIfNotExists(u);
        edges.add(new Edge(v, u));
        return this;
    }

    /**
     *
     * @param vLabel a labeled vertex.
     * @param uLabel a labeled vertex.
     * @return a reference to this object.
     */
    public GraphBuilder<V, E> addEdge(V vLabel, V uLabel) {
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
    public GraphBuilder<V, E> addEdge(Edge e) {
        createIfNotExists(e.source());
        createIfNotExists(e.target());
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
    public GraphBuilder<V, E> addEdges(String edges) {
        StringBuilder s;
        String[] edgeTokens = edges.split(",");
        for (String edgeToken : edgeTokens) {
            String[] edgeVertices = edgeToken.trim().split("-");
            try {
                String vstr = edgeVertices[0].trim();
                String ustr = edgeVertices[1].trim();
                int v, u;
                try {
                    v = createIfNotExists(Integer.parseInt(vstr));
                    u = createIfNotExists(Integer.parseInt(ustr));
                } catch (NumberFormatException e) {
                    v = createIfNotExists(vstr);
                    u = createIfNotExists(ustr);
                }
                addEdge(v, u);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException(
                        "Incorrect format for edges: " + edgeToken);
            }
        }
        return this;
    }

    private int createIfNotExists(int v) {
        if (!IntArrays.contains(vertices, v) && !dynamicVertices.contains(v)) {
            dynamicVertices.add(v);
        }
        return v;
    }

    private int createIfNotExists(String vstr) {
        int v = findVertexWithLabel(vstr);
        if (v == -1) {
            v = nextVertexNumber();
            dynamicVertices.add(v);
            labelMap.put((V) vstr, v);
        }
        return v;
    }

    private int findVertexWithLabel(String strLabel) {
        for (V label : labelMap.keySet()) {
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
    public GraphBuilder<V, E> addPath(int... path) {
        for (int v : path) {
            createIfNotExists(v);
        }
        paths.add(path);
        return this;
    }

    /**
     *
     * @param cycle
     * @return
     */
    public GraphBuilder<V, E> addCycle(int... cycle) {
        for (int v : cycle) {
            createIfNotExists(v);
        }
        cycles.add(cycle);
        return this;
    }

    /**
     *
     * @param clique
     * @return
     */
    public GraphBuilder<V, E> addClique(int... clique) {
        for (int v : clique) {
            createIfNotExists(v);
        }
        cliques.add(clique);
        return this;
    }

    /**
     *
     * @param name
     * @return
     */
    public GraphBuilder<V, E> named(String name) {
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
            if (maxEdges == null || maxEdges < edges.size()) {
                maxEdges = edges.size();
            }
        }
        //number of edges
        /*
        int maxEdges = maxEdges();
        if (numEdges != null && numEdges > maxEdges) {
            throw new IllegalArgumentException("The number of edges exceeds the maximum possible: " + maxEdges);
        }*/
        if (avgDegree != null && avgDegree > numVertices - 1) {
            throw new IllegalArgumentException("Invalid average degree, "
                    + "it must be in the range: [0," + (numVertices - 1) + "]");
        }
        if (numVertices > 0 && maxEdges != null && density != null) {
            throw new IllegalArgumentException("Illegal combination of parameters: numEdges and density");
        }
        if (numVertices > 0 && maxEdges != null && avgDegree != null) {
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
        if (maxEdges != null) {
            return (int) ((directed ? 1 : 2) * maxEdges / n);
        }
        if (density != null) {
            return (int) (density * (n - 1));
        }
        return 0;
    }

    private GraphImpl<V, E> build() {
        validate();
        var g = newInstance();
        g.setName(name);

        //weights
        for (int v : weightMap.keySet()) {
            g.setVertexWeight(v, weightMap.get(v));
        }
        //labels
        for (V label : labelMap.keySet()) {
            int v = labelMap.get(label);
            g.setVertexLabel(v, label);
        }
        //edges
        for (Edge e : edges) {
            g.addEdge(e);
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
        return g;
    }

    @Deprecated
    private <T> T buildAs(Class<T> clazz) {
        if (clazz.equals(Digraph.class)) {
            directed = true;
        } else if (clazz.equals(Multigraph.class)) {
            allowingMultiEdges = true;
        } else if (clazz.equals(DirectedMultigraph.class)) {
            directed = true;
            allowingMultiEdges = true;
        } else if (clazz.equals(Pseudograph.class)) {
            allowingMultiEdges = true;
            allowingSelfLoops = true;
        } else if (clazz.equals(DirectedPseudograph.class)) {
            directed = true;
            allowingMultiEdges = true;
            allowingSelfLoops = true;
        }
        return (T) build();
    }

    /**
     *
     * @return an undirected simple graph
     */
    public Graph<V, E> buildGraph() {
        return build();
    }

    /**
     *
     * @return a directed graph
     */
    public Digraph<V, E> buildDigraph() {
        directed = true;
        return (Digraph<V, E>) build();
    }

    /**
     *
     * @return an undirected multigraph
     */
    public Multigraph<V, E> buildMultigraph() {
        allowingMultiEdges = true;
        return (Multigraph<V, E>) build();
    }

    /**
     *
     * @return a directed multigraph
     */
    public DirectedMultigraph<V, E> buildDirectedMultigraph() {
        directed = true;
        allowingMultiEdges = true;
        return (DirectedMultigraph<V, E>) build();
    }

    /**
     *
     * @return an undirected pseudograph
     */
    public Pseudograph<V, E> buildPseudograph() {
        allowingMultiEdges = true;
        allowingSelfLoops = true;
        return (Pseudograph<V, E>) build();
    }

    /**
     *
     * @return a directed pseudograph
     */
    public DirectedPseudograph<V, E> buildDirectedPseudograph() {
        directed = true;
        allowingMultiEdges = true;
        allowingSelfLoops = true;
        return (DirectedPseudograph<V, E>) build();
    }

}
