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
package ro.uaic.info.graph.model;

import java.util.HashSet;
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.Graph;

/**
 * * A set of edges of a graph. No duplicates are allowed.
 *
 * @author Cristian Frăsinaru
 */
public class EdgeSet1 extends HashSet<Edge> {

    private final Graph graph;

    public EdgeSet1(Graph graph) {
        this.graph = graph;
    }

    @Override
    public boolean add(Edge e) {
        //CheckArguments.graphContainsEdge(graph, e);
        return super.add(e);
    }
    

}
