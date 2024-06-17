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
package org.graph4j.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.GraphUtils;
import org.graph4j.support.ChordalGraphSupport;
import org.graph4j.util.IntArrays;

/**
 * Generates a random chordal graph. A <em>chordal</em> graph is a graph where
 * every cycle of four or more vertices has a chord. A <em>chord</em> is an edge
 * that connects two non-consecutive vertices within the cycle, but it's not
 * part of the cycle itself.
 *
 *
 * Reference: Oylum Seeker, Pinar Heggernes, Tinaz Ekim, and Z. Caner Taskin,
 * (2022). Generation of random chordal graphs using subtrees of a tree. RAIRO -
 * Operations Research. 56. 10.1051/ro/2022027.
 *
 * @see ChordalGraphSupport
 * @author Cristian Frăsinaru
 */
public class RandomChordalGraphGenerator extends AbstractGraphGenerator {

    private final int maxSubtreeSize;

    /**
     * Creates a generator for random chordal graphs with the specified number
     * of vertices.
     *
     * @param numVertices the number of vertices of the generated graph.
     */
    public RandomChordalGraphGenerator(int numVertices) {
        this(numVertices, 1 + new Random().nextInt(numVertices / 4));
    }

    /**
     * Creates a generator for random chordal graphs with the specified number
     * of vertices, with the possibility to adjust the density of the generated
     * graph. The smaller the value for {@code maxSubtreeSize}, the lower the
     * density of the graph.
     *
     * @param numVertices the number of vertices of the generated graph.
     * @param maxSubtreeSize the maximum size of the subtrees that are used for
     * the computation of the intersection graph.
     */
    public RandomChordalGraphGenerator(int numVertices, int maxSubtreeSize) {
        super(numVertices);
        if (maxSubtreeSize < 1 || maxSubtreeSize >= numVertices) {
            throw new IllegalArgumentException("Invalid maximm subtree size: " + maxSubtreeSize);
        }
        this.maxSubtreeSize = maxSubtreeSize;
    }

    /**
     * Creates a random chordal graph.
     *
     * @return a random chordal graph.
     */
    public Graph create() {
        //create a random tree
        //create n distinct subtrees
        //create the intersection graph of the subtrees
        int n = vertices.length;
        var tree = GraphGenerator.randomTree(n);
        List<Graph> subtrees = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            subtrees.add(createRandomSubtree(tree));
        }
        return GraphUtils.createIntersectionGraph(subtrees, false);
    }

    private Graph createRandomSubtree(Graph tree) {
        int n = tree.numVertices();
        int[][] neighbors = new int[n][];
        int[] maxNeighborPos = new int[n];
        for (int i = 0; i < n; i++) {
            neighbors[i] = IntArrays.copyOf(tree.neighbors(tree.vertexAt(i)));
            maxNeighborPos[i] = neighbors[i].length - 1;
        }
        //
        var rnd = new Random();
        int k = 1 + rnd.nextInt(maxSubtreeSize); //num of vertices in the subtree
        var subtree = GraphBuilder.empty().estimatedNumVertices(k).buildGraph();
        subtree.setSafeMode(false);
        int x = tree.vertexAt(rnd.nextInt(n)); //the starting point
        subtree.addVertex(x);
        int[] candidates = new int[n];
        candidates[0] = x;
        int candSize = 1;
        for (int j = 0; j < k; j++) {
            //pick a vertex of the subtree which still has neighbors in the tree
            int vertexPos = rnd.nextInt(candSize);
            int v = candidates[vertexPos];
            int vi = tree.indexOf(v);
            int neighborPos = rnd.nextInt(maxNeighborPos[vi] + 1);
            int u = neighbors[vi][neighborPos];
            neighbors[vi][neighborPos] = neighbors[vi][maxNeighborPos[vi]--];
            if (maxNeighborPos[vi] < 0) {
                candidates[vertexPos] = candidates[candSize - 1];
                candSize--;
            }
            if (subtree.containsVertex(u)) {
                continue;
            }
            subtree.addVertex(u);
            candidates[candSize++] = u;
            subtree.addEdge(v, u);
        }
        return subtree;
    }

}
