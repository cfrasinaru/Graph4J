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
package ro.uaic.info.graph.generate;

import java.util.Random;
import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.GraphBuilder;
import ro.uaic.info.graph.util.CheckArguments;

/**
 * Random bipartite graph generator.
 *
 * @author Cristian Frăsinaru
 */
public class GnpBipartiteGenerator extends AbstractBipartiteGenerator {

    private final double edgeProbability;
    private final Random random;

    public GnpBipartiteGenerator(int n1, int n2, double edgeProbability) {
        super(n1, n2);
        CheckArguments.probability(edgeProbability);
        this.edgeProbability = edgeProbability;
        this.random = new Random();
    }

    public GnpBipartiteGenerator(int first1, int last1, int first2, int last2, double edgeProbability) {
        super(first1, last1, first2, last2);
        CheckArguments.probability(edgeProbability);
        this.edgeProbability = edgeProbability;
        this.random = new Random();
    }

    public Digraph createDigraph() {
        int n1 = last1 - first1 + 1;
        int n2 = last2 - first2 + 1;
        var g = GraphBuilder.vertices(vertices)
                .estimatedAvgDegree(Math.max(n1, n2)).buildDigraph();
        addEdges(g, null);
        return g;
    }

    @Override
    protected void addEdges(Graph g, Boolean leftToRight) {
        boolean safeMode = g.isSafeMode();
        g.setSafeMode(false);
        for (int v = first1; v <= last1; v++) {
            for (int u = first2; u <= last2; u++) {
                if (random.nextDouble() >= edgeProbability) {
                    continue;
                }
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
