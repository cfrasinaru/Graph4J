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
import org.graph4j.alg.sp.FloydWarshallShortestPath;
import org.graph4j.traverse.BFSIterator;

/**
 * Uses BFS from each vertex to compute the distances between all pairs of
 * vertices. Parallel implementation.
 *
 * Complexity O(n(n+m)).
 *
 * @author Cristian Frăsinaru
 */
class DistancesCalculator extends GraphAlgorithm {

    private int[][] dist;

    public DistancesCalculator(Graph graph) {
        super(graph);
    }

    /**
     * 
     * @return all the distances.
     */
    public int[][] calculate() {
        int n = graph.numVertices();
        this.dist = new int[n][n];
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
        return dist;
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
            Arrays.fill(dist[vi], Integer.MAX_VALUE);
            var bfs = new BFSIterator(graph, vertex);
            while (bfs.hasNext()) {
                var node = bfs.next();
                if (node.component() > 0) {
                    break;
                }
                int u = node.vertex();
                int ui = graph.indexOf(u);
                dist[vi][ui] = node.level();
            }
        }
    }

    //uses Floyd-Warshall to compute all pairs shortest paths
    //complexity O(n^3)
    @Deprecated
    private int[][] distancesFW() {
        int n = graph.numVertices();
        this.dist = new int[n][n];
        var alg = new FloydWarshallShortestPath(graph);
        int n1 = directed ? n : n - 1;
        for (int i = 0; i < n1; i++) {
            int v = graph.vertexAt(i);
            //var alg = new DijkstraShortestPathHeap(graph, v);
            int from = directed ? 0 : i + 1;
            for (int j = from; j < n; j++) {
                int u = graph.vertexAt(j);
                double d = alg.getPathWeight(v, u);
                //double d = alg.getPathWeight(u);
                if (d == Double.POSITIVE_INFINITY) {
                    dist[i][j] = Integer.MAX_VALUE;
                } else {
                    dist[i][j] = (int) d;
                }
                if (!directed) {
                    dist[j][i] = dist[i][j];
                }
            }
        }
        return dist;
    }

    //uses BFS from each vertex to compute all pairs shortest paths
    //complexity O(n(n+m))
    @Deprecated
    private int[][] distancesBFS() {
        int n = graph.numVertices();
        this.dist = new int[n][n];
        for (int v : graph.vertices()) {
            int vi = graph.indexOf(v);
            Arrays.fill(dist[vi], Integer.MAX_VALUE);
            var bfs = new BFSIterator(graph, v);
            while (bfs.hasNext()) {
                var node = bfs.next();
                if (node.component() > 0) {
                    break;
                }
                int u = node.vertex();
                int ui = graph.indexOf(u);
                dist[vi][ui] = node.level();
            }
        }
        return dist;
    }

}
