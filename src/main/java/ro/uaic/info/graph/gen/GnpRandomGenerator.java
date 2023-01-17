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
 * Erdős–Rényi model. Each possible edge is added cosidering a given
 * probability. The time complexity of the algorithm is O(n^2), since it
 * iterates through all possible edges, so it is not efficient for large sparse
 * graphs.
 *
 * @see GnmRandomGenerator
 * @author Cristian Frăsinaru
 */
public class GnpRandomGenerator extends AbstractGenerator {

    private final double edgeProbability;
    private final Random random;

    /**
     *
     * @param numVertices number of vertices
     * @param edgeProbability probability that two vertices are connected
     */
    public GnpRandomGenerator(int numVertices, double edgeProbability) {
        this(0, numVertices - 1, edgeProbability);
    }

    /**
     *
     * @param firstVertex first vertex number of the graph
     * @param lastVertex last vertex number of the graph
     * @param edgeProbability probability that two vertices are connected
     */
    public GnpRandomGenerator(int firstVertex, int lastVertex, double edgeProbability) {
        super(firstVertex, lastVertex);
        CheckArguments.probability(edgeProbability);
        this.edgeProbability = edgeProbability;
        this.random = new Random();
    }

    private GraphBuilder builder() {
        return new GraphBuilder().vertices(vertices).estimatedDensity(edgeProbability);
    }

    /**
     *
     * @return
     */
    public Graph createGraph() {
        var g = builder().buildGraph();
        createEdges(g, false, false);
        return g;
    }

    /**
     *
     * @return
     */
    public Digraph createDigraph() {
        var g = builder().buildDigraph();
        createEdges(g, true, false);
        return g;
    }

    /**
     *
     * @return
     */
    public Multigraph createMultiGraph() {
        var g = builder().buildMultigraph();
        createEdges(g, false, false);
        return g;
    }

    /**
     *
     * @return
     */
    public DirectedMultigraph createDirectedMultigraph() {
        var g = builder().buildDirectedMultigraph();
        createEdges(g, true, false);
        return g;
    }

    /**
     *
     * @return
     */
    public Pseudograph createPseudograph() {
        var g = builder().buildPseudograph();
        createEdges(g, false, true);
        return g;
    }

    /**
     *
     * @return
     */
    public DirectedPseudograph createDirectedPseudograph() {
        var g = builder().buildDirectedPseudograph();
        createEdges(g, true, true);
        return g;
    }

    /**
     *
     * @param g
     * @param directed
     * @param allowsSelfLoops
     */
    private void createEdges(Graph g, boolean directed, boolean allowsSelfLoops) {
        int n = vertices.length;
        int n1 = directed ? n : n - 1;
        for (int i = 0; i < n1; i++) {
            int v = vertices[i];
            int from = directed ? 0 : i + 1;
            for (int j = from; j < n; j++) {
                if (!allowsSelfLoops && i == j) {
                    continue;
                }
                int u = vertices[j];
                if (random.nextDouble() < edgeProbability) {
                    g.addEdge(v, u);
                }
            }
        }
    }

}
