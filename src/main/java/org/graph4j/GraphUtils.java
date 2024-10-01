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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import static org.graph4j.Graph.WEIGHT;
import org.graph4j.ordering.AcyclicOrientation;
import org.graph4j.connectivity.EdgeConnectivityAlgorithm;
import org.graph4j.connectivity.VertexConnectivityAlgorithm;
import org.graph4j.util.IntArrays;
import org.graph4j.util.Validator;
import org.graph4j.util.VertexCollection;
import org.graph4j.util.VertexSet;

/**
 * Static utility methods that operate on or return graphs, either directed or
 * undirected.
 *
 * @author Cristian Frăsinaru
 */
public class GraphUtils {

    /**
     * Convenience method for obtaining the support graph of a digraph,
     * multigraph or pseudograph. If the input graph is a simple, undirected
     * graph, it returns the graph itself.
     *
     * @param graph a reference to a digraph, multigraph or pseudograph.
     * @return the support graph.
     */
    public static Graph createSupportGraph(Graph graph) {
        if (graph instanceof Digraph) {
            return ((Digraph) graph).supportGraph();
        } else if (graph instanceof Multigraph) {
            return ((Multigraph) graph).supportGraph();
        } else {
            return graph;
        }
    }

    /**
     * Performs the disjoint union operation. The <em>disjoint union</em> of the
     * specified graphs will have all their vertices and edges. The vertex sets
     * of the graphs must be pairwise disjoint.
     *
     * @param graphs the graphs to perform disjoint union on.
     * @return a new graph, representing the disjoint union of the graphs.
     */
    public static Graph disjointUnion(Graph... graphs) {
        int n = graphs.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                Validator.haveDisjointVertices(graphs[i], graphs[j]);
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
        result.setSafeMode(false);
        for (Graph g : graphs) {
            for (int v : g.vertices()) {
                for (var it = g.neighborIterator(v); it.hasNext();) {
                    int u = it.next();
                    if (v < u) {
                        result.addEdge(v, u);
                    }
                }
            }
        }
        result.setSafeMode(true);
        return result;
    }

    /**
     * Performs the union of the specified graph. The <em>union</em> will have
     * all the distinct vertices and the edges of the given graphs. Vertices
     * with the same number are contracted. If the graphs have pairwise distinct
     * vertex sets, union is the same as disjoint union.
     *
     * @param graphs the graphs to perform union on.
     * @return a new graph, representing the union of the graphs.
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
        result.setSafeMode(false);
        for (Graph g : graphs) {
            for (int v : g.vertices()) {
                for (var it = g.neighborIterator(v); it.hasNext();) {
                    int u = it.next();
                    if (v < u && !result.containsEdge(v, u)) {
                        result.addEdge(v, u);
                    }
                }
            }
        }
        result.setSafeMode(true);
        return result;
    }

    /**
     * Performs the join operation on the specified graphs. The <em>join</em> is
     * the union of the graphs, together with all the edges joining vertices
     * from each graph to each graph. The vertex sets of the graphs must be
     * pairwise disjoint.
     *
     * @param graphs the first graphs to perform join on.
     * @return a new graph, representing the join of the graphs.
     */
    public static Graph join(Graph... graphs) {
        Graph result = disjointUnion(graphs);
        result.setSafeMode(false);
        for (int i = 0, k = graphs.length; i < k - 1; i++) {
            var g1 = graphs[i];
            for (int j = i + 1; j < k; j++) {
                var g2 = graphs[j];
                for (int v : g1.vertices()) {
                    for (int u : g2.vertices()) {
                        result.addEdge(v, u);
                    }
                }
            }
        }
        result.setSafeMode(true);
        return result;
    }

    /**
     * The corresponding directed graph of an undirected graph G has all the
     * vertices of G and a pair of symmetrical arcs for each edge of G.
     *
     * If the input graph is a {@link Multigraph} it returns a
     * {@link DirectedMultigraph}.
     *
     * If the input graph is a {@link Pseudograph} it returns a
     * {@link DirectedPseudograph}.
     *
     * @param graph the input graph.
     * @return a new digraph corresponding to the input graph.
     */
    public static Digraph toDigraph(Graph graph) {
        if (graph == null) {
            return null;
        }
        if (graph instanceof Digraph) {
            return (Digraph) graph.copy();
        }
        Digraph digraph;
        if (graph instanceof Multigraph) {
            digraph = GraphBuilder.verticesFrom(graph).buildDirectedMultigraph();
        } else if (graph instanceof Pseudograph) {
            digraph = GraphBuilder.verticesFrom(graph).buildDirectedPseudograph();
        } else {
            digraph = GraphBuilder.verticesFrom(graph).buildDigraph();
        }
        digraph.setSafeMode(false);
        for (var it = graph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            digraph.addEdge(e);
            if (!e.isSelfLoop()) {
                digraph.addEdge(e.flip());
            }
        }
        digraph.setSafeMode(true);
        return digraph;
    }

