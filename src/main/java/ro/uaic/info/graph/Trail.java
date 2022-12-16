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
package ro.uaic.info.graph;

import java.util.HashSet;

/**
 * A <i>trail</i> is a walk with no repeated edge. A trail is closed if the last
 * vertex equals the first one.
 *
 * Vertices can repeat. Edges can not repeat.
 *
 * The length of a walk is its number of edges.
 *
 * @author Cristian Frăsinaru
 */
public class Trail extends Walk {

    /**
     *
     * @param graph
     * @param vertices
     */
    public Trail(Graph graph, int... vertices) {
        this(graph, true, vertices);
    }

    protected Trail(Graph graph, boolean checkEdges, int... vertices) {
        super(graph, vertices);
        if (checkEdges) {
            var edges = new HashSet<>();
            for (int i = 0; i < size - 1; i++) {
                Edge e = new Edge(vertices[i], vertices[i + 1]);
                if (edges.contains(e)) {
                    throw new IllegalArgumentException(
                            "Vertices do not form a trail, duplicate edge: " + e);
                }
                edges.add(e);
            }
        }
    }

}
