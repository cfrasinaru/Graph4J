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

import org.graph4j.Graph;

/**
 * Represents an algorithm that accepts any graph as input, directed or not.
 *
 * For example, DFS or BFS traversal algorithms are implemented exactly the
 * same, regardless the fact that the graph is directed or not, by inspecting
 * the adjacency lists of the vertices provided by the {@link Graph} data type.
 *
 * @author Cristian Frăsinaru
 */
public abstract class GraphAlgorithm {

    protected final Graph graph;
    protected final boolean directed;

    /**
     * Constructs an algorithm which will be executed on the input graph.
     *
     * @param graph the input graph.
     */
    public GraphAlgorithm(Graph graph) {
        this.graph = graph;
        this.directed = graph.isDirected();
    }

    /**
     * Returns the input graph on which the algorithm is executed.
     *
     * @return the input graph.
     */
    public Graph getGraph() {
        return graph;
    }

}
