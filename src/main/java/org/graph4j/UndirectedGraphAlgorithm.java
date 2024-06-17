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
package org.graph4j;

import org.graph4j.Graph;
import org.graph4j.util.Validator;

/**
 * Abstract class for algorithms that accept only undirected graphs as input.
 *
 * @author Cristian Frăsinaru
 */
public abstract class UndirectedGraphAlgorithm {

    protected final Graph graph;

    /**
     * Creates a new instance of the algorithm.
     *
     * @param graph the input undirected graph
     * @throws NullPointerException if the graph is null.
     * @throws IllegalArgumentException if the graph is directed.
     */
    public UndirectedGraphAlgorithm(Graph graph) {
        Validator.requireUndirected(graph);
        this.graph = graph;
    }

}
