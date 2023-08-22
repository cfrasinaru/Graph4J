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

import org.graph4j.alg.coloring.DSaturGreedyColoring;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.alg.coloring.GreedyColoring;
import org.graph4j.alg.coloring.Coloring;
import org.graph4j.generate.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class GreedyColoringTest {

    public GreedyColoringTest() {
    }

    @Test
    public void manual() {
        int red = 0, green = 1, yellow = 2;
        var g = GraphGenerator.cycle(5);
        var col = new Coloring(g);
        col.setColor(0, red);
        col.setColor(1, green);
        col.setColor(2, red);
        col.setColor(3, green);
        col.setColor(4, yellow);

        assertTrue(col.isComplete());
        assertTrue(col.isProper());
        assertEquals(3, col.getColorClasses().size());

        col.setColor(4, red);
        assertEquals(2, col.getColorClasses().size());
        assertFalse(col.isProper());
    }

    @Test
    public void empty() {
        var g = GraphGenerator.empty(5);
        var alg = new GreedyColoring(g);
        var col = alg.findColoring();
        assertEquals(1, col.numUsedColors());
        assertTrue(col.isProper());
    }

    @Test
    public void complete() {
        var g = GraphGenerator.complete(5);
        var alg = new GreedyColoring(g);
        var col = alg.findColoring();
        assertEquals(5, col.numUsedColors());
        assertTrue(col.isProper());
    }

    @Test
    public void dsaturBipartite() {
        var g = GraphGenerator.completeBipartite(5, 6);
        var alg = new DSaturGreedyColoring(g);
        var col = alg.findColoring();
        assertEquals(2, col.numUsedColors());
        assertTrue(col.isProper());
    }

    @Test
    public void dsaturWheel() {
        var g = GraphGenerator.wheel(6); //center+C5
        var alg = new DSaturGreedyColoring(g);
        var col = alg.findColoring();
        assertEquals(4, col.numUsedColors());
        assertTrue(col.isProper());
    }

}
