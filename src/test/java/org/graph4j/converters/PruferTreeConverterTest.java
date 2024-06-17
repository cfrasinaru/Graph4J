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
package org.graph4j.converters;

import org.graph4j.GraphBuilder;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Cristian Frăsinaru
 */
public class PruferTreeConverterTest {

    @Test
    public void encodeSimple1() {
        //4445, wikipedia
        var g = GraphBuilder.edges("1-4,2-4,3-4,4-5,5-6").buildGraph(); 
        var alg = new PruferTreeEncoder(g);
        assertArrayEquals(new int[]{4, 4, 4, 5}, alg.createSequence());
    }

    @Test
    public void encodeSimple2() {
        //240133, article
        var g = GraphBuilder.edges("0-1,0-4,1-3,2-4,2-5,3-6,3-7").buildGraph(); 
        var alg = new PruferTreeEncoder(g);
        assertArrayEquals(new int[]{2, 4, 0, 1, 3, 3}, alg.createSequence());
    }

}
