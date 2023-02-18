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
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.GraphBuilder;
import ro.uaic.info.graph.generate.EdgeWeightsGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class SimpleTest {

    public SimpleTest() {
    }

    @Test
    public void labels() {
        Graph<String, String> g = GraphBuilder.numVertices(3).buildGraph();
        g.setVertexLabel(0, "a");
        g.setVertexLabel(1, "b");
        g.setVertexLabel(2, "c");
        for (int v : g.vertices()) {
            assertEquals(String.valueOf((char) ('a' + v)), g.getVertexLabel(v));
        }

        g.addEdge(0, 1, "01");
        g.addEdge(0, 2, "02");
        g.addEdge(1, 2, "12");
        for (Edge e : g.edges()) {
            assertEquals(e.source() + "" + e.target(), g.getEdgeLabel(e.source(), e.target()));
        }
    }

    @Test
    public void matrix4Graph() {
        var g = GraphBuilder.numVertices(5).addPath(0, 1, 2, 3, 4).buildGraph();
        EdgeWeightsGenerator.fill(g, 99);

        int[][] a = g.adjacencyMatrix();
        assertEquals(0, a[1][1]);
        assertEquals(1, a[3][4]);
        assertEquals(1, a[4][3]);

        double[][] c = g.costMatrix();
        assertEquals(0, c[0][0]);
        assertEquals(Double.POSITIVE_INFINITY, c[0][2]);
        assertEquals(99, c[0][1]);
        assertEquals(99, c[1][0]);

        int[][] im = g.incidenceMatrix();
        int k = 0;
        for (Edge e : g.edges()) {
            int vi = g.indexOf(e.source());
            int ui = g.indexOf(e.target());
            assertEquals(1, im[vi][k]);
            assertEquals(1, im[ui][k]);
            k++;
        }
    }

    @Test
    public void matrix4Digraph() {
        var g = GraphBuilder.numVertices(5).addCycle(0, 1, 2, 3, 4).buildDigraph();
        EdgeWeightsGenerator.fill(g, 99);
        int[][] a = g.adjacencyMatrix();
        assertEquals(0, a[0][2]);
        assertEquals(1, a[2][3]);
        assertEquals(0, a[3][2]);

        double[][] c = g.costMatrix();
        assertEquals(99, c[0][1]);
        assertEquals(0, c[0][0]);
        assertEquals(Double.POSITIVE_INFINITY, c[0][2]);
        assertEquals(Double.POSITIVE_INFINITY, c[1][0]);

        int[][] im = g.incidenceMatrix();
        int k = 0;
        for (Edge e : g.edges()) {
            int vi = g.indexOf(e.source());
            int ui = g.indexOf(e.target());
            assertEquals(1, im[vi][k]);
            assertEquals(-1, im[ui][k]);
            k++;
        }
    }
}
