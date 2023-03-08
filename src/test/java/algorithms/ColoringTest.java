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

import org.checkerframework.checker.units.qual.g;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.alg.coloring.GreedyColoring;
import org.graph4j.alg.coloring.VertexColoring;
import org.graph4j.generate.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class ColoringTest {

    public ColoringTest() {
    }

    @Test
    public void manual() {
        var g = GraphGenerator.cycle(5);
        var col = new VertexColoring<String>(g);
        col.setColor(0, "red");
        col.setColor(1, "green");
        col.setColor(2, "red");
        col.setColor(3, "green");
        col.setColor(4, "yellow");

        assertTrue(col.isComplete());
        assertTrue(col.isProper());
        assertEquals(3, col.getColorClasses().size());

        col.setColor(4, "red");
        assertEquals(2, col.getColorClasses().size());
        assertFalse(col.isProper());
    }

    @Test
    public void emptyGreedy() {
        var g = GraphGenerator.empty(5);
        var alg = new GreedyColoring(g);
        var col = alg.findColoring();
        assertEquals(1, col.numUsedColors());
        assertTrue(col.isProper());
    }

    @Test
    public void completeGreedy() {
        var g = GraphGenerator.complete(5);
        var alg = new GreedyColoring(g);
        var col = alg.findColoring();
        assertEquals(5, col.numUsedColors());
        assertTrue(col.isProper());
    }

}
