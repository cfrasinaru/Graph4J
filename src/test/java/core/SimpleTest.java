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

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import ro.uaic.info.graph.Cycle;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.Path;
import ro.uaic.info.graph.Trail;
import ro.uaic.info.graph.Walk;
import ro.uaic.info.graph.build.GraphBuilder;

/**
 *
 * @author Cristian Frăsinaru
 */
public class SimpleTest {

    public SimpleTest() {
    }

    @Test
    public void testLabels() {
        Graph<String, String> g = GraphBuilder.numVertices(3).buildGraph();
        g.setVertexLabel(0, "a");
        g.setVertexLabel(1, "b");
        g.setVertexLabel(2, "c");
        for (int v : g.vertices()) {
            assertEquals(String.valueOf((char) ('a' + v)), g.getVertexLabel(v));
        }

        g.addLabeledEdge(0, 1, "01");
        g.addLabeledEdge(0, 2, "02");
        g.addLabeledEdge(1, 2, "12");
        for (int[] e : g.edges()) {
            assertEquals(e[0] + "" + e[1], g.getEdgeLabel(e[0], e[1]));
        }
    }

    @Test
    public void testWalkTrailPath() {
        var g = GraphBuilder.vertexRange(1, 6)
                .addClique(1, 2, 3)
                .addEdge(1, 4).addEdge(3, 4).addEdge(4, 5)
                .buildGraph();
        var badWalk = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Walk(g, 1, 2, 1, 6));
        assertNotNull(badWalk);
        var badTrail = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Trail(g, 1, 2, 3, 1, 2));
        assertNotNull(badTrail);
        var badPath = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Path(g, 1, 2, 3, 1));
        assertNotNull(badPath);
        var badCycle = Assertions.assertThrows(IllegalArgumentException.class,
                () -> new Cycle(g, 1, 3, 4, 5));
        assertNotNull(badCycle);
        //
        assertEquals(5, new Walk(g, 1, 2, 1, 2, 3, 4).length());
        assertEquals(4, new Trail(g, 1, 2, 3, 1, 4).length());
        assertEquals(4, new Path(g, 1, 2, 3, 4, 5).length());
        assertEquals(3, new Cycle(g, 1, 2, 3).length());
    }

}
