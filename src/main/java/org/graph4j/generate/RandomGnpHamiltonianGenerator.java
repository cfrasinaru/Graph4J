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
package org.graph4j.generate;

import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.util.CheckArguments;
import org.graph4j.util.Cycle;
import org.graph4j.util.IntArrays;

/**
 * Creates a random Hamiltonian graph or digraph.
 *
 * Each possible edge, except for the ones in the initial cycle, is added
 * cosidering a given probability. If the edge probability is 0, the algorithm
 * returns a cycle graph.
 *
 * @see RandomGnpGraphGenerator
 * @author Cristian Frăsinaru
 */
public class RandomGnpHamiltonianGenerator extends AbstractGraphGenerator {

    private final double edgeProbability;
    private Cycle cycle;

    /**
     *
     * @param numVertices number of vertices.
     * @param edgeProbability probability that two vertices are connected.
     */
    public RandomGnpHamiltonianGenerator(int numVertices, double edgeProbability) {
        this(0, numVertices - 1, edgeProbability);
    }

    /**
     *
     * @param firstVertex first vertex number of the graph.
     * @param lastVertex last vertex number of the graph.
     * @param edgeProbability probability that two vertices are connected.
     */
    public RandomGnpHamiltonianGenerator(int firstVertex, int lastVertex, double edgeProbability) {
        super(firstVertex, lastVertex);
        CheckArguments.probability(edgeProbability);
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

    private void createEdges(Graph g) {
        g.setSafeMode(false);
        //create a Hamiltonian cycle
        int[] temp = IntArrays.shuffle(vertices);
        for (int i = 0, n = vertices.length; i < n; i++) {
            g.addEdge(temp[i], temp[(i + 1) % n]);
        }
        this.cycle = new Cycle(g, temp);
        addRandomEdges(g, edgeProbability);
        g.setSafeMode(true);
    }

    /**
     * The method returns the Hamiltonian cycle that was created as part of the
     * generation process. Returns {@code null} if the graph was not created.
     *
     * @return a Hamiltonian cycle of the generated graph.
     */
    public Cycle getHamiltonianCycle() {
        return cycle;
    }

}
