/*
 * Copyright (C) 2024 Cristian Frăsinaru and contributors
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

import java.util.Random;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.generators.GraphGenerator;
import org.graph4j.util.Clique;
import org.graph4j.util.Cycle;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Cristian Frăsinaru
 */
public class ChordalGraphSupportTest {

    private void checkNotChordal(Graph g) {
        var alg = new ChordalGraphSupport(g);
        assertFalse(alg.isChordal());
        Cycle hole = alg.findHole();
        assertNotNull(hole);
        assertTrue(hole.size() >= 4);
    }

    @Test
    public void testNotChordal() {
        int n = 5 + new Random().nextInt(10);
        checkNotChordal(GraphGenerator.grid(n, n));
        checkNotChordal(GraphGenerator.cycle(n));
        checkNotChordal(GraphGenerator.wheel(n));
    }

    @Test
    public void testAcyclic() {
        int n = 20;
        var g = GraphGenerator.randomForest(n);
        if (g.isEdgeless()) {
            g.addEdge(0, 1);
        }
        var alg = new ChordalGraphSupport(g);
        assertTrue(alg.isChordal());
        assertEquals(2, alg.getMaximumCliqueSize());
        assertEquals(2, alg.getOptimalColoring().numUsedColors());
    }

    @Test
    public void testGemGraph() {
        //gem graph is F1,4 (fan graph)
        var g = GraphBuilder.edges("0-1,0-2,0-3,0-4,1-2,2-3,3-4").buildGraph();
        var alg = new ChordalGraphSupport(g);
        assertTrue(alg.isChordal());
        assertEquals(3, alg.getMaximumCliqueSize());
        assertEquals(3, alg.getOptimalColoring().numUsedColors());
        assertEquals(3, alg.getMaximalCliques().size());
        assertEquals(2, alg.getMaximumStableSet().size());
        assertEquals(2, alg.getMinimumCliqueCover().size());
    }

    @Test
    public void testMinimalVertexSeparators() {
        //from Minimal vertex separators of chordal graphs
        //Fig.1 Base sets of a chordal graph
        var g = GraphBuilder.edges("1-3,1-12,2-3,2-12,3-4,3-11,3-12,4-11,4-12,5-11,5-12,6-7,7-11,7-12,8-9,8-11,8-12,9-10,9-11,9-12,10-11,10-12,11-12").buildGraph();
        var alg = new ChordalGraphSupport(g);
        assertTrue(alg.isChordal());
        var map = alg.getMinimumVertexSeparators();
        assertEquals(4, map.keySet().size());
        int[][] sep = {{9, 11, 12}, {11, 12}, {7}, {3, 12}};
        int[] mul = {1, 3, 1, 2};
        for (int i = 0; i < sep.length; i++) {
            assertEquals(mul[i], map.get(new Clique(g, sep[i])));
        }
    }

}
