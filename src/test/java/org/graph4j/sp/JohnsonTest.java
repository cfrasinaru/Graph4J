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
package org.graph4j.sp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.util.Path;
import org.graph4j.GraphBuilder;
import org.graph4j.alg.sp.FloydWarshallShortestPath;
import org.graph4j.alg.sp.JohnsonShortestPath;
import org.graph4j.generate.EdgeWeightsGenerator;
import org.graph4j.generate.RandomGnpGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class JohnsonTest {

    public JohnsonTest() {
    }

    @Test
    public void simple() {
        var g = GraphBuilder.numVertices(3).buildDigraph();
        g.addEdge(0, 1, 3);
        g.addEdge(0, 2, 2);
        g.addEdge(1, 2, -2);

        var alg = new JohnsonShortestPath(g);
        assertEquals(1, alg.getPathWeight(0, 2));
    }

    @Test
    public void cross() {
        int n = 20;
        var g = new RandomGnpGraphGenerator(n, Math.random()).createGraph();
        EdgeWeightsGenerator.randomDoubles(g, 0, 1);

        var alg1 = new JohnsonShortestPath(g);
        var alg2 = new FloydWarshallShortestPath(g);
        double eps = 1.0E-6;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double w1 = alg1.getPathWeight(i, j);
                double w2 = alg2.getPathWeight(i, j);
                assertTrue(Math.abs(w1 - w2) < eps);
            }
        }

    }
}
