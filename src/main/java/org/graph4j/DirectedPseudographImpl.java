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

/**
 *
 * @author Cristian Frăsinaru
 */
class DirectedPseudographImpl<V, E> extends PseudographImpl<V, E> implements DirectedPseudograph<V, E> {

    protected DirectedPseudographImpl() {
    }

    protected DirectedPseudographImpl(int[] vertices, int maxVertices, int avgDegree,
            boolean directed, boolean allowingMultipleEdges, boolean allowingSelfLoops) {
        super(vertices, maxVertices, avgDegree, directed, allowingMultipleEdges, allowingSelfLoops);
    }

    @Override
    protected DirectedPseudographImpl newInstance() {
        return new DirectedPseudographImpl();
    }

    @Override
    protected DirectedPseudographImpl newInstance(int[] vertices, int maxVertices, int avgDegree,
            boolean directed, boolean allowingMultipleEdges, boolean allowingSelfLoops) {
        return new DirectedPseudographImpl(vertices, maxVertices, avgDegree, directed, allowingMultipleEdges, allowingSelfLoops);
    }

    @Override
    public DirectedPseudograph<V, E> copy() {
        return (DirectedPseudograph<V, E>) super.copy();
    }
    
    @Override
    public DirectedPseudograph<V, E> copy(boolean vertexWeights, boolean vertexLabels, boolean edges, boolean edgeWeights, boolean edgeLabels) {
        return (DirectedPseudograph<V, E>) super.copy(vertexWeights, vertexLabels, edges, edgeWeights, edgeLabels);
    }

    @Override
    public DirectedPseudograph<V, E> subgraph(int... vertices) {
        return (DirectedPseudograph<V, E>) super.subgraph(vertices);
    }

    @Override
    public DirectedPseudograph<V, E> complement() {
        return (DirectedPseudograph<V, E>) super.complement();
    }

}
