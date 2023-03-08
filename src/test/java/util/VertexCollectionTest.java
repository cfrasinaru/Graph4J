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
import org.graph4j.GraphBuilder;
import org.graph4j.util.Clique;
import org.graph4j.util.StableSet;
import org.graph4j.util.VertexHeap;
import org.graph4j.util.VertexQueue;
import org.graph4j.util.VertexSet;

/**
 *
 * @author Cristian Frăsinaru
 */
public class VertexCollectionTest {

    public VertexCollectionTest() {
    }

    @Test
    public void testSet() {
        var g = GraphBuilder.numVertices(10).buildGraph();
        var set = new VertexSet(g);
        set.addAll(0, 1, 2, 3, 4);
        set.removeAll(0, 1);
        set.retainAll(3, 4);
        assertEquals(2, set.size());
        assertTrue(set.contains(3) && set.contains(4));
    }

    @Test
    public void testClique() {
        var g = GraphBuilder.numVertices(10).addClique(3, 4, 5).buildGraph();
        var clique = new Clique(g);
        clique.addAll(2, 3, 4);
        assertFalse(clique.isValid());
        clique.remove(2);
        clique.add(5);
        assertTrue(clique.isValid());
    }

    @Test
    public void testStableSet() {
        var g = GraphBuilder.numVertices(10).addClique(3, 4, 5).buildGraph();
        var stable = new StableSet(g);
        stable.addAll(2, 3, 4);
        assertFalse(stable.isValid());
        stable.removeAll(3, 4);
        stable.addAll(0, 1, 2);
        assertTrue(stable.isValid());
    }

    @Test
    public void testHeap() {
        int[] u = {10, 20, 30, 40, 50, 60, 70, 80};
        var g = GraphBuilder.numVertices(8).buildGraph();
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

    @Test
    public void testQueue() {
        var g = GraphBuilder.numVertices(10).buildDigraph();
        var queue = new VertexQueue(g);
        queue.add(2);
        queue.add(3);
        queue.add(4);
        assertEquals(2, queue.peek());
        assertEquals(2, queue.poll());
        assertFalse(queue.contains(2));
        assertEquals(3, queue.peek());
        queue.add(5);
        queue.add(6);
        assertEquals(4, queue.size());
        assertTrue(queue.contains(6));
    }

}
