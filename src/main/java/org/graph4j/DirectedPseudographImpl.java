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
import java.util.HashMap;
import java.util.Map;
import org.graph4j.util.VertexSet;

/**
 *
 * @author Cristian Frăsinaru
 */
class DirectedPseudographImpl<V, E> extends DirectedMultigraphImpl<V, E> implements DirectedPseudograph<V, E> {

    protected Map<Integer, Integer> selfLoops;

    protected DirectedPseudographImpl() {
    }

    protected DirectedPseudographImpl(int[] vertices, int maxVertices, int avgDegree,
            boolean directed, boolean allowingMultipleEdges, boolean allowingSelfLoops,
            int vertexDataSize, int edgeDataSize) {
        super(vertices, maxVertices, avgDegree, directed, allowingMultipleEdges, allowingSelfLoops,
                vertexDataSize, edgeDataSize);
        selfLoops = new HashMap<>();
    }

    @Override
    protected DirectedPseudographImpl newInstance() {
        return new DirectedPseudographImpl();
    }

    @Override
    protected DirectedPseudographImpl newInstance(int[] vertices, int maxVertices, int avgDegree,
            boolean directed, boolean allowingMultipleEdges, boolean allowingSelfLoops,
            int vertexDataSize, int edgeDataSize) {
        return new DirectedPseudographImpl(vertices, maxVertices, avgDegree, directed,
                allowingMultipleEdges, allowingSelfLoops, vertexDataSize, edgeDataSize);
    }

    @Override
    public DirectedPseudograph<V, E> copy() {
        return copy(true, true, true, true, true);
    }

    @Override
    public DirectedPseudograph<V, E> copy(boolean vertexWeights, boolean vertexLabels, boolean edges, boolean edgeWeights, boolean edgeLabels) {
        var copy = (DirectedPseudographImpl<V, E>) super.copy(vertexWeights, vertexLabels, edges, edgeWeights, edgeLabels);
        copy.selfLoops = edges ? new HashMap<>(selfLoops) : new HashMap<>();
        return copy;
    }

    @Override
    public DirectedPseudograph<V, E> subgraph(VertexSet vertexSet) {
        return (DirectedPseudograph<V, E>) super.subgraph(vertexSet);
    }

    @Override
    public DirectedPseudograph<V, E> subgraph(Collection<Edge> edges) {
        return (DirectedPseudograph<V, E>) super.subgraph(edges);
    }

    @Override
    public DirectedPseudograph<V, E> complement() {
        return (DirectedPseudograph<V, E>) super.complement();
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
    public int addEdge(int v, int u) {
        int pos = super.addEdge(v, u);
        if (v == u) {
            selfLoops.put(v, selfLoops.getOrDefault(v, 0) + 1);
        }
        return pos;
    }

    @Override
    protected void removeEdgeAt(int vi, int pos) {
        super.removeEdgeAt(vi, pos);
        int v = vertices[vi];
        int u = adjList[vi][pos];
        if (v == u) {
            selfLoops.put(v, selfLoops.get(v) - 1);
        }
    }

}
