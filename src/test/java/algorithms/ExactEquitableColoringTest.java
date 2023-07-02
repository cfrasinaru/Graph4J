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

import org.graph4j.alg.coloring.eq.BacktrackEquitableColoring;
import org.graph4j.alg.coloring.eq.GurobiAssignmentEquitableColoring;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.generate.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class ExactEquitableColoringTest {

    public ExactEquitableColoringTest() {
    }

    @Test
    public void empty() {
        var g = GraphGenerator.empty(5);
        var alg = new BacktrackEquitableColoring(g);
        var col = alg.findColoring();
        assertEquals(1, col.numUsedColors());
        assertTrue(alg.isValid(col));
    }

    @Test
    public void complete() {
        var g = GraphGenerator.complete(5);
        var alg = new BacktrackEquitableColoring(g);
        var col = alg.findColoring();
        assertEquals(5, col.numUsedColors());
        assertTrue(alg.isValid(col));
    }

    @Test
    public void bipartite() {
        var g = GraphGenerator.completeBipartite(5, 6);
        var alg = new BacktrackEquitableColoring(g);
        var col = alg.findColoring();
        assertEquals(2, col.numUsedColors());
        assertTrue(alg.isValid(col));
    }

    @Test
    public void cross() {
        int n = 20;
        var g = GraphGenerator.randomGnp(n, Math.random());

        var alg1 = new BacktrackEquitableColoring(g);
        var col1 = alg1.findColoring();
        assertTrue(alg1.isValid(col1));

        var alg2 = new GurobiAssignmentEquitableColoring(g);
        var col2 = alg2.findColoring();
        assertTrue(alg2.isValid(col2));

        assertEquals(col1.numUsedColors(), col2.numUsedColors());
    }

    
    /*
    @Test
    public void testMany() {
        int n = 30;
        for (int i = 0; i < 100; i++) {
            var g = GraphGenerator.randomGnp(n, Math.random());
            var alg1 = new BacktrackEquitableColoring(g);
            var col1 = alg1.findColoring();

            var alg2 = new GurobiAssignmentEquitableColoring(g);
            var col2 = alg2.findColoring();

            assert col1.numUsedColors() == col2.numUsedColors();
        }
    }*/

}
