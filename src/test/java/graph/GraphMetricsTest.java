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
import ro.uaic.info.graph.model.VertexSet;
import ro.uaic.info.graph.alg.GraphMetrics;
import ro.uaic.info.graph.GraphBuilder;

/**
 *
 * @author Cristian Frăsinaru
 */
public class GraphMetricsTest {

    @Test
    public void test1() {
        //https://mathworld.wolfram.com/GraphRadius.html
        var g = new GraphBuilder().vertexRange(1, 7).addEdges("1-2,2-3,2-4,2-5,4-6,5-7").buildGraph();
        var gm = new GraphMetrics(g);
        assertArrayEquals(new int[]{3, 2, 3, 3, 3, 4, 4}, gm.eccentricities());
        assertEquals(4, gm.diameter());
        assertEquals(2, gm.radius());
        assertEquals(Integer.MAX_VALUE, gm.girth());
        assertEquals(new VertexSet(g, new int[]{2}), gm.center());
        assertEquals(new VertexSet(g, new int[]{6, 7}), gm.periphery());
        assertEquals(new VertexSet(g, new int[]{6, 7}), gm.pseudoPeriphery());
    }

    @Test
    public void test2() {
        //https://cs.stackexchange.com/questions/73698/example-of-pseudo-peripheral-vertex-which-is-not-peripheral-vertex
        var g = new GraphBuilder().vertexRange(1, 9).addPath(1, 2, 3, 4, 5, 6, 7).addEdges("8-2,8-6,9-2,9-6").buildGraph();
        var gm = new GraphMetrics(g);
        assertArrayEquals(new int[]{4, 3, 4, 3, 4, 3, 4, 3, 3}, gm.eccentricities());
        assertEquals(4, gm.diameter());
        assertEquals(3, gm.radius());
        assertEquals(4, gm.girth());
        assertEquals(new VertexSet(g, new int[]{2, 4, 6, 8, 9}), gm.center());
        assertEquals(new VertexSet(g, new int[]{1, 3, 5, 7}), gm.periphery());
        assertEquals(new VertexSet(g, new int[]{1, 3, 4, 5, 7, 8, 9}), gm.pseudoPeriphery());
    }
}
