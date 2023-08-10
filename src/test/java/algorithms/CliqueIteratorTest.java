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

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.graph4j.alg.clique.BFSCliqueIterator;
import org.graph4j.alg.clique.DFSCliqueIterator;
import org.graph4j.generate.GraphGenerator;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Cristian Frăsinaru
 */
public class CliqueIteratorTest {

    public CliqueIteratorTest() {
    }

    @Test
    public void empty() {
        var g = GraphGenerator.empty(5);
        //each vertex is a clique
        assertEquals(5, new DFSCliqueIterator(g).getAll().size());
    }

    @Test
    public void complete() {
        var g = GraphGenerator.complete(5);
        //all subsets, except the emptyset
        assertEquals(31, new DFSCliqueIterator(g).getAll().size());
    }

    @Test
    public void cycle() {
        var g = GraphGenerator.cycle(5);
        //all vertices and all edges
        assertEquals(10, new DFSCliqueIterator(g).getAll().size());
    }

    @Test
    public void cross() {
        var g = GraphGenerator.randomGnp(10, Math.random());
        int count1 = new DFSCliqueIterator(g).getAll().size();
        int count2 = new BFSCliqueIterator(g).getAll().size();
        assertTrue(count1 == count2);
    }
}
