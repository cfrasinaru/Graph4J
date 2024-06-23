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
package org.graph4j.core;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.graph4j.GraphBuilder;
import org.graph4j.GraphUtils;
import org.graph4j.generators.CompleteGraphGenerator;
import org.graph4j.generators.GraphGenerator;
import org.graph4j.util.VertexSet;

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
        var g1 = g.subgraph(new VertexSet(g, array));
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
    public void contractVerticesWeighted() {
        var g = GraphBuilder.numVertices(4).buildGraph();
        g.addEdge(0, 1, 10);
        g.addEdge(0, 2, 20);
        g.addEdge(0, 3, 30);
        g.contractVertices(1,2,3); //becomes 4
        assertTrue(g.containsVertex(4));//new vertex
        assertEquals(2, g.numVertices()); //0-4
        assertEquals(60, g.getEdgeWeight(0, 4));
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
        var g = GraphUtils.disjointUnion(g1, g2, g3);
        assertEquals(8, g.numVertices());
        assertEquals(6, g.numEdges());
    }

    @Test
    public void join2() {
        var g1 = GraphBuilder.vertices(1, 2, 3).addEdges("1-2,1-3,2-3").buildGraph();
        var g2 = GraphBuilder.vertices(4, 5, 6).addEdges("4-5,5-6").buildGraph();
        var g = GraphUtils.join(g1, g2);
        int n1 = g1.numVertices();
        int n2 = g2.numVertices();
        assertEquals(n1 + n2, g.numVertices());
        assertEquals(g1.numEdges() + g2.numEdges() + n1 * n2, g.numEdges());
    }

    @Test
    public void join3() {
        var g1 = GraphBuilder.vertices(1, 2, 3).addEdges("1-2,1-3,2-3").buildGraph();
        var g2 = GraphBuilder.vertices(4, 5, 6).addEdges("4-5,5-6").buildGraph();
        var g3 = GraphBuilder.vertices(7, 8, 9, 10).addEdges("7-8,8-9,9-10,10-7").buildGraph();
        var g = GraphUtils.join(g1, g2, g3);
        int n1 = g1.numVertices();
        int n2 = g2.numVertices();
        int n3 = g3.numVertices();
        long m1 = g1.numEdges();
        long m2 = g2.numEdges();
        long m3 = g3.numEdges();
        assertEquals(n1 + n2 + n3, g.numVertices());
        assertEquals(m1 + m2 + m3 + n1 * n2 + n1 * n3 + n2 * n3, g.numEdges());
    }

    @Test
    public void union() {
        var g1 = GraphBuilder.vertices(1, 2, 3).addEdges("1-2,2-3").buildGraph();
        var g2 = GraphBuilder.vertices(2, 3, 4).addEdges("2-3,3-4").buildGraph();
        var g = GraphUtils.union(g1, g2);
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

    @Test
    public void supportWeightedMultigraph() {
        //cumulates weights
        var g = GraphBuilder.numVertices(4).buildMultigraph();
        g.addEdge(0,1,10);
        g.addEdge(0,1,10);
        g.addEdge(0,1,10);
        g.addEdge(0,2,10);
        g.addEdge(0,2,10);
        g.addEdge(0,3,10);        
        var support = g.supportGraph();
        assertEquals(3, support.numEdges());
        assertEquals(30, support.getEdgeWeight(0, 1));        
        assertEquals(20, support.getEdgeWeight(0, 2));        
        assertEquals(10, support.getEdgeWeight(0, 3));        
    }
    
    @Test
    public void transpose() {
        var g = GraphBuilder.numVertices(4)
                .addEdges("0-1, 0-2, 0-3, 1-3")
                .buildDigraph();
        var t = GraphUtils.transpose(g);
        assertEquals(g.numEdges(), t.numEdges());
        assertTrue(t.containsEdge(1, 0));
        assertTrue(t.containsEdge(2, 0));
        assertTrue(t.containsEdge(3, 0));
        assertTrue(t.containsEdge(3, 1));
    }

    @Test
    public void addRemove1() {
        int n = 10;
        var g = GraphGenerator.randomGnp(n, 0.5);
        g.addVertex(n);
        g.removeVertex(n);
        g.addVertex(n);
    }

    @Test
    public void addRemove2() {
        int n = 10;
        var g = GraphBuilder.numVertices(n).buildDigraph();
        g.addVertex(n);
        for (int i = 0; i < n - 1; i++) {
            g.addEdge(n, i);
        }
        g.removeVertex(n);
    }

}
