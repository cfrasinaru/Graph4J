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

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.alg.clique.MaximalCliqueFinder;
import org.graph4j.generators.GraphGenerator;
import org.graph4j.generators.RandomGnpGraphGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class MaximalCliqueTest {

    @Test
    public void random() {
        int n = 20;
        var g = new RandomGnpGraphGenerator(n, Math.random()).createGraph();
        var alg = new MaximalCliqueFinder(g);
        var q = alg.getMaximalClique();
        assertTrue(q.isValid());
    }

    @Test
    public void bipartite() {
        var g = GraphGenerator.completeBipartite(5, 6);
        var alg = new MaximalCliqueFinder(g);
        var q = alg.getMaximalClique();
        assertTrue(q.isValid() && q.size() == 2);
    }
    
    @Test
    public void complete() {
        int n = 10;
        var g = GraphGenerator.complete(n);
        var alg = new MaximalCliqueFinder(g);
        var opt = alg.findMaximumClique(0);
        assertEquals(n, opt.size());
    }
    
}
