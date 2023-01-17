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
package util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import ro.uaic.info.graph.GraphBuilder;
import ro.uaic.info.graph.model.VertexHeap;

/**
 *
 * @author Cristian Frăsinaru
 */
public class VertexHeapTest {

    public VertexHeapTest() {
    }

    @Test
    public void testHeap() {
        int[] u = {10, 20, 30, 40, 50, 60, 70, 80};
        var g = new GraphBuilder().numVertices(8).buildGraph();
        VertexHeap heap = new VertexHeap(g, (i, j) -> u[i] - u[j]);
        assertEquals(0, heap.peek());
        
        u[7] = 5;
        heap.update(7);
        assertEquals(7, heap.peek());

        u[4] = 1;
        heap.update(4);
        assertEquals(4, g.vertexAt(heap.poll()));
        assertEquals(7, g.vertexAt(heap.poll()));
        assertEquals(0, g.vertexAt(heap.poll()));
        
    }

}
