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
import org.graph4j.Graphs;
import org.graph4j.alg.GraphMeasures;
import org.graph4j.alg.SimpleGraphAlgorithm;
import org.graph4j.util.Clique;
import org.graph4j.util.VertexSet;

/**
 * Heuristic for determining a maximal clique.
 *
 * @author Cristian Frăsinaru
 */
public class MaximalCliqueFinder extends SimpleGraphAlgorithm {

    public MaximalCliqueFinder(Graph graph) {
        super(graph);
    }

    public Clique getClique() {
        if (Graphs.isComplete(graph)) {
            return new Clique(graph, graph.vertices());
        }
        Clique clique = null;
        int maxSize = -1;
        int maxDeg = GraphMeasures.maxDegree(graph);
        //int attempts = 1;
        int attempts = (int)Math.sqrt(graph.numVertices()); //?
        for (int i = 0; i < attempts; i++) {
            var bk = new BronKerboschCliqueIterator(graph, attempts > 1, false);
            var q = bk.next();
            int size = q.size();
            if (size > maxSize) {
                clique = q;
                maxSize = size;
            }
            if (maxSize == maxDeg + 1) {
                break;
            }
        }
        return clique;
    }

    /**
     *
     * @return a maximal clique.
     */
    public Clique getClique2() {
        var clique = new Clique(graph);
        var cand = new VertexSet(graph, graph.vertices());
        int first = GraphMeasures.maxDegreeVertex(graph);
        clique.add(first);
        cand.remove(first);
        cand.retainAll(graph.neighbors(first));
        while (!cand.isEmpty()) {
            int v = chooseVertex(graph, cand, clique);
            clique.add(v);
            cand.remove(v);
            cand.retainAll(graph.neighbors(v));
        }
        return clique;
    }

    private int chooseVertex(Graph g, VertexSet cand, VertexSet set) {
        int bestDeg = -1;
        int bestVertex = -1;
        for (int v : cand.vertices()) {
            int count = g.degree(v);
            if (count > bestDeg) {
                bestDeg = count;
                bestVertex = v;
            }
        }
        return bestVertex;
    }

    //The first vertex added to Q should be the vertex in G 
    //that has the largest number of neighbors.
    //Subsequent vertices added to Q should be chosen as those that 
    //have a maximal number of neighbors that are adjacent to vertices in Q. 
    //Ties in condition (b) can be broken by selecting the vertex 
    //with the maximum number of neighbors. 
    //choose the best candidate, according to (a) and (b)
    private int chooseVertex2(Graph g, VertexSet cand, VertexSet set) {
        int bestAdj = -1;
        int bestDeg = Integer.MAX_VALUE;
        int bestVertex = -1;
        for (int v : cand.vertices()) {
            int count = countNeighborsAdjTo(g, v, set);
            if (count > bestAdj) {
                bestAdj = count;
                bestDeg = g.degree(v);
                bestVertex = v;
            } else if (count == bestAdj) {
                count = g.degree(v);
                if (count < bestDeg) {
                    bestVertex = v;
                    bestDeg = count;
                }
            }
        }
        return bestVertex;
    }

    //neighbors of v adjacent to vertices in the set
    private int countNeighborsAdjTo(Graph g, int v, VertexSet set) {
        int count = 0;
        for (var it = g.neighborIterator(v); it.hasNext();) {
            int u = it.next();
            if (isAdjacentTo(g, u, set)) {
                count++;
            }
        }
        return count;
    }

    //true if v is adjacent to a vertex in set
    private boolean isAdjacentTo(Graph g, int v, VertexSet set) {
        for (int u : set.vertices()) {
            if (g.containsEdge(v, u)) {
                return true;
            }
        }
        return false;
    }

}
