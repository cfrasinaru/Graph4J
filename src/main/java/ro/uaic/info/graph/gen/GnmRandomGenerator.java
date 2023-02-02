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
package ro.uaic.info.graph.gen;

import java.util.Random;
import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.DirectedMultigraph;
import ro.uaic.info.graph.DirectedPseudograph;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.Multigraph;
import ro.uaic.info.graph.Pseudograph;
import ro.uaic.info.graph.GraphBuilder;
import ro.uaic.info.graph.util.CheckArguments;

/**
 *
 *
 * @author Cristian Frăsinaru
 */
public class GnmRandomGenerator extends AbstractGenerator {

    private final int numEdges;
    private Random random;
    private int[] edgeValues;
    private static final int MAX_EDGES = 1024 * 1024 * 1024;

    /**
     *
     * @param numVertices number of vertices
     * @param numEdges number of edges
     */
    public GnmRandomGenerator(int numVertices, int numEdges) {
        this(0, numVertices - 1, numEdges);
    }

    /**
     *
     * @param firstVertex
     * @param lastVertex
     * @param numEdges
     */
    public GnmRandomGenerator(int firstVertex, int lastVertex, int numEdges) {
        super(firstVertex, lastVertex);
        CheckArguments.numberOfEdges(numEdges);
        this.numEdges = numEdges;
        this.random = new Random();
    }

    /**
     *
     * @return
     */
    public Graph createGraph() {
        var g = GraphBuilder.vertices(vertices).estimatedNumEdges(numEdges).buildGraph();
        createEdges(g, false);
        return g;
    }

    /**
     *
     * @return a randomly generated connected graph
     */
    public Graph createConnectedGraph() {
        int n = vertices.length;
        if (numEdges < n - 1) {
            throw new IllegalArgumentException(
                    "Too few edges to create a connected graph: " + numEdges);
        }
        var g = new RandomTreeGenerator(vertices[0], vertices[n - 1]).create();
        createEdges(g, false);
        return g;
    }

    /**
     *
     * @return a randomly generated acyclic graph (a forest)
     */
    public Graph createAcyclicGraph() {
        int n = vertices.length;
        if (numEdges > n - 1) {
            throw new IllegalArgumentException(
                    "Too many edges to create an acyclic graph: " + numEdges);
        }
        throw new UnsupportedOperationException();//TODO
    }

    /**
     *
     * @return
     */
    public Digraph createDigraph() {
        var g = GraphBuilder.vertices(vertices).estimatedNumEdges(numEdges).buildDigraph();
        createEdges(g, true);
        return g;
    }

    /**
     *
     * @return
     */
    public Multigraph createMultiGraph() {
        var g = GraphBuilder.vertices(vertices).estimatedNumEdges(numEdges).buildMultigraph();
        createEdgesProbabilistic(g, true, false);
        return g;
    }

    /**
     *
     * @return
     */
    public DirectedMultigraph createDirectedMultigraph() {
        var g = GraphBuilder.vertices(vertices).estimatedNumEdges(numEdges).buildDirectedMultigraph();
        createEdgesProbabilistic(g, true, false);
        return g;
    }

    /**
     *
     * @return
     */
    public Pseudograph createPseudograph() {
        var g = GraphBuilder.vertices(vertices).estimatedNumEdges(numEdges).buildPseudograph();
        createEdgesProbabilistic(g, true, true);
        return g;
    }

    /**
     *
     * @return
     */
    public DirectedPseudograph createDirectedPseudograph() {
        var g = GraphBuilder.vertices(vertices).estimatedNumEdges(numEdges).buildDirectedPseudograph();
        createEdgesProbabilistic(g, true, true);
        return g;
    }

    //chooses either model depending on graph sparsity
    private void createEdges(Graph g, boolean directed) {
        int n = vertices.length;
        if (numEdges <= Math.log(n) * n || g.maxEdges() > MAX_EDGES) {
            createEdgesProbabilistic(g, false, false);
        } else {
            createEdgesFisherYates(g, directed);
        }
    }

    private void checkMaxEdges(Graph g) {
        if (numEdges > g.maxEdges()) {
            throw new IllegalArgumentException(
                    "The number of edges is greater than the maximum possible: "
                    + numEdges + " > " + g.maxEdges());
        }
    }

    //suitable for smaller graphs
    private void createEdgesFisherYates(Graph g, boolean directed) {
        checkMaxEdges(g);
        int n = vertices.length;
        long max = g.maxEdges();
        if (max >= Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "The number of vertices is too large: " + n);
        }
        this.edgeValues = new int[(int) max];
        int size = 0;
        int n1 = directed ? n : n - 1;
        for (int i = 0; i < n1; i++) {
            int v = vertices[i];
            int from = directed ? 0 : i + 1;
            for (int j = from; j < n; j++) {
                if (i == j) {
                    continue;
                }
                int u = vertices[j];
                //g may already have some edges
                if (!g.containsEdge(v, u)) {
                    edgeValues[size++] = i * n + j;
                }
            }
        }
        int e = 0;
        while (g.numEdges() < numEdges) {
            int pos = random.nextInt(size - e);
            int v = vertices[edgeValues[pos] / n];
            int u = vertices[edgeValues[pos] % n];
            g.addEdge(v, u);
            edgeValues[pos] = edgeValues[size - 1 - e];
            e++;
        }
    }

    //suitable for large sparse graphs
    private void createEdgesProbabilistic(Graph g, boolean allowsMultipleEdges, boolean allowsSelfLoops) {
        checkMaxEdges(g);
        int n = vertices.length;
        while (g.numEdges() < numEdges) {
            int v = random.nextInt(n);
            int u = random.nextInt(n);
            if (!allowsSelfLoops && v == u) {
                continue;
            }
            if (!allowsMultipleEdges && g.containsEdge(v, u)) {
                continue;
            }
            g.addEdge(v, u);
        }
    }
}
