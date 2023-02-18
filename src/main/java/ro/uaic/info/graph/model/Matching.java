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

import ro.uaic.info.graph.Graph;

/**
 *
 * @author Cristian Frăsinaru
 */
public class Matching extends EdgeSet {

    public Matching(Graph graph) {
        super(graph);
    }

    public Matching(Graph graph, int initialCapacity) {
        super(graph, initialCapacity);
    }

    public boolean isValid() {
        int n = graph.numVertices();
        int[] count = new int[n];
        for (int[] e : edges()) {
            int v = e[0];
            int u = e[1];
            int vi = graph.indexOf(v);
            int ui = graph.indexOf(u);
            count[vi]++;
            count[ui]++;
            if (count[vi] > 1 || count[ui] > 1) {
                System.out.println("Problems with " + v + "-" + u);
                return false;
            }
        }
        return true;
    }
}
