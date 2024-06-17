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
import org.graph4j.GraphAlgorithm;
import org.graph4j.measures.GraphMeasures;
import org.graph4j.traversal.BFSIterator;
import org.graph4j.util.VertexSet;

/**
 * The <i>radius</i> of a graph is the minimum eccentricity of vertices. A
 * disconnected graph has infinite radius.
 *
 *
 * @author Cristian Frăsinaru
 */
@Deprecated
class RadiusCalculator extends GraphAlgorithm {

    private int radiusLB, radiusUB;
    private VertexSet vertexSet;
    private int[] eccLB, eccUB;
    private int[] dist;
    private boolean selector;

    public RadiusCalculator(Graph graph) {
        super(graph);
    }

    /**
     *
     * @return the radius of the graph.
     */
    public int calculate() {
        if (graph.isComplete()) {
            return 1;
        }
        int n = graph.numVertices();
        this.radiusLB = Integer.MIN_VALUE;
        this.radiusUB = Integer.MAX_VALUE;
        this.eccLB = new int[n];
        this.eccUB = new int[n];
        Arrays.fill(eccLB, Integer.MIN_VALUE);
        Arrays.fill(eccUB, Integer.MAX_VALUE);
        this.dist = new int[n];
        //int deg[] = graph.degrees();
        vertexSet = new VertexSet(graph, graph.vertices());
        next:
        while (!vertexSet.isEmpty()) {
            int v = selectVertex();
            vertexSet.remove(v);
            if (graph.isUniversal(v)) {
                return 1;
            }
            //calculate eccentricity of v
            int ecc = computeEcc(v);
            if (ecc == Integer.MAX_VALUE) {
                return ecc;
            }
            //update the radius            
            if (radiusUB > ecc) {
                radiusUB = ecc;
            }
            int halfEcc = ecc >> 1;
            if (radiusLB < halfEcc) {
                radiusLB = halfEcc;
            }
            if (radiusLB == radiusUB) {
                return radiusUB;
            }
            //update the bounds and remove some vertices
            for (var it = vertexSet.iterator(); it.hasNext();) {
                int wi = it.next();
                int newEccLB = Math.max(dist[wi], ecc - dist[wi]);
                if (eccLB[wi] < newEccLB) {
                    eccLB[wi] = newEccLB;
                }
                int newEccUB = ecc + dist[wi];
                if (eccUB[wi] > newEccUB) {
                    eccUB[wi] = newEccUB;
                }
                if ((eccLB[wi] >= radiusUB && eccUB[wi] + 1 <= 2 * radiusLB)
                        || eccLB[wi] == eccUB[wi]) {
                    it.remove();
                }
            }
        }
        return radiusUB;
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
        if (radiusLB == Integer.MIN_VALUE) {
            return GraphMeasures.maxDegreeVertex(graph);
        }
        selector = !selector;
        if (selector) {
            return selectSmallestLB();
        }
        return selectLargestUB();
    }

    private int selectSmallestLB() {
        int selected = -1, min = Integer.MAX_VALUE;
        for (int v : vertexSet.vertices()) {
            int vi = graph.indexOf(v);
            if (min > eccLB[vi]) {
                min = eccLB[vi];
                selected = v;
            }
        }
        return selected;
    }

    private int selectLargestUB() {
        int selected = -1, max = Integer.MIN_VALUE;
        for (int v : vertexSet.vertices()) {
            int vi = graph.indexOf(v);
            if (max < eccUB[vi]) {
                max = eccUB[vi];
                selected = v;
            }
        }
        return selected;
    }

}
