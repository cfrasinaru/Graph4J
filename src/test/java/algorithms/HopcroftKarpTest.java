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
import org.graph4j.GraphBuilder;
import org.graph4j.generate.GraphGenerator;
import org.graph4j.alg.matching.HopcroftKarpMaximumMatching;
import org.graph4j.util.StableSet;

/**
 *
 * @author Cristian Frăsinaru
 */
public class HopcroftKarpTest {

    public HopcroftKarpTest() {
    }

    @Test
    public void empty() {
        var g = GraphBuilder.numVertices(5).buildGraph();
        var alg = new HopcroftKarpMaximumMatching(g);
        assertEquals(0, alg.getMatching().size());
        assertEquals(5, alg.getMaximumStableSet().size());
        assertEquals(0, alg.getMinimumVertexCover().size());
    }

    @Test
    public void simple1() {
        var g = GraphBuilder.vertices(0, 1, 2, 3, 4, 5)
                .addEdges("0-3,0-4,0-5,1-5,2-4").buildGraph();
        var alg = new HopcroftKarpMaximumMatching(g);
        assertEquals(3, alg.getMatching().size());
        assertEquals(6 - 3, alg.getMaximumStableSet().size());
        assertEquals(3, alg.getMinimumVertexCover().size());
    }

    @Test
    public void simple2() {
        var g = GraphBuilder.numVertices(8)
                .addEdges("0-5, 0-6, 0-7, 1-4, 1-6, 1-7, 2-4, 2-7, 3-7").buildGraph();
        var left = new StableSet(g, new int[]{0, 1, 2, 3});
        var right = new StableSet(g, new int[]{4, 5, 6, 7});
        var alg = new HopcroftKarpMaximumMatching(g, left, right);
        assertEquals(4, alg.getMatching().size());
        assertEquals(8 - 4, alg.getMaximumStableSet().size());
        assertEquals(4, alg.getMinimumVertexCover().size());
    }

    @Test
    public void simple3() {
        var g = GraphBuilder.numVertices(6).addEdges("0-3,1-3, 2-4,2-5,2-3").buildGraph();
        var alg = new HopcroftKarpMaximumMatching(g);
        assertEquals(2, alg.getMatching().size());
        assertEquals(4, alg.getMaximumStableSet().size());
        assertEquals(2, alg.getMinimumVertexCover().size());
    }

    @Test
    public void complete() {
        int n = 5;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                var g = GraphGenerator.completeBipartite(i, j);
                var alg = new HopcroftKarpMaximumMatching(g);
                int niu = alg.getMatching().size();
                assertEquals(Math.min(i, j), niu);
                assertEquals((i + j) - niu, alg.getMaximumStableSet().size());
                assertEquals(niu, alg.getMinimumVertexCover().size());
            }
        }
    }

    /*
        for (int i = 0; i < 100; i++) {
            var g = GraphGenerator.randomBipartite(10, 10, 0.1);
            var jg = Tools.createJGraphT(g);
            var gg = Tools.createAlgs4Graph(g);
            var bip = BipartitionAlgorithm.getInstance(g);
            var left = bip.getLeftSide();
            var right = bip.getRightSide();
            var alg1 = new HopkroftKarpBipartiteMatching(g);
            var alg2 = new HopcroftKarp(gg);
            var alg3 = new HopcroftKarpMaximumCardinalityBipartiteMatching(jg,IntArrays.asSet(left.vertices()), IntArrays.asSet(right.vertices()));
            int m1 = alg1.getMatching().size();            
            int m2 = alg2.size();
            int m3 = alg3.getMatching().getEdges().size();
            if (m1 != m2 || m1 != m3) {
                System.out.println(g);
                break;
            }
     */
}
