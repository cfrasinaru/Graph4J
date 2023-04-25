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
package algorithms;

import org.graph4j.alg.coloring.BacktrackColoring;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.generate.GraphGenerator;

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

    /*
        int n = 21;
        for (int i = 0; i < 100; i++) {
            var g = new RandomGnpGraphGenerator(n, 0.3).createGraph();
            
            var alg1 = new BacktrackColoring(g, 10_000);
            var col1 = alg1.findColoring();            

            var alg2 = new org.jgrapht.alg.color.BrownBacktrackColoring(Converter.createJGraphT(g));
            var col2 = alg2.getColoring();
            
            if (col1.numUsedColors() != col2.getNumberColors()) {
                System.out.println(g);
                break;
            }
        }
     */
}
