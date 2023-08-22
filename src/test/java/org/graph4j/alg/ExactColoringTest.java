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
package org.graph4j.alg;

import org.graph4j.GraphBuilder;
import org.graph4j.Graphs;
import org.graph4j.alg.coloring.Coloring;
import org.graph4j.alg.coloring.GurobiAssignmentColoring;
import org.graph4j.alg.coloring.BacktrackColoring;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.generate.GraphGenerator;
import org.graph4j.generate.RandomGnpGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class ExactColoringTest {

    public ExactColoringTest() {
    }

    @Test
    public void empty() {
        var g = GraphGenerator.empty(5);
        var alg = new BacktrackColoring(g);
        var col = alg.findColoring();
        assertEquals(1, col.numUsedColors());
        assertTrue(col.isProper());
    }

    @Test
    public void complete() {
        var g = GraphGenerator.complete(5);
        var alg = new BacktrackColoring(g);
        var col = alg.findColoring();
        assertEquals(5, col.numUsedColors());
        assertTrue(col.isProper());
    }

    @Test
    public void bipartite() {
        var g = GraphGenerator.completeBipartite(5, 6);
        var alg = new BacktrackColoring(g);
        var col = alg.findColoring();
        assertEquals(2, col.numUsedColors());
        assertTrue(col.isProper());
    }

    @Test
    public void wheel() {
        var g = GraphGenerator.wheel(6); //center+C5
        var alg = new BacktrackColoring(g);
        var col = alg.findColoring();
        assertEquals(4, col.numUsedColors());
        assertTrue(col.isProper());
    }

    @Test
    public void other() {
        var g = GraphBuilder.vertices(10, 20, 30, 40)
                .addEdges("10-20,10-30,20-30,10-40")
                .buildGraph();
        var alg = new BacktrackColoring(g);
        var col = alg.findColoring();
        assertEquals(3, col.numUsedColors());
        assertTrue(col.isProper());
    }

    @Test
    public void disconnected() {
        var g = GraphBuilder.numVertices(9)
                .addEdges("0-1,0-2,1-2")
                .addEdges("3-4,3-5,3-6,4-5,4-6,5-6")
                .addEdges("7-8")
                .buildGraph();
        assertFalse(Graphs.isConnected(g));
        var alg1 = new BacktrackColoring(g);
        var col1 = alg1.findColoring();
        assertEquals(4, col1.numUsedColors());

    }

    @Test
    public void cross() {
        int n = 20;
        var g = new RandomGnpGraphGenerator(n, Math.random()).createGraph();

        var alg1 = new BacktrackColoring(g);
        var col1 = alg1.findColoring();

        var alg2 = new GurobiAssignmentColoring(g);
        var col2 = alg2.findColoring();

        assertEquals(col1.numUsedColors(), col2.numUsedColors());
    }

    @Test
    public void findAll() {
        int n = 5;
        var g = GraphGenerator.complete(n);
        var initialColoring = new Coloring(g);
        initialColoring.setColor(0, 1);
        initialColoring.setColor(1, 2);
        var alg = new BacktrackColoring(g, initialColoring);
        var cols = alg.findAllColorings(n, 0);
        assertEquals(6, cols.size());
    }

    /*
    @Test
    public void testMany() {
        int n = 20;
        for (int i = 0; i < 1000; i++) {
            var g = new RandomGnpGraphGenerator(n, Math.random()).createGraph();

            try {
                var alg1 = new BacktrackColoring(g);
                var col1 = alg1.findColoring();

                var alg2 = new org.jgrapht.alg.color.BrownBacktrackColoring(Converter.createJGraphT(g));
                var col2 = alg2.getColoring();

                var alg3 = new GurobiAssignmentColoring(g);
                var col3 = alg3.findColoring();

                if (col1.numUsedColors() != col2.getNumberColors() || col2.getNumberColors() != col3.numUsedColors()) {
                    System.out.println(col1.numUsedColors() + ", " + col2.getNumberColors() + ", " + col3.numUsedColors());
                    System.out.println(g);
                    break;
                }
            } catch (Exception e) {
                System.out.println(g);
            }
        }
    }*/

}