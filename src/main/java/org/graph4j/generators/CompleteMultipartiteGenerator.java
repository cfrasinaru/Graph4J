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
package org.graph4j.generators;

import org.graph4j.Graph;

/**
 *
 * Generator for complete multipartite graphs.
 *
 * A <em>complete k-partite</em> graph is a graph whose vertices can be
 * decomposed into k disjoint stable sets (no two vertices within the same set
 * are adjacent) and every pair of graph vertices in distinct stable sets are
 * adjacent.
 *
 * @author Cristian Frăsinaru
 */
public class CompleteMultipartiteGenerator extends RandomMultipartiteGenerator {

    /**
     * Creates a generator for a complete multipartite graph, where the number
     * of vertices of each stable set is specified.
     *
     * @param numVertices the number of vertices in each stable set.
     */
    public CompleteMultipartiteGenerator(int... numVertices) {
        super(1.0, numVertices);
    }

    /**
     * Creates a complete multipartite graph.
     *
     * @return a complete multipartite graph
     */
    @Override
    public Graph create() {
        var g = super.create();
        var name = new StringBuilder("K");
        for (int i = 0; i < numVertices.length; i++) {
            if (i > 0) {
                name.append(",");
            }
            name.append(numVertices[i]);
        }
        g.setName(name.toString());
        return g;
    }

}
