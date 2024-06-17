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
package org.graph4j.spanning;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.GraphTests;
import org.graph4j.generators.EdgeWeightsGenerator;
import org.graph4j.generators.RandomGnpGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class MSTTest {

    @Test
    public void primTree1() {
        Graph g = GraphBuilder.vertexRange(1, 4).addEdges("1-2,2-3,3-4,4-1,4-2").buildGraph();
        g.setEdgeWeight(1, 2, 3);
        g.setEdgeWeight(2, 3, 1);
        g.setEdgeWeight(3, 4, 4);
        g.setEdgeWeight(4, 2, 1);
        g.setEdgeWeight(4, 1, 1);
        var alg = new PrimMinimumSpanningTree(g);
        assertEquals(3, alg.getWeight());
        assertEquals(3, alg.getTree().numEdges());
        assertTrue(GraphTests.isConnected(alg.getTree()));
    }

    @Test
    public void primForrest() {
        Graph g = GraphBuilder.vertexRange(1, 4).buildGraph();
        g.addEdge(1, 2, 3);
        g.addEdge(3, 4, 4);
        var alg = new PrimMinimumSpanningTree(g);
        assertEquals(7, alg.getWeight());
        assertEquals(2, alg.getTree().numEdges());
        assertFalse(GraphTests.isConnected(alg.getTree()));
    }

        @Test
    public void crossTest() {
        double epsilon = 1.0E-12;   
        for (int i = 0; i < 10; i++) {
            Graph g = new RandomGnpGraphGenerator(20, Math.random()).createGraph();
            EdgeWeightsGenerator.randomDoubles(g, 0, 1);
            double p = new PrimMinimumSpanningTree(g).getWeight();
            double k1 = new KruskalMinimumSpanningTree(g).getWeight();
            double k2 = new ParallelFilterKruskal(g).getWeight();
            assertTrue(Math.abs(p - k1) < epsilon);
            assertTrue(Math.abs(k1 - k2) < epsilon);
        }
    }

}
