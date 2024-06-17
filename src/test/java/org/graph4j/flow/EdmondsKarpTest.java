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

/**
 *
 * @author Cristian Frăsinaru
 */
public class EdmondsKarpTest {

    @Test
    public void simpleEK() {
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
        var alg = new EdmondsKarpMaximumFlow(g);
        assertEquals(13, alg.getMaximumFlowValue());
        assertEquals(13, alg.getMaximumFlowData().value());
        assertEquals(7, alg.getFlowValue(2, t));
        assertEquals(6, alg.getFlowValue(3, t));

        assertTrue(alg.getSourcePart().contains(s));
        assertTrue((alg.getSinkPart().contains(t)));

        double minCutValue = 0;
        for (var e : alg.getMinimumCutEdges()) {
            minCutValue += g.getEdgeData(CAPACITY, e);
        }
        assertEquals(alg.getMaximumFlowValue(), minCutValue);
    }

}
