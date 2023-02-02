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

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.GraphBuilder;
import ro.uaic.info.graph.traverse.BFSIterator;
import ro.uaic.info.graph.traverse.DFSIterator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class IteratorTest {

    @Test
    public void vertexIterator() {
        var g = GraphBuilder.vertexRange(1, 4).addEdges("1-2,2-3,3-4").buildGraph();
        for (var it = g.vertexIterator(); it.hasNext();) {
            assertEquals(g.vertexAt(0), it.next());
            it.remove();
        }
        assertEquals(0, g.numEdges());
    }

    @Test
    public void edgeIterator() {
        var g = GraphBuilder.vertexRange(1, 4).addEdges("1-2,2-3,3-4").buildGraph();
        int i = 1;
        for (var it = g.edgeIterator(); it.hasNext();) {
            assertEquals(new Edge(i, i + 1), it.next());
            i++;
            it.remove();
        }
        assertEquals(0, g.numEdges());
    }

    @Test
    public void testDFS() {
        var g = GraphBuilder.numVertices(8)
                .addEdges("0-1,1-2,0-3,3-4,0-5,5-6")
                .buildGraph();
        var sb = new StringBuilder();
        var dfs = new DFSIterator(g);
        while (dfs.hasNext()) {
            sb.append(dfs.next().vertex());
        }
        assertEquals("01234567", sb.toString());
    }

    @Test
    public void testBFS() {
        var g = GraphBuilder.numVertices(8)
                .addEdges("0-1,1-2,0-3,3-4,0-5,5-6")
                .buildGraph();
        var sb = new StringBuilder();
        var dfs = new BFSIterator(g);
        while (dfs.hasNext()) {
            sb.append(dfs.next().vertex());
        }
        assertEquals("01352467", sb.toString());
    }

}
