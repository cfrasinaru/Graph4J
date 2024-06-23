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

import org.graph4j.realization.HavelHakimiGraphRealization;
import java.util.stream.IntStream;
import org.graph4j.realization.GraphRealizationAlgorithm;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.util.IntArrays;

/**
 *
 * @author Cristian Frăsinaru
 */
public class GraphRealizationTest {

    public GraphRealizationTest() {
    }

    private void check(int[] degrees) {
        var alg = new HavelHakimiGraphRealization(degrees);
        var g = alg.getGraph();
        assertTrue(IntArrays.haveSameValues(degrees, g.degrees()));
        assertTrue(GraphRealizationAlgorithm.checkErdosGallaiCondition(degrees));
    }

    @Test
    public void empty() {
        int n = 10;
        int[] degrees = new int[n]; //zeros
        check(degrees);
    }

    @Test
    public void complete() {
        int n = 10;
        int[] degrees = new int[n];
        IntStream.range(0, n).forEach(i -> degrees[i] = n - 1);
        check(degrees);
    }

    @Test
    public void regular() {
        int n = 10;
        int k = (int) (n * Math.random());
        int[] degrees = new int[n];
        IntStream.range(0, n).forEach(i -> degrees[i] = k);
        check(degrees);
    }

    @Test
    public void universal() {
        int n = 10;
        int[] degrees = new int[n];
        degrees[0] = n - 1;
        IntStream.range(1, n).forEach(i -> degrees[i] = 1);
        check(degrees);
    }

    @Test
    public void random() {
        int n = 10;
        int count = 1;
        for (int i = 0; i < count; i++) {
            var g1 = GraphGenerator.randomGnp(n, Math.random());
            var alg = new HavelHakimiGraphRealization(g1.degrees());
            var g2 = alg.getGraph();
            assertTrue(IntArrays.haveSameValues(g1.degrees(), g2.degrees()));
            assertTrue(GraphRealizationAlgorithm.checkErdosGallaiCondition(g1.degrees()));
        }
    }

    @Test
    public void notGraphical() {
        int n = 9;
        int[] degrees = IntStream.range(0, n).toArray();
        var ex = assertThrows(IllegalArgumentException.class, () -> {
            var alg = new HavelHakimiGraphRealization(degrees);
            alg.getGraph();
        });
        assertNotNull(ex);
        assertFalse(GraphRealizationAlgorithm.checkErdosGallaiCondition(degrees));
    }

}
