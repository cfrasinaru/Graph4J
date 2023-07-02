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
package algorithms;

import org.graph4j.GraphBuilder;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.alg.coloring.Coloring;
import org.graph4j.alg.ordering.VertexOrderings;

/**
 *
 * @author Cristian Frăsinaru
 */
public class VertexOrderingTest {

    public VertexOrderingTest() {
    }

    @Test
    public void largestFirst() {
        var g = GraphBuilder.numVertices(4).addEdges("0-1,0-2,0-3,1-2").buildGraph();
        //degrees = {0:3, 1:2, 2:2, 3:1}
        int[] ord = VertexOrderings.largestDegreeFirst(g);
        assertArrayEquals(new int[]{0, 1, 2, 3}, ord);
    }

    @Test
    public void smallestLast() {
        var g = GraphBuilder.numVertices(4).addEdges("0-1,0-2,0-3,1-2").buildGraph();
        int[] ord = VertexOrderings.smallestDegreeLast(g);
        assertArrayEquals(new int[]{1, 2, 0, 3}, ord);

    }
    
    @Test
    public void smallestFirst() {
        var g = GraphBuilder.numVertices(4).addEdges("0-1,0-2,0-3,1-2").buildGraph();
        int[] ord = VertexOrderings.smallestDegreeFirst(g);
        assertArrayEquals(new int[]{3, 1, 2, 0}, ord);

    }

}
