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
package org.graph4j.alg.sp;

import org.graph4j.Graph;
import org.graph4j.GraphAlgorithm;
import org.graph4j.traversal.BFSIterator;
import org.graph4j.traversal.SearchNode;
import org.graph4j.util.Validator;
import org.graph4j.util.Path;

/**
 * Determines the path with the fewest edges connecting two vertices. For
 * unweighted graphs, breadth-first search can be used to find the shortest path
 * between two vertices.
 *
 * @author Cristian Frăsinaru
 */
//Slower than {@link BidirectionalDijkstra}.
public class BFSSinglePairShortestPath extends GraphAlgorithm
        implements SinglePairShortestPath {

    private final int source;
    private final int target;
    private final int[] forbiddenVertices;
    private Path bestPath;
    private double bestWeight;

    /**
     * Creates an algorithm to find the path with the fewest edges between two
     * specified vertices. If the input graph has weights on its edges, they are
     * ignored.
     *
     * @param graph the input graph.
     * @param source the source vertex number.
     * @param target the target vertex number.
     */
    public BFSSinglePairShortestPath(Graph graph, int source, int target) {
        this(graph, source, target, null);
    }

    /**
     * Creates an algorithm to find the path with the fewest edges between two
     * specified vertices, not passing through some forbidden vertices.
     *
     * @param graph the input graph.
     * @param source the source vertex number.
     * @param target the target vertex number.
     * @param forbiddenVertices vertices that are not allowed in the path; can
     * be {@code null} if there are no forbidden vertices.
     */
    public BFSSinglePairShortestPath(Graph graph, int source, int target, int[] forbiddenVertices) {
        super(graph);
        if (graph.hasEdgeWeights()) {
            throw new IllegalArgumentException(
                    "BFSSinglePairShortestPath should be used only for graphs with unweighted edges.");
        }
        Validator.containsVertex(graph, source);
        Validator.containsVertex(graph, target);
        this.source = source;
        this.target = target;
        this.forbiddenVertices = forbiddenVertices;
    }

    @Override
    public int getSource() {
        return source;
    }

    @Override
    public int getTarget() {
        return target;
    }

    @Override
    public Path findPath() {
        if (source == target) {
            return new Path(graph, new int[]{source});
        }
        if (bestPath == null) {
            compute();
        }
        return bestPath;
    }

    @Override
    public double getPathWeight() {
        if (source == target) {
            return 0;
        }
        if (bestPath == null) {
            compute();
        }
        return bestWeight;
    }

    private void compute() {
        if (graph.containsEdge(source, target)) {
            bestPath = new Path(graph, new int[]{source, target});
            bestWeight = bestPath.computeEdgesWeight();
            return;
        }
        bestWeight = Double.POSITIVE_INFINITY; //bestPath is null
        int n = graph.numVertices();
        boolean[] visited = new boolean[n];
        int[] before1 = new int[n];
        int[] before2 = new int[n];
        before1[graph.indexOf(source)] = -1;
        before2[graph.indexOf(target)] = -1;
        int meeting = -1;
        //
        //use a BFSTraverser
        var bfs1 = new BFSIterator(graph, source, forbiddenVertices, false);
        var bfs2 = new BFSIterator(graph, target, forbiddenVertices, true); //reverse
        int currentLevel = 0;
        SearchNode currentNode1 = null;
        SearchNode currentNode2 = null;
        over:
        while (bfs1.hasNext() && bfs2.hasNext()) {
            //bfs from source - scan a level
            while (bfs1.hasNext()) {
                var node = currentNode1 == null ? bfs1.next() : currentNode1;
                int vertex = node.vertex();
                int level = node.level();
                if (vertex != source && level == 0) {
                    //another cc (no path)
                    return;
                }
                if (level > currentLevel) {
                    currentNode1 = node;
                    break;
                }
                currentNode1 = null;
                int vi = graph.indexOf(vertex);
                if (node.parent() != null) {
                    before1[vi] = node.parent().vertex();
                }
                if (visited[vi]) {
                    meeting = vi;
                    break over;
                }
                visited[vi] = true;
            }

            //bfs from target - scan a level            
            while (meeting < 0 && bfs2.hasNext()) {
                var node = currentNode2 == null ? bfs2.next() : currentNode2;
                int vertex = node.vertex();
                int level = node.level();
                if (vertex != target && level == 0) {
                    //another cc (no path)
                    return;
                }
                if (level > currentLevel) {
                    currentNode2 = node;
                    break;
                }
                currentNode2 = null;
                int vi = graph.indexOf(vertex);
                if (node.parent() != null) {
                    before2[vi] = node.parent().vertex();
                }
                if (visited[vi]) {
                    meeting = vi;
                    break over;
                }
                visited[vi] = true;
            }
            currentLevel++;
        }

        if (meeting < 0) {
            return;
        }

        //compute the path
        bestPath = new Path(graph);
        int vi = meeting;
        while (vi >= 0) {
            bestPath.add(graph.vertexAt(vi));
            vi = before1[vi];
        }
        bestPath.reverse();
        int ui = before2[meeting];
        while (ui >= 0) {
            bestPath.add(graph.vertexAt(ui));
            ui = before2[ui];
        }
        bestWeight = bestPath.computeEdgesWeight();
        assert bestWeight == bestPath.length();
    }
}
