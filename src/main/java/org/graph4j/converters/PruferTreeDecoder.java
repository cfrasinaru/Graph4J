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

import java.util.Arrays;
import java.util.Objects;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;

/**
 * Decodes a Prufer sequence to a tree. The sequence must contain values from 0
 * to the length of the sequence plus 2, representing the vertex numbers of the
 * resulting tree.
 *
 * The time complexity is O(|V|).
 *
 * See: "X. Wang, L. Wang and Y. Wu, "An Optimal Algorithm for Prufer Codes,"
 * Journal of Software Engineering and Applications, Vol. 2 No. 2, 2009, pp.
 * 111-115. doi: 10.4236/jsea.2009.22016."
 *
 * @author Cristian Frăsinaru
 */
public class PruferTreeDecoder {

    private final int[] pruferCode;
    private int[] degrees;

    /**
     * Creates a new decoder.
     *
     * @param pruferCode the Prufer sequence.
     */
    public PruferTreeDecoder(int[] pruferCode) {
        Objects.requireNonNull(pruferCode);
        int n = pruferCode.length + 2;
        for (int i = 0; i < n - 2; i++) {
            if (pruferCode[i] < 0 || pruferCode[i] >= n) {
                throw new IllegalArgumentException("Invalid prufer sequence: " + pruferCode[i]);
            }
        }
        this.pruferCode = pruferCode;
    }

    /**
     * Decodes a Prufer sequence to a tree.
     *
     * @return the tree corresponding to the Prufer sequence.
     */
    public Graph createTree() {
        int n = pruferCode.length + 2;
        Graph tree = GraphBuilder.numVertices(n)
                .estimatedNumEdges(n - 1).buildGraph();
        tree.setSafeMode(false);
        if (n == 2) {
            tree.addEdge(0, 1);
            return tree;
        }
        this.degrees = new int[n];
        Arrays.fill(degrees, 1);
        for (int i = 0; i < n - 2; i++) {
            degrees[pruferCode[i]]++;
        }
        int x = findMinLeaf(0);
        int index = x;
        for (int i = 0; i < n - 2; i++) {
            int y = pruferCode[i];
            tree.addEdge(x, y);
            degrees[x]--;
            degrees[y]--;
            if (y < index && degrees[y] == 1) {
                x = y;
            } else {
                index = x = findMinLeaf(index + 1);
            }
        }
        // two nodes with degree 1 will remain
        int v = -1, u = -1;
        for (int k = 0; k < n; k++) {
            if (degrees[k] == 1) {
                if (v == -1) {
                    v = k;
                } else {
                    u = k;
                    break;
                }
            }
        }
        tree.addEdge(v, u);
        tree.setSafeMode(true);
        return tree;
    }

    private int findMinLeaf(int fromPos) {
        for (int k = fromPos, n = degrees.length; k < n; k++) {
            if (degrees[k] == 1) {
                return k;
            }
        }
        throw new RuntimeException();
    }

}
