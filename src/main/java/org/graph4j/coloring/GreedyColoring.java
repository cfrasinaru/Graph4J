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
package org.graph4j.coloring;

import org.graph4j.Graph;
import org.graph4j.ordering.InvalidVertexOrdering;
import org.graph4j.util.Validator;

/**
 * {@inheritDoc}
 *
 * <p>
 * The order in which vertices are colored is either according to their indices
 * in the graph, or can be specified in the constructor.
 *
 * @author Cristian Frăsinaru
 */
public class GreedyColoring extends GreedyColoringBase {

    protected int pos = 0;
    protected int[] vertexOrdering;

    /**
     * The vertices will be colored in the order of their graph indices.
     *
     * @param graph the input graph.
     */
    public GreedyColoring(Graph graph) {
        super(graph);
        this.vertexOrdering = graph.vertices();
    }

    /**
     * Creates a greedy algorithm that will color the vertices of the graph in
     * the specified order. Checks if the ordering is valid.
     *
     * @param graph the input graph.
     * @param vertexOrdering an ordering of the graph vertices.
     * @throws InvalidVertexOrdering if the ordering is invalid.
     */
    public GreedyColoring(Graph graph, int[] vertexOrdering) {
        this(graph, vertexOrdering, true);
    }

    /**
     * Creates a greedy algorithm that will color the vertices of the graph in
     * the specified order.Validation of the ordering is optional.
     *
     * @param graph the input graph.
     * @param vertexOrdering an ordering of the graph vertices.
     * @param validateOrdering {@code true} if the order should be validate,
     * {@code false} otherwise.
     * @throws InvalidVertexOrdering if the ordering is invalid.
     */
    public GreedyColoring(Graph graph, int[] vertexOrdering, boolean validateOrdering) {
        super(graph);
        if (validateOrdering) {
            Validator.checkVertexOrdering(graph, vertexOrdering);
        }
        this.vertexOrdering = vertexOrdering;
    }

    @Override
    protected void init() {
        pos = 0;
    }

    @Override
    protected boolean hasUncoloredVertices() {
        return pos < vertexOrdering.length;
    }

    @Override
    protected int nextUncoloredVertex() {
        return vertexOrdering[pos++];
    }

}
