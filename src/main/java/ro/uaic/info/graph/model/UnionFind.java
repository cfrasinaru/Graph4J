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
package ro.uaic.info.graph.model;

import java.util.Arrays;

/**
 * The elements of this data structure are graph vertex indices.
 *
 * @author Cristian Frăsinaru
 */
public class UnionFind {

    private final int numVertices;
    private final int[] parent;
    private int numSets;

    /**
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
     *
     * @return the number of sets.
     */
    public int numSets() {
        return numSets;
    }
}
