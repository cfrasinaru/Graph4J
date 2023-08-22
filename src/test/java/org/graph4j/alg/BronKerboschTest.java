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

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.graph4j.GraphBuilder;
import org.graph4j.alg.clique.BronKerboschCliqueIterator;
import org.graph4j.generate.GraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class BronKerboschTest {

    public BronKerboschTest() {
    }

    @Test
    public void simple() {
        var g = GraphBuilder.numVertices(5)
                .addEdges("0-1,0-2,1-2,0-3,0-4,3-4")
                .buildGraph();
        var alg = new BronKerboschCliqueIterator(g);
        int count = 0;
        while (alg.hasNext()) {
            alg.next();
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void complete() {
        var g = GraphGenerator.complete(5);
        var alg = new BronKerboschCliqueIterator(g);
        int count = 0;
        while (alg.hasNext()) {
            alg.next();
            count++;
        }
        assertEquals(1, count);
    }

    @Test
    public void cycle() {
        var g = GraphGenerator.cycle(5);
        var alg = new BronKerboschCliqueIterator(g);
        int count = 0;
        while (alg.hasNext()) {
            alg.next();
            count++;
        }
        assertEquals(5, count);
    }

    /*
    private void test() {
        int n = 50;
        double p = 0.1;
        for (int i = 0; i < 100; i++) {
            var g = new RandomGnpGraphGenerator(n, p).createGraph();
            int c1 = 0;
            var alg1 = new BronKerboschCliqueIterator(g);
            while (alg1.hasNext()) {
                alg1.next();
                c1++;
            }
            int c2 = 0;
            var jg = Converter.createJGraphT(g);
            var alg2 = new BronKerboschCliqueFinder(jg);
            Iterator<Set<Integer>> it = alg2.iterator();
            while (it.hasNext()) {
                it.next();
                c2++;
            }
            if (c1 != c2) {
                System.out.println("Ooops!");
            }
        }
    }
    */
}
