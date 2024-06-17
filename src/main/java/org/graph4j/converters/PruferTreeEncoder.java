/*
 * Copyright (C) 2024 Cristian Frăsinaru and contributors
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
package org.graph4j.converters;

import org.graph4j.Graph;
import org.graph4j.SimpleGraphAlgorithm;
import org.graph4j.exceptions.NotATreeException;
import org.graph4j.util.IntArrays;

/**
 * Encodes a tree to its Prufer sequence. The time complexity is O(|V|).
 *
 * See: "X. Wang, L. Wang and Y. Wu, "An Optimal Algorithm for Prufer Codes,"
 * Journal of Software Engineering and Applications, Vol. 2 No. 2, 2009, pp.
 * 111-115. doi: 10.4236/jsea.2009.22016."
 *
 * @author Cristian Frăsinaru
 */
public class PruferTreeEncoder extends SimpleGraphAlgorithm {

    private int[] vertices;
    private int[] degrees;
    private boolean[] visited;
    private boolean orderedVertices;

    /**
     * Creates a new encoder. The constructor does not verify if the input
     * graph is actually a tree.
     *
     * @param tree the input tree.
     */
    public PruferTreeEncoder(Graph tree) {
        super(tree);
        if (tree.numVertices() < 2) {
            throw new IllegalArgumentException("The tree should have at least 2 vertices");
        }
    }

    /**
     * Encodes a tree to its Prufer sequence.
     *
     * @return the Prufer sequence of the tree.
     * @throws NotATreeException if the graph is not a tree.
     */
    public int[] createSequence() {
        int n = graph.numVertices();
        this.vertices = graph.vertices();
        this.degrees = graph.degrees();
        this.visited = new boolean[n];
        this.orderedVertices = graph.isDefaultVertexNumbering()
                || IntArrays.isSortedAscending(vertices);

        int[] pruferCode = new int[n - 2];
        int x = findMinLeaf(0);
        int index = graph.indexOf(x);
        visited[index] = true;
        for (int i = 0; i < n - 2; i++) {
            int y = findParent(x);
            int yi = graph.indexOf(y);
            pruferCode[i] = y;
            degrees[yi]--;
            if (yi < index && degrees[yi] == 1) {
                x = y;
                visited[graph.indexOf(x)] = true;
            } else {
                x = findMinLeaf(index + 1);
                index = graph.indexOf(x);
                visited[index] = true;
            }
        }
        return pruferCode;
    }

    private int findParent(int x) {
        assert degrees[graph.indexOf(x)] == 1;
        for (var it = graph.neighborIterator(x); it.hasNext();) {
            int y = it.next();
            if (!visited[graph.indexOf(y)]) {
                return y;
            }
        }
        return -1;
    }

    private int findMinLeaf(int fromPos) {
        int minLeaf = Integer.MAX_VALUE;
        for (int i = fromPos, n = vertices.length; i < n; i++) {
            int v = vertices[i];
            if (degrees[i] == 1) {
                if (orderedVertices) {
                    return v;
                }
                if (v < minLeaf) {
                    minLeaf = v;
                }
            }
        }
        if (minLeaf == Integer.MAX_VALUE) {
            throw new NotATreeException();
        }
        return minLeaf;
    }

}
