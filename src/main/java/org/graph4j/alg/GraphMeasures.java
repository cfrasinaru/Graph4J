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
package org.graph4j.alg;

import java.util.stream.IntStream;
import org.graph4j.Graph;

/**
 * Various <i>sizes</i> related to a graph.
 *
 * WARNING: To be rewritten.
 *
 * @author Cristian Frăsinaru
 */
public class GraphMeasures {

    /**
     *
     * @param graph the input graph.
     * @return the minimum degree of the vertices.
     */
    public static int minDegree(Graph graph) {
        return graph.degree(minDegreeVertex(graph));
    }

    /**
     *
     * @param graph the input graph.
     * @return a vertex number of minimum degree.
     */
    public static int minDegreeVertex(Graph graph) {
        int minVertex = -1;
        int minDeg = Integer.MAX_VALUE;
        for (int v : graph.vertices()) {
            int deg = graph.degree(v);
            if (deg < minDeg) {
                minDeg = deg;
                minVertex = v;
            }
        }
        return minVertex;
    }

    /**
     *
     * @param graph the input graph.
     * @return the maximum degree of the vertices.
     */
    public static int maxDegree(Graph graph) {
        return graph.degree(maxDegreeVertex(graph));
    }

    /**
     *
     * @param graph the input graph.
     * @return a vertex number of maximum degree.
     */
    public static int maxDegreeVertex(Graph graph) {
        int maxVertex = -1;
        int maxDeg = -1;
        for (int v : graph.vertices()) {
            int deg = graph.degree(v);
            if (deg > maxDeg) {
                maxDeg = deg;
                maxVertex = v;
            }
        }
        return maxVertex;
    }

    /**
     *
     * @param graph the input graph.
     * @return the average degree of the vertices.
     */
    public double avgDegree(Graph graph) {
        return IntStream.of(graph.vertices())
                .map(v -> graph.degree(v)).average().orElse(0);
    }
}
