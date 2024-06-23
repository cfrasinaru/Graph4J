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
package org.graph4j.shortestpath;

import java.util.Arrays;
import org.graph4j.Graph;
import org.graph4j.GraphAlgorithm;
import org.graph4j.traversal.BFSIterator;
import org.graph4j.util.Validator;
import org.graph4j.util.Path;

/**
 * Determines the shortest paths from a source vertex to all other vertices, in
 * an unweighted graph, using a breadth-first traversal.
 *
 * @author Cristian Frăsinaru
 */
public class BFSSingleSourceShortestPath extends GraphAlgorithm
        implements SingleSourceShortestPath {

    protected final int source;
    protected double[] dist;
    protected int[] before;

    /**
     * Creates an algorithm to find all shortest paths starting in the specified
     * source, in an unweighted graph. If the input graph has weights on its
     * edges, they are ignored.
     *
     * @param graph the input graph.
     * @param source the source vertex number.
     */
    public BFSSingleSourceShortestPath(Graph graph, int source) {
        super(graph);
        Validator.containsVertex(graph, source);
        this.source = source;
    }

    @Override
    public int getSource() {
        return source;
    }

    @Override
    public Path computePath(int target) {
        Validator.containsVertex(graph, target);
        compute(target);
        return createPathEndingIn(target);
    }

    @Override
    public Path findPath(int target) {
        Validator.containsVertex(graph, target);
        if (before == null) {
            compute(-1);
        }
        int ti = graph.indexOf(target);
        if (dist[ti] == Double.POSITIVE_INFINITY) {
            return null;
        }
        return createPathEndingIn(ti);
    }

    @Override
    public double getPathWeight(int target) {
        if (dist == null) {
            compute(-1);
        }
        return dist[graph.indexOf(target)];
    }

    @Override
    public double[] getPathWeights() {
        if (dist == null) {
            compute(-1);
        }
        return dist;
    }

    //computes the paths from the source
    //if the target is specified (>=0) it stops as soon as it is solved
    protected void compute(int target) {
        int n = graph.numVertices();
        this.dist = new double[n];
        this.before = new int[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(before, -1);
        dist[graph.indexOf(source)] = 0;

        var bfs = new BFSIterator(graph, source);
        while (bfs.hasNext()) {
            var node = bfs.next();
            if (node.component() > 0) {
                break;
            }
            int u = node.vertex();
            int ui = graph.indexOf(u);
            dist[ui] = node.level();
            if (node.parent() != null) {
                before[ui] = node.parent().vertex();
            }
            if (u == target) {
                break;
            }
        }

    }

    protected Path createPathEndingIn(int vi) {
        Path path = new Path(graph, (int) dist[vi] + 1);
        while (vi >= 0) {
            path.add(graph.vertexAt(vi));
            vi = before[vi];
        }
        path.reverse();
        return path;
    }

}
