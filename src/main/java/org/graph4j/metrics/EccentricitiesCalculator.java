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
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.graph4j.Graph;
import org.graph4j.alg.GraphAlgorithm;
import org.graph4j.traverse.BFSIterator;

/**
 * Uses BFS from each vertex to compute all the eccentricities.
 *
 * Parallel implementation.
 *
 * Complexity O(n(n+m)).
 *
 * @author Cristian Frăsinaru
 */
@Deprecated
class EccentricitiesCalculator extends GraphAlgorithm {

    private int[] ecc;

    public EccentricitiesCalculator(Graph graph) {
        super(graph);
    }

    /**
     *
     * @return all the eccentricities.
     */
    public int[] calculate() {
        int n = graph.numVertices();
        this.ecc = new int[n];
        Arrays.fill(ecc, Integer.MAX_VALUE);
        int cores = Runtime.getRuntime().availableProcessors();
        var executor = Executors.newFixedThreadPool(cores);
        for (int v : graph.vertices()) {
            executor.execute(new Task(v));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
        }
        return ecc;
    }

    //BFS starting in a specified vertex
    private class Task implements Runnable {

        int vertex;

        public Task(int vertex) {
            this.vertex = vertex;
        }

        @Override
        public void run() {
            int vi = graph.indexOf(vertex);
            var bfs = new BFSIterator(graph, vertex);
            while (bfs.hasNext()) {
                var node = bfs.next();
                if (node.component() > 0) {
                    ecc[vi] = Integer.MAX_VALUE;
                    return;
                }
                ecc[vi] = node.level();
            }
        }
    }

}
