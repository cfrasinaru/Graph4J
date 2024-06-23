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
public class PushRelabelTest {

    @Test
    public void simple1() {
        //0->1->2->3->4->5
        var g = NetworkBuilder.numVertices(6).source(0).sink(5).buildNetwork();
        g.addEdge(0, 1, 5);
        g.addEdge(1, 2, 4);
        g.addEdge(2, 3, 3);
        g.addEdge(3, 4, 2);
        g.addEdge(4, 5, 1);
        var alg = new PushRelabelMaximumFlow(g);
        assertEquals(1, alg.getMaximumFlowValue());
    }

    @Test
    public void simple2() {
        //0->1->2->3->4->5
        var g = NetworkBuilder.numVertices(6).source(0).sink(5).buildNetwork();
        g.addEdge(0, 1, 5);
        g.addEdge(1, 2, 5);
        g.addEdge(2, 3, 5);
        g.addEdge(3, 4, 5);
        g.addEdge(4, 5, 2);
        var alg = new PushRelabelMaximumFlow(g);
        assertEquals(2, alg.getMaximumFlowValue());
    }

    @Test
    public void simple3() {
        //exemplul din curs
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
        var alg = new PushRelabelMaximumFlow(g);
        assertEquals(13, alg.getMaximumFlowValue());
        assertEquals(13, alg.getMaximumFlowData().value());
        assertEquals(7, alg.getFlowValue(2, t));
        assertEquals(6, alg.getFlowValue(3, t));
    }

    @Test
    public void globalRelabel() {
        //[0->3=3.0, 0->4=0.0, 1->0=4.0, 1->4=4.0, 2->0=1.0, 2->1=2.0, 2->3=4.0, 
        // 2->4=0.0, 3->0=2.0, 3->2=2.0, 4->0=1.0, 4->1=0.0, 4->2=0.0, 4->3=1.0]
        var g = NetworkBuilder.numVertices(5).source(0).sink(4).buildNetwork();
        g.addEdge(0, 3, 3);
        g.addEdge(0, 4, 0);
        g.addEdge(1, 0, 4);
        g.addEdge(1, 4, 4);
        g.addEdge(2, 0, 1);
        g.addEdge(2, 1, 2);
        g.addEdge(2, 3, 4);
        g.addEdge(2, 4, 0);
        g.addEdge(3, 0, 2);
        g.addEdge(3, 2, 2);
        g.addEdge(4, 0, 1);
        g.addEdge(4, 1, 0);
        g.addEdge(4, 2, 0);
        g.addEdge(4, 3, 1);

        var alg = new PushRelabelMaximumFlow(g);
        assertEquals(2, alg.getMaximumFlowValue());
    }

    @Test
    public void algorithmConsistencyTest() {
        int n = 10;
        var g = new RandomGnpGraphGenerator(n, Math.random()).createNetwork();
        new EdgeDataGenerator(g, CAPACITY).randomIntegers(0, n);
        var alg1 = new EdmondsKarpMaximumFlow(g);
        var alg2 = new PushRelabelMaximumFlow(g);
        double x = alg1.getMaximumFlowValue();
        double y = alg2.getMaximumFlowValue();
        assertEquals(x, y);
    }

}