    public static DirectedMultigraph toDirectedMultigraph(Multigraph graph) {
        return (DirectedMultigraph) toDigraph(graph);
    }

    public static DirectedPseudograph toDirectedPseudograph(Pseudograph graph) {
        return (DirectedPseudograph) toDigraph(graph);
    }

    /**
     * Creates a network having the same vertices and edges as the specified
     * graph.
     *
     * @param graph the input graph.
     * @return a new network corresponding to the input digraph.
     */
    public static Network toNetwork(Graph graph) {
        if (graph == null) {
            return null;
        }
        int n = graph.numVertices();
        if (n < 2) {
            throw new IllegalArgumentException("Networks must have at least two vertices.");
        }
        return toNetwork(graph, graph.vertexAt(0), graph.vertexAt(n - 1));
    }

    /**
     * Creates a network having the same vertices and edges as the specified
     * graph. The weights of the graphs become capacities in the resulting
     * network.
     *
     * @param graph the input graph.
     * @param source the source vertex of the network.
     * @param sink the sink vertex of the network.
     * @return a new network corresponding to the input digraph.
     */
    public static Network toNetwork(Graph graph, int source, int sink) {
        if (graph == null) {
            return null;
        }
        if (graph instanceof Network) {
            return (Network) graph.copy();
        }
        Network network = NetworkBuilder.verticesFrom(graph)
                .source(source)
                .sink(sink)
                .buildNetwork();
        for (var it = graph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            network.addLabeledEdge(e.source(), e.target(), e.label(),
                    e.dataOrDefault(WEIGHT, 0));
            if (!graph.isDirected()) {
                network.addEdge(e.flip());
            }
        }
        return network;
    }

    /**
     * Creates the transpose of a directed graph. The <em>transpose</em> of a
     * directed graph G is another directed graph having the same set of
     * vertices with all of the edges reversed compared to the orientation of
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
     * Creates the line graph of an undirected graph.
     *
     * @param graph the input graph.
     * @return the line graph of the input graph.
     */
    public static Graph<Edge, Object> createLineGraph(Graph graph) {
        Validator.requireUndirected(graph);
        if (graph == null) {
            return null;
        }
        Graph<Edge, Object> lineGraph
                = GraphBuilder.labeledVertices(graph.edges()).buildGraph();
        lineGraph.setSafeMode(false);
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
        lineGraph.setSafeMode(true);
        return lineGraph;
    }

    /**
     * Creates the line graph of a directed graph.
     *
     * @param digraph the input digraph.
     * @return the line graph of the input digraph.
     */
    public static Digraph<Edge, Object> createLineGraph(Digraph digraph) {
        if (digraph == null) {
            return null;
        }
        Digraph<Edge, Object> lineGraph
                = GraphBuilder.labeledVertices(digraph.edges()).buildDigraph();
        lineGraph.setSafeMode(false);
        for (int v : digraph.vertices()) {
            for (Edge e1 : digraph.incomingEdgesTo(v)) {
                for (Edge e2 : digraph.outgoingEdgesFrom(v)) {
                    lineGraph.addEdge(e1, e2);
                }
            }
        }
        lineGraph.setSafeMode(true);
        return lineGraph;
    }

    /**
     * Creates the acyclic orientation of an undirected graph. An <em>acyclic
     * orientation</em> of an undirected graph means assigning a direction to
     * each edge (an orientation) that does not form any directed cycle and
     * therefore makes it into a directed acyclic graph (DAG). Every undirected
     * graph has an acyclic orientation.
     *
     * @see AcyclicOrientation
     * @param graph an undirected graph.
     * @return a directed acyclic graph, corresponding to the input graph.
     */
    public static Digraph createAcyclicOrientation(Graph graph) {
        return new AcyclicOrientation(graph).create();
    }

    /**
     * Computes the <em>edge connectivity</em> number, that is the minimum size
     * of a set of edges whose removal disconnects the graph. An upper bound of
     * this number is the maximum degree of vertex.
     *
     * @see EdgeConnectivityAlgorithm
     * @param graph the input graph.
     * @return the edge connectivity number.
     */
    public static int computeEdgeConnectivity(Graph graph) {
        return new EdgeConnectivityAlgorithm(graph).getConnectivityNumber();
    }

    /**
     * Computes the <em>vertex connectivity</em> number, that is the minimum
     * size of a set of vertices whose removal disconnects the graph. If the
     * graph is complete, it returns {@code n-1}, where {@code n} is the number
     * of vertices in the graph.
     *
     * @see VertexConnectivityAlgorithm
     * @param graph the input graph.
     * @return the vertex connectivity number.
     */
    public static int computeVertexConnectivity(Graph graph) {
        return new VertexConnectivityAlgorithm(graph).getConnectivityNumber();
    }

