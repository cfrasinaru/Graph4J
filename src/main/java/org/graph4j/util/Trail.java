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

import java.util.HashMap;
import org.graph4j.Edge;
import org.graph4j.Graph;
import org.graph4j.Multigraph;

/**
 * A <i>trail</i> is a walk with no repeated edge. Vertices can repeat. Edges
 * can not repeat.
 *
 * In order to ensure these properties are respected, call {@code validate}.
 *
 * A trail is closed if the last vertex equals the first one.
 *
 * The length of a walk is its number of edges.
 *
 * @see Walk
 * @see Circuit
 * @see Path
 * @see Cycle
 * @author Cristian Frăsinaru
 */
public class Trail extends Walk {

    public Trail(Graph graph) {
        super(graph);
    }

    public Trail(Graph graph, int initialCapacity) {
        super(graph, initialCapacity);
    }

    public Trail(Graph graph, int[] vertices) {
        super(graph, vertices);
    }

    @Override
    public boolean isValid() {
        if (!super.isValid()) {
            return false;
        }
        try {
            checkDuplicateEdges();
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    protected final void checkDuplicateEdges() {
        var edges = new HashMap<Edge, Integer>();
        for (int i = 0; i < numVertices - 1; i++) {
            Edge e = new Edge(vertices[i], vertices[i + 1], isDirected());
            int max = 1;
            if (graph.isAllowingMultipleEdges()) {
                max = ((Multigraph) graph).multiplicity(e);
            }
            if (edges.getOrDefault(e, 0) >= max) {
                throw new IllegalArgumentException(
                        "Vertices do not form a trail, duplicate edge: " + e);
            }
            edges.put(e, edges.getOrDefault(e, 0) + 1);
        }
    }

}
