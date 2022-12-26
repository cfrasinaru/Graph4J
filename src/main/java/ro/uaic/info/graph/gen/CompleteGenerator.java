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

import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.build.GraphBuilder;

/**
 * Generates complete graphs or digraphs. A complete graph contains edges
 * between all pairs of vertices. A complete digraph contains symmetrical arcs
 * (oriented edges) between all pairs of vertices.
 *
 * @author Cristian Frăsinaru
 */
public class CompleteGenerator extends AbstractGenerator {

    public CompleteGenerator(int numVertices) {
        super(numVertices);
    }

    public CompleteGenerator(int firstVertex, int lastVertex) {
        super(firstVertex, lastVertex);
    }

    /**
     *
     * @return a complete graph
     */
    public Graph createGraph() {
        int n = vertices.length;
        var g = GraphBuilder.vertices(vertices).avgDegree(n - 1)
                .named("K" + n).buildGraph();
        addEdges(g, false);
        return g;
    }

    /**
     *
     * @return a complete digraph
     */
    public Digraph createDigraph() {
        var g = GraphBuilder.vertices(vertices)
                .avgDegree(vertices.length - 1).buildDigraph();
        addEdges(g, true);
        return g;
    }

    private void addEdges(Graph g, boolean directed) {
        int n = vertices.length;
        int n1 = directed ? n : n - 1;
        for (int i = 0; i < n1; i++) {
            int from = directed ? 0 : i + 1;
            for (int j = from; j < n; j++) {
                if (i == j) {
                    continue;
                }
                int v = vertices[i];
                int u = vertices[j];
                g.addEdge(v, u);
            }
        }
    }

}
