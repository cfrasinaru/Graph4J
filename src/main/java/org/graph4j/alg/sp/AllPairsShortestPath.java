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

import org.graph4j.Graph;
import org.graph4j.util.Path;

/**
 * A contract for all-pairs shortest path algorithms.
 *
 * @see JohnsonShortestPath
 * @see FloydWarshallShortestPath
 * @author Cristian Frăsinaru
 */
public interface AllPairsShortestPath {

    /**
     * Returns the input graph on which the algorithm is executed.
     *
     * @return the input graph.
     */
    Graph getGraph();

    /**
     * Returns the shortest path between source and target. On the first
     * invocation of this method, it computes shortest paths between any two
     * vertices of the graph, then it returns the requested one. All the
     * shortest paths are stored for later retrieval, so subsequent invocations
     * will return the already computed paths.
     *
     * @param source the number of the source vertex.
     * @param target the number of the target vertex.
     * @return the shortest path from the source to the target, or null if no
     * path exists.
     */
    Path findPath(int source, int target);

    /**
     *
     * @param source the number of the source vertex.
     * @param target the number of the target vertex.
     * @return the weight of the shortest path from the source to the target, or
     * <code>Double.POSTIVE_INFINITY</code> if no path exists.
     */
    default double getPathWeight(int source, int target) {
        return findPath(source, target).computeEdgesWeight();
    }

    /**
     * Returns a matrix containing the weights of the shortest paths for every
     * pair of vertices.
     *
     * @return a matrix containing the weights of the shortest paths for every
     * pair of vertices.
     */
    default double[][] getPathWeights() {
        //this implementation is not efficient and it usually overridden
        var g = getGraph();
        int n = g.numVertices();
        double[][] weights = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                weights[i][j] = getPathWeight(g.vertexAt(i), g.vertexAt(j));
            }
        }
        return weights;
    }

    /**
     * Returns the default implementation of this interface.
     *
     * @param graph the input graph.
     * @return the default implementation of this interface.
     */
    static AllPairsShortestPath getInstance(Graph graph) {
        if (!graph.hasEdgeWeights()) {
            return new BFSAllPairsShortestPath(graph);
        }
        return new JohnsonShortestPath(graph);
    }

}
