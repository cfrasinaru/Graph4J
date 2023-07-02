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
package org.graph4j.alg.coloring.bb;

import org.graph4j.util.Domain;
import org.graph4j.Graph;
import org.graph4j.alg.coloring.Coloring;

/**
 * A node in the branch and bound systematic search tree.
 *
 * @author Cristian Frăsinaru
 */
class Node {

    Graph graph;
    Coloring coloring;
    Domain[] domains;
    Node parent;
    int v, u, w;

    public Node(Node parent, Graph graph, Domain[] domains, Coloring coloring, int v, int u, int w) {
        this.parent = parent;
        this.graph = graph;
        this.domains = domains;
        this.coloring = coloring;
        this.v = v;
        this.u = u;
        this.w = w;
    }

   private void prepare() {
        //resolve singleton domains
        for (var dom : domains) {
            int v = dom.vertex();
            if (coloring.isColorSet(v)) {
                continue;
            }
            if (dom.size() == 1) {
                coloring.setColor(v, dom.valueAt(0));
            }
        }
    }

}
