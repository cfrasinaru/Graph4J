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
package org.graph4j.alg;

import org.graph4j.GraphBuilder;
import org.graph4j.measures.TriangleCounter;
import org.graph4j.generators.GraphGenerator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Cristian Frăsinaru
 * @author Gabriel Ignat
 */
public class TriangleCounterTest {

    @Test
    public void empty() {
        int n = 10;
        var g = GraphGenerator.empty(n);
        long count = new TriangleCounter(g).count();
        assertEquals(0, count); //C(n,3)
    }

    @Test
    public void complete() {
        int n = 10;
        var g = GraphGenerator.complete(n);
        long count = new TriangleCounter(g).count();
        assertEquals(n * (n - 1) * (n - 2) / 6, count); //C(n,3)
    }

    @Test
    public void wheel() {
        int n = 10;
        var g = GraphGenerator.wheel(n);
        long count = new TriangleCounter(g).count();
        assertEquals(n - 1, count);
    }

    @Test
    public void multipleEdges1() {
        var g = GraphBuilder.numVertices(4).buildMultigraph();
        int[][] edges = {{0, 1}, {1, 2}, {2, 0}, {1, 3}, {2, 3}, {2, 1}};
        for (int[] e : edges) {
            g.addEdge(e[0], e[1]);
        }
        assertEquals(4, new TriangleCounter(g).count());
    }

    @Test
    public void testMultipleEdges2() {
        var g = GraphBuilder.numVertices(4).buildMultigraph();
        int[][] edges = {{0, 1}, {1, 2}, {2, 0}, {1, 3}, {2, 3}, {2, 1}, {0, 2}, {0, 2}};
        for (int[] e : edges) {
            g.addEdge(e[0], e[1]);
        }
        assertEquals(8, new TriangleCounter(g).count());
    }
    /*
    private void test() {
        int n = 20;
        for (int i = 0; i < 1000; i++) {
            var g = GraphGenerator.randomGnp(n, Math.random());
            long x = new TriangleCounter(g).count();
            long y = org.jgrapht.GraphMetrics.getNumberOfTriangles(Converter.createJGraphT(g));
            if (x != y) {
                System.out.println(x + " != " + y);
                System.out.println(g);
                break;
            }
        }
    }   
    
    //Bug in JGraphT
    private void test() {
        int n = 1000;
        for (int i = 0; i < 10; i++) {
            var g = GraphGenerator.randomGnp(n, 0.01);
            var jg = Converter.createJGraphT(g);
            var alg = new TriangleCounter(g);
            long x = alg.count();
            long y = org.jgrapht.GraphMetrics.getNumberOfTriangles(jg);
            long z1 = alg.naiveCount(new VertexList(g, g.vertices()));
            long z2 = naiveCountTriangles(jg);
            if (x != y) {
                System.out.println(x + " != " + y);
                System.out.println("z1=" + z1);
                System.out.println("z2=" + z2);
                System.out.println(g);
                break;
            }
        }
    }    
     */
}
