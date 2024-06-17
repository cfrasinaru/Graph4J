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
package org.graph4j.util;

import org.graph4j.Graph;
import org.graph4j.alg.connectivity.TarjanBiconnectivity;

/**
 * A <em>block</em> of a graph is a maximal 2-connected subgraph (it has no cut
 * vertex).
 *
 * @author Cristian Frăsinaru
 */
public class Block extends VertexSet {

    public Block(Graph graph) {
        super(graph);
    }

    public Block(Graph graph, int initialCapacity) {
        super(graph, initialCapacity);
    }

    public Block(Graph graph, int[] vertices) {
        super(graph, vertices);
    }

    /**
     * @return {@code true} if the vertices represent a block.
     */
    public boolean isValid() {
        return new TarjanBiconnectivity(graph.subgraph(this)).isBiconnected();
    }
}
