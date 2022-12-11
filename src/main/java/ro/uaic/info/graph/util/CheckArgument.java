/*
 * Copyright (C) 2022 Faculty of Computer Science Iasi, Romania
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
public class CheckArgument {

    public static void numberOfVertices(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number of vertices must be non-negative: " + n);
        }
    }

    public static void numberOfEdges(int m) {
        if (m < 0) {
            throw new IllegalArgumentException("Number of edges must be non-negative: " + m);
        }
    }

    public static void probability(double p) {
        if (p < 0 || p > 1) {
            throw new IllegalArgumentException("Probability must be in the range [0,1]: " + p);
        }
    }

    public static void disjointVertices(Graph g1, Graph g2) {
        if (Tools.arrayIntersects(g1.vertices(), g2.vertices())) {
            throw new IllegalArgumentException("Graphs must have disjoint vertex sets");
        }
    }
}
