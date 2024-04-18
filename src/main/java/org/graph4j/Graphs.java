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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.graph4j.alg.ordering.AcyclicOrientation;
import org.graph4j.alg.bipartite.BipartitionAlgorithm;
import org.graph4j.alg.connectivity.BiconnectivityAlgorithm;
import org.graph4j.alg.connectivity.BridgeDetectionAlgorithm;
import org.graph4j.alg.cycle.CycleFinder;
import org.graph4j.alg.connectivity.ConnectivityAlgorithm;
import org.graph4j.alg.connectivity.EdgeConnectivityAlgorithm;
import org.graph4j.alg.connectivity.VertexConnectivityAlgorithm;
import org.graph4j.traverse.DFSIterator;
import org.graph4j.util.CheckArguments;

/**
 * This class consists of static methods that operate on or return graphs.
 *
 * @author Cristian Frăsinaru
 */
public class Graphs {

    private Graphs() {
    }

    /**
     * Convenience method for obtaining the support graph of a digraph,
     * multigraph or pseudograph. If the input graph is a simple, undirected
     * graph, it returns the graph itself.
     *
     * @param graph a reference to a digraph, multigraph or pseudograph.
     * @return the support graph.
     */
    public static Graph supportGraph(Graph graph) {
        if (graph instanceof Digraph) {
            return ((Digraph) graph).supportGraph();
        } else if (graph instanceof Multigraph) {
            return ((Multigraph) graph).supportGraph();
        } else {
            return graph;
        }
    }

    /**
     * The <i>disjoint union</i> will have all the vertices and the edges of the
     * given graphs. The vertex sets of the graphs must be pairwise disjoint.
     *
     * @param graphs the graphs to perform disjoint union on.
     * @return a ew graph, representing the disjoint union of the given graphs.
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
     * @param graphs the graphs to perform union on.
     * @return a new graph, representing the union of the given graphs.
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
     * @return a new graph, representing the
     * <a href="https://mathworld.wolfram.com/GraphJoin.html">join</a> of the
     * given graphs.
     *
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
     * Determines if a graph is bipartite (its vertices can be partitioned in
     * two disjoint stable sets).
     *
     * @see BipartitionAlgorithm
     * @param graph the input graph.
     * @return {@code true} if the graph is bipartite.
     */
    public static boolean isBipartite(Graph graph) {
        return BipartitionAlgorithm.getInstance(graph).isBipartite();
    }

    /**
     * Determines if a graph is connected. If the input graph is directed, the
     * algorithm is performed on its support graph (it tests weak connectivity).
     *
     * @see ConnectivityAlgorithm
     * @param graph the input graph.
     * @return {@code true} if the graph is connected.
     */
    public static boolean isConnected(Graph graph) {
        return new ConnectivityAlgorithm(graph).isConnected();
    }

    /**
     * Determines if the given graph is a tree: connected and having
     * {@code numVertices - 1} edges.
     *
     * @param graph the input graph.
     * @return {@code true} if the graph is a tree.
     */
    public static boolean isTree(Graph graph) {
        return (graph.numEdges() == graph.numVertices() - 1)
                && isConnected(graph);
    }