    /**
     * Creates a new graph by rotating the vertex numbers in the input graph by
     * a specified distance, while preserving the edges. The rotated graph is
     * isomorphic with the original one.
     *
     * @param graph the input graph to be rotated.
     * @param distance the distance to rotate with.
     * @return a new graph created by rotating the input graph.
     */
    public static Graph rotate(Graph graph, int distance) {
        var list1 = IntStream.range(0, graph.numVertices()).boxed()
                .collect(Collectors.toList());
        var list2 = new ArrayList<>(list1);
        Collections.rotate(list2, distance);
        return mapVertexNumbers(graph, IntArrays.fromList(list2));
    }

    /**
     * Creates a new graph by shuffling the vertex numbers in the input graph,
     * while preserving the edges. The shuffled graph is isomorphic with the
     * original one.
     *
     * @param graph the input graph to be shuffled.
     * @return a new graph created by shuffling the input graph.
     */
    public static Graph shuffle(Graph graph) {
        var list1 = IntStream.range(0, graph.numVertices()).boxed()
                .collect(Collectors.toList());
        var list2 = new ArrayList<>(list1);
        Collections.shuffle(list2);
        return mapVertexNumbers(graph, IntArrays.fromList(list2));
    }

    /**
     * Creates a new graph by mapping the vertex numbers in the input graph to
     * the specified values, while preserving the edges. The new graph is
     * isomorphic with the original one.
     *
     * @param graph the input graph.
     * @param vertices a mapping between the indices of the graph and their new
     * numbers.
     * @return a new graph created by mapping the vertex numbers.
     */
    public static Graph mapVertexNumbers(Graph graph, int[] vertices) {
        Objects.requireNonNull(graph);
        Objects.requireNonNull(vertices);
        int n = graph.numVertices();
        if (vertices.length != n) {
            throw new IllegalArgumentException("The number of vertices must be: " + n);
        }
        Validator.hasNoDuplicateVertices(vertices);
        boolean directed = graph.isDirected();
        var graph2 = GraphBuilder.vertices(vertices)
                .estimatedNumEdges(graph.numEdges()).buildGraph();
        graph2.setSafeMode(false);
        for (int v : graph.vertices()) {
            int vId2 = vertices[graph.indexOf(v)];
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int uId2 = vertices[graph.indexOf(it.next())];
                if (directed || vId2 < uId2) {
                    graph2.addEdge(graph2.vertexAt(vId2), graph2.vertexAt(uId2));
                }
            }
        }
        graph2.setSafeMode(true);
        return graph2;
    }

    /**
     * Checks if a vertex partition is valid, meaning the subsets are disjoint
     * and cover all vertices in the graph.
     *
     * @param graph the graph whose vertices are partitioned.
     * @param subsets the subsets of the partition.
     * @return {@code true} if the partition is valid, {@code false} otherwise.
     */
    public static boolean isPartitionValid(Graph graph, List<? extends VertexCollection> subsets) {
        for (int v : graph.vertices()) {
            int count = 0;
            for (var set : subsets) {
                if (set.contains(v)) {
                    count++;
                }
            }
            if (count != 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates the intersection graph of the vertex sets of the specified
     * graphs.An <em>intersection graph</em> is a graph where each vertex
     * corresponds to a set, and there is an edge between two vertices if and
     * only if the corresponding sets intersect.
     *
     * @param graphs the input graphs for the operation.
     * @param vertexLabeled if {@code true} each vertex of the intersection
     * graph is labeled with the corresponding input graph.
     * @return the intersection graph of the vertex sets of the specified
     * graphs.
     */
    public static Graph createIntersectionGraph(List<Graph> graphs, boolean vertexLabeled) {
        int n = graphs.size();
        Graph ig = (vertexLabeled ? GraphBuilder.labeledVertices(graphs)
                : GraphBuilder.numVertices(n)).buildGraph();
        ig.setSafeMode(false);
        for (int i = 0; i < n - 1; i++) {
            int[] vertices1 = graphs.get(i).vertices();
            for (int j = i + 1; j < n; j++) {
                int[] vertices2 = graphs.get(j).vertices();
                if (IntArrays.intersects(vertices1, vertices2)) {
                    ig.addEdge(i, j);
                }
            }
        }
        ig.setSafeMode(true);
        return ig;
    }

    /**
     * Determines the set of distinct vertices belonging to a collection of
     * edges.
     *
     * @param graph the input graph.
     * @param edges a collection of edges.
     * @return the vertices in the specified collection of edges.
     */
    public static VertexSet getVertices(Graph graph, Collection<Edge> edges) {
        var vertices = new VertexSet(graph);
        for (Edge e : edges) {
            vertices.add(e.source());
            vertices.add(e.target());
        }
        return vertices;
    }

    /**
     * Computes the sum of the weights of the edges in a specified collection
     *
     * @param graph the input graph.
     * @param edges a collection of edges.
     * @return the sum of the weights of the edges.
     */
    public static double computeWeight(Graph graph, Collection<Edge> edges) {
        double weight = 0.0;
        for (Edge e : edges) {
            weight += e.weight();
        }
        return weight;
    }

}
