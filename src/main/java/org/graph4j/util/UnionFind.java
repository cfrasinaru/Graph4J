/*
 * Copyright (C) 2023 Cristian Frăsinaru and contributors
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
package org.graph4j.util;

import java.util.Arrays;

/**
 * A <em>union-find</em> data structure (also called <em>disjoint-set</em> or
 * <em>merge–find</em>) stores a collection of disjoint (non-overlapping) sets.
 * The elements of this data structure are graph vertex indices. Initially, all
 * the vertices are added as singletons (one element sets).
 *
 *
 * <p>
 * The time complexity of the {@link #union(int, int) } operation is
 * <em>O(1)</em>, while {@link #find(int)} has a complexity <em>O(a(n))</em>
 * where <em>a</em> is is the inverse Ackermann function. The inverse Ackermann
 * function grows extremely slow, so this factor is 4 or less for practical
 * values of n.
 *
 * @author Cristian Frăsinaru
 */
public class UnionFind {

    private final int numVertices;
    private final int[] parent;
    private int numSets;

    /**
     * Creates a union-find data structures having <code>numVertices</code>
     * singleton sets, each containing one vertex index, from <code>0</code> to
     * <code>numVertices-1</code>.
     *
     * @param numVertices the number of vertices in the graph.
     */
    public UnionFind(int numVertices) {
        this.numVertices = numVertices;
        this.numSets = numVertices;
        parent = new int[numVertices];
        Arrays.fill(parent, -1);
    }

    /**
     * Finds the root of the set containing the given vertex index. The root of
     * a set S is the vertex index r such parent[vi]=r for all vi in S. The
     * method performs also path compression.
     *
     * @param vi a vertex index.
     * @return the root of the set containing vi.
     */
    public int find(int vi) {
        if (vi < 0 || vi >= numVertices) {
            throw new IllegalArgumentException("Invalid vertex index: " + vi);
        }
        //finding root
        int root = vi;
        while (parent[root] >= 0) {
            root = parent[root];
        }
        //path compression
        int j = vi;
        while (parent[j] >= 0) {
            int temp = parent[j];
            parent[j] = root;
            j = temp;
        }
        return root;
    }

    /**
     * Performs the union of the two sets represented by root1 and root2. The
     * new set is represented by the root of the set containing more elements.
     *
     * @param root1 the root of the first set.
     * @param root2 the root of the second set.
     */
    public void union(int root1, int root2) {
        //the total number of vertices in the new set
        int t = parent[root1] + parent[root2];
        if (-parent[root1] >= -parent[root2]) {
            //the new root is the first set root
            parent[root1] = t;
            parent[root2] = root1;
        } else {
            //the new root is the second set root
            parent[root2] = t;
            parent[root1] = root2;
        }
        numSets--;
    }

    /**
     * Returns the number of disjoint sets in the data structure.
     *
     * @return the number of sets.
     */
    public int numSets() {
        return numSets;
    }
}
