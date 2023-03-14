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
package org.graph4j.alg.sp;

import java.util.Arrays;
import org.graph4j.util.Cycle;
import org.graph4j.Graph;
import org.graph4j.util.Path;
import org.graph4j.alg.GraphAlgorithm;
import org.graph4j.util.VertexList;
import org.graph4j.util.CheckArguments;

/**
 * Bellman-Ford-Moore's algorithm finds the minimum cost paths between a vertex
 * and all the other vertices in a graph, with the condition that there are no
 * cycles of negative weight. The cost of a path is the sum of its edges
 * weights.
 *
 * The complexity of this implementation is O(n m), where m is the number of
 * edges and n the number of vertices.
 *
 * @author Cristian Frăsinaru
 */
public class BellmanFordShortestPath extends GraphAlgorithm
        implements SingleSourceShortestPath {

    private final int source;
    private double[] cost;
    private int[] before;
    private int[] size;

    /**
     * Creates an algorithm to find all shortest paths starting in the source.
     *
     * @param graph the input graph.
     * @param source the source vertex number.
     */
    public BellmanFordShortestPath(Graph graph, int source) {
        super(graph);
        CheckArguments.graphContainsVertex(graph, source);
        this.source = source;
    }

    @Override
    public int getSource() {
        return source;
    }

    @Override
    public Path findPath(int target) {
        if (before == null) {
            compute();
        }
        int ti = graph.indexOf(target);
        if (cost[ti] == Double.POSITIVE_INFINITY) {
            return new Path(graph, new int[]{});
        }
        return createPathEndingIn(ti);
    }

    @Override
    public double getPathWeight(int target) {
        if (cost == null) {
            compute();
        }
        return cost[graph.indexOf(target)];
    }

    //computes the paths and stores them in the map
    private void compute() {
        int n = graph.numVertices();
        this.cost = new double[n];
        this.before = new int[n];
        this.size = new int[n];
        var changed = new VertexList(graph, n);

        Arrays.fill(before, -1);
        Arrays.fill(cost, Double.POSITIVE_INFINITY);
        int si = graph.indexOf(source);
        cost[si] = 0;
        changed.add(si);

        var tempChanged = new VertexList(graph, n);
        double[] tempCost = new double[n];
        System.arraycopy(cost, 0, tempCost, 0, n);

        //one more step than necessary, in order to detect negative cycles
        for (int k = 0; k < n; k++) {
            //only paths of lenght k + 1 are allowed (starting in source)
            tempChanged.clear();
            for (int vi : changed.vertices()) {
                int v = graph.vertexAt(vi);
                for (var it = graph.neighborIterator(v); it.hasNext();) {
                    int u = it.next();
                    int ui = graph.indexOf(u);
                    double weight = it.getEdgeWeight();
                    if (tempCost[ui] > cost[vi] + weight) {
                        tempCost[ui] = cost[vi] + weight;
                        before[ui] = vi;
                        size[ui] = size[vi] + 1;
                        tempChanged.add(ui);
                    }
                }
            }
            System.arraycopy(tempCost, 0, cost, 0, n);
            changed.clear();
            changed.addAll(tempChanged.vertices());
        }
        if (!changed.isEmpty()) {
            int vi = changed.get(0);
            throw new NegativeCycleException(createCycleEndingIn(vi));
        }
    }

    private Path createPathEndingIn(int vi) {
        var path = new Path(graph, size[vi] + 1);
        while (vi >= 0) {
            path.add(graph.vertexAt(vi));
            vi = before[vi];
        }
        path.reverse();
        return path;
    }

    private Cycle createCycleEndingIn(int vi) {
        var cycle = new Cycle(graph);
        while (!cycle.contains(graph.vertexAt(vi))) {
            cycle.add(graph.vertexAt(vi));
            vi = before[vi];
        }
        return cycle;
    }

}
