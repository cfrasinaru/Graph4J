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
import org.graph4j.util.IntArrays;

/**
 * {@inheritDoc}
 *
 * <p>
 * The order in which vertices are colored is chosen randomly each time the
 * method {@code findColoring} is invoked.
 *
 * @author Cristian Frăsinaru
 */
public class RandomGreedyColoring extends GreedyColoring {

    /**
     * The vertices will be colored in a randomly chosen order.
     *
     * @param graph the input graph;
     */
    public RandomGreedyColoring(Graph graph) {
        super(graph);
    }

    @Override
    protected void init() {
        super.init();
        this.vertexOrdering = IntArrays.shuffle(graph.vertices());
    }

}
