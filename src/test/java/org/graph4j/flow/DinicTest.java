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
package org.graph4j.flow;

import org.graph4j.Network;
import static org.graph4j.Network.CAPACITY;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.NetworkBuilder;
import org.graph4j.generators.EdgeDataGenerator;
import org.graph4j.generators.RandomGnpGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class DinicTest {

    @Test
    public void courseExample() {
        int s = 0;
        int t = 5;
        var g = NetworkBuilder.numVertices(6).source(s).sink(t).buildNetwork();
        g.addEdge(s, 1, 9);
        g.addEdge(s, 4, 8);
        g.addEdge(1, 2, 10);
        g.addEdge(1, 3, 5);
        g.addEdge(2, t, 7);
        g.addEdge(3, t, 6);
        g.addEdge(4, 1, 2);
        g.addEdge(4, 3, 5);
        var alg = new DinicMaximumFlow(g);
        assertEquals(13, alg.getMaximumFlowValue());
    }

    @Test
    public void simpleBackward() {
        //example for backward edges
        var g = NetworkBuilder.edges("0-1,0-5,1-2,1-3,2-7,3-4,4-7,5-6,6-2").source(0).sink(7).buildNetwork();
        new EdgeDataGenerator(g, CAPACITY).fill(1);
        var alg = new DinicMaximumFlow(g);
        assertEquals(2, alg.getMaximumFlowValue());
    }

    @Test
    public void wikipedia() {
        // Wikipedia
        var g = NetworkBuilder.numVertices(6).source(0).sink(5).buildNetwork();
        g.addEdge(0, 1, 10);
        g.addEdge(0, 2, 10);
        g.addEdge(1, 2, 2);
        g.addEdge(1, 3, 4);
        g.addEdge(1, 4, 8);
        g.addEdge(2, 4, 9);
        g.addEdge(3, 5, 10);
        g.addEdge(4, 3, 6);
        g.addEdge(4, 5, 10);
        var alg = new DinicMaximumFlow(g);
        assertEquals(19, alg.getMaximumFlowValue());
    }

    @Test
    public void succPosTest() {
        // succ test
        var g = NetworkBuilder.numVertices(10).source(0).sink(9).buildNetwork();
        g.addEdge(0, 1, 2);
        g.addEdge(0, 5, 3);
        g.addEdge(1, 2, 2);
        g.addEdge(2, 3, 1);
        g.addEdge(3, 4, 2);
        g.addEdge(4, 9, 2);
        g.addEdge(5, 6, 3);
        g.addEdge(6, 3, 1);
        g.addEdge(6, 7, 2);
        g.addEdge(7, 8, 2);
        g.addEdge(8, 9, 2);
        var alg = new DinicMaximumFlow(g);
        assertEquals(4, alg.getMaximumFlowValue());
    }

    public void algorithmConsistencyTest() {
        int n = 100;
        for (int i = 0; i < 10; i++) {
            Network g = new RandomGnpGraphGenerator(n, Math.random()).createNetwork();
            new EdgeDataGenerator(g, CAPACITY).randomIntegers(0, n);
            var alg1 = new EdmondsKarpMaximumFlow(g);
            var alg2 = new DinicMaximumFlow(g);
            double x = alg1.getMaximumFlowValue();
            double y = alg2.getMaximumFlowValue();
            assertEquals(x, y);
        }
    }

}
