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
package org.graph4j.alg.coloring;

import org.graph4j.Graph;
import org.graph4j.ordering.SmallestDegreeLastOrdering;
import org.graph4j.ordering.VertexOrderings;

/**
 * {@inheritDoc}
 *
 * <p>
 * The smallest-last greedy algorithm computes the vertex ordering from the end
 * to the beginning. At each step, the node of minimum degree in the current
 * graph is selected and then it is removed from the graph.
 *
 * So, the first vertex to be selected is the one with the smallest degree in
 * the graph, and it is stored at the end of the ordering. Suppose that the
 * vertices <code>V'={v<sub>i+1</sub>,..., v<sub>n</sub>}</code> have been
 * already selected, the next vertex to be chosen is v<sub>i</sub> in
 * <code>V-V'</code> such that the degree of v<sub>i</sub> in the subgraph
 * induced by the remaining vertices <code>V-V'</code> is minimal.
 *
 * @see SmallestDegreeLastOrdering
 * @author Cristian Frăsinaru
 */
public class SmallestDegreeLastColoring extends GreedyColoring {

    public SmallestDegreeLastColoring(Graph graph) {
        super(graph);
        this.vertexOrdering = VertexOrderings.smallestDegreeLast(graph);
    }

}
