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
import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.DirectedMultigraph;
import ro.uaic.info.graph.DirectedPseudograph;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.Multigraph;
import ro.uaic.info.graph.Pseudograph;

/**
 * Support class for creating a graph, directed or not, weighted or not, holding
 * data or not.
 *
 * @author Cristian Frăsinaru
 */
public class GraphBuilder {

    private int[] vertices;
    private Integer maxVertices;
    private Integer numEdges;
    private Integer avgDegree;
    private Double density;
    private boolean sorted;
    private boolean directed;
    private boolean allowsSelfLoops;
    private boolean allowsMultiEdges;
    //
    private final List<int[]> edges = new ArrayList<>();
    private final List<int[]> paths = new ArrayList<>();
    private final List<int[]> cycles = new ArrayList<>();
    private final List<int[]> cliques = new ArrayList<>();

    private GraphBuilder() {
    }

    private GraphImpl newInstance() {
        if (allowsMultiEdges) {
            if (allowsSelfLoops) {
                if (directed) {
                    return new DirectedPseudographImpl(vertices, maxVertices, avgDegree(),
                            sorted, directed, allowsMultiEdges, allowsSelfLoops);
                } else {
                    return new PseudographImpl(vertices, maxVertices, avgDegree(),
                            sorted, directed, allowsMultiEdges, allowsSelfLoops);
                }
            } else {
                if (directed) {
                    return new DirectedMultigraphImpl(vertices, maxVertices, avgDegree(),
                            sorted, directed, allowsMultiEdges, allowsSelfLoops);

                } else {
                    return new MultigraphImpl(vertices, maxVertices, avgDegree(),
                            sorted, directed, allowsMultiEdges, allowsSelfLoops);
                }
            }
        }
        if (directed) {
            return new DigraphImpl(vertices, maxVertices, avgDegree(),
                    sorted, directed, allowsMultiEdges, allowsSelfLoops);
        }
        return new GraphImpl(vertices, maxVertices, avgDegree(),
                sorted, directed, allowsMultiEdges, allowsSelfLoops);
    }

    /**
     *
     * @return
     */
    public static GraphBuilder empty() {
        return numVertices(0);
    }

    /**
     * The created graph will contain all the vertices numbered from
     * <code>0</code> to <code>numVertices - 1</code>.
     *
     * @param numVertices the actual number of vertices in the graph
     * @return
     */
    public static GraphBuilder numVertices(int numVertices) {
        if (numVertices < 0) {
            throw new IllegalArgumentException("Number of vertices must be positive");
        }
        var builder = new GraphBuilder();
        builder.vertices = IntStream.range(0, numVertices).toArray();
        return builder;
    }

    /**
     *
     * @param firstVertex inclusive
     * @param lastVertex inclusive
     * @return
     */
    public static GraphBuilder vertexRange(int firstVertex, int lastVertex) {
        var builder = new GraphBuilder();
        builder.vertices = IntStream.rangeClosed(firstVertex, lastVertex).toArray();
        return builder;
    }

    /**
     *
     * @param vertices
     * @return
     */
    public static GraphBuilder vertices(int... vertices) {
        var builder = new GraphBuilder();
        builder.vertices = vertices;
        return builder;
    }

    /**
     * This property can be specified in order to optimize the memory
     * allocation.
     *
     * @param maxVertices the estimated maximum number of vertices
     * @return
     */
    public GraphBuilder maxVertices(int maxVertices) {
        if (maxVertices < 0) {
            throw new IllegalArgumentException("Maximum number of vertices must be positive");
        }
        this.maxVertices = maxVertices;
        return this;
    }

    /**
     * This property can be specified in order to determine the average degree
     * of the vertices or the density of the graph, optimizing memory
     * allocation.
     *
     * It is also used when creating a random graph.
     *
     * @param numEdges the estimated number of edges
     * @return
     */
    public GraphBuilder numEdges(int numEdges) {
        if (numEdges < 0) {
            throw new IllegalArgumentException("Number of edges must be positive");
        }
        this.numEdges = numEdges;
        return this;
    }

    /**
     * This property can be specified in order to optimize memory allocation.
     *
     * It is also used when creating a random graph.
     *
     * @param avgDegree estimated average degree of the vertices
     * @return
     */
    public GraphBuilder avgDegree(int avgDegree) {
        if (avgDegree < 0) {
            throw new IllegalArgumentException("Average degree must be positive");
        }
        this.avgDegree = avgDegree;
        return this;
    }

