/*
 * Copyright (C) 2023 Cristian Frăsinaru and contributors
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

//https://en.wikipedia.org/wiki/Watts%E2%80%93Strogatz_model
import java.util.Random;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.util.CheckArguments;

/**
 * The Watts–Strogatz model produces graphs with small-world properties,
 * including short average path lengths and high clustering.
 *
 * Consider the desired number of vertices n and their mean degree k. Construct
 * a regular ring lattice, that is a graph with n vertices each connected to k
 * neighbors, k/2 on each side.
 *
 * For every vertex v, take every edge (v,u) connecting v to its k/2 rightmost
 * neighbors, and rewire it with a given probability. Rewiring is done by
 * replacing the edge (v,u) with (v,w) where w is chosen uniformly at random
 * from all possible vertices, avoiding self-loops and duplicate edges.
 *
 * It is also possible to create supplementary new edges, instead or in addition
 * to rewiring.
 *
 * <p>
 * Bibliography:
 *
 * D. J. Watts and S. H. Strogatz. Collective dynamics of small-world networks.
 * Nature 393(6684):440--442, 1998.
 *
 * M. E. J. Newman and D. J. Watts, Renormalization group analysis of the
 * small-world network model, Physics Letters A, 263, 341, 1999.
 *
 * <p>
 *
 * @author Cristian Frăsinaru
 */
public class WattsStrogatzGenerator extends AbstractGraphGenerator {

    private final int averageDegree;
    private final double rewireProbability;
    private final double addProbability;

    /**
     *
     * @param numVertices the number of vertices.
     * @param averageDegree the average degree of the vertices (the number of
     * k-nearest neighbors).
     * @param rewireProbability the probability to rewire an edge.
     * @param addProbability the probability to add a new edge (use 0 for the
     * original model).
     */
    public WattsStrogatzGenerator(int numVertices, int averageDegree,
            double rewireProbability, double addProbability) {
        this(0, numVertices - 1, averageDegree, rewireProbability, addProbability);
    }

    /**
     *
     * @param firstVertex the number of the first vertex in the generated graph.
     * @param lastVertex the number of the last vertex in the generated graph.
     * @param degree the average degree of the vertices (the number of k-nearest
     * neighbors).
     * @param rewireProbability the probability to rewire an edge.
     * @param addProbability the probability to add a new edge (use 0 for the
     * original model).
     */
    public WattsStrogatzGenerator(int firstVertex, int lastVertex, int degree,
            double rewireProbability, double addProbability) {
        super(firstVertex, lastVertex);
        int n = vertices.length;
        if (degree < 0) {
            throw new IllegalArgumentException("The degree must be non-negative.");
        }
        if (degree >= n) {
            throw new IllegalArgumentException("The degree must be less than: " + n);
        }
        CheckArguments.probability(rewireProbability);
        CheckArguments.probability(addProbability);
        this.averageDegree = degree;
        this.rewireProbability = rewireProbability;
        this.addProbability = addProbability;
    }

    /**
     *
     * @return a random Watts-Strogatz network.
     */
    public Graph createGraph() {
        var g = GraphBuilder.vertices(vertices).estimatedAvgDegree(averageDegree)
                .buildGraph();
        addEdges(g);
        return g;
    }

    private void addEdges(Graph g) {
        boolean safeMode = g.isSafeMode();
        g.setSafeMode(false);
        int n = vertices.length;
        int k = averageDegree;
        var rand = new Random();

        //create the ring
        //if k is even, this is a k-regular graph
        for (int i = 0; i < n; i++) {
            int v = vertices[i];
            for (int j = 0; j < k; j++) {
                int u = vertices[(i + j + 1) % n];
                g.addEdge(v, u);
            }
        }

        // rewire edges
        for (int i = 0; i < n; i++) {
            int v = vertices[i];
            for (int j = 0; j < k / 2; j++) {
                int u = vertices[(i + j + 1) % n];
                if (rand.nextDouble() < rewireProbability) {
                    int w = rand.nextInt(n);
                    if (g.addEdge(v, w) > 0) {
                        g.removeEdge(v, u);
                    }
                }
                if (rand.nextDouble() < addProbability) {
                    int w = rand.nextInt(n);
                    g.addEdge(v, w);
                }

            }
        }
        g.setSafeMode(safeMode);
    }
}
