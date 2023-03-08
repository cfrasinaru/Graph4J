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

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.alg.bipartite.BipartitionAlgorithm;
import org.graph4j.generate.GraphGenerator;
import org.graph4j.generate.RandomTreeGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class BipartiteTest {

    public BipartiteTest() {
    }

    @Test
    public void completeBipartite() {
        var g = GraphGenerator.completeBipartite(5, 6);
        //[0,1,2,3,4],[5,6,7,8,9,10]
        var alg = BipartitionAlgorithm.getInstance(g);
        assertTrue(alg.isBipartite());
        assertEquals(5, alg.getLeftSide().size());
        assertEquals(6, alg.getRightSide().size());
        assertEquals(alg.getLeftSide(), alg.getSide(0));
        assertEquals(alg.getRightSide(), alg.getSide(5));
        assertNull(alg.findOddCycle());
    }

    @Test
    public void randomTree() {
        var g = new RandomTreeGenerator(10).create();
        var alg = BipartitionAlgorithm.getInstance(g);
        assertTrue(alg.isBipartite());
    }

    @Test
    public void oddCycle() {
        var g = GraphGenerator.cycle(7);
        var alg = BipartitionAlgorithm.getInstance(g);
        assertFalse(alg.isBipartite());
        assertNotNull(alg.findOddCycle());
    }

    @Test
    public void evenCycle() {
        var g = GraphGenerator.cycle(8);
        var alg = BipartitionAlgorithm.getInstance(g);
        assertTrue(alg.isBipartite());
    }

}
