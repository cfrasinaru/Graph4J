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
package core;

import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import ro.uaic.info.graph.build.GraphBuilder;
import ro.uaic.info.graph.alg.CycleDetector;

/**
 *
 * @author Cristian Frăsinaru
 */
public class CycleDetectorTest {

    @Test
    public void graphAnyCycle() {
        var g = GraphBuilder.vertexRange(1, 5).addEdges("1-2,2-3,3-4,4-5").buildGraph();
        var detector = new CycleDetector(g);
        assertFalse(detector.containsCycle());

        g.addEdge(2, 4);
        assertTrue(detector.containsCycle());
        assertTrue(Arrays.equals(new int[]{2, 3, 4}, detector.findAnyCycle()));
    }

    @Test
    public void digraphAnyCycle() {
        var g = GraphBuilder.vertexRange(1, 5).addEdges("1-2,2-3,3-4,4-5").buildDigraph();
        var detector = new CycleDetector(g);
        assertFalse(detector.containsCycle());

        g.addEdge(2, 4);
        assertFalse(detector.containsCycle());

        g.removeEdge(2, 4);
        g.addEdge(4, 2);
        assertTrue(detector.containsCycle());
        assertTrue(Arrays.equals(new int[]{2, 3, 4}, detector.findAnyCycle()));
    }

    @Test
    public void graphShortestCycle() {
        var g = GraphBuilder.vertexRange(1, 9)
                .addPath(1, 2, 3, 4, 5, 6, 7).addEdge(7, 1)
                .sorted()
                .addClique(1, 8, 9)
                .buildGraph();
        var detector = new CycleDetector(g);
        assertTrue(Arrays.equals(new int[]{1, 2, 3, 4, 5, 6, 7}, detector.findAnyCycle()));
        assertTrue(Arrays.equals(new int[]{1, 8, 9}, detector.findShortestCycle()));
    }

}
