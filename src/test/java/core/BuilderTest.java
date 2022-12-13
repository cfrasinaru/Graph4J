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
import ro.uaic.info.graph.build.GraphBuilder;

/**
 *
 * @author Cristian Frăsinaru
 */
public class BuilderTest {

    public BuilderTest() {
    }

    @Test
    public void empty() {
        var g = GraphBuilder.empty().buildGraph();
        assertEquals(0, g.numVertices());
        assertEquals(0, g.numEdges());
    }

    @Test
    public void buildGraph1() {
        var g = GraphBuilder.vertexRange(0, 3)
                .addClique(0, 1, 2, 3).buildGraph();
        assertEquals(6, g.numEdges());

        g.addVertices(8, 9);
        g.addEdge(0, 8);
        g.addEdge(0, 9);
        assertEquals(5, g.degree(0));

        g.removeVertex(0);
        g.addEdge(8, 9);
        assertEquals(4, g.numEdges());
    }

    public void buildGraph2() {
        int n = 10;
        var g = GraphBuilder.vertexRange(1, n)
                .complete().buildGraph();
        assertEquals(n * (n - 1) / 2, g.numEdges());
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                assertTrue(g.containsEdge(g.vertexAt(i), g.vertexAt(j)));
                g.removeEdge(g.vertexAt(i), g.vertexAt(j));
                assertFalse(g.containsEdge(g.vertexAt(i), g.vertexAt(j)));
            }
        }
    }

    @Test
    public void buildDigraph() {
        var g = GraphBuilder.vertexRange(0, 4)
                .addEdges("0-2, 0-3, 1-2, 1-3, 2-3, 3-4")
                .buildDigraph();
        assertEquals(6, g.numEdges());
        assertEquals(0, g.inDegree(0));
        assertEquals(0, g.inDegree(1));
        assertEquals(2, g.inDegree(2));
        assertEquals(3, g.inDegree(3));
        assertEquals(1, g.inDegree(4));
    }

    @Test
    public void buildWeightedGraph() {
        var g = GraphBuilder.vertexRange(0, 3)
                .addClique(0, 1, 2, 3)
                .buildGraph();
        double x = 0.0, y = 0.0;
        for (int v : g.vertices()) {
            g.setVertexWeight(v, x++);
        }
        int n = g.numVertices();
        for (int i = 0; i < n - 1; i++) {
            int v = g.vertexAt(i);
            for (int j = i + 1; j < n; j++) {
                int u = g.vertexAt(j);
                g.setEdgeWeight(v, u, y++);
            }
        }
        assertEquals(3.0, g.getVertexWeight(3));
        assertEquals(5.0, g.getEdgeWeight(2, 3));

        g.addWeightedVertex(999, 999.0);
        g.addWeightedEdge(999, 0, 999.0);
        assertEquals(999, g.getVertexWeight(999));
        assertEquals(999, g.getEdgeWeight(0, 999));
    }

    @Test
    public void buildWeightedDigraph() {
        var g = GraphBuilder.vertexRange(0, 3)
                .addPath(0, 1, 2, 3)
                .buildDigraph();
        double x = 0.0, y = 0.0;
        for (int v : g.vertices()) {
            g.setVertexWeight(v, x++);
        }
        for (int i = 0; i < g.numVertices() - 1; i++) {
            g.setEdgeWeight(g.vertexAt(i), g.vertexAt(i + 1), y++);
        }
        assertEquals(3.0, g.getVertexWeight(3));
        assertEquals(2.0, g.getEdgeWeight(2, 3));
    }
    

}
