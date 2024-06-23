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
package org.graph4j.shortestpath;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.graph4j.Graph;
import org.graph4j.util.Path;
import org.graph4j.GraphAlgorithm;
import org.graph4j.traversal.BFSIterator;
import org.graph4j.util.Validator;

/**
 * Determines the shortest paths between all pairs of vertices, in an unweighted
 * graph, using breadth-first traversals.
 *
 * @author Cristian Frăsinaru
 */
public class BFSAllPairsShortestPath extends GraphAlgorithm
        implements AllPairsShortestPath {

    private double[][] dist;
    private int[][] before;
    //before[i][j] = the vertex before j on the shortest path from i to j

    /**
     * Creates an algorithm for finding all pair shortest paths in an unweighted
     * graph. If the input graph has weights on its edges, they are ignored.
     *
     * @param graph the input graph.
     */
    public BFSAllPairsShortestPath(Graph graph) {
        super(graph);
        if (graph.hasEdgeWeights()) {
            throw new IllegalArgumentException(
                    "BFSAllPairsShortestPath should be used only for graphs with unweighted edges.");
        }
    }

    @Override
    public Path findPath(int source, int target) {
        Validator.containsVertex(graph, source);
        Validator.containsVertex(graph, target);
        if (before == null) {
            computeAll();
        }
        int si = graph.indexOf(source);
        int ti = graph.indexOf(target);
        if (dist[si][ti] == Double.POSITIVE_INFINITY) {
            return new Path(graph, new int[]{});
        }
        return createPathBetween(si, ti);
    }

    @Override
    public double getPathWeight(int source, int target) {
        Validator.containsVertex(graph, source);
        Validator.containsVertex(graph, target);
        if (dist == null) {
            computeAll();
        }
        return dist[graph.indexOf(source)][graph.indexOf(target)];
    }

    @Override
    public double[][] getPathWeights() {
        if (dist == null) {
            computeAll();
        }
        return dist;
    }

    private void computeAll() {
        int n = graph.numVertices();
        this.dist = new double[n][n];
        this.before = new int[n][n];
        int cores = Runtime.getRuntime().availableProcessors();
        var executor = Executors.newFixedThreadPool(cores);
        for (int v : graph.vertices()) {
            executor.execute(new BFSAllPairsShortestPath.Task(v));
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
        }
    }

    private Path createPathBetween(int vi, int ui) {
        var path = new Path(graph);
        while (ui != vi) {
            path.add(graph.vertexAt(ui));
            ui = before[vi][ui];
        }
        path.add(graph.vertexAt(vi));
        path.reverse();
        return path;
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
            Arrays.fill(dist[vi], Double.POSITIVE_INFINITY);
            Arrays.fill(before[vi], -1);
            var bfs = new BFSIterator(graph, vertex);
            while (bfs.hasNext()) {
                var node = bfs.next();
                if (node.component() > 0) {
                    break;
                }
                int u = node.vertex();
                int ui = graph.indexOf(u);
                dist[vi][ui] = node.level();
                if (node.parent() != null) {
                    before[vi][ui] = node.parent().vertex();
                }
            }
        }
    }

}
