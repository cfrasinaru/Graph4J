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
package org.graph4j.core;

import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.util.VertexSet;
import org.graph4j.metrics.GraphMetrics;
import org.graph4j.GraphBuilder;
import org.graph4j.generate.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class GraphMetricsTest {

    @Test
    public void test1() {
        //https://mathworld.wolfram.com/GraphRadius.html
        var g = GraphBuilder.vertexRange(1, 7).addEdges("1-2,2-3,2-4,2-5,4-6,5-7").buildGraph();
        var gm = new GraphMetrics(g);
        assertArrayEquals(new int[]{3, 2, 3, 3, 3, 4, 4}, gm.eccentricities());
        assertEquals(4, gm.diameter());
        assertEquals(2, gm.radius());
        assertEquals(Integer.MAX_VALUE, gm.girth());
        assertEquals(new VertexSet(g, new int[]{2}), gm.center());
        assertEquals(new VertexSet(g, new int[]{6, 7}), gm.periphery());
        assertEquals(new VertexSet(g, new int[]{6, 7}), gm.pseudoPeriphery());
    }

    @Test
    public void test2() {
        //https://cs.stackexchange.com/questions/73698/example-of-pseudo-peripheral-vertex-which-is-not-peripheral-vertex
        var g = GraphBuilder.vertexRange(1, 9).addPath(1, 2, 3, 4, 5, 6, 7).addEdges("8-2,8-6,9-2,9-6").buildGraph();
        var gm = new GraphMetrics(g);
        assertArrayEquals(new int[]{4, 3, 4, 3, 4, 3, 4, 3, 3}, gm.eccentricities());
        assertEquals(4, gm.diameter());
        assertEquals(3, gm.radius());
        assertEquals(4, gm.girth());
        assertEquals(new VertexSet(g, new int[]{2, 4, 6, 8, 9}), gm.center());
        assertEquals(new VertexSet(g, new int[]{1, 3, 5, 7}), gm.periphery());
        assertEquals(new VertexSet(g, new int[]{1, 3, 4, 5, 7, 8, 9}), gm.pseudoPeriphery());
    }

    @Test
    public void path() {
        int n = 1 + new Random().nextInt(100);
        var g = GraphGenerator.path(n);
        var gm = new GraphMetrics(g);
        assertEquals(n - 1, gm.diameter());
        assertEquals(n / 2, gm.radius());
    }

    @Test
    public void cycle() {
        int n = 6;
        var g = GraphGenerator.cycle(n);
        var gm = new GraphMetrics(g);
        assertEquals(n / 2, gm.diameter());
        assertEquals(n / 2, gm.radius());
    }

    @Test
    public void complete() {
        int n = 1 + new Random().nextInt(100);
        var g = GraphGenerator.complete(n);
        var gm = new GraphMetrics(g);
        assertEquals(1, gm.radius());
        assertEquals(1, gm.diameter());
    }

    /*
    private void testDiam() {
        int n = 10;
        for (int i = 0; i < 100; i++) {
            //var g = new RandomGnpGraphGenerator(n, Math.random()).createGraph();
            var g = GraphGenerator.path(n);
            //var g = GraphGenerator.randomTree(n);
            //var g = GraphGenerator.cycle(n);
            int d1 = new ExtremaCalculator(g).getDiameter();
            double d2 = org.jgrapht.GraphMetrics.getDiameter(Converter.createJGraphT(g));
            if (d2 == Double.POSITIVE_INFINITY) {
                d2 = Integer.MAX_VALUE;
            }
            if (d1 != d2) {
                System.out.println("NO! " + d1 + ", " + d2);
                System.out.println(g);
                break;
            }
        }
    }*/
}