    /**
     * Determines if there is a path connecting two vertices. If the input graph
     * is directed, the path takes into account the edge orientations.
     *
     * @param graph the input graph.
     * @param v a vertex number.
     * @param u a vertex number.
     * @return {@code true} if there is a path connecting v and u.
     */
    public static boolean hasPath(Graph graph, int v, int u) {
        var dfs = new DFSIterator(graph, v);
        while (dfs.hasNext()) {
            var node = dfs.next();
            if (node.component() > 0) {
                break;
            }
            if (node.vertex() == u) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determiens if the graph is biconnected (2-connected).
     *
     * @see BiconnectivityAlgorithm
     * @param graph the input graph
     * @return {@code true} if the graph is biconnected (2-connected).
     */
    public static boolean isBiconnected(Graph graph) {
        return BiconnectivityAlgorithm.getInstance(graph).isBiconnected();
    }

    /**
     * @see BridgeDetectionAlgorithm
     * @param graph the input graph
     * @return {@code true} if the graph does not contain any bridge.
     */
    public static boolean isBridgeless(Graph graph) {
        return new BridgeDetectionAlgorithm(graph).isBridgeless();
    }

    /**
     * @see CycleFinder
     * @param graph the input graph
     * @return {@code true} if the graph contains at least one cycle.
     */
    public static boolean containsCycle(Graph graph) {
        return new CycleFinder(graph).containsCycle();
    }

    /**
     * A <em>regular</em> graph is a graph where each vertex has the same number
     * of neighbors (degree). For a regular directed graph, the indegree and
     * outdegree of each vertex must also be equal.
     *
     * @param graph an input graph.
     * @return {@code true} if all vertices have the same degree.
     */
    public static boolean isRegular(Graph graph) {
        if (graph.isEmpty()) {
            return true;
        }
        int deg = graph.degree(graph.vertexAt(0));
        return isRegular(graph, deg);
    }

    /**
     * A <em>k-regular</em> graph is a graph where each vertex has k neighbors.
     * For a k-regular directed graph, the indegree and outdegree of each vertex
     * must both be equal to k.
     *
     * @param graph the input graph.
     * @param k a degree.
     * @return {@code true} if the graph is k-regular.
     */
    public static boolean isRegular(Graph graph, int k) {
        for (int v : graph.vertices()) {
            if (graph.degree(v) != k) {
                return false;
            }
        }
        if (graph.isDirected()) {
            var digraph = (Digraph) graph;
            for (int v : graph.vertices()) {
                if (digraph.indegree(v) != digraph.outdegree(v)) {
                    return false;
                }
            }
        }
        return true;
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
        transpose.setSafeMode(false);
        for (var it = digraph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            transpose.addEdge(e.flip());
        }
        transpose.setSafeMode(true);
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

    /**
     * Computes the edge connectivity number, that is the minimum size of a set
     * of edges whose removal disconnects the graph. An upper bound of this
     * number is the maximum degree of vertex.
     *
     * @see EdgeConnectivityAlgorithm
     * @param graph the input graph.
     * @return the edge connectivity number.
     */
    public static int edgeConnectivity(Graph graph) {
        return new EdgeConnectivityAlgorithm(graph).getConnectivityNumber();
    }

    /**
     * Computes the vertex connectivity number, that is the minimum size of a
     * set of vertices whose removal disconnects the graph. If the graph is
     * complete, it returns {@code n-1}, where {@code n} is the number of
     * vertices in the graph.
     *
     * @see VertexConnectivityAlgorithm
     * @param graph the input graph.
     * @return the vertex connectivity number.
     */
    public static int vertexConnectivity(Graph graph) {
        return new VertexConnectivityAlgorithm(graph).getConnectivityNumber();
    }

    /**
     * Checks if an undirected graph has Ore's property:
     * <code>deg(v) + deg(u) &gt;= |V(G)|</code>, for every pair of distinct
     * non-adjacent vertices v and u. If a graph has Ore's property, then it is
     * Hamiltonian. If the input graph is a multigraph or pseudograph, it checks
     * the property on its support graph.
     *
     * @param graph the input graph.
     * @return {@code true} if the graph has Ore's property, false otherwise.
     */
    public static boolean hasOreProperty(Graph graph) {
        CheckArguments.graphUndirected(graph);
        graph = supportGraph(graph);
        int n = graph.numVertices();
        if (n < 3) {
            return false;
        }
        int[] vertices = graph.vertices();
        int[] deg = graph.degrees();
        for (int i = 0; i < n - 1; i++) {
            int v = vertices[i];
            for (int j = i + 1; j < n; j++) {
                int u = vertices[j];
                if (deg[i] + deg[j] < n && !graph.containsEdge(v, u)) {
                    return false;
                }
            }
        }
        return true;
    }

}
