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

import org.graph4j.realization.KleitmanWangDigraphRealization;
import java.util.stream.IntStream;
import org.graph4j.realization.DigraphRealizationAlgorithm;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.util.IntArrays;

/**
 *
 * @author Cristian Frăsinaru
 */
public class DigraphRealizationTest {

    public DigraphRealizationTest() {
    }

    private void check(int[] in, int[] out) {
        var alg = new KleitmanWangDigraphRealization(in, out);
        var g = alg.getDigraph();
        assertTrue(IntArrays.haveSameValues(in, g.indegrees()));
        assertTrue(IntArrays.haveSameValues(out, g.outdegrees()));
        assertTrue(DigraphRealizationAlgorithm.checkFulkersonChenAnsteeCondition(in, out));
    }

    @Test
    public void empty() {
        int n = 10;
        int[] zeros = new int[n]; //zeros
        check(zeros, zeros);
    }

    @Test
    public void complete() {
        int n = 10;
        int[] degrees = new int[n];
        IntStream.range(0, n).forEach(i -> degrees[i] = n - 1);
        check(degrees, degrees);
    }

    @Test
    public void regular() {
        int n = 10;
        int k = (int) (n * Math.random());
        int[] degrees = new int[n];
        IntStream.range(0, n).forEach(i -> degrees[i] = k);
        check(degrees, degrees);
    }

    @Test
    public void universal() {
        int n = 10;
        int[] in = new int[n];
        in[0] = 0;
        IntStream.range(1, n).forEach(i -> in[i] = 1);
        int[] out = new int[n];
        out[0] = n - 1;
        IntStream.range(1, n).forEach(i -> out[i] = 0);
        check(in, out);
    }

    @Test
    public void random() {
        int n = 10;
        int count = 1;
        for (int i = 0; i < count; i++) {
            var g1 = new RandomGnpGraphGenerator(n, Math.random()).createDigraph();
            var alg = new KleitmanWangDigraphRealization(g1.indegrees(), g1.outdegrees());
            var g2 = alg.getDigraph();
            assertTrue(IntArrays.haveSameValues(g1.degrees(), g2.degrees()));
            assertTrue(DigraphRealizationAlgorithm.checkFulkersonChenAnsteeCondition(g1.indegrees(), g2.outdegrees()));
        }
    }

    @Test
    public void notDigraphical() {
        int n = 9;
        int[] degrees = IntStream.range(0, n).toArray();
        var ex = assertThrows(IllegalArgumentException.class, () -> {
        var alg = new KleitmanWangDigraphRealization(degrees, degrees);
            alg.getDigraph();
        });
        assertNotNull(ex);
        assertFalse(DigraphRealizationAlgorithm.checkFulkersonChenAnsteeCondition(degrees, degrees));
    }

}
