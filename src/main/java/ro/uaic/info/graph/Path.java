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

import ro.uaic.info.graph.util.CheckArguments;

/**
 * A <i>path</i> is a trail with no duplicate vertices.
 *
 * Vertices can not repeat. Edges can not repeat.
 *
 * The length of a walk is its number of edges.
 *
 * @author Cristian Frăsinaru
 */
public class Path extends Trail {

    /**
     *
     * @param graph
     * @param vertices
     */
    public Path(Graph graph, int... vertices) {
        super(graph, false, vertices);
        CheckArguments.noDuplicates(vertices);
    }

}
