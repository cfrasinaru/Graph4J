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

/**
 *
 * @author Cristian Frăsinaru
 */
class PseudographImpl<V, E> extends MultigraphImpl<V, E> implements Pseudograph<V, E> {

    protected PseudographImpl() {
    }

    protected PseudographImpl(int[] vertices, int maxVertices, int avgDegree,
            boolean sorted, boolean directed, boolean allowingMultipleEdges, boolean allowingSelfLoops) {
        super(vertices, maxVertices, avgDegree, sorted, directed, allowingMultipleEdges, allowingSelfLoops);
    }

    @Override
    protected PseudographImpl newInstance() {
        return new PseudographImpl();
    }

    @Override
    protected PseudographImpl newInstance(int[] vertices, int maxVertices, int avgDegree,
            boolean sorted, boolean directed, boolean allowingMultipleEdges, boolean allowingSelfLoops) {
        return new PseudographImpl(vertices, maxVertices, avgDegree, sorted, directed, allowingMultipleEdges, allowingSelfLoops);
    }

    @Override
    public int degree(int v) {
        return super.degree(v) + selfLoops(v);
    }

    @Override
    public int selfLoops(int v) {
        return selfLoops.getOrDefault(v, 0);
    }

    @Override
    public Pseudograph<V, E> copy() {
        return (PseudographImpl) super.copy();
    }

    @Override
    public Pseudograph<V, E> copy(boolean vertexWeights, boolean vertexLabels, boolean edges, boolean edgeWeights, boolean edgeLabels) {
        return (Pseudograph<V, E>) super.copy(vertexWeights, vertexLabels, edges, edgeWeights, edgeLabels);
    }

    @Override
    public Pseudograph<V, E> subgraph(int... vertices) {
        return (PseudographImpl) super.subgraph(vertices);
    }

}
