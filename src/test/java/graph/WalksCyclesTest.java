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
package graph;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import ro.uaic.info.graph.model.Cycle;
import ro.uaic.info.graph.model.Path;
import ro.uaic.info.graph.model.Trail;
import ro.uaic.info.graph.model.Walk;
import ro.uaic.info.graph.GraphBuilder;
import ro.uaic.info.graph.gen.CompleteGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class WalksCyclesTest {

    public WalksCyclesTest() {
    }

    @Test
    public void walkTrailPathCycle() {
        var g = new GraphBuilder().vertexRange(1, 6)
                .addClique(1, 2, 3)
                .addEdge(1, 4).addEdge(3, 4).addEdge(4, 5)
                .buildGraph();
        assertFalse(new Walk(g, 1, 2, 1, 6).isValid());
        assertFalse(new Trail(g, 1, 2, 3, 1, 2).isValid());
        assertFalse(new Path(g, 1, 2, 3, 1).isValid());
        assertFalse(new Cycle(g, 1, 3, 4, 5).isValid());
        //
        assertEquals(5, new Walk(g, 1, 2, 1, 2, 3, 4).length());
        assertEquals(4, new Trail(g, 1, 2, 3, 1, 4).length());
        assertEquals(4, new Path(g, 1, 2, 3, 4, 5).length());
        assertEquals(3, new Cycle(g, 1, 2, 3).length());
    }

    @Test
    public void equalsCycleWalkUndirected() {
        var g = new CompleteGenerator(5).createGraph();
        Cycle c1 = new Cycle(g, 0, 1, 2, 3, 4);
        Cycle c2 = new Cycle(g, 2, 3, 4, 0, 1);
        Cycle c3 = new Cycle(g, 0, 4, 3, 2, 1);
        assertEquals(c1, c2);
        assertEquals(c1, c3);
        assertEquals(c2, c3);

        Path p1 = new Path(g, 0, 1, 2, 3, 4);
        Path p2 = new Path(g, 4, 3, 2, 1, 0);
        Path p3 = new Path(g, 1, 2, 3, 4, 0);
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
    }

    @Test
    public void equalsCycleWalkDirected() {
        var g = new CompleteGenerator(5).createDigraph();
        Cycle c1 = new Cycle(g, 0, 1, 2, 3, 4);
        Cycle c2 = new Cycle(g, 2, 3, 4, 0, 1);
        Cycle c3 = new Cycle(g, 0, 4, 3, 2, 1);
        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
        assertNotEquals(c2, c3);

        Path p1 = new Path(g, 0, 1, 2, 3, 4);
        Path p2 = new Path(g, 4, 3, 2, 1, 0);
        Path p3 = new Path(g, 1, 2, 3, 4, 0);
        assertNotEquals(p1, p2);
        assertNotEquals(p1, p3);
    }

}
