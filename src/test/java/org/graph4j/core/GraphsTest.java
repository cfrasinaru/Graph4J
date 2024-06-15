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

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.GraphBuilder;
import org.graph4j.Graphs;
import org.graph4j.generate.GraphGenerator;
import org.graph4j.generate.RandomOreGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class GraphsTest {

    @Test
    public void hasPath() {
        var g = GraphBuilder.numVertices(7).addEdges("0-1,1-2,3-4,3-5").buildGraph();
        assertTrue(Graphs.hasPath(g, 0, 2));
        assertTrue(Graphs.hasPath(g, 3, 5));
        assertFalse(Graphs.hasPath(g, 0, 3));
    }

    @Test
    public void hasDirectedPath() {
        var g = GraphBuilder.numVertices(7).addEdges("0-1,1-2,5-4,4-3,3-2").buildDigraph();
        assertTrue(Graphs.hasPath(g, 0, 2));
        assertTrue(Graphs.hasPath(g, 5, 2));
        assertFalse(Graphs.hasPath(g, 2, 5));
        assertFalse(Graphs.hasPath(g, 0, 3));
    }

    @Test
    public void regular() {
        int n = 5;
        var g = GraphGenerator.complete(n);
        assertTrue(Graphs.isRegular(g, n - 1));
        g.removeEdge(0, 1);
        assertFalse(Graphs.isRegular(g));
    }
    
    @Test
    public void hasOreProperty() {
        int n = 10;
        var g = new RandomOreGraphGenerator(n).createGraph();
        assertTrue(Graphs.hasOreProperty(g));
    }
    
}
