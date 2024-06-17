/*
 * Copyright (C) 2024 Cristian Frăsinaru and contributors
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
package org.graph4j.route;

import org.graph4j.Graph;
import org.graph4j.InvalidVertexException;
import org.graph4j.GraphAlgorithm;
import org.graph4j.alg.sp.BFSSinglePairShortestPath;
import org.graph4j.traversal.DFSIterator;
import org.graph4j.util.Path;

/**
 * Algorithms for finding paths in unweighted directed or undirected graph.
 *
 * @author Cristian Frăsinaru
 */
public class PathFinder extends GraphAlgorithm {

    public PathFinder(Graph graph) {
        super(graph);
    }

    /**
     * Finds the path with the fewest edges connecting two vertices. If the
     * input graph is directed, the path takes into account the edge
     * orientations. The path is found using a BFS iterator, therefore it is an
     * induced path. The weights of the edges (if the graph is edge-weighted)
     * are not taken in consideration. For determining a shortest path in an
     * edge-weighted graph (see {@link org.graph4j.alg.sp} package).
     *
     * @see BFSSinglePairShortestPath
     * @param graph the input graph.
     * @param v a vertex number.
     * @param u a vertex number.
     * @param forbiddenVertices vertices that are not allowed in the path; can
     * be {@code null} if there are no forbidden vertices.
     * @return a path connecting v and u or {@code null} if no such path exists.
     * @throws InvalidVertexException if any of the specified vertices is not in
     * the graph.
     */
    public Path findShortestPath(Graph graph, int v, int u, int[] forbiddenVertices) {
        return new BFSSinglePairShortestPath(graph, v, u, forbiddenVertices).findPath();
    }

    /**
     * Determines if there is a path connecting two vertices. If the input graph
     * is directed, the path takes into account the edge orientations.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return {@code true} if there is a path connecting v and u, {@code false}
     * otherwise.
     */
    public boolean hasPath(int v, int u) {
        var dfs = new DFSIterator(graph, v);
        while (dfs.hasNext()) {
            var node = dfs.next();
            if (node.component() > 0) {
                break;
            }
            if (node.vertex() == u) {
                return true;
            }
        }
        return false;
    }

}
