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

import ro.uaic.info.graph.Digraph;

/**
 *
 * @author Cristian Frăsinaru
 * @param <V>
 * @param <E>
 */
class DigraphImpl<V, E> extends GraphImpl<V, E> implements Digraph<V, E> {

    protected DigraphImpl() {
    }

    protected DigraphImpl(int[] vertices, int maxVertices, int avgDegree,
            boolean sorted, boolean directed, boolean allowsMultiEdges, boolean allowsSelfLoops) {
        super(vertices, maxVertices, avgDegree, sorted, directed, allowsMultiEdges, allowsSelfLoops);
    }

    @Override
    protected GraphImpl newInstance() {
        return new DigraphImpl();
    }

    @Override
    protected GraphImpl newInstance(int[] vertices, int maxVertices, int avgDegree,
            boolean sorted, boolean directed, boolean allowsMultiEdges, boolean allowsSelfLoops) {
        return new DigraphImpl(vertices, maxVertices, avgDegree, sorted, directed, allowsMultiEdges, allowsSelfLoops);
    }

    @Override
    public Digraph<V, E> copy() {
        return (Digraph<V, E>) super.copy();
    }

    @Override
    public Digraph<V, E> subgraph(int... vertices) {
        return (Digraph<V, E>) super.subgraph(vertices);
    }

    @Override
    public Digraph<V, E> complement() {
        return (Digraph<V, E>) super.complement();
    }

}
