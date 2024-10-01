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
import org.graph4j.ordering.VertexOrderings;

/**
 * {@inheritDoc}
 *
 * <p>
 * The vertices are colored in decreasing order by their degree. The vertex
 * ordering is computed statically before the algorithm starts.
 *
 * <p>
 * The Largest First Vertex Ordering (LFVO) produces an ordering of the vertices
 * such that the vertices with the largest degree are processed first.
 *
 * @see VertexOrderings
 * @author Cristian Frăsinaru
 */
public class LargestDegreeFirstColoring extends GreedyColoring {

    /**
     * The vertices will be colored in decreasing order by their degree.
     *
     * @param graph the input graph;
     */
    public LargestDegreeFirstColoring(Graph graph) {
        super(graph);
        this.vertexOrdering = VertexOrderings.largestDegreeFirst(graph);
    }

}
