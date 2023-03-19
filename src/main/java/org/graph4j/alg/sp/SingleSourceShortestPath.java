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
 * Contract for single-source shortest path algorithms. The graph and the source
 * will be specifed by the implementation constructors.
 *
 * @author Cristian Frăsinaru
 */
public interface SingleSourceShortestPath {

    /**
     *
     * @return the source of the paths
     */
    int getSource();

    /**
     * Attempts at finding the shortest path from the source to the target
     * without computing all the paths. It may be implemented by starting the
     * computation for all the shortest paths and stopping the algorithm when
     * the shortest path to the target is found. If a implementation does not
     * have this ability, it simply invokes <code>getPath</code> instead.
     *
     * @param target the target vertex number.
     * @return the shortest path from the source to the target.
     */
    default Path computePath(int target) {
        return findPath(target);
    }

    /**
     * Returns the shortest path between source and target. On the first
     * invocation of this method, it computes all the shortest paths starting in
     * source and then it returns the requested one. All the shortest paths are
     * stored for later retrieval, so subsequent invocations will return the
     * already computed paths.
     *
     * @param target the target vertex number.
     * @return the shortest path from the source to the target, or null if no
     * path exists.
     */
    Path findPath(int target);

    /**
     * Returns the weight of the shortest path from the source to the target.
     *
     * @param target the number of the target vertex
     * @return the weight of the shortest path from the source to the target, or
     * <code>Double.POSTIVE_INFINITY</code> if no path exist.
     */
    default double getPathWeight(int target) {
        //this implementation is not efficient and it usually overridden
        Path path = findPath(target);
        return path != null ? path.computeEdgesWeight() : Double.POSITIVE_INFINITY;
    }

    /**
     * Returns the default implementation of this interface.
     *
     * @param graph the input graph.
     * @param source the source vertex.
     * @return the default implementation of this interface.
     */
    static SingleSourceShortestPath getInstance(Graph graph, int source) {
        //if it has negative cost edges, should use Bellman-Ford-Moore
        boolean negativeCostEdge = false;
        for (var it = graph.edgeIterator(); it.hasNext();) {
            it.next();
            if (it.getWeight() < 0) {
                negativeCostEdge = true;
                break;
            }
        }
        if (negativeCostEdge) {
            return new BellmanFordShortestPath(graph, source);
        }
        //otherwise Dijkstra
        //if it is dense, the default impl should perform better (not really)
        //if it is sparse, the heap-based one should perform better (YES!)
        //both implementations perform virtually the same for dense graphs          
        return new DijkstraShortestPathHeap(graph, source);
    }

}
