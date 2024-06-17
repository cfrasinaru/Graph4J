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
package org.graph4j.isomorphism;

import org.junit.jupiter.api.Test;
import org.graph4j.GraphBuilder;
import org.graph4j.GraphUtils;
import org.graph4j.generators.GraphGenerator;
import org.graph4j.util.IntArrays;
import org.graph4j.util.VertexSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        var empty1 = GraphBuilder.empty().buildGraph();
        var empty2 = GraphBuilder.empty().buildGraph();
        var alg = new TreeIsomorphism(empty1, empty2);
        assertTrue(alg.areIsomorphic());
    }

    @Test
    public void testSingleVertex() {
        var tree1 = GraphBuilder.vertices(1).buildGraph();
        var tree2 = GraphBuilder.vertices(2).buildGraph();
        var alg = new TreeIsomorphism(tree1, tree2);
        assertTrue(alg.areIsomorphic());
    }

    @Test
    public void testSingleEdge() {
        var tree1 = GraphBuilder.vertices(1, 2).addEdge(1, 2).buildGraph();
        var tree2 = GraphBuilder.vertices(3, 4).addEdge(4, 3).buildGraph();
        var alg = new TreeIsomorphism(tree1, tree2);
        assertTrue(alg.areIsomorphic());
    }

    @Test
    public void testSimple() {
        //https://www.baeldung.com/cs/isomorphic-trees
        var tree1 = GraphBuilder.edges("a-b,a-c,a-d,b-e,d-f,d-g").buildGraph();
        var tree2 = GraphBuilder.edges("1-2,1-3,1-4,3-5,3-6,5-7").buildGraph();
        var alg = new TreeIsomorphism(tree1, tree2);
        assertTrue(alg.areIsomorphic());
    }

    @Test
    public void testLarge() {
        int n = 1000;
        var tree1 = GraphGenerator.randomTree(n);
        var tree2 = GraphUtils.shuffle(tree1);
        var alg = new TreeIsomorphism(tree1, tree2);
        assertTrue(alg.areIsomorphic());
        Isomorphism iso = alg.findIsomorphism();
        assertTrue(iso.isValid());
    }
    
    @Test
    public void testDirectedGraph() {
        var empty = GraphBuilder.empty().buildDigraph();
        var ex = assertThrows(IllegalArgumentException.class, () -> {
            var alg = new TreeIsomorphism(empty, empty);
        });
        assertNotNull(ex);
    }

    @Test
    public void testRootedForests() {
        var forest1 = GraphBuilder.edges("0-1,2-3,2-4").buildGraph();
        var roots1 = new VertexSet(forest1, new int[]{0,2});
        var forest2 = GraphBuilder.edges("0-1,0-2,3-4").buildGraph();
        var roots2 = new VertexSet(forest2, new int[]{0,3});
        var alg = new RootedForestIsomorphism(forest1, forest2, roots1, roots2);
        assertTrue(alg.areIsomorphic());
        Isomorphism iso = alg.findIsomorphism();
        assertTrue(iso.isValid());
        assertEquals(3, iso.mapping(0));
        assertEquals(4, iso.mapping(1));
        assertEquals(0, iso.mapping(2));
        assertEquals(1, iso.mapping(3));
        assertEquals(2, iso.mapping(4));

        assertEquals(2, iso.inverse(0));
        assertEquals(3, iso.inverse(1));
        assertEquals(4, iso.inverse(2));
        assertEquals(0, iso.inverse(3));
        assertEquals(1, iso.inverse(4));
    }

    @Test
    public void testForest() {
        int n = 30;        
        var forest1 = GraphGenerator.randomForest(n);
        var forest2 = GraphUtils.shuffle(forest1);
        var alg = new ForestIsomorphism(forest1, forest2);
        assertTrue(alg.areIsomorphic());
        Isomorphism iso = alg.findIsomorphism();
        assertTrue(iso.isValid());
    }

    @Test
    public void testNotIsomorphicTrees() {
        //https://math.stackexchange.com/questions/2127524/two-non-isomorphic-trees
        var tree1 = GraphBuilder.edges("0-1,1-2,2-3,3-4,5-1").buildGraph();
        var tree2 = GraphBuilder.edges("0-1,1-2,2-3,3-4,5-2").buildGraph();
        assertTrue(IntArrays.haveSameValues(tree1.degrees(), tree2.degrees()));
        var alg = new TreeIsomorphism(tree1, tree2);
        assertFalse(alg.areIsomorphic());
    }
    
    @Test
    public void testNonIsomorphForests() {
        var forest1 = GraphBuilder.edges("0-1, 2-3,3-4, 5-6,6-7,7-9,8-9,10-6").buildGraph();
        var forest2 = GraphBuilder.edges("0-1, 2-3,3-4, 5-6,6-7,7-9,8-9,10-7").buildGraph();
        assertTrue(IntArrays.haveSameValues(forest1.degrees(), forest2.degrees()));
        var alg = new ForestIsomorphism(forest1, forest2);
        assertFalse(alg.areIsomorphic());
    }
    
}
