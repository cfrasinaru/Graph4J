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
package org.graph4j.support;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.generators.GraphGenerator;
import org.graph4j.generators.RandomTreeGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class BipartiteGraphSupportTest {

    public BipartiteGraphSupportTest() {
    }

    @Test
    public void completeBipartite() {
        int n1 = 5;
        int n2 = 6;
        var g = GraphGenerator.completeBipartite(n1, n2);
        //[0,1,2,3,4],[5,6,7,8,9,10]
        var alg = new BipartiteGraphSupport(g);
        assertTrue(alg.isBipartite());
        assertEquals(n1, alg.getLeftSide().size());
        assertEquals(n2, alg.getRightSide().size());
        assertEquals(alg.getLeftSide(), alg.getSide(0));
        assertEquals(alg.getRightSide(), alg.getSide(n1));
        assertNull(alg.findOddCycle());
        assertTrue(alg.getColoring().numUsedColors() == 2);
        assertTrue(alg.getMaximumMatching().size() == n1);
        assertTrue(alg.getMinimumVertexCover().size() == n1);
        assertTrue(alg.getMaximumStableSet().size() == n2);
    }

    @Test
    public void randomTree() {
        var g = new RandomTreeGenerator(10).createTree();
        var alg = new BipartiteGraphSupport(g);
        assertTrue(alg.isBipartite());
    }

    @Test
    public void oddCycle() {
        var g = GraphGenerator.cycle(7);
        var alg = new BipartiteGraphSupport(g);
        assertFalse(alg.isBipartite());
        assertNotNull(alg.findOddCycle());
    }

    @Test
    public void evenCycle() {
        var g = GraphGenerator.cycle(8);
        var alg = new BipartiteGraphSupport(g);
        assertTrue(alg.isBipartite());
    }

}
