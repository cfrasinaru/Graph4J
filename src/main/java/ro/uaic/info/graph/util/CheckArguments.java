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
package ro.uaic.info.graph.util;

import ro.uaic.info.graph.Graph;

/**
 *
 * @author Cristian Frăsinaru
 */
public class CheckArguments {

    public static void notNull(Object obj) {
        notNull(obj, "The argument is null");
    }

    public static void notNull(Object obj, String msg) {
        if (obj == null) {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     *
     * @param n
     */
    public static void numberOfVertices(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number of vertices must be non-negative: " + n);
        }
    }

    /**
     *
     * @param first
     * @param last
     */
    public static void vertexRange(int first, int last) {
        if (first < 0) {
            throw new IllegalArgumentException("Vertex numbers must be non-negative: " + first);
        }
        if (last < 0) {
            throw new IllegalArgumentException("Vertex numbers must be non-negative: " + last);
        }
        if (first > last) {
            throw new IllegalArgumentException("Incorrect vertex range: [" + first + "," + last + "]");
        }
    }

    /**
     *
     * @param m
     */
    public static void numberOfEdges(int m) {
        if (m < 0) {
            throw new IllegalArgumentException("Number of edges must be non-negative: " + m);
        }
    }

    /**
     *
     * @param p
     */
    public static void probability(double p) {
        if (p < 0 || p > 1) {
            throw new IllegalArgumentException("Probability must be in the range [0,1]: " + p);
        }
    }

    /**
     *
     * @param g1
     * @param g2
     */
    public static void disjointVertices(Graph g1, Graph g2) {
        if (Tools.arrayIntersects(g1.vertices(), g2.vertices())) {
            throw new IllegalArgumentException("Graphs must have disjoint vertex sets");
        }
    }
}
