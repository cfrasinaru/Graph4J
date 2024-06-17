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
package org.graph4j;

import java.util.Collection;
import java.util.HashSet;
import org.graph4j.util.VertexSet;

/**
 * Multiple (parallel) edges are allowed.
 *
 * @author Cristian Frăsinaru
 * @param <V> the type of vertex labels in this graph.
 * @param <E> the type of edge labels in this graph.
 */
public interface Multigraph<V, E> extends Graph<V, E> {

    /**
     * The <i>support graph</i> of a multigraph or a pseudograph G is an
     * undirected graph containing all the vertices of G, self loops are removed
     * and multiple edges are merged into a single one. The resulting graph is
     * unweighted and the labels are all null.
     *
     * @return a new graph, representing the support graph.
     */
    Graph<V, E> supportGraph();

    /**
     *
     * @return an identical copy of the multigraph.
     */
    @Override
    Multigraph<V, E> copy();

    @Override
    Multigraph<V, E> subgraph(VertexSet vertexSet);

    @Override
    Multigraph<V, E> subgraph(Collection<Edge> edges);

    /**
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return how many times u appears in the adjacency list of v.
     */
    int multiplicity(int v, int u);

    /**
     *
     * @param e an edge.
     * @return how many times an edge appears in the multigraph.
     */
    default int multiplicity(Edge e) {
        return multiplicity(e.source(), e.target());
    }

    @Override
    default boolean isUniversal(int v) {
        var set = new HashSet<>();
        for (var it = neighborIterator(v); it.hasNext();) {
            set.add(it.next());
        }
        return set.size() == numVertices() - 1;
    }

    @Override
    default long maxEdges() {
        return Long.MAX_VALUE;
    }
}
