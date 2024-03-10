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
package org.graph4j.iso;

import org.junit.jupiter.api.Test;
import org.graph4j.GraphBuilder;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author Cristian Frăsinaru
 */
public class TreeIsomorphismTest {

    public TreeIsomorphismTest() {
    }

    @Test
    public void testEmptyGraph() {
        var empty = GraphBuilder.empty().buildGraph();
        var ex = assertThrows(IllegalArgumentException.class, () -> {
            var alg = new NotRootedTreeIsomorphism(empty, empty);
        });
        assertNotNull(ex);
    }

    @Test
    public void testSingleVertex() {
        var tree1 = GraphBuilder.vertices(1).buildGraph();
        var tree2 = GraphBuilder.vertices(2).buildGraph();
        var alg = new NotRootedTreeIsomorphism(tree1, tree2);
        assertTrue(alg.areIsomorphic());
        IsomorphicGraphMapping mapping = alg.getMapping();
        assertTrue(mapping.isValidIsomorphism());
    }

    @Test
    public void testSingleEdge() {
        var tree1 = GraphBuilder.vertices(1, 2).addEdge(1, 2).buildGraph();
        var tree2 = GraphBuilder.vertices(3, 4).addEdge(3, 4).buildGraph();
        var alg = new NotRootedTreeIsomorphism(tree1, tree2);
        assertTrue(alg.areIsomorphic());
        IsomorphicGraphMapping mapping = alg.getMapping();
        assertTrue(mapping.isValidIsomorphism());
    }

}
