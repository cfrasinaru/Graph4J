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
package org.graph4j.generate;

import java.util.Random;
import org.graph4j.Graph;

/**
 *
 * Generator for complete bipartite graphs.
 *
 * A <em>bipartite</em> graph is a graph whose vertices can be decomposed into
 * two disjoint stable sets (no two vertices within the same set are adjacent).
 *
 * @author Cristian Frăsinaru
 */
public class CompleteBipartiteGenerator extends AbstractBipartiteGenerator {

    public CompleteBipartiteGenerator(int n1, int n2) {
        super(n1, n2);
    }

    public CompleteBipartiteGenerator(int first1, int last1, int first2, int last2) {
        super(first1, last1, first2, last2);
    }

    /**
     *
     * @return a complete bipartite graph
     */
    @Override
    public Graph createGraph() {
        var g = super.createGraph();
        int n1 = last1 - first1 + 1;
        int n2 = last2 - first2 + 1;
        g.setName("K" + n1 + "," + n2);
        return g;
    }

    @Override
    protected void addEdges(Graph g, Boolean leftToRight) {
        Random random = new Random();
        boolean safeMode = g.isSafeMode();
        g.setSafeMode(false);
        for (int v = first1; v <= last1; v++) {
            for (int u = first2; u <= last2; u++) {
                boolean l2r;
                if (leftToRight != null) {
                    l2r = leftToRight;
                } else {
                    l2r = random.nextDouble() < 0.5;
                }
                if (l2r) {
                    g.addEdge(v, u);
                } else {
                    g.addEdge(u, v);
                }
            }
        }
        g.setSafeMode(safeMode);
    }

}
