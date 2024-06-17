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
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.Network;
import org.graph4j.NetworkBuilder;

/**
 * Generates complete graphs or digraphs. A complete graph contains edges
 * between all pairs of vertices. A complete digraph contains symmetrical arcs
 * (oriented edges) between all pairs of vertices.
 *
 * @author Cristian Frăsinaru
 */
public class CompleteGraphGenerator extends AbstractGraphGenerator {

    public CompleteGraphGenerator(int numVertices) {
        super(numVertices);
    }

    public CompleteGraphGenerator(int firstVertex, int lastVertex) {
        super(firstVertex, lastVertex);
    }

    /**
     *
     * @return a complete graph.
     */
    public Graph createGraph() {
        int n = vertices.length;
        var g = GraphBuilder.vertices(vertices).estimatedAvgDegree(n - 1)
                .named("K" + n).buildGraph();
        addEdges(g, false);
        return g;
    }

    /**
     *
     * @return a complete directed graph.
     */
    public Digraph createDigraph() {
        var g = GraphBuilder.vertices(vertices)
                .estimatedAvgDegree(vertices.length - 1).buildDigraph();
        addEdges(g, true);
        return g;
    }

    /**
     *
     * @return a complete network.
     */
    public Network createNetwork() {
        var g = NetworkBuilder.vertices(vertices)
                .estimatedAvgDegree(vertices.length - 1).buildNetwork();
        addEdges(g, true);
        return g;
    }
    
    private void addEdges(Graph g, boolean directed) {
        g.setSafeMode(false);
        int n = vertices.length;
        if (directed) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        g.addEdge(vertices[i], vertices[j]);
                    }
                }
            }
        } else {
            for (int i = 0; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {
                    g.addEdge(vertices[i], vertices[j]);
                }
            }
        }
        g.setSafeMode(true);
    }

}
