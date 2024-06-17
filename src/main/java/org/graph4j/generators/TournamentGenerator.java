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
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.support.TournamentSupport;

/**
 * Generates a tournament graph. A <em>tournament</em> is a directed graph
 * obtained by assigning a direction for each edge in an undirected complete
 * graph. That is, it is an orientation of a complete graph.
 *
 * @see TournamentSupport
 * @author Cristian Frăsinaru
 */
public class TournamentGenerator extends AbstractGraphGenerator {

    private final Random rand = new Random();

    public TournamentGenerator(int numVertices) {
        super(numVertices);
    }

    public TournamentGenerator(int firstVertex, int lastVertex) {
        super(firstVertex, lastVertex);
    }

    /**
     * Creates a random tournament.
     *
     * @return a random tournament.
     */
    public Digraph createRandom() {
        var g = GraphBuilder.vertices(vertices).buildDigraph();
        addEdges(g, true);
        return g;
    }

    /**
     * Creates an acyclic tournament.
     *
     * @return an acyclic tournament.
     */
    public Digraph createAcyclic() {
        var g = GraphBuilder.vertices(vertices).buildDigraph();
        addEdges(g, false);
        return g;
    }

    private void addEdges(Graph g, boolean random) {
        g.setSafeMode(false);
        int n = vertices.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (!random || rand.nextBoolean()) {
                    g.addEdge(vertices[i], vertices[j]);
                } else {
                    g.addEdge(vertices[j], vertices[i]);
                }
            }
        }
        g.setSafeMode(true);
    }

}
