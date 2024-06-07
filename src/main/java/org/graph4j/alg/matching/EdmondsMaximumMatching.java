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
package org.graph4j.alg.matching;

import org.graph4j.Graph;
import org.graph4j.alg.SimpleGraphAlgorithm;
import org.graph4j.util.Matching;

import java.util.Arrays;

/**
 * An implementation of Edmonds' maximum matching Blossom algorithm, as described by Gabow in his 1976 paper
 * `An implementation of Edmonds' algorithm for maximum matching`. The original implementation of Edmonds had a
 * computation time of O(n^4), while this algorithm has a computation time proportional to O(n^3), with the actual upper
 * bound being O(mn * a(m, n)), where a(m, n) is the inverse of the Ackermann function. Its key being that, as opposed
 * to Edmonds implementation, it doesn't actually shrink blossoms, it instead uses some clever data structures which
 * are used for finding alternating paths.
 *
 * @author Alexandru Mitreanu
 * @author Alina Căprioară
 */
public class EdmondsMaximumMatching extends SimpleGraphAlgorithm implements MatchingAlgorithm {
    // we store n for faster memory access (faster than a method call)
    private final int n; // number of vertices
    // label[i] can be 4 things:
    // - -1                           - non-outer
    // - 0                            - start label
    // - [1, n]                       - vertex label
    // - y << 32 + x, for an edge xy  - edge label
    private final long[] label;
    // ij in matching <=> mate[i] = j and mate[j] = i
    private final int[] mate;

    // union-find data structure used for faster traversal of non-outer nodes
    // most of the time parent[i] = first[i] (i.e. the first non-outer node on path P(i))
    // except for the case when we merge two trees, which takes O(1)
    private final int[] parent;

    // queue used for the search, we use our own implementation because this queue also stores the outer vertices in the
    // current search, which we use in the label method and when resetting the search
    int[] q;
    int qFirst, qLast;

