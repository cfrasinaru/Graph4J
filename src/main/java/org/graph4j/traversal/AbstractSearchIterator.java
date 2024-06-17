/*
 * Copyright (C) 2024 Cristian Frăsinaru and contributors
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
package org.graph4j.traversal;

import java.util.Iterator;
import java.util.Objects;
import org.graph4j.Graph;
import org.graph4j.InvalidVertexException;
import org.graph4j.util.Validator;

/**
 * Base class for search iterator.
 *
 * @see DFSIterator
 * @see BFSIterator
 * @see LexBFSIterator
 * @author Cristian Frăsinaru
 */
@Deprecated
abstract class AbstractSearchIterator implements Iterator<SearchNode> {

    protected final Graph graph;
    protected final int startVertex; //vertex
    protected int numVertices;
    protected int numIterations;
    protected int compIndex;
    protected int orderNumber;
    protected int maxLevel = -1;
    protected SearchNode visited[];

    /**
     * Creates an iterator starting with the first vertex of the graph (the one
     * at index 0)
     *
     * @param graph the input graph.
     */
    public AbstractSearchIterator(Graph graph) {
        Objects.requireNonNull(graph);
        this.graph = graph;
        this.startVertex = graph.isEmpty() ? -1 : graph.vertexAt(0);
    }

    /**
     * Creates an iterator starting with the specified vertex.
     *
     * @param graph the input graph.
     * @param start the start vertex number.
     * @throws InvalidVertexException if the graph does not contain the start
     * vertex.
     */
    public AbstractSearchIterator(Graph graph, int start) {
        Objects.requireNonNull(graph);
        Validator.containsVertex(graph, start);
        this.graph = graph;
        this.startVertex = start;
    }

    /**
     * Returns the number of connected components identified so far by the
     * iterator.
     *
     * @return the number of connected components.
     */
    public int numComponents() {
        return compIndex;
    }

    /**
     * Returns the maximum level in the search tree, identified so far by the
     * iterator. The root of the search tree is considered at level 0.
     *
     * @return the maximum level in the search tree.
     */
    public int maxLevel() {
        return maxLevel;
    }
}
