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
 * Multiple (parallel) edges are allowed.
 *
 * @author Cristian Frăsinaru
 * @param <V>
 * @param <E>
 */
public interface Multigraph<V, E> extends Graph<V, E> {

    /**
     * The <i>support graph</i> of a multigraph or a pseudograph G is an
     * undirected graph containing all the vertices of G, self loops are removed
     * and multiple edges are merged into a single one. The resulting graph is
     * unweighted and the labels are all null.
     *
     * @return a new graph, representing the support graph
     */
    Graph<V, E> supportGraph();

    /**
     *
     * @return an identical copy of the pseudograph
     */
    @Override
    Multigraph<V, E> copy();

    /**
     *
     * @param vertices
     * @return the subgraph induced by the given vertices
     */
    @Override
    Multigraph<V, E> subgraph(int... vertices);

    /**
     *
     * @param v a vertex number
     * @param u a vertex number
     * @return how many times u appears in the ajacency list of v
     */
    int multiplicity(int v, int u);

    /**
     *
     * @param e an edge
     * @return how many times an edge appears in the multigraph
     */
    default int multiplicity(Edge e) {
        return multiplicity(e.source(), e.target());
    }

    @Override
    default boolean isUniversal(int v) {
        var set = new HashSet<>();
        for (int u : neighbors(v)) {
            set.add(v);
        }
        return set.size() == numVertices() - 1;
    }

    @Override
    default long maxEdges() {
        return Long.MAX_VALUE;
    }
}
