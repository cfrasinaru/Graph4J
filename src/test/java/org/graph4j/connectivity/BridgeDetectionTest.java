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
package org.graph4j.connectivity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.GraphBuilder;
import org.graph4j.alg.connectivity.BridgeDetectionAlgorithm;
import org.graph4j.alg.connectivity.BridgeDetectionAlgorithm;
import org.graph4j.generate.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class BridgeDetectionTest {

    @Test
    public void complete() {
        var g = GraphGenerator.complete(10);
        var alg = new BridgeDetectionAlgorithm(g);
        assertTrue(alg.isBridgeless());
        assertEquals(0, alg.getBridges().size());
    }

    @Test
    public void cycle() {
        var g = GraphGenerator.cycle(10);
        var alg = new BridgeDetectionAlgorithm(g);
        assertEquals(0, alg.getBridges().size());
        assertTrue(alg.isBridgeless());
    }

    @Test
    public void path() {
        var g = GraphGenerator.path(10);
        var alg = new BridgeDetectionAlgorithm(g);
        assertEquals(9, alg.getBridges().size());
        assertFalse(alg.isBridgeless());
    }

    @Test
    public void oneBridge() {
        var g = GraphBuilder.numVertices(6).addEdges("0-1,1-2,2-0,1-3,3-4,4-5,5-3").buildGraph();
        var alg = new BridgeDetectionAlgorithm(g);
        assertEquals(1, alg.getBridges().size()); //1-3
    }

    /*
    int n = 100;
    for (int i = 0; i < 100; i++) {
        var g = new RandomGnpGraphGenerator(n, 0.01).createGraph();
        var alg1 = new BridgeDetectionAlgorithm(g);
        var alg2 = new BiconnectivityInspector(Converter.createJGraphT(g));
        double x1 = alg1.getBridges().size();
        double x2 = alg2.getBridges().size();
        if (x1 != x2) {
            System.out.println(g);
            System.out.println(x1);
            System.out.println(x2);
            break;
        }
    }*/
}
