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

import ro.uaic.info.graph.build.GraphBuilder;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import ro.uaic.info.graph.alg.AcyclicOrientation;
import ro.uaic.info.graph.alg.CycleFinder;
import ro.uaic.info.graph.alg.GraphConnectivity;
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
     *
     * @param graph
     * @return
     */
    public static boolean isConnected(Graph graph) {
        return new GraphConnectivity(graph).isConnected();
    }

    /**
     *
     * @param graph
     * @return
     */
    public static boolean isAcyclic(Graph graph) {
        return !new CycleFinder(graph).containsCycle();
    }

    /**
     * The corresponding directed graph of an undirected graph G has all the
     * vertices of G and a pair of symmetrical arcs for each edge of G.
     *
     * @param graph
     * @return the digraph corresponding to this graph
     */
    public static Digraph toDigraph(Graph graph) {
        var digraph = GraphBuilder.vertices(graph.vertices()).buildDigraph();
        for (int v : graph.vertices()) {
            if (graph.isVertexWeighted()) {
                digraph.setVertexWeight(v, graph.getVertexWeight(v));
            }
            for (int u : graph.neighbors(v)) {
                if (v < u) {
                    Edge e = graph.edge(v, u);
                    digraph.addEdge(e);
                    digraph.addEdge(e.flip());
                }
            }
        }
        return digraph;
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
