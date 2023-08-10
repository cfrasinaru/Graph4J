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
package org.graph4j.alg.clique;

import org.graph4j.Graph;
import org.graph4j.alg.SimpleGraphAlgorithm;
import org.graph4j.util.Clique;
import org.graph4j.util.VertexHeap;
import org.graph4j.util.VertexSet;

/**
 * Computes a maximal clique. This class contains both a simple heuristic for
 * determining a single maximal clique and a method that attempts to find the
 * maximum clique by enumerating all of them using Bron-Kerbosch algorithm.
 *
 * A <em>maximal</em> clique is a clique that cannot be extended by including
 * one more adjacent vertex.
 *
 * A <em>maximum</em> clique is a clique of largest size in the graph.
 * Determining a maximum clique is a NP-hard problem.
 *
 * @see BronKerboschCliqueIterator
 * @author Cristian Frăsinaru
 */
public class MaximalCliqueFinder extends SimpleGraphAlgorithm {

    private boolean[] visited;

    public MaximalCliqueFinder(Graph graph) {
        super(graph);
    }

    /**
     * Returns a maximal clique. The algorithm tries to create a maximal clique
     * starting with vertices of higher degree. For each vertex, it invokes the
     * method {@link #getMaximalClique(int)} in order to construct a maximal
     * clique including that vertex. At the end, it returns the largest clique
     * found.
     *
     * @return a maximal clique.
     */
    public Clique getMaximalClique() {
        if (graph.isComplete()) {
            return new Clique(graph, graph.vertices());
        }
        int[] deg = graph.degrees();
        var heap = new VertexHeap(graph, (i, j) -> deg[j] - deg[i]);
        visited = new boolean[graph.numVertices()];
        Clique maxClique = null;
        while (!heap.isEmpty()) {
            int v = graph.vertexAt(heap.poll());
            int vi = graph.indexOf(v);
            if (maxClique != null && deg[vi] < maxClique.size()) {
                break;
            }
            if (visited[vi]) {
                continue;
            }
            Clique clique = getMaximalClique(v);
            if (maxClique == null || clique.size() > maxClique.size()) {
                maxClique = clique;
            }
        }
        assert maxClique != null;
        assert maxClique.isValid();
        return maxClique;
    }

    /**
     * Creates a maximal clique starting with the given vertex. The additional
     * vertices of the clique are added in descending order of their degree in
     * the graph.
     *
     * @param startVertex the first vertex added to the maximal clique.
     * @return a maximal clique.
     */
    public Clique getMaximalClique(int startVertex) {
        var clique = new Clique(graph);
        clique.add(startVertex);
        var cand = new VertexSet(graph, graph.neighbors(startVertex));
        if (visited == null) {
            visited = new boolean[graph.numVertices()];
        }
        visited[graph.indexOf(startVertex)] = true;
        while (!cand.isEmpty()) {
            int v = chooseVertex(cand);
            visited[graph.indexOf(v)] = true;
            clique.add(v);
            cand.remove(v);
            cand.retainAll(graph.neighbors(v));
        }
        return clique;
    }

    private int chooseVertex(VertexSet cand) {
        int bestDeg = -1;
        int bestVertex = -1;
        for (int v : cand.vertices()) {
            int count = graph.degree(v);
            if (count > bestDeg) {
                bestDeg = count;
                bestVertex = v;
            }
        }
        return bestVertex;
    }

    /**
     * This method iterates over all maximal cliques of the graph, in order to
     * find the maximum one.If it cannot finish in the alloted time, it returns
     * {@code null}.
     *
     * @param timeLimit a time limit in milliseconds (0 for no time limit).
     * @return the maximum clique of the graph or {@code null} if it cannot be
     * found in the alloted time.
     */
    public Clique findMaximumClique(long timeLimit) {
        if (graph.isComplete()) {
            return new Clique(graph, graph.vertices());
        }
        long startTime = System.currentTimeMillis();
        Clique maxClique = null;
        var alg = new BronKerboschCliqueIterator(graph);
        while (alg.hasNext()) {
            if (timeLimit > 0
                    && System.currentTimeMillis() - startTime > timeLimit) {
                return null;
            }
            Clique clique = alg.next();
            System.out.println(clique);
            if (maxClique == null || clique.size() > maxClique.size()) {
                maxClique = clique;
            }
        }
        return maxClique;
    }

}