    /**
     * This property can be specified in order to optimize memory allocation.
     *
     * It is also used when creating a random graph.
     *
     * @param density
     * @return
     */
    public GraphBuilder density(double density) {
        if (density < 0 || density > 1) {
            throw new IllegalArgumentException("Density must be in the range [0,1]");
        }
        this.density = density;
        return this;
    }

    /**
     * Specifies if the graph should maintain its adjacency lists sorted.The
     * graph creation will take longer since each addition in the adjacency list
     * of a vertex v will be performed in <code>O(log(degree(v)))</code> instead
     * of <code>O(1)</code>, but the operation <code>containsEdge(v,u)</code>
     * will be performed faster, going down from <code>O(degree(v))</code> to
     * <code>O(log(degree(v)))</code>.
     *
     *
     * @return
     */
    public GraphBuilder sorted() {
        this.sorted = true;
        return this;
    }

    /**
     *
     * @return
     */
    public GraphBuilder complete() {
        addClique(vertices);
        return this;
    }

    /**
     *
     * @return
     */
    public GraphBuilder path() {
        addPath(vertices);
        return this;
    }

    /**
     *
     * @return
     */
    public GraphBuilder cycle() {
        addCycle(vertices);
        return this;
    }

    /**
     *
     * @param v
     * @param u
     * @return
     */
    public GraphBuilder addEdge(int v, int u) {
        edges.add(new int[]{v, u});
        return this;
    }

    /**
     *
     * @param edges
     * @return
     */
    public GraphBuilder addEdges(int[][] edges) {
        for (int e[] : edges) {
            if (e.length != 2) {
                throw new IllegalArgumentException(
                        "The edges array must have two columns: " + Arrays.toString(e));
            }
            this.edges.add(e);
        }
        return this;
    }

    /**
     * Example: "1-2, 2-3, 3-1"
     *
     * @param edges
     * @return
     */
    public GraphBuilder addEdges(String edges) {
        String[] edgeTokens = edges.split(",");
        for (String edgeToken : edgeTokens) {
            String[] edgeVertices = edgeToken.trim().split("-");
            try {
                int v = Integer.parseInt(edgeVertices[0].trim());
                int u = Integer.parseInt(edgeVertices[1].trim());
                addEdge(v, u);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException(
                        "Incorrect format for edges: " + edgeToken);
            }
        }
        return this;
    }

    /**
     *
     * @param path
     * @return
     */
    public GraphBuilder addPath(int... path) {
        paths.add(path);
        return this;
    }

    /**
     *
     * @param cycle
     * @return
     */
    public GraphBuilder addCycle(int... cycle) {
        cycles.add(cycle);
        return this;
    }

    /**
     *
     * @param clique
     * @return
     */
    public GraphBuilder addClique(int... clique) {
        cliques.add(clique);
        return this;
    }

    private void validate() {
        int numVertices = vertices.length;
        if (maxVertices == null || maxVertices < numVertices) {
            maxVertices = numVertices;
        }
        if (!edges.isEmpty()) {
            if (numEdges == null || numEdges < edges.size()) {
                numEdges = edges.size();
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

    private int maxEdges() {
        int n = vertices.length;
        return n * (n - 1) / (directed ? 1 : 2);
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
        //edges
        for (int[] e : edges) {
            g.addEdge(e[0], e[1]);
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

    <T> T buildAs(Class<T> clazz) {
        if (clazz.equals(Digraph.class)) {
            directed = true;
        } else if (clazz.equals(Multigraph.class)) {
            allowsMultiEdges = true;
        } else if (clazz.equals(DirectedMultigraph.class)) {
            directed = true;
            allowsMultiEdges = true;
        } else if (clazz.equals(Pseudograph.class)) {
            allowsMultiEdges = true;
            allowsSelfLoops = true;
        } else if (clazz.equals(DirectedPseudograph.class)) {
            directed = true;
            allowsMultiEdges = true;
            allowsSelfLoops = true;
        }
        return (T) build();
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
        allowsMultiEdges = true;
        return (Multigraph) build();
    }

    /**
     *
     * @return a directed multigraph
     */
    public DirectedMultigraph buildDirectedMultigraph() {
        directed = true;
        allowsMultiEdges = true;
        return (DirectedMultigraph) build();
    }

    /**
     *
     * @return an undirected pseudograph
     */
    public Pseudograph buildPseudograph() {
        allowsMultiEdges = true;
        allowsSelfLoops = true;
        return (Pseudograph) build();
    }

    /**
     *
     * @return a directed pseudograph
     */
    public DirectedPseudograph buildDirectedPseudograph() {
        directed = true;
        allowsMultiEdges = true;
        allowsSelfLoops = true;
        return (DirectedPseudograph) build();
    }

}
