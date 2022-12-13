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
package ro.uaic.info.graph.build;

import ro.uaic.info.graph.Multigraph;

/**
 *
 * @author Cristian Frăsinaru
 */
class MultigraphImpl<V, E> extends GraphImpl<V, E> implements Multigraph<V, E> {

    protected MultigraphImpl() {
    }

    protected MultigraphImpl(int[] vertices, int maxVertices, int avgDegree,
            boolean sorted, boolean directed, boolean allowsMultiEdges, boolean allowsSelfLoops) {
        super(vertices, maxVertices, avgDegree, sorted, directed, allowsMultiEdges, allowsSelfLoops);
    }

    @Override
    protected MultigraphImpl newInstance() {
        return new MultigraphImpl();
    }

    @Override
    protected MultigraphImpl newInstance(int[] vertices, int maxVertices, int avgDegree,
            boolean sorted, boolean directed, boolean allowsMultiEdges, boolean allowsSelfLoops) {
        return new MultigraphImpl(vertices, maxVertices, avgDegree, sorted, directed, allowsMultiEdges, allowsSelfLoops);
    }

    @Override
    public void setEdgeLabel(int v, int u, E label) {
        throw new UnsupportedOperationException("Cannot set labels on individual edges in a multigraph or pseudograph.");
    }

    @Override
    public void setEdgeWeight(int v, int u, double weight) {
        throw new UnsupportedOperationException("Cannot set weights on individual edges in a multigraph or pseudograph.");
    }

    @Override
    public Multigraph<V, E> copy() {
        return (Multigraph<V, E>) super.copy();
    }

    @Override
    public Multigraph<V, E> subgraph(int... vertices) {
        return (Multigraph<V, E>) super.subgraph(vertices);
    }
}
