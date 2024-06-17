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
package org.graph4j.sp;

import java.util.Random;
import org.graph4j.Digraph;
import org.graph4j.alg.sp.BFSSingleSourceShortestPath;
import org.graph4j.alg.sp.DijkstraShortestPathHeap;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.generators.RandomGnpGraphGenerator;
import org.graph4j.util.Path;

/**
 *
 * @author Cristian Frăsinaru
 */
public class BFSSingleSourceTest {

    public BFSSingleSourceTest() {
    }

    @Test
    public void cross() {
        Random random = new Random();
        int n = 20;
        for (int i = 0; i < 10; i++) {
            Digraph g = new RandomGnpGraphGenerator(n, Math.random()).createDigraph();
            //unweighted
            int v = random.nextInt(n);
            int u = random.nextInt(n);
            var alg1 = new DijkstraShortestPathHeap(g, v);
            var alg2 = new BFSSingleSourceShortestPath(g, v);
            double x1 = alg1.getPathWeight(u);
            double x2 = alg2.getPathWeight(u);
            assertEquals(x1, x2);

            Path p1 = alg1.findPath(u);
            Path p2 = alg2.findPath(u);
            assertTrue((p1 == null && p2 == null) || (p1 != null && p2 != null && p1.length() == p2.length()));
        }
    }

}
