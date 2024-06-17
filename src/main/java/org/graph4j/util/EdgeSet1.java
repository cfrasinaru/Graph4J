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
package org.graph4j.util;

import org.graph4j.Graph;
import org.graph4j.Edge;

/**
 * A set of edges in a graph.
 *
 * @author Cristian Frăsinaru
 */
@Deprecated
class EdgeSet1 extends EdgeArray {

    public EdgeSet1(Graph graph) {
        super(graph);
    }

    public EdgeSet1(Graph graph, int initialCapacity) {
        super(graph, initialCapacity);
    }

    public EdgeSet1(Graph graph, int[][] edges) {
        super(graph, edges);
    }

    public EdgeSet1(Graph graph, Edge[] edges) {
        super(graph, edges);
    }

    //WORK IN PROGRESS
}
