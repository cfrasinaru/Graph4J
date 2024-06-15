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

<<<<<<< Updated upstream
import org.junit.Test;
=======
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
>>>>>>> Stashed changes
import org.graph4j.GraphBuilder;
import org.graph4j.generate.GraphGenerator;
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
    }

    @Test
    public void testSingleEdge() {
        var tree1 = GraphBuilder.vertices(1, 2).addEdge(1, 2).buildGraph();
        var tree2 = GraphBuilder.vertices(3, 4).addEdge(4, 3).buildGraph();
        var alg = new NotRootedTreeIsomorphism(tree1, tree2);
        assertTrue(alg.areIsomorphic());
    }

    @Test
    public void testSimple() {
        //https://www.baeldung.com/cs/isomorphic-trees
        var tree1 = GraphBuilder.edges("a-b,a-c,a-d,b-e,d-f,d-g").buildGraph();
        var tree2 = GraphBuilder.edges("1-2,1-3,1-4,3-5,3-6,5-7").buildGraph();
        var alg = new NotRootedTreeIsomorphism(tree1, tree2);
        assertTrue(alg.areIsomorphic());
    }

    @Test
    public void testLarge() {
        int n = 1000;
        int k = 1 + new Random().nextInt(n - 1);
        var tree1 = GraphGenerator.randomTree(n);
        var tree2 = GraphBuilder.numVertices(n).buildGraph();
        List<Integer> list1 = IntStream.range(0, n).boxed().collect(Collectors.toList());
        List<Integer> list2 = new ArrayList<>(list1);
        Collections.rotate(list2, k); //permutation of list1
        for (var e : tree1.edges()) {
            int v2 = list2.get(e.source());
            int u2 = list2.get(e.target());
            tree2.addEdge(v2, u2);
        }
        var alg = new NotRootedTreeIsomorphism(tree1, tree2);
        assertTrue(alg.areIsomorphic());
        IsomorphicGraphMapping mapping = alg.getMapping();
        assertTrue(mapping.isValidIsomorphism());
    }

}
