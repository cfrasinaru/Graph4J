/*
 * Copyright (C) 2024 Cristian Frăsinaru and contributors
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

import org.graph4j.alg.connectivity.BiconnectivityAlgorithm;
import org.graph4j.alg.connectivity.BridgeDetectionAlgorithm;
import org.graph4j.alg.connectivity.ConnectivityAlgorithm;
import org.graph4j.alg.connectivity.StrongConnectivityAlgorithm;
import org.graph4j.alg.connectivity.TarjanBiconnectivity;
import org.graph4j.alg.connectivity.TarjanStrongConnectivity;
import org.graph4j.route.CycleFinder;
import org.graph4j.alg.eulerian.EulerianCircuitAlgorithm;
import org.graph4j.alg.eulerian.HierholzerEulerianCircuit;
import org.graph4j.ordering.TopologicalOrdering;
import org.graph4j.measures.GraphMeasures;
import org.graph4j.measures.TriangleCounter;
import org.graph4j.support.BipartiteGraphSupport;
import org.graph4j.support.ChordalGraphSupport;
import org.graph4j.traversal.DFSTraverser;
import org.graph4j.traversal.DFSVisitor;
import org.graph4j.traversal.SearchNode;
import org.graph4j.util.Validator;
import static org.graph4j.GraphUtils.createSupportGraph;
import org.graph4j.hamiltonian.PalmerHamiltonianCycle;

/**
 *
 * @author Cristian Frăsinaru
 */
public class GraphTests {

    /**
     * Determines if a graph is <em>bipartite</em>, meaning that its vertices
     * can be partitioned in two disjoint stable sets.
     *
     * @see BipartiteGraphSupport
     * @param graph the input graph.
     * @return {@code true} if the graph is bipartite, {@code false} otherwise.
     */
    public static boolean isBipartite(Graph graph) {
        return new BipartiteGraphSupport(graph).isBipartite();
    }

    /**
     * Determines if a graph is <em>connected</em>, meaning that there exists a
     * path between every pair of vertices. If the input graph is directed, the
     * algorithm is performed on its support graph (it tests weak connectivity).
     *
     * @see ConnectivityAlgorithm
     * @param graph the input graph.
     * @return {@code true} if the graph is connected, {@code false} otherwise.
     */
    public static boolean isConnected(Graph graph) {
        return new ConnectivityAlgorithm(graph).isConnected();
    }

    /**
     * Determines if a graph is a <em>tree</em>, that is an undirected,
     * connected and acyclic graph. It uses the property that a tree has
     * {@code n - 1} edges, where {@code n} is the number of vertices in the
     * graph.
     *
     * @param graph the input graph.
     * @return {@code true} if the graph is a tree, {@code false} otherwise.
     * @throws IllegalArgumentException if the graph is directed.
     */
    public static boolean isTree(Graph graph) {
        Validator.requireUndirected(graph);
        if (graph.numEdges() != graph.numVertices() - 1) {
            return false;
        }
        //return isConnected(graph);
        //better, if the graph has a cycle
        var dfs = new DFSTraverser(graph);
        dfs.traverse(new DFSVisitor() {
            @Override
            public void backEdge(SearchNode from, SearchNode to) {
                interrupt();
            }

            @Override
            public void startVertex(SearchNode node) {
                if (node.component() > 0) {
                    interrupt();
                }
            }
        });
        return !dfs.isInterrupted();
    }

    /**
     * Determines if a graph is a <em>forest</em>, an undirected and acyclic
     * graph. A forest can also be defined as a disjoint reunion of trees.
     *
     * @param graph the input graph.
     * @return {@code true} if the graph is a forest, {@code false} otherwise.
     * @throws IllegalArgumentException if the graph is directed.
     */
    public static boolean isForest(Graph graph) {
        Validator.requireUndirected(graph);
        if (graph.numEdges() >= graph.numVertices()) {
            return false;
        }
        int cc = new ConnectivityAlgorithm(graph).countConnectedComponents();
        return graph.numEdges() == graph.numVertices() - cc;
    }

    /**
     * Determines if a directed graph is an <em>arborescence</em> (also called
     * <em>poly-tree</em>), that is a directed rooted tree in which all edges
     * point away from the root. Technically, an arborescence is a directed tree
     * with maximum indegree equal to 1.
     *
     * @param digraph the input directed graph.
     * @return {@code true} if the digraph is an arborescence, {@code false}
     * otherwise.
     */
    public static boolean isArborescence(Digraph digraph) {
        return isTree(digraph.supportGraph()) && GraphMeasures.maxIndegree(digraph) <= 1;
    }

