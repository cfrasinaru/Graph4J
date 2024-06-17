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
package org.graph4j.hamiltonian;

import org.graph4j.Graph;
import org.graph4j.GraphAlgorithm;
import org.graph4j.util.Validator;
import org.graph4j.util.Path;

/**
 * Backtracking algorithm for determining the longest path in a graph (directed
 * or not).
 *
 * @see Path
 * @author Cristian Frăsinaru
 */
public class BacktrackLongestPathAlgoritm extends GraphAlgorithm {

    private Path currentBest;
    private Path currentPath;
    private int source, target;

    public BacktrackLongestPathAlgoritm(Graph graph) {
        super(graph);
    }

    /**
     * Returns the longest path in the graph. If the graph contians more paths
     * of maximum length, it returns the first one found.
     *
     * @return the longest path in the graph.
     */
    public Path getLongestPath() {
        Path best = null;
        for (int i = 0, n = graph.numVertices(); i < n; i++) {
            source = graph.vertexAt(i);
            target = -1;
            compute();
            if (best == null || currentBest.length() > best.length()) {
                if (currentBest.isHamiltonian()) {
                    return currentBest;
                }
                best = new Path(graph, currentBest.vertices());
            }
        }
        return best;
    }

    /**
     * Returns the longest path in the graph, starting in the {@code source}
     * vertex.
     *
     * @param source the source vertex number.
     * @return the longest path in the graph, starting in the {@code source}
     * vertex.
     */
    public Path getLongestPath(int source) {
        Validator.containsVertex(graph, source);
        this.source = source;
        this.target = -1;
        return compute();
    }

    /**
     * Returns the longest path in the graph, starting in the {@code source}
     * vertex and ending in the {@code target} vertex.
     *
     * @param source the source vertex number.
     * @param target the target vertex number.
     * @return the longest path in the graph, starting in the {@code source}
     * vertex and ending in the {@code target} vertex.
     */
    public Path getLongestPath(int source, int target) {
        Validator.containsVertex(graph, source);
        Validator.containsVertex(graph, target);
        this.source = source;
        this.target = target;
        return compute();
    }

    private Path compute() {
        currentBest = new Path(graph);
        currentPath = new Path(graph);
        currentPath.add(source);
        getPathRec();
        assert currentBest.isValid();
        return currentBest;
    }

    private boolean getPathRec() {
        int last = currentPath.lastVertex();
        for (var it = graph.neighborIterator(last); it.hasNext();) {
            int v = it.next();
            if (currentPath.contains(v)) {
                continue;
            }
            assert graph.containsEdge(last, v);
            currentPath.add(v);
            if ((target < 0 || v == target) && (currentBest == null || currentPath.length() > currentBest.length())) {
                currentBest = new Path(graph, currentPath.vertices());
                if (currentBest.isHamiltonian()) {
                    return false;
                }
            }
            if (!getPathRec()) {
                return false;
            }
            currentPath.remove(v);
        }
        return true;
    }

    @Deprecated
    private boolean initialCheck() {
        int n = graph.numVertices();
        int[] vertices = graph.vertices();
        int[] deg = graph.degrees();
        for (int i = 0; i < n; i++) {
            if (deg[i] == 0) {
                return false;
            }
            if (deg[i] == 1 && vertices[i] != source && vertices[i] != target) {
                return false;
            }
            int count = countForced(i);
            if (count > 2) {
                return false;
            } else if (count == 2 && source != vertices[i]) {
                return false;
            }
        }
        return true;
    }

    @Deprecated
    private int countForced(int v) {
        int count = 0;
        //for undirected graphs
        for (var it = graph.neighborIterator(v); it.hasNext();) {
            int u = it.next();
            if (graph.degree(u) == 2) {
                count++;
            }
        }
        return count;
    }

}
