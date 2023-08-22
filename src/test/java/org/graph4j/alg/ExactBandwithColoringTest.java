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
package org.graph4j.alg;

import org.graph4j.alg.coloring.bw.BacktrackBandwithColoring;
import org.graph4j.alg.coloring.bw.GurobiBandwithColoring;
import org.graph4j.alg.coloring.bw.GurobiOptBandwithColoring;
import org.graph4j.generate.EdgeWeightsGenerator;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.generate.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class ExactBandwithColoringTest {

    public ExactBandwithColoringTest() {
    }

    @Test
    public void cross() {
        int n = 10;
        var g = GraphGenerator.randomGnp(n, 0.1);
        EdgeWeightsGenerator.randomIntegers(g, 1, 5);

        var alg1 = new BacktrackBandwithColoring(g);
        var col1 = alg1.findColoring();
        assertTrue(alg1.isValid(col1));

        var alg2 = new GurobiOptBandwithColoring(g);
        var col2 = alg2.findColoring();
        assertTrue(alg2.isValid(col2));

        assertEquals(col1.maxColorNumber(), col2.maxColorNumber());
    }

    /*
    @Test
    public void testMany() {
        int n = 20;
        for (int i = 0; i < 10; i++) {
            var g = GraphGenerator.randomGnp(n, 0.1);
            EdgeWeightsGenerator.randomIntegers(g, 1, 5);
            var alg1 = new BacktrackBandwithColoring(g);
            var col1 = alg1.findColoring();

            var alg2 = new GurobiBandwithColoring(g);
            var col2 = alg2.findColoring();

            assert col1.maxColorNumber()== col2.maxColorNumber();
        }
    }*/
    
}
