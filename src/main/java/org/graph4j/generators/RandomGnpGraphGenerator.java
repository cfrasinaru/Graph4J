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
package org.graph4j.generators;

import org.graph4j.Digraph;
import org.graph4j.DirectedMultigraph;
import org.graph4j.DirectedPseudograph;
import org.graph4j.Graph;
import org.graph4j.Multigraph;
import org.graph4j.Pseudograph;
import org.graph4j.GraphBuilder;
import org.graph4j.Network;
import org.graph4j.NetworkBuilder;
import org.graph4j.util.Validator;

/**
 * Erdős–Rényi G(n,p) model. Each possible edge is added cosidering a given
 * probability. The time complexity of the algorithm is O(n^2), since it
 * iterates through all possible edges, so it is not efficient for large sparse
 * graphs.
 *
 * @see RandomGnmGraphGenerator
 * @author Cristian Frăsinaru
 */
public class RandomGnpGraphGenerator extends AbstractGraphGenerator {

    private final double edgeProbability;

    /**
     *
     * @param numVertices number of vertices.
     * @param edgeProbability probability that two vertices are connected.
     */
    public RandomGnpGraphGenerator(int numVertices, double edgeProbability) {
        this(0, numVertices - 1, edgeProbability);
    }

    /**
     *
     * @param firstVertex first vertex number of the graph.
     * @param lastVertex last vertex number of the graph.
     * @param edgeProbability probability that two vertices are connected.
     */
    public RandomGnpGraphGenerator(int firstVertex, int lastVertex, double edgeProbability) {
        super(firstVertex, lastVertex);
        Validator.checkProbability(edgeProbability);
        this.edgeProbability = edgeProbability;
    }

    private GraphBuilder builder() {
        return GraphBuilder.vertices(vertices).estimatedDensity(edgeProbability);
    }

    /**
     *
     * @return a random graph.
     */
    public Graph createGraph() {
        var g = builder().buildGraph();
        createEdges(g);
        return g;
    }

    /**
     *
     * @return a random directed graph.
     */
    public Digraph createDigraph() {
        var g = builder().buildDigraph();
        createEdges(g);
        return g;
    }

    /**
     *
     * @return a random network.
     */
    public Network createNetwork() {
        var g = NetworkBuilder.vertices(vertices).estimatedDensity(edgeProbability).buildNetwork();
        createEdges(g);
        return g;
    }
    
    /**
     *
     * @return a random multigraph.
     */
    public Multigraph createMultiGraph() {
        var g = builder().buildMultigraph();
        createEdges(g);
        return g;
    }

    /**
     *
     * @return a random directed multigraph.
     */
    public DirectedMultigraph createDirectedMultigraph() {
        var g = builder().buildDirectedMultigraph();
        createEdges(g);
        return g;
    }

    /**
     *
     * @return a random pseudograph.
     */
    public Pseudograph createPseudograph() {
        var g = builder().buildPseudograph();
        createEdges(g);
        return g;
    }

    /**
     *
     * @return a random directed pseudograph.
     */
    public DirectedPseudograph createDirectedPseudograph() {
        var g = builder().buildDirectedPseudograph();
        createEdges(g);
        return g;
    }

    private void createEdges(Graph g) {
        g.setSafeMode(false);
        addRandomEdges(g, edgeProbability);
        g.setSafeMode(true);
    }

}
