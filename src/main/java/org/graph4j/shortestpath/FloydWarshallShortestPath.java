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
import org.graph4j.util.Cycle;
import org.graph4j.Graph;
import org.graph4j.util.Path;
import org.graph4j.GraphAlgorithm;
import org.graph4j.util.Validator;

/**
 * Floyd-Warshall's algorithm finds the shortest paths between all pairs of
 * vertices in an edge-weighted directed graph. It allows some of the edge
 * weights to be negative numbers, but no negative-weight cycles may exist.
 *
 * It has a complexity of O(n^3). It is best suited for dense graphs. In case of
 * sparse graphs {@link JohnsonShortestPath} algorithm may perform better.
 *
 * @see JohnsonShortestPath
 * @author Cristian Frăsinaru
 */
public class FloydWarshallShortestPath extends GraphAlgorithm
        implements AllPairsShortestPath {

    private double[][] cost;
    private int[][] before;
    //before[i][j] = the vertex before j on the shortest path from i to j

    public FloydWarshallShortestPath(Graph graph) {
        super(graph);
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
        if (cost[si][ti] == Double.POSITIVE_INFINITY) {
            return new Path(graph, new int[]{});
        }
        return createPathBetween(si, ti);
    }

    @Override
    public double getPathWeight(int source, int target) {
        Validator.containsVertex(graph, source);
        Validator.containsVertex(graph, target);        
        if (cost == null) {
            if (directed) {
                computeAll();
            } else {
                computeWeights();
            }
        }
        return cost[graph.indexOf(source)][graph.indexOf(target)];
    }

    @Override
    public double[][] getPathWeights() {
        if (cost == null) {
            if (directed) {
                computeAll();
            } else {
                computeWeights();
            }
        }
        return cost;
    }

    private void initBefore() {
        int n = graph.numVertices();
        this.before = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(before[i], -1);
        }
        for (int v : graph.vertices()) {
            int vi = graph.indexOf(v);
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int ui = graph.indexOf(it.next());
                before[vi][ui] = vi;
            }
        }
        if (graph.isAllowingSelfLoops()) {
            for (int i = 0; i < n; i++) {
                before[i][i] = -1;
            }
        }
    }

    private void computeAll() {
        this.cost = graph.weightMatrix();
        initBefore();
        int n = graph.numVertices();
        //compute shortest paths using only k=0,1,...,n-1 as intermediate vertices
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (before[i][k] < 0) {
                    continue;
                }
                for (int j = 0; j < n; j++) {
                    if (cost[i][j] > cost[i][k] + cost[k][j]) {
                        cost[i][j] = cost[i][k] + cost[k][j];
                        before[i][j] = before[k][j];
                    }
                }
                if (cost[i][i] < 0) {
                    Cycle cycle = createCycleBetween(i, i);
                    if (directed || cycle.length() > 2) {
                        throw new NegativeCycleException(cycle);
                    }
                }
            }
        }
    }

    //weights only - optimized for undirected graphs
    private void computeWeights() {
        this.cost = graph.weightMatrix();
        int n = graph.numVertices();
        //compute shortest paths using only k=0,1,...,n-1 as intermediate vertices
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (i == k || cost[i][k] == Double.POSITIVE_INFINITY) {
                    continue;
                }
                for (int j = i; j < n; j++) {
                    if (cost[i][j] > cost[i][k] + cost[k][j]) {
                        cost[i][j] = cost[i][k] + cost[k][j];
                        cost[j][i] = cost[i][j];
                    }
                }
                if (cost[i][i] < 0) {
                    Cycle cycle = createCycleBetween(i, i);
                    if (cycle.length() > 2) {
                        throw new NegativeCycleException(cycle);
                    }
                }
            }
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

    private Cycle createCycleBetween(int vi, int ui) {
        var cycle = new Cycle(graph);
        while (!cycle.contains(graph.vertexAt(ui))) {
            cycle.add(graph.vertexAt(ui));
            ui = before[vi][ui];
        }
        return cycle;
    }

}
