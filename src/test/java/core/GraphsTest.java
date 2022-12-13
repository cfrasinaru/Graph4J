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

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.Graphs;
import ro.uaic.info.graph.gen.GnmRandomGenerator;
import ro.uaic.info.graph.gen.GnpRandomGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class GraphsTest {

    public GraphsTest() {
    }

    @Test
    public void complete() {
        int n = 10;
        Graph g = Graphs.complete(n);
        assertEquals((long) n * (n - 1) / 2, g.numEdges());
    }

    @Test
    public void completeBipartite() {
        int n0 = 10;
        int n1 = 5;
        Graph g = Graphs.completeBipartite(n0, n1);
        assertEquals(n0 * n1, g.numEdges());
    }

    @Test
    public void path() {
        int n = 10;
        Graph g = Graphs.path(n);
        assertEquals(n - 1, g.numEdges());
    }

    @Test
    public void cycle() {
        int n = 10;
        Graph g = Graphs.cycle(n);
        assertEquals(n, g.numEdges());
        assertTrue(g.containsEdge(0, n - 1));
    }

    @Test
    public void randomGnm() {
        int n = 10;
        int m = 20;
        Graph g = new GnmRandomGenerator(n, m).createGraph();
        assertEquals(m, g.numEdges());
    }

    @Test
    public void randomGnp() {
        int n = 100;
        var g1 = new GnpRandomGenerator(n, 0).createGraph();
        var g2 = new GnpRandomGenerator(n, 1).createGraph();
        assertEquals(0, g1.numEdges());
        assertEquals(n * (n - 1) / 2, g2.numEdges());
    }

}
