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
package org.graph4j.spanning;

import org.graph4j.Graph;
import org.graph4j.GraphUtils;
import org.graph4j.generators.EdgeWeightsGenerator;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.graph4j.generators.GraphGenerator;
import org.graph4j.generators.RandomForestGenerator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class SpanningTreeIteratorTest {

    private int countSpanningTrees(Graph g) {
        var it = new SpanningTreeIterator(g);
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        return count;
    }

    @Test
    public void complete() {
        int n = 4;
        var g = GraphGenerator.complete(n);
        assertEquals(Math.pow(n, n - 2), countSpanningTrees(g));
    }

    @Test
    public void completeBipartite() {
        int n1 = 4, n2 = 5;
        var g = GraphGenerator.completeBipartite(n1, n2);
        assertEquals(Math.pow(n1, n2 - 1) * Math.pow(n2, n1 - 1), countSpanningTrees(g));
    }

    @Test
    public void cycle() {
        int n = 10;
        var g = GraphGenerator.cycle(n);
        assertEquals(n, countSpanningTrees(g));
    }

    @Test
    public void tree() {
        int n = 10;
        var g = GraphGenerator.randomTree(n);
        assertEquals(1, countSpanningTrees(g));
    }

    @Test
    public void disconnected() {
        int n = 10;
        var g = new RandomForestGenerator(n, 2).createForest();
        var it = new SpanningTreeIterator(g);
        assertFalse(it.hasNext());
    }

    @Test
    public void weightedTest() {
        int n = 5;
        var g = GraphGenerator.complete(n);
        EdgeWeightsGenerator.randomIntegers(g, 0, 9);
        double minWeight = MinimumSpanningTreeAlgorithm.getInstance(g).getWeight();
        var it = new WeightedSpanningTreeIterator(g);
        double prevWeight = Double.NEGATIVE_INFINITY;
        int count = 0;
        while (it.hasNext()) {
            double weight = GraphUtils.computeWeight(g, it.next());
            if (count++ == 0) {
                assertEquals(minWeight, weight);
            }
            assertTrue(weight >= prevWeight);
            prevWeight = weight;
        }
        //assertEquals(minWeight, );
    }

}
