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

import java.util.Random;
import org.graph4j.Digraph;
import org.graph4j.GraphBuilder;
import org.graph4j.GraphTests;
import org.graph4j.util.Validator;
import org.graph4j.util.IntArrays;

/**
 * Generates a random directed acyclic graph (DAG).
 *
 * Each possible arc is added considering a given probability.
 *
 * @see RandomGnpGraphGenerator
 * @author Cristian Frăsinaru
 */
public class RandomDAGGenerator extends AbstractGraphGenerator {

    private final double edgeProbability;
    private int[] ordering;

    /**
     *
     * @param numVertices number of vertices.
     * @param edgeProbability probability that two vertices are connected.
     */
    public RandomDAGGenerator(int numVertices, double edgeProbability) {
        this(0, numVertices - 1, edgeProbability);
    }

    /**
     *
     * @param firstVertex first vertex number of the graph.
     * @param lastVertex last vertex number of the graph.
     * @param edgeProbability probability that two vertices are connected.
     */
    public RandomDAGGenerator(int firstVertex, int lastVertex, double edgeProbability) {
        super(firstVertex, lastVertex);
        Validator.checkProbability(edgeProbability);
        this.edgeProbability = edgeProbability;
    }

    /**
     *
     * @return a random directed acyclic graph.
     */
    public Digraph createDAG() {
        var g = GraphBuilder.vertices(vertices).estimatedDensity(edgeProbability).buildDigraph();
        ordering = IntArrays.shuffle(vertices);
        g.setSafeMode(false);
        var random = new Random();
        for (int i = 0, n = vertices.length; i < n - 1; i++) {
            int v = g.vertexAt(ordering[i]);
            for (int j = i + 1; j < n; j++) {
                if (random.nextDouble() < edgeProbability) {
                    int u = g.vertexAt(ordering[j]);
                    g.addEdge(v, u);
                }

            }
        }
        g.setSafeMode(true);
        assert GraphTests.isAcyclic(g);
        return g;
    }

    /**
     * The method returns the topological ordering of the vertices, based on
     * which the DAG was created.
     *
     * @return the topological ordering of the generated DAG.
     */
    public int[] getTopologicalOrdering() {
        return ordering;
    }

}
