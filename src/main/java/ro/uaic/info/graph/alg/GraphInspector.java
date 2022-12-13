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
package ro.uaic.info.graph.alg;

import ro.uaic.info.graph.Graph;

/**
 *
 * @author Cristian Frăsinaru
 */
public class GraphInspector {

    private final Graph graph;

    public GraphInspector(Graph graph) {
        this.graph = graph;
    }

    /**
     * 
     * @return
     */
    public int minDegreeVertex() {
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
     * @return
     */
    public int maxDegreeVertex() {
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

}