    /**
     * Get an Edmonds' Maximum Matching algorithm instance.
     *
     * @param graph the input graph
     * @author Alexandru Mitreanu
     * @author Alina Căprioară
     */
    public EdmondsMaximumMatching(Graph graph) {
        super(graph);

        n = graph.numVertices();
        label = new long[n + 1];
        mate = new int[n + 1];
        q = new int[n];
        qFirst = qLast = 0;

        parent = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            parent[i] = i;
        }
    }

    // finds first[x]
    private int find(int x) {
        if (parent[x] == x) {
            return x;
        }

        parent[x] = find(parent[x]);
        return parent[x];
    }

    // does first[x] = y, where:
    // x is an outer vertex which gets added to the subtree of y
    // y is always a non-outer vertex which should become a root in the union find data structure
    private void union(int x, int y) {
        parent[y] = y;

        // if they have the same parent, nothing must be done
        if (x == y) {
            return;
        }

        // pick the root of the new tree as the vertex with the highest rank
        parent[x] = y;
    }

    // recursively augment the path P(x)
    private void augment(int x, int y) {
        // match x to y (y is assumed to have been matched to x by the caller of this method)
        int t = mate[x];
        mate[x] = y;

        if (mate[t] != x) {
            return;
        }

        // if x has a vertex label, we can continue on the path using mate and label arrays
        if (1 <= label[x] && label[x] <= n) {
            mate[t] = (int) label[x]; // match t to label[x]
            augment((int) label[x], t); // match label[x] to t and the rest of the path to starting vertex
            return;
        }

        // else x must have an edge label, so we retrieve the vertices forming the said edge and continue augmenting the
        // paths "away" from it
        int v = (int) (label[x] & 0xFFFFFFFFL), w = (int) (label[x] >> 32);
        augment(v, w);
        augment(w, v);
    }

    // label non-outer vertices in paths P(x) and P(y)
    //
    // this procedure works by relabeling each non-outer vertex on P(x) and P(y) with an edge label xy, which helps
    // the algorithm in finding an augmenting path later
    // besides that, all outer vertices that have their first[i] a relabeled vertex, must update their first, which
    // is aided by a union-find data structure, which speeds up the process immensely, being a key of the efficiency
    // of our implementation
    private void label(int x, int y) {
        long edgeLabel = (long) x + ((long) y << 32); // retrieve the label of the edge
        int r = find(x);
        int s = find(y);
        // this will be the index of the first non-outer vertex both on P(x) and P(y) (variable will also be used as an
        // aux for swap)
        int join = 0;

        // if they have the same non-outer vertex as the first on their paths to the start, there are no new vertices
        // that we can label
        if (r == s) {
            return;
        }

        // flag r and s
        label[r] = -edgeLabel;
        label[s] = -edgeLabel;

        // alternatively flag the non-outer vertices on the paths P(x) and P(y) until we reach the common root which we
        // will store in join
        while (s != 0) {
            join = r;
            r = s;
            s = join;

            r = find((int) label[mate[r]]);

            // if we find a vertex that is labeled, it means we found the root
            if (label[r] == -edgeLabel) {
                join = r;
                break;
            }

            label[r] = -edgeLabel;
        }

        // mark all non-outer vertices on P(x) and P(y) (excluding join) with an edge label
        // also, at the same time, all outer vertices are marked with their new first as join when we do the union
        // use r as the iterator
        r = find(x);
        while (r != join) {
            label[r] = edgeLabel;
            union(r, join);
            q[qLast++] = r;
            r = find((int) label[mate[r]]);
        }

        r = find(y);
        while (r != join) {
            label[r] = edgeLabel;
            union(r, join);
            q[qLast++] = r;
            r = find((int) label[mate[r]]);
        }
    }

    /**
     * Using a BFS search strategy, the algorithm tries to expand the current matching by 1 in a number of n/2
     * iterations, each with a time complexity of O(ma(m, n))
     *
     * @return the maximum possible matching in the given graph
     * @author Alexandru Mitreanu
     * @author Alina Căprioară
     */
    @Override
    public Matching getMatching() {
        int u = 1;
        int x; // current vertex of search
        int v; // temporary variable

        // initialize data structures
        for (int i = 0; i <= n; i++) {
            label[i] = -1;
            mate[i] = 0;
        }
        qFirst = qLast = 0;

        while (u <= n) {
            // skip matched vertices
            if (mate[u] != 0) {
                u++;
                continue;
            }

            // start the search from unmatched vertex u
            label[u] = 0;
            union(u, 0);
            q[qLast++] = u;

            // at this stage, we begin the search in a BFS manner, storing a queue of outer edges (label[i] >= 0)
            // which we use to examine edges xy in which x is taken from the queue and is an outer edge
            //
            // the queue helps us later when resetting the search, because we can reset exactly the outer vertices
            // that we found so far in our current iteration
            search:
            while (qFirst < qLast) {
                x = q[qFirst++];

                // walk through its edge list
                for (int y : graph.neighbors(graph.vertexAt(x - 1))) {
                    y = graph.indexOf(y) + 1;

                    // if we find an edge that is unmatched, it means we can augment the path and stop the current search
                    if (mate[y] == 0 && y != u) {
                        mate[y] = x;
                        augment(x, y);
                        break search;
                    }

                    // if y is outer it means we found two paths P(x), P(y) that can be joined (thus forming a blossom
                    // or joining to outer paths which will form an augmenting path)
                    if (label[y] >= 0) {
                        label(x, y);
                        continue;
                    }

                    v = mate[y];

                    // if mate of y is non-outer, it means we can extend the path P(x) with edge (y, mate[y])
                    if (label[v] < 0) {
                        label[v] = x;
                        union(v, y);
                        q[qLast++] = v;
                    }

                    // else the edge doesn't contribute with anything to the current search, so it can be skipped
                }
            }

            // prepare the data structures for the next search
            //
            // here the queue comes in handy because it has stored all the outer nodes found in the current search
            // their reset being as easy as walking through the queue, which gives a better performance than actually
            // iterating through the whole node list
            label[0] = -1;
            for (int i = 0, qi; i < qLast; i++) {
                qi = q[i];
                label[qi] = label[mate[qi]] = -1;
                union(qi, qi);
                union(mate[qi], mate[qi]);
            }
            qFirst = qLast = 0;

            u++;
        }

        // matching is found, build it from mate array
        Matching m = new Matching(graph);
        for (int i = 1; i <= n; i++) {
            if (mate[i] != 0) {
                m.add(graph.vertexAt(i - 1), graph.vertexAt(mate[i] - 1));
            }
        }

        return m;
    }
}
