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
package ro.uaic.info.graph.alg.sp;

import ro.uaic.info.graph.Cycle;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.Path;
import ro.uaic.info.graph.alg.GraphAlgorithm;

/**
 *
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
    public Path getPath(int source, int target) {
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
        if (cost == null) {
            computeAll();
            /*
            if (directed) {
                computeAll();
            } else {
                computeWeights();
            }*/
        }
        return cost[graph.indexOf(source)][graph.indexOf(target)];
    }

    private void computeAll() {
        int n = graph.numVertices();
        this.cost = graph.costMatrix();
        this.before = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                before[i][j] = (i == j ? -1 : i);
            }
        }
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (i == k || cost[i][k] == Double.POSITIVE_INFINITY) {
                    continue;
                }
                for (int j = 0; j < n; j++) {
                    if (j == k || cost[k][j] == Double.POSITIVE_INFINITY) {
                        continue;
                    }
                    if (cost[i][j] > cost[i][k] + cost[k][j]) {
                        cost[i][j] = cost[i][k] + cost[k][j];
                        before[i][j] = before[k][j];
                        if (i == j && cost[i][j] < 0) {
                            Cycle cycle = createCycleBetween(i, j);
                            if (directed || cycle.length() > 2) {
                                throw new NegativeCycleException(cycle);
                            }
                        }
                    }
                }
            }
        }
    }

    //weights only for undirected graphs
    @Deprecated //Not correct.Why?
    private void computeWeights() {
        int n = graph.numVertices();
        this.cost = graph.costMatrix(); //symmetrical
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (i == k || cost[i][k] == Double.POSITIVE_INFINITY) {
                    continue;
                }
                for (int j = i; j < n; j++) {
                    if (j == k || cost[k][j] == Double.POSITIVE_INFINITY) {
                        continue;
                    }
                    if (cost[i][j] > cost[i][k] + cost[k][j]) {
                        cost[i][j] = cost[i][k] + cost[k][j];
                        cost[j][i] = cost[i][j];
                        if (i == j && cost[i][j] < 0) {
                            Cycle cycle = createCycleBetween(i, j);
                            if (cycle.length() > 2) {
                                throw new NegativeCycleException(cycle);
                            }
                        }
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
