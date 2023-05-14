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
package org.graph4j.alg;

import org.graph4j.Digraph;
import org.graph4j.DirectedMultigraph;
import org.graph4j.DirectedPseudograph;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.util.CheckArguments;

/**
 * Accepts only undirected graphs as input.
 *
 * If a directed graph, multigraph or pseudograph is provided using a reference
 * of type <code>Graph</code>, the input graph of the algorithm will be obtained
 * by removing the orientation of the edges.
 *
 * @author Cristian Frăsinaru
 */
public abstract class UndirectedGraphAlgorithm {
    
    protected final Graph graph;

    /**
     *
     * @param graph the input graph
     */
    public UndirectedGraphAlgorithm(Graph graph) {
        CheckArguments.graphNotEmpty(graph);
        int n = graph.numVertices();
        long m = graph.numEdges();
        if (graph instanceof DirectedPseudograph) {
            this.graph = GraphBuilder.numVertices(n).estimatedNumEdges(m).buildPseudograph();
        } else if (graph instanceof DirectedMultigraph) {
            this.graph = GraphBuilder.numVertices(n).estimatedNumEdges(m).buildMultigraph();
        } else if (graph instanceof Digraph) {
            this.graph = GraphBuilder.numVertices(n).estimatedNumEdges(m).buildGraph();
        } else {
            this.graph = graph;
        }
        if (this.graph != graph) {
            boolean safeMode = graph.isSafeMode();
            graph.setSafeMode(false);
            for (int v : graph.vertices()) {
                for (var it = graph.neighborIterator(v); it.hasNext();) {
                    this.graph.addEdge(v, it.next());
                }
            }
            graph.setSafeMode(safeMode);
        }
    }
    
}
