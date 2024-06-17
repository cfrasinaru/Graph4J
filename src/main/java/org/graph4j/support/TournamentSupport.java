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
package org.graph4j.support;

import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.DirectedGraphAlgorithm;
import org.graph4j.generators.TournamentGenerator;
import org.graph4j.util.Path;
import org.graph4j.util.VertexQueue;

/**
 * Support class for tournament graphs. A <em>tournament</em>
 * is a directed graph obtained by assigning a direction for each edge in an
 * undirected complete graph. That is, it is an orientation of a complete graph.
 *
 * Every tournament has a Hamiltonian path.
 *
 * @see TournamentGenerator
 * @author Cristian Frăsinaru
 */
public class TournamentSupport extends DirectedGraphAlgorithm {

    private Boolean tournament;

    public TournamentSupport(Digraph graph) {
        super(graph);
    }

    /**
     * Determines if the graph represents a tournament.
     *
     * @return {@code true} if the digraph is a tournament, {@code false}
     * otherwise.
     */
    public boolean isTournament() {
        if (tournament == null) {
            tournament = checkTournament();
        }
        return tournament;
    }

    private boolean checkTournament() {
        int n = graph.numVertices();
        if (graph.isAllowingMultipleEdges() || graph.isAllowingSelfLoops()) {
            for (int v : graph.vertices()) {
                if (graph.indegree(v) + graph.outdegree(v) != n - 1) {
                    return false;
                }
            }
            return true;
        }
        return graph.numEdges() == Graph.maxEdges(n);
    }

    /**
     * Determines a Hamiltonian path in the tournament.
     *
     * @return a Hamiltonian path in the tournament.
     * @throws IllegalArgumentException if the input graph is not a tournament.
     */
    public Path getHamiltonianPath() {
        if (!isTournament()) {
            throw new IllegalArgumentException("The input digraph is not a tournament.");
        }
        var queue = new VertexQueue(graph, graph.vertices());
        Path path = new Path(graph, graph.numVertices());
        while (!queue.isEmpty()) {
            int v = queue.poll();
            int k = path.numVertices();
            int i = k - 1;
            //looking for an edge u -> v, with u in the path
            while (i >= 0 && !graph.containsEdge(path.get(i), v)) {
                i--;
            }
            if (i < 0) {
                //all edges are from  v to the path
                //v,u0,...,uk-1
                path.insert(0, v);
            } else {
                //found and edge from ui in the path to v
                path.insert(i + 1, v);
            }
        }
        assert path.isValid() && path.isHamiltonian();
        return path;
    }

}