    /**
     * Determines if a directed graph is a <em>branching</em> (also called
     * <em>poly-forest</em>), that is a collection of disjoint arborescences.
     * Technically, a branching is a directed forest with maximum indegree equal
     * to 1.
     *
     * @param digraph the input directed graph.
     * @return {@code true} if the digraph is a branching, {@code false}
     * otherwise.
     */
    public static boolean isBranching(Digraph digraph) {
        return isForest(digraph.supportGraph()) && GraphMeasures.maxIndegree(digraph) <= 1;
    }

    /**
     * Determines if the graph is <em>biconnected (2-connected)</em>. that is it
     * does not contain a cut-vertex.
     *
     * @see BiconnectivityAlgorithm
     * @see TarjanBiconnectivity
     * @param graph the input graph
     * @return {@code true} if the graph is biconnected (2-connected),
     * {@code false} otherwise.
     */
    public static boolean isBiconnected(Graph graph) {
        return BiconnectivityAlgorithm.getInstance(graph).isBiconnected();
    }

    /**
     * Determines if a directed graph is <em>strongly connected</em>, that is
     * for every pair of vertices u and v, there exists both directed paths from
     * u to v and from v to u.
     *
     * @see TarjanStrongConnectivity
     * @param digraph the input directed graph.
     * @return {@code true} if the digraph is strongly connected, {@code false}
     * otherwise.
     */
    public static boolean isStronglyConnected(Digraph digraph) {
        return StrongConnectivityAlgorithm.getInstance(digraph).isStronglyConnected();
    }

    /**
     * Determines if a directed graph is <em>unilateral connected</em>, that is
     * for every pair of vertices u and v, there exists either a directed path
     * from u to v or one from v to u.
     *
     * @see TarjanStrongConnectivity
     * @see TopologicalOrdering
     * @param digraph the input directed graph.
     * @return {@code true} if the digraph is unilateral connected,
     * {@code false} otherwise.
     */
    public static boolean isUnilateralConnected(Digraph digraph) {
        var alg = StrongConnectivityAlgorithm.getInstance(digraph);
        if (alg.isStronglyConnected()) {
            return true;
        }
        var condensation = alg.createCondensation();
        // if it it has a unique topological order, it is unilateral
        return new TopologicalOrdering(condensation).isUnique();
    }

    /**
     * Determines if a directed graph is <em>weakly connected</em>, that is its
     * support graph is connected.
     *
     * @see #isConnected(Graph)
     * @see ConnectivityAlgorithm
     * @param digraph the input directed graph.
     * @return {@code true} if the digraph is weakly connected, {@code false}
     * otherwise.
     */
    public static boolean isWeaklyConnected(Digraph digraph) {
        return isConnected(digraph.supportGraph());
    }

    /**
     * Determines if the graph is <em>bridgeless</em>, that is it does not
     * contain any bridge.
     *
     * @see BridgeDetectionAlgorithm
     * @param graph the input graph
     * @return {@code true} if the graph does not contain any bridge.
     */
    public static boolean isBridgeless(Graph graph) {
        return new BridgeDetectionAlgorithm(graph).isBridgeless();
    }

    /**
     * Determines if the graph is <em>acyclic</em>, meaning it does not contain
     * any cycle.
     *
     * @see CycleFinder
     * @param graph the input graph.
     * @return {@code true} if the graph is acyclic, {@code false} otherwise.
     */
    public static boolean isAcyclic(Graph graph) {
        return !new CycleFinder(graph).containsCycle();
    }

    /**
     * Determines if the graph is <em>regular</em>, that is a graph where each
     * vertex has the same number of neighbors (degree). For a regular directed
     * graph, the indegree and outdegree of each vertex must also be equal.
     *
     * @param graph an input graph.
     * @return {@code true} if all vertices have the same degree, {@code false}
     * otherwise.
     */
    public static boolean isRegular(Graph graph) {
        if (graph.isEmpty()) {
            return true;
        }
        int deg = graph.degree(graph.vertexAt(0));
        return isRegular(graph, deg);
    }

