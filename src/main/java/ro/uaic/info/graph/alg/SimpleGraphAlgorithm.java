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
package ro.uaic.info.graph.alg;

import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.Multigraph;
import ro.uaic.info.graph.util.Tools;

/**
 * Accepts only simple, undirected graphs as input.
 *
 * If a directed graph, multigraph or pseudograph is provided using a reference
 * of type <code>Graph</code>, the input graph of the algorithm will be its
 * support graph.
 *
 * @author Cristian Frăsinaru
 */
public abstract class SimpleGraphAlgorithm {

    protected final Graph graph;

    /**
     *
     * @param graph the input graph
     */
    public SimpleGraphAlgorithm(Graph graph) {
        if (graph instanceof Digraph digraph) {
            System.out.println("Got a digraph");
            this.graph = digraph.supportGraph();
            System.out.println(this.graph);
            Tools.printMatrix(this.graph.adjacencyMatrix());
        } else if (graph instanceof Multigraph multigraph) {
            this.graph = multigraph.supportGraph();
        } else {
            this.graph = graph;
        }
    }

}
