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

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.graph4j.GraphBuilder;
import org.graph4j.Graphs;
import org.graph4j.generate.CompleteGraphGenerator;
import org.graph4j.generate.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class OperationsTest {

    public OperationsTest() {
    }

    @Test
    public void copy() {
        int n = 10;
        var g = GraphGenerator.complete(n);
        var g1 = g.copy();
        assertEquals(g.numVertices(), g1.numVertices());
        assertEquals(g.numEdges(), g1.numEdges());

        var dg = new CompleteGraphGenerator(n).createDigraph();
        var dg1 = dg.copy();
        assertEquals(dg.numVertices(), dg1.numVertices());
        assertEquals(dg.numEdges(), dg1.numEdges());
    }

    @Test
    public void subgraph() {
        int n = 10;
        var g = new CompleteGraphGenerator(n).createDigraph();

        int k = n / 2;
        var array = new int[k];
        for (int i = 0; i < k; i++) {
            array[i] = i;
        }
        var g1 = g.subgraph(array);
        assertEquals(k, g1.numVertices());
        assertEquals(k * (k - 1), g1.numEdges());
    }

    @Test
    public void complement() {
        int n = 5;
        var g = GraphBuilder.numVertices(n)
                .addEdges("0-1, 0-3, 1-2, 1-4, 2-3")
                .buildGraph();
        var c = g.complement();
        assertEquals(n * (n - 1) / 2 - g.numEdges(), c.numEdges());
    }

    @Test
    public void contractVertices() {
        int n = 5;
        var g = GraphBuilder.numVertices(n)
                .addEdges("0-1, 0-2, 0-3, 1-3, 1-4")
                .buildGraph();
        g.contractVertices(0, 1); //2-5,3-5,4-5
        assertTrue(g.containsVertex(5));//new vertex
        assertEquals(n - 1, g.numVertices()); //2,3,4,5
        assertTrue(Arrays.equals(new int[]{2, 3, 4}, g.neighbors(5)));
    }

    @Test
    public void duplicateVertex() {
        int n = 5;
        var g = GraphBuilder.numVertices(n)
                .addEdges("0-1, 0-2, 0-3, 1-3, 1-4")
                .buildGraph();
        g.duplicateVertex(0);
        assertTrue(g.containsVertex(5));//new vertex
        assertEquals(n + 1, g.numVertices());
        assertTrue(Arrays.equals(new int[]{1, 2, 3}, g.neighbors(5)));
    }

    @Test
    public void splitEdge() {
        int n = 3;
        var g = GraphBuilder.numVertices(n)
                .addEdges("0-1, 1-2")
                .buildGraph();
        g.splitEdge(1, 2);
        assertTrue(g.containsVertex(3));//new vertex
        assertEquals(n + 1, g.numVertices());
        assertTrue(g.containsEdge(3, 1));
        assertTrue(g.containsEdge(3, 2));
        assertFalse(g.containsEdge(1, 1));
    }

    @Test
    public void disjointUnion() {
        var g1 = GraphBuilder.vertices(1, 2, 3).addEdges("1-2,1-3,2-3").buildGraph();
        var g2 = GraphBuilder.vertices(4, 5, 6).addEdges("4-5,5-6").buildGraph();
        var g3 = GraphBuilder.vertices(7, 8).addEdges("7-8").buildGraph();
        var g = Graphs.disjointUnion(g1, g2, g3);
        assertEquals(8, g.numVertices());
        assertEquals(6, g.numEdges());
    }

    @Test
    public void join() {
        var g1 = GraphBuilder.vertices(1, 2, 3).addEdges("1-2,1-3,2-3").buildGraph();
        var g2 = GraphBuilder.vertices(4, 5, 6).addEdges("4-5,5-6").buildGraph();
        var g = Graphs.join(g1, g2);
        int n1 = g1.numVertices();
        int n2 = g2.numVertices();
        assertEquals(n1 + n2, g.numVertices());
        assertEquals(g1.numEdges() + g2.numEdges() + n1 * n2, g.numEdges());
    }

    @Test
    public void union() {
        var g1 = GraphBuilder.vertices(1, 2, 3).addEdges("1-2,2-3").buildGraph();
        var g2 = GraphBuilder.vertices(2, 3, 4).addEdges("2-3,3-4").buildGraph();
        var g = Graphs.union(g1, g2);
        assertEquals(4, g.numVertices());
        assertEquals(3, g.numEdges());
    }

    @Test
    public void supportGraph() {
        var g1 = GraphBuilder.numVertices(3).addClique(0, 1, 2).buildDigraph();
        assertEquals(6, g1.numEdges());
        assertEquals(3, g1.supportGraph().numEdges());

        var g2 = GraphBuilder.numVertices(3).addEdges("0-0,0-0,0-1,0-1,1-2,2-2").buildPseudograph();
        assertEquals(2, g2.supportGraph().numEdges());
    }

}
