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
package org.graph4j.metrics;

import java.util.Arrays;
import org.graph4j.Graph;
import org.graph4j.alg.GraphAlgorithm;
import org.graph4j.traverse.BFSIterator;
import org.graph4j.util.VertexSet;

/**
 * The algorithm described in Takes & Kosters, "Determining the Diameter of
 * Small World Networks", 2011
 *
 * @author Cristian Frăsinaru
 */
@Deprecated
class DiameterCalculator extends GraphAlgorithm {

    private int diamUB, diamLB;
    private int[] eccLB, eccUB;
    private int[] dist;
    private VertexSet candidates;
    private boolean selector;

    public DiameterCalculator(Graph graph) {
        super(graph);
    }

    /**
     *
     * @return the diameter of the graph.
     */
    public int calculate() {
        if (graph.isComplete()) {
            return 2;
        }
        int n = graph.numVertices();
        candidates = new VertexSet(graph, graph.vertices());
        diamLB = Integer.MIN_VALUE;
        diamUB = Integer.MAX_VALUE;
        dist = new int[n];
        eccLB = new int[n];
        eccUB = new int[n];
        Arrays.fill(eccLB, Integer.MIN_VALUE);
        Arrays.fill(eccUB, Integer.MAX_VALUE);

        while (diamLB < diamUB && !candidates.isEmpty()) {
            int v = selectVertex();
            int ecc = computeEcc(v);
            if (ecc == Integer.MAX_VALUE) {
                return ecc;
            }
            diamLB = Math.max(diamLB, ecc);
            diamUB = Math.min(diamUB, 2 * ecc);
            //
            for (var it = candidates.iterator(); it.hasNext();) {
                int wi = graph.indexOf(it.next());
                eccLB[wi] = Math.max(eccLB[wi], Math.max(ecc - dist[wi], dist[wi]));
                eccUB[wi] = Math.min(eccUB[wi], ecc + dist[wi]);
                if ((eccUB[wi] <= diamLB && eccLB[wi] >= diamUB / 2)
                        || eccLB[wi] == eccUB[wi]) {
                    it.remove();
                }
            }
        }
        return diamLB;
    }

    private int computeEcc(int v) {
        Arrays.fill(dist, 0);
        var bfs = new BFSIterator(graph, v);
        int ecc = -1;
        while (bfs.hasNext()) {
            var node = bfs.next();
            if (node.component() > 0) {
                return Integer.MAX_VALUE;
            }
            ecc = node.level();
            dist[graph.indexOf(node.vertex())] = ecc;
        }
        return ecc;
    }

    private int selectVertex() {
        selector = !selector;
        if (selector) {
            return selectVertexMinLB();
        }
        return selectVertexMaxUB();
    }

    private int selectVertexMinLB() {
        int selected = -1, minLB = Integer.MAX_VALUE;
        for (int v : candidates.vertices()) {
            int vi = graph.indexOf(v);
            if (minLB > eccLB[vi]) {
                minLB = eccLB[vi];
                selected = v;
            } else if (minLB == eccLB[vi] && graph.degree(v) > graph.degree(selected)) {
                selected = v;
            }
        }
        return selected;
    }

    private int selectVertexMaxUB() {
        int selected = -1, maxUB = Integer.MIN_VALUE;
        for (int v : candidates.vertices()) {
            int vi = graph.indexOf(v);
            if (maxUB < eccUB[vi]) {
                maxUB = eccUB[vi];
                selected = v;
            } else if (maxUB == eccUB[vi] && graph.degree(v) > graph.degree(selected)) {
                selected = v;
            }
        }
        return selected;
    }

}
