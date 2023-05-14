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
package org.graph4j.alg.cut;

import org.graph4j.Graph;
import org.graph4j.alg.GraphMeasures;
import org.graph4j.alg.SimpleGraphAlgorithm;
import org.graph4j.util.VertexSet;

/**
 * The vertex separator problem (VSP) is to find a partition of V into nonempty
 * three classes A, B, C such that there is no edge between A and B,
 * <code>max(|A|,|B|) &lt;= f(n)</code> and <code>|C|</code> is minimum.
 *
 * C is called <em>separator</em>, A and B are called <em>shores</em>.
 *
 * The value of the parameter f(n) which is more used in literature is 2n/3.
 *
 * <p>
 * There is no guarantee that the vertex separator created by the greedy
 * algorithm is of minimum size.
 *
 * @author Cristian Frăsinaru
 *
 */
public class GreedyVertexSeparator extends SimpleGraphAlgorithm {

    private VertexSet leftShore, rightShore, separator;

    public GreedyVertexSeparator(Graph graph) {
        super(graph);
    }

    /**
     * Returns the separator computed by the method {@link #getSeparator(int)}.
     * with the default argument <code>2n/3</code>, where <code>n</code> is the
     * number of vertices in the graph.
     *
     * @return a vertex separator set.
     */
    public VertexSeparator getSeparator() {
        return getSeparator(0);
    }

    /**
     * Computes a vertex separator set, having a specified maximum shore size.
     * There is no guarantee that the vertex separator returned is of minimum
     * size or that the right shore does not exceed the maximum shore size. The
     * right shore may be empty (in case of complete graphs, for example).
     *
     * @param maxShoreSize maximum size of the largest shore.
     * @return a vertex separator set.
     */
    public VertexSeparator getSeparator(int maxShoreSize) {
        int n = graph.numVertices();
        if (maxShoreSize <= 0) {
            maxShoreSize = 2 * n / 3;
        }
        int v = GraphMeasures.minDegreeVertex(graph);
        leftShore = new VertexSet(graph);
        leftShore.add(v);
        separator = new VertexSet(graph, graph.neighbors(v));

        rightShore = new VertexSet(graph, graph.vertices());
        rightShore.remove(v);
        rightShore.removeAll(separator.vertices());

        //grow the left shore
        while (leftShore.size() < maxShoreSize && rightShore.size() > maxShoreSize) {
            v = choose(separator, rightShore);
            leftShore.add(v);
            separator = neighborhood(leftShore);
            rightShore = new VertexSet(graph, graph.vertices());
            rightShore.removeAll(leftShore.vertices());
            rightShore.removeAll(separator.vertices());
        }

        //reduce the right shore
        /*
        while (rightShore.size() > maxShoreSize) {
            v = rightShore.pop();
            separator.add(v);
        }*/
        return new VertexSeparator(separator, leftShore, rightShore);
    }

    private int choose(VertexSet sep, VertexSet right) {
        int minVertex = -1;
        int min = Integer.MAX_VALUE;
        for (int u : sep.vertices()) {
            var nb = right.intersection(graph.neighbors(u));
            if (nb.size() < min) {
                min = nb.size();
                minVertex = u;
            }
        }
        for (int u : right.vertices()) {
            var nb = right.intersection(graph.neighbors(u));
            if (nb.size() < min) {
                min = nb.size();
                minVertex = u;
            }
        }
        return minVertex;
    }

    private VertexSet neighborhood(VertexSet set) {
        var nb = new VertexSet(graph);
        for (int v : set.vertices()) {
            nb.addAll(graph.neighbors(v));
        }
        nb.removeAll(set.vertices());
        return nb;
    }

}
