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
import org.graph4j.traversal.BFSIterator;
import org.graph4j.util.VertexSet;

/**
 * Calculates radius or diameter or center or periphery of a graph.
 *
 * [1] F. W. Takes, W. A. Kosters, "Determining the diameter of small world
 * networks." Proceedings of the 20th ACM international conference on
 * Information and knowledge management, 2011
 * https://dl.acm.org/doi/abs/10.1145/2063576.2063748
 *
 * [2] F. W. Takes, W. A. Kosters, "Computing the Eccentricity Distribution of
 * Large Graphs." Algorithms, 2013 https://www.mdpi.com/1999-4893/6/1/100
 *
 * [3] M. Borassi, P. Crescenzi, M. Habib, W. A. Kosters, A. Marino, F. W.
 * Takes, "Fast diameter and radius BFS-based computation in (weakly connected)
 * real-world graphs: With an application to the six degrees of separation
 * games. " Theoretical Computer Science, 2015
 * https://www.sciencedirect.com/science/article/pii/S0304397515001644
 *
 * TODO: special treatment for vertices of degree one and two (?)
 *
 * @author Cristian Frăsinaru
 */
@Deprecated
class ParallelExtremaCalculator extends GraphAlgorithm {

    private int radiusLB, radiusUB, diamLB, diamUB;
    private int[] eccLB, eccUB;
    private VertexSet candidates;
    private boolean selector;
    private boolean connected;
    //
    private Integer radius, diameter;
    private VertexSet center, periphery;

    Type extremaType;

    private enum Type {
        RADIUS, DIAMETER, CENTER, PERIPHERY
    };

    public ParallelExtremaCalculator(Graph graph) {
        super(graph);
    }

    /**
     *
     * @return the diameter of the graph.
     */
    public int getDiameter() {
        if (diameter != null) {
            return diameter;
        }
        extremaType = Type.DIAMETER;
        calculate();
        diameter = diamLB;
        return diameter;
    }

    /**
     *
     * @return the radius of the graph.
     */
    public int getRadius() {
        if (radius != null) {
            return radius;
        }
        extremaType = Type.RADIUS;
        calculate();
        radius = radiusUB;
        return radius;
    }

    /**
     *
     * @return the periphery.
     */
    public VertexSet getPeriphery() {
        if (periphery != null) {
            return periphery;
        }
        extremaType = Type.PERIPHERY;
        calculate();
        periphery = new VertexSet(graph);
        if (connected) {
            for (int v : graph.vertices()) {
                if (eccLB[graph.indexOf(v)] == diamLB) {
                    periphery.add(v);
                }
            }
        }
        return periphery;
    }

    /**
     *
     * @return the center.
     */
    public VertexSet getCenter() {
        if (center != null) {
            return center;
        }
        extremaType = Type.CENTER;
        calculate();
        center = new VertexSet(graph);
        if (connected) {
            for (int v : graph.vertices()) {
                if (eccUB[graph.indexOf(v)] == radiusUB) {
                    center.add(v);
                }
            }
        }
        return center;
    }

    private void calculate() {
        int n = graph.numVertices();
        candidates = new VertexSet(graph, graph.vertices());
        radiusLB = 0;
        radiusUB = n;
        diamLB = 0;
        diamUB = n;
        eccLB = new int[n];
        eccUB = new int[n];
        Arrays.fill(eccLB, 0);
        Arrays.fill(eccUB, n);
        connected = true;
        //
        int cores = Runtime.getRuntime().availableProcessors();
        Thread[] threads = new Thread[cores];
        for (int i = 0; i < cores; i++) {
            threads[i] = new Worker();
            threads[i].start();
        }
        try {
            for (int i = 0; i < cores; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {
        }
    }

    private synchronized int selectVertex() {
        selector = !selector;
        if (selector) {
            return selectVertexMinLB();
        }
        return selectVertexMaxUB();
    }

    private synchronized int selectVertexMinLB() {
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

    private synchronized int selectVertexMaxUB() {
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

    //BFS starting in a specified vertex
    private class Worker extends Thread {

        int[] dist;

        public Worker() {
            this.dist = new int[graph.numVertices()];
        }

        @Override
        public void run() {
            int n = graph.numVertices();
            while (true) {
                int v = selectVertex();
                if (v < 0) {
                    return;
                }
                int ecc = computeEcc(v);
                if (ecc == Integer.MAX_VALUE) {
                    connected = false;
                    diamLB = ecc;
                    radiusUB = ecc;
                    return;
                }
                radiusLB = Math.max(radiusLB, ecc >> 1);
                radiusUB = Math.min(radiusUB, ecc);

                diamLB = Math.max(diamLB, ecc);
                diamUB = Math.min(diamUB, ecc << 1);

                if ((extremaType == Type.DIAMETER && (diamLB == diamUB || diamLB == n - 1)
                        || (extremaType == Type.RADIUS && (radiusLB == radiusUB || radiusUB <= 2)))) {
                    return;
                }

                updateBounds(ecc);
            }
        }

        private int computeEcc(int v) {
            int ecc = Integer.MAX_VALUE;
            var bfs = new BFSIterator(graph, v);
            while (bfs.hasNext()) {
                var node = bfs.next();
                if (node.component() > 0) {
                    return Integer.MAX_VALUE;
                }
                int u = node.vertex();
                int ui = graph.indexOf(u);
                ecc = dist[ui] = node.level();
            }
            return ecc;
        }

        private void updateBounds(int ecc) {
            synchronized (ParallelExtremaCalculator.this) {
                for (var it = candidates.iterator(); it.hasNext();) {
                    int wi = graph.indexOf(it.next());
                    int newEccLB = Math.max(dist[wi], ecc - dist[wi]);
                    if (eccLB[wi] < newEccLB) {
                        eccLB[wi] = newEccLB;
                    }
                    int newEccUB = ecc + dist[wi];
                    if (eccUB[wi] > newEccUB) {
                        eccUB[wi] = newEccUB;
                    }

                    //removing vertices
                    if (eccLB[wi] == eccUB[wi]
                            || (extremaType == Type.DIAMETER
                            && (eccUB[wi] <= diamLB && 2 * eccLB[wi] >= diamUB))
                            || (extremaType == Type.PERIPHERY
                            && (eccUB[wi] < diamLB && (diamLB == diamUB || eccLB[wi] > diamUB)))
                            || (extremaType == Type.RADIUS
                            && (eccLB[wi] >= radiusUB && eccUB[wi] + 1 <= 2 * radiusLB))
                            || (extremaType == Type.CENTER
                            && (eccLB[wi] > radiusUB && (radiusLB == radiusUB || eccUB[wi] + 1 < 2 * radiusLB)))) {
                        it.remove();
                    }
                }
            }
        }
    }

}
