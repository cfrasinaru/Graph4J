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

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.util.Cycle;
import org.graph4j.GraphBuilder;
import org.graph4j.alg.cycle.CycleFinder;

/**
 *
 * @author Cristian Frăsinaru
 */
public class CycleDetectorTest {

    @Test
    public void graphAnyCycle() {
        var g = GraphBuilder.vertexRange(1, 5).addEdges("1-2,2-3,3-4,4-5").buildGraph();
        var detector = new CycleFinder(g);
        assertFalse(detector.containsCycle());

        g.addEdge(2, 4);
        assertTrue(detector.containsCycle());
        assertEquals(new Cycle(g, new int[]{2, 3, 4}), detector.findAnyCycle());
    }

    @Test
    public void digraphAnyCycle() {
        var g = GraphBuilder.vertexRange(1, 5).addEdges("1-2,2-3,3-4,4-5").buildDigraph();
        var detector = new CycleFinder(g);
        assertFalse(detector.containsCycle());

        g.addEdge(2, 4);
        assertFalse(detector.containsCycle());

        g.removeEdge(2, 4);
        g.addEdge(4, 2);
        assertTrue(detector.containsCycle());
        assertEquals(new Cycle(g, new int[]{2, 3, 4}), detector.findAnyCycle());
    }

    @Test
    public void graphShortestCycle() {
        var g = GraphBuilder.vertexRange(1, 11)
                .addPath(1, 2, 3, 4, 5, 6, 7).addEdge(7, 1)
                .addPath(1, 8, 9)
                .addPath(1, 10, 11)
                .addEdge(9, 11)
                .buildGraph();
        var detector = new CycleFinder(g);
        assertEquals(new Cycle(g, new int[]{1, 2, 3, 4, 5, 6, 7}), detector.findAnyCycle());
        assertEquals(new Cycle(g, new int[]{1, 8, 9, 11, 10}), detector.findShortestCycle());
    }

    @Test
    public void graphVertexCycle() {
        var g = GraphBuilder.vertexRange(1, 11)
                .addPath(1, 2, 3, 4, 5, 6, 7, 8, 9).addEdge(3, 8)
                .addClique(6, 10, 11)
                .buildGraph();
        var detector = new CycleFinder(g);
        Cycle c1 = new Cycle(g, new int[]{3, 4, 5, 6, 7, 8});
        Cycle c2 = new Cycle(g, new int[]{6, 10, 11});
        assertEquals(c1, detector.findAnyCycle(6));
        assertEquals(c2, detector.findShortestCycle(6));
        assertEquals(c1, detector.findEvenCycle());
        assertEquals(c2, detector.findOddCycle());
    }
    
    @Test
    public void specialCases() {
        var g1 = GraphBuilder.vertexRange(0,2).addEdges("0-1,1-1,1-2").buildPseudograph();
        assertEquals(1, new CycleFinder(g1).findAnyCycle().length()); //1-1
        var g2 = GraphBuilder.vertexRange(0,2).addEdges("0-1,1-2,1-2").buildMultigraph();
        assertEquals(2, new CycleFinder(g2).findAnyCycle().length()); //1-2
        var g3 = GraphBuilder.vertexRange(0,2).addEdges("0-1,1-2,2-1").buildDigraph();
        assertEquals(2, new CycleFinder(g3).findAnyCycle().length()); //1-2
    }
    

}
