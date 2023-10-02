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
package org.graph4j.alg.connectivity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.GraphBuilder;
import org.graph4j.generate.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class VertexConnectivityTest {

    @Test
    public void empty() {
        int n = 10;
        var g = GraphGenerator.empty(n);
        var alg = new VertexConnectivityAlgorithm(g);
        assertEquals(0, alg.countMaximumDisjointPaths(0, n - 1));
        assertEquals(0, alg.getMaximumDisjointPaths(0, n - 1).size());
    }

    @Test
    public void complete() {
        int n = 10;
        var g = GraphGenerator.complete(n);
        g.removeEdge(0, n - 1);
        var alg = new VertexConnectivityAlgorithm(g);
        assertEquals(n - 2, alg.countMaximumDisjointPaths(0, n - 1));
        assertEquals(n - 2, alg.getMaximumDisjointPaths(0, n - 1).size());
    }

    @Test
    public void path() {
        int n = 10;
        var g = GraphGenerator.path(n);
        var alg = new VertexConnectivityAlgorithm(g);
        assertEquals(1, alg.countMaximumDisjointPaths(0, n - 1));
        assertEquals(1, alg.getMaximumDisjointPaths(0, n - 1).size());
    }

    @Test
    public void simple1() {
        //two paths that have a common internal vertex
        var g = GraphBuilder.numVertices(7).addEdges("0-1,0-2,1-3,2-3,3-4,3-5,4-6,5-6").buildGraph();
        var alg = new VertexConnectivityAlgorithm(g);
        assertEquals(1, alg.countMaximumDisjointPaths(0, 6));
        assertEquals(1, alg.getMaximumDisjointPaths(0, 6).size());
    }

    @Test
    public void simple2() {
        //two paths with nothing in common but the extremities
        var g = GraphBuilder.numVertices(6).addEdges("0-1,1-2,2-5,0-3,3-4,4-5").buildGraph();
        var alg = new VertexConnectivityAlgorithm(g);
        assertEquals(2, alg.countMaximumDisjointPaths(0, 5));
        assertEquals(2, alg.getMaximumDisjointPaths(0, 5).size());
    }

    @Test
    public void duality() {
        int n = 20;
        double p = Math.random();
        var g = GraphGenerator.randomGnp(n, p);
        var alg = new VertexConnectivityAlgorithm(g);
        for (int s : g.vertices()) {
            for (int t : g.vertices()) {
                if (s == t || g.containsEdge(s, t)) {
                    continue;
                }
                int x = alg.countMaximumDisjointPaths(s, t);
                int y = alg.getMinimumCut(s, t).size();
                assertEquals(x, y);
            }
        }
    }

}
