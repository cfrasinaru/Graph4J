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

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import ro.uaic.info.graph.build.GraphBuilder;
import ro.uaic.info.graph.search.BFSIterator;
import ro.uaic.info.graph.search.DFSIterator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class IteratorTest {

    @Test
    public void testDFS() {
        var g = GraphBuilder
                .numVertices(8)
                .addEdges(new int[][]{{0, 1}, {1, 2}, {0, 3}, {3, 4}, {0, 5}, {5, 6}})
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
        var g = GraphBuilder
                .numVertices(8)
                .addEdges(new int[][]{{0, 1}, {1, 2}, {0, 3}, {3, 4}, {0, 5}, {5, 6}})
                .buildGraph();
        var sb = new StringBuilder();
        var dfs = new BFSIterator(g);
        while (dfs.hasNext()) {
            sb.append(dfs.next().vertex());
        }
        assertEquals("01352467", sb.toString());
    }

}
