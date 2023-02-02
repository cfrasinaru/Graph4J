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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import ro.uaic.info.graph.alg.AcyclicOrientation;
import ro.uaic.info.graph.alg.cycle.CycleDetectionAlgorithm;
import ro.uaic.info.graph.alg.connectivity.TarjanBiconnectivity;
import ro.uaic.info.graph.alg.connectivity.ConnectivityAlgorithm;
import ro.uaic.info.graph.traverse.DFSTraverser;
import ro.uaic.info.graph.traverse.DFSVisitor;
import ro.uaic.info.graph.traverse.SearchNode;
import ro.uaic.info.graph.util.CheckArguments;

/**
 * This class consists of static methods that operate on or return graphs.
 *
 * @see Graph
 * @author Cristian Frăsinaru
 */
public class Graphs {

    private Graphs() {
    }

    /**
     * The <i>disjoint union</i> will have all the vertices and the edges of the
     * given graphs. The vertex sets of the graphs must be pairwise disjoint.
     *
     * @param graphs the graphs to perform disjoint union on
     * @return a ew graph, representing the disjoint union of the given graphs
     */
    public static Graph disjointUnion(Graph... graphs) {
        int n = graphs.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                CheckArguments.disjointVertices(graphs[i], graphs[j]);
            }
        }
        int numVertices = Stream.of(graphs).mapToInt(g -> g.numVertices()).sum();
        int[] vertices = new int[numVertices];
        int i = 0;
        for (Graph g : graphs) {
            for (int v : g.vertices()) {
                vertices[i++] = v;
            }
        }
        Graph result = GraphBuilder.vertices(vertices).buildGraph();
        for (Graph g : graphs) {
            for (int v : g.vertices()) {
                for (int u : g.neighbors(v)) {
                    if (v < u) {
                        result.addEdge(v, u);
                    }
                }
            }
        }
        return result;
    }

    /**
     * The <i>union</i> will have all the distinct vertices and the edges of the
     * given graphs. Vertices with the same number are contracted. If the graphs
     * have pairwise distinct vertex sets, union is the same as disjoint union.
     *
     * @param graphs the graphs to perform union on
     * @return a new graph, representing the union of the given graphs
     */
    public static Graph union(Graph... graphs) {
        Set<Integer> set = new HashSet<>();
        for (Graph g : graphs) {
            for (int v : g.vertices()) {
                set.add(v);
            }
        }
        int[] vertices = set.stream().mapToInt(v -> v).toArray();
        Graph result = GraphBuilder.vertices(vertices).buildGraph();
        for (Graph g : graphs) {
            for (int v : g.vertices()) {
                for (int u : g.neighbors(v)) {
                    if (v < u && !result.containsEdge(v, u)) {
                        result.addEdge(v, u);
                    }
                }
            }
        }
        return result;
    }

    /**
     * The <i>join</i> operation is the union of two graphs, together with all
     * the edges joining vertices from the first graph to vertices in the second
     * graph. The vertex sets of the graphs must be pairwise disjoint.
     *
     *
     * @param g1 the first graph
     * @param g2 the second graph
     * @return a new graph, representing the join of the given graphs
     * https://mathworld.wolfram.com/GraphJoin.html</a>
     */
    public static Graph join(Graph g1, Graph g2) {
        CheckArguments.disjointVertices(g1, g2);
        Graph result = disjointUnion(g1, g2);
        for (int v : g1.vertices()) {
            for (int u : g2.vertices()) {
                result.addEdge(v, u);
            }
        }
        return result;
    }

    /**
     * @see ConnectivityAlgorithm
     * @param graph the input graph
     * @return {@code true} if the graph is connected
     */
    public static boolean isConnected(Graph graph) {
        return new ConnectivityAlgorithm(graph).isConnected();
    }

    /**
     * @see TarjanBiconnectivity
     * @param graph the input graph
     * @return {@code true} if the graph is biconnected (2-connected)
     */
    public static boolean isBiconnected(Graph graph) {
        return new TarjanBiconnectivity(graph).isBiconnected();
    }

    /**
     * @see CycleDetectionAlgorithm
     * @param graph the input graph
     * @return {@code true} if the graph contains at least one cycle.
     */
    public static boolean containsCycle(Graph graph) {
        return new CycleDetectionAlgorithm(graph).containsCycle();
    }

    /**
     * The corresponding directed graph of an undirected graph G has all the
     * vertices of G and a pair of symmetrical arcs for each edge of G.
     *
     * @param graph the input graph.
     * @return the digraph corresponding to the input graph.
     */
    public static Digraph toDigraph(Graph graph) {
        if (graph == null) {
            return null;
        }
        if (graph instanceof Digraph) {
            return (Digraph) graph.copy();
        }
        var digraph = GraphBuilder.verticesFrom(graph).buildDigraph();
        for (var it = graph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            digraph.addEdge(e);
            digraph.addEdge(e.flip());
        }
        return digraph;
    }

    /**
     * Transpose of a directed graph G is another directed graph on the same set
     * of vertices with all of the edges reversed compared to the orientation of
     * the corresponding edges in G.
     *
     * @param digraph the input digraph.
     * @return the transpose of the input digraph.
     */
    public static Digraph transpose(Digraph digraph) {
        var transpose = GraphBuilder.verticesFrom(digraph).buildDigraph();
        for (var it = digraph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            transpose.addEdge(e.flip());
        }
        return transpose;
    }

    /**
     *
     * @param graph the input graph.
     * @return the line graph of the input graph.
     */
    public static Graph<Edge, Object> lineGraph(Graph graph) {
        if (graph == null) {
            return null;
        }
        Graph<Edge, Object> lineGraph
                = GraphBuilder.labeledVertices(graph.edges()).buildGraph();
        for (int v : graph.vertices()) {
            Edge[] edges = graph.edgesOf(v);
            for (Edge e1 : edges) {
                for (Edge e2 : edges) {
                    if (e1.compareTo(e2) < 0) {
                        lineGraph.addEdge(e1, e2);
                    }
                }
            }
        }
        return lineGraph;
    }

    /**
     *
     * @param digraph the input directed graph.
     * @return the line graph of the input digraph.
     */
    public static Digraph<Edge, Object> lineGraph(Digraph digraph) {
        if (digraph == null) {
            return null;
        }
        Digraph<Edge, Object> lineGraph
                = GraphBuilder.labeledVertices(digraph.edges()).buildDigraph();
        for (int v : digraph.vertices()) {
            for (Edge e1 : digraph.incomingEdgesTo(v)) {
                for (Edge e2 : digraph.outgoingEdgesFrom(v)) {
                    lineGraph.addEdge(e1, e2);
                }
            }
        }
        return lineGraph;
    }

    /**
     * An <i>acyclic orientation</i> of an undirected graph is an assignment of
     * a direction to each edge (an orientation) that does not form any directed
     * cycle and therefore makes it into a directed acyclic graph. Every graph
     * has an acyclic orientation.
     *
     * @see AcyclicOrientation
     * @param graph an undirected graph
     * @return a directed acyclic graph, corresponding to the input graph
     */
    public static Digraph acyclicOrientation(Graph graph) {
        return new AcyclicOrientation(graph).create();
    }

}