    /**
     * Determines if the graph is <em>k-regular</em>, that is a graph where each
     * vertex has exactly k neighbors. For a k-regular directed graph, the
     * indegree and outdegree of each vertex must both be equal to k.
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
     * Determines if a graph is <em>cubic</em>, meaning that all of its vertices
     * have degree 3. In case of directed graphs, the indegree and outdegree of
     * each vertex must both be equal to 3.
     *
     * @see #isRegular(Graph, int)
     * @param graph the input graph.
     * @return {@code true} if the graph is cubic, {@code false} otherwise.
     */
    public static boolean isCubic(Graph graph) {
        return isRegular(graph, 3);
    }

    /**
     * Determines if a graph is <em>Eulerian</em>, that is it contains a circuit
     * passing through all of its edges.
     *
     * @see EulerianCircuitAlgorithm
     * @see HierholzerEulerianCircuit
     * @param graph the input graph.
     * @return {@code true} if the graph is Eulerian.
     */
    public static boolean isEulerian(Graph graph) {
        return EulerianCircuitAlgorithm.getInstance(graph).isEulerian();
    }

    /**
     * Determines if a graph is triangle-free, that is no three distinct
     * vertices form a triangle.
     *
     * @see GraphMeasures
     * @see TriangleCounter
     * @param graph the input graph.
     * @return {@code true} if the graph is triangle-free.
     */
    public static boolean isTriangleFree(Graph graph) {
        return GraphMeasures.numberOfTriangles(graph) == 0;
    }

    /**
     * Determines if a graph is <em>chordal</em>, meaning it does not contain an
     * induced cycle of four or more vertices.
     *
     * @see ChordalGraphSupport
     * @param graph the input graph.
     * @return {@code true} if the graph is chordal, {@code false} otherwise.
     */
    public static boolean isChordal(Graph graph) {
        return new ChordalGraphSupport(graph).isChordal();
    }

    /**
     * Checks if an undirected graph has Ore's property:
     * <code>deg(v) + deg(u) &gt;= |V|</code>, for every pair of distinct
     * non-adjacent vertices v and u. If a graph has Ore's property, then it is
     * Hamiltonian.
     *
     * @see PalmerHamiltonianCycle
     * @param graph the input graph.
     * @return {@code true} if the graph has Ore's property, false otherwise.
     * @throws IllegalArgumentException if the graph is directed.
     */
    public static boolean hasOreProperty(Graph graph) {
        Validator.requireUndirected(graph);
        graph = createSupportGraph(graph);
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

    /**
     * Checks if the specified vertices represent a clique, meaning that the
     * subgraph induced by them is complete.
     *
     * @param graph the input graph.
     * @param vertices an array of vertices.
     * @return {@code true} if the vertices form a clique, {@code false}
     * otherwise.
     * @throws IllegalArgumentException if the graph is directed.
     * @throws InvalidVertexException if any of the vertices is not in the
     * graph.
     */
    public static boolean isClique(Graph graph, int[] vertices) {
        Validator.requireUndirected(graph);
        for (int i = 0, n = vertices.length; i < n - 1; i++) {
            int v = vertices[i];
            for (int j = i + 1; j < n; j++) {
                int u = vertices[j];
                if (!graph.containsEdge(v, u)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if the specified vertices represent a stable set, meaning that the
     * subgraph induced by them is edgeless.
     *
     * @param graph the input graph.
     * @param vertices an array of vertices.
     * @return {@code true} if the vertices form a stable set, {@code false}
     * otherwise.
     * @throws IllegalArgumentException if the graph is directed.
     * @throws InvalidVertexException if any of the vertices is not in the
     * graph.
     */
    public static boolean isStableSet(Graph graph, int[] vertices) {
        Validator.requireUndirected(graph);
        for (int i = 0, n = vertices.length; i < n - 1; i++) {
            int v = vertices[i];
            for (int j = i + 1; j < n; j++) {
                int u = vertices[j];
                if (graph.containsEdge(v, u)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if a graph is <em>overfull</em>, meaning its size is greater than
     * the product of its maximum degree and half of its order floored:
     * <code>|E| &gt; &Delta;(G) |V|/2</code>, where <code>&Delta;(G)</code> is
     * the maximum degree of the graph.
     *
     * Overfull property play a role in results concerning edge coloring.
     *
     * @param graph the input graph.
     * @return {@code true} if the graph is overfull, {@code false} otherwise.
     */
    public static boolean isOverfull(Graph graph) {
        int n = graph.numVertices();
        long m = graph.numEdges();
        int maxDegree = GraphMeasures.maxDegree(graph);
        return m > maxDegree * (n / 2);
    }

}
