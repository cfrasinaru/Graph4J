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
package org.graph4j.generators;

import org.graph4j.realization.BipartiteRealizationAlgorithm;
import org.graph4j.realization.HavelHakimiBipartiteRealization;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.util.IntArrays;

/**
 *
 * @author Cristian Frăsinaru
 */
public class BipartiteRealizationTest {

    public BipartiteRealizationTest() {
    }

    @Test
    public void test() {
        int n1 = 10;
        int n2 = 5;
        for (int i = 0; i < 1; i++) {
            var bip = new RandomGnpBipartiteGenerator(n1, n2, Math.random());
            var g1 = bip.createGraph();
            System.out.println(g1);
            int[] deg1 = new int[n1];
            for (int v = 0; v < n1; v++) {
                deg1[v] = g1.degree(v);
            }
            int[] deg2 = new int[n2];
            for (int v = 0; v < n2; v++) {
                deg2[v] = g1.degree(n1 + v);
            }
            var alg = new HavelHakimiBipartiteRealization(deg1, deg2);
            var g2 = alg.getGraph();
            assertTrue(IntArrays.haveSameValues(g1.degrees(), g2.degrees()));
            assertTrue(BipartiteRealizationAlgorithm.checkGaleRyserCondition(deg1, deg2));
        }

    }
}
