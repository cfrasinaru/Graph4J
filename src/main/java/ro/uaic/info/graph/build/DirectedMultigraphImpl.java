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

import ro.uaic.info.graph.DirectedMultigraph;

/**
 *
 * @author Cristian Frăsinaru
 */
class DirectedMultigraphImpl<V, E> extends MultigraphImpl<V, E> implements DirectedMultigraph<V, E> {

    protected DirectedMultigraphImpl() {
    }

    protected DirectedMultigraphImpl(int[] vertices, int maxVertices, int avgDegree,
            boolean sorted, boolean directed, boolean allowsMultiEdges, boolean allowsSelfLoops) {
        super(vertices, maxVertices, avgDegree, sorted, directed, allowsMultiEdges, allowsSelfLoops);
    }

    @Override
    protected DirectedMultigraphImpl newInstance() {
        return new DirectedMultigraphImpl();
    }

    @Override
    protected DirectedMultigraphImpl newInstance(int[] vertices, int maxVertices, int avgDegree,
            boolean sorted, boolean directed, boolean allowsMultiEdges, boolean allowsSelfLoops) {
        return new DirectedMultigraphImpl(vertices, maxVertices, avgDegree, sorted, directed, allowsMultiEdges, allowsSelfLoops);
    }

    @Override
    public DirectedMultigraph<V, E> copy() {
        return (DirectedMultigraph<V, E>) super.copy();
    }

    @Override
    public DirectedMultigraph<V, E> subgraph(int... vertices) {
        return (DirectedMultigraph<V, E>) super.subgraph(vertices);
    }

    @Override
    public DirectedMultigraph<V, E> complement() {
        return (DirectedMultigraph<V, E>) super.complement();
    }

}
