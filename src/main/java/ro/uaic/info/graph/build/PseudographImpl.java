/*
 * Copyright (C) 2022 Faculty of Computer Science Iasi, Romania
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

import java.util.HashMap;
import java.util.Map;
import ro.uaic.info.graph.Pseudograph;

/**
 *
 * @author Cristian Frăsinaru
 */
class PseudographImpl<V, E> extends MultigraphImpl<V, E> implements Pseudograph<V, E> {

    protected Map<Integer, Integer> selfLoops = new HashMap<>();

    protected PseudographImpl() {
    }

    protected PseudographImpl(int[] vertices, int maxVertices, int avgDegree,
            boolean sorted, boolean directed, boolean allowsMultiEdges, boolean allowsSelfLoops) {
        super(vertices, maxVertices, avgDegree, sorted, directed, allowsMultiEdges, allowsSelfLoops);
    }

    @Override
    protected PseudographImpl newInstance() {
        return new PseudographImpl();
    }

    @Override
    protected PseudographImpl newInstance(int[] vertices, int maxVertices, int avgDegree,
            boolean sorted, boolean directed, boolean allowsMultiEdges, boolean allowsSelfLoops) {
        return new PseudographImpl(vertices, maxVertices, avgDegree, sorted, directed, allowsMultiEdges, allowsSelfLoops);
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
    public void addEdge(int v, int u) {
        super.addEdge(v, u);
        if (v == u && allowsSelfLoops) {
            selfLoops.put(v, selfLoops(v) + 1);
        }
    }

    @Override
    public void removeEdge(int v, int u) {
        int multi = 0;
        while (adjListPos(v, u) >= 0) {
            removeFromAdjList(v, u);
            if (v != u && !directed) {
                removeFromAdjList(u, v);
            }
            multi++;
        }
        numEdges -= multi;
    }

    @Override
    public Pseudograph<V, E> copy() {
        var copy = (PseudographImpl) super.copy();
        copy.selfLoops = new HashMap<>(selfLoops);
        return copy;
    }

    @Override
    public Pseudograph<V, E> subgraph(int... vertices) {
        var sub = (PseudographImpl) super.subgraph(vertices);
        for (int v : vertices) {
            sub.selfLoops.put(v, selfLoops.get(v));
        }
        return sub;
    }

}
