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
import org.graph4j.Graph;
import org.graph4j.util.Path;
import org.graph4j.alg.GraphAlgorithm;
import org.graph4j.util.CheckArguments;

/**
 * Dijkstra's algorithm finds the minimum cost paths between a vertex (called
 * source) and all the other vertices in a graph, with the condition that there
 * are no negative weigthed edges. The cost of a path is the sum of its edges
 * weights.
 *
 * @see DijkstraShortestPathDefault
 * @see DijkstraShortestPathHeap
 * @author Cristian Frăsinaru
 */
public abstract class DijkstraShortestPathBase extends GraphAlgorithm
        implements SingleSourceShortestPath {

    protected final int source;
    protected final int[] vertices;
    protected double cost[];
    protected int[] before;
    protected int[] size;
    protected boolean solved[];
    protected int numSolved;

    /**
     * Creates an algorithm to find all shortest paths starting in the source.
     *
     * @param graph the input graph.
     * @param source the source vertex number.
     */
    public DijkstraShortestPathBase(Graph graph, int source) {
        super(graph);
        CheckArguments.graphContainsVertex(graph, source);
        this.vertices = graph.vertices();
        this.source = source;
    }

    @Override
    public int getSource() {
        return source;
    }

    @Deprecated
    protected Path[] getAllPaths() {
        Path[] paths = new Path[vertices.length];
        if (before == null) {
            compute(-1);
        }
        for (int i = 0, n = vertices.length; i < n; i++) {
            paths[i] = createPathEndingIn(i);
        }
        return paths;
    }

    @Override
    public Path computePath(int target) {
        CheckArguments.graphContainsVertex(graph, target);
        compute(target);
        return createPathEndingIn(target);
    }

    @Override
    public Path findPath(int target) {
        if (before == null) {
            compute(-1);
        }
        int ti = graph.indexOf(target);
        if (cost[ti] == Double.POSITIVE_INFINITY) {
            return null;
        }
        return createPathEndingIn(ti);
    }

    @Override
    public double getPathWeight(int target) {
        if (cost == null) {
            compute(-1);
        }
        return cost[graph.indexOf(target)];
    }

    protected void preCompute() {
    }

    protected void postUpdate(int vi) {
    }

    protected abstract int findMinIndex();

    //computes the paths from the source
    //if the target is specified (>=0) it stops as soon as it is solved
    protected void compute(int target) {
        int n = vertices.length;
        this.cost = new double[n];
        this.before = new int[n];
        this.size = new int[n];
        this.solved = new boolean[n];
        this.numSolved = 0;
        Arrays.fill(cost, Double.POSITIVE_INFINITY);
        Arrays.fill(before, -1);
        preCompute();

        cost[graph.indexOf(source)] = 0;
        while (true) {
            int vi = findMinIndex();
            solved[vi] = true;
            numSolved++;
            int v = vertices[vi];
            if (v == target || numSolved == n) {
                break;
            }
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                int ui = graph.indexOf(u);
                if (solved[ui]) {
                    continue;
                }
                double weight = it.getEdgeWeight();
                if (weight < 0) {
                    throw new IllegalArgumentException(
                            "Negative weighted edges are not permited: " + graph.edge(v, u));
                }
                if (cost[ui] > cost[vi] + weight) {
                    cost[ui] = cost[vi] + weight;
                    before[ui] = vi;
                    size[ui] = size[vi] + 1;
                    postUpdate(ui);
                }
            }
        }
    }

    protected Path createPathEndingIn(int vi) {
        Path path = new Path(graph, size[vi] + 1);
        while (vi >= 0) {
            path.add(vertices[vi]);
            vi = before[vi];
        }
        path.reverse();
        return path;
    }

}
