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
package org.graph4j.alg.coloring;

import org.graph4j.Graph;
import org.graph4j.measures.GraphMeasures;
import org.graph4j.alg.SimpleGraphAlgorithm;
import org.graph4j.util.StableSet;
import org.graph4j.util.VertexSet;

/**
 * {@inheritDoc}
 *
 * <p>
 * The RLF algorithm assigns colors to a graph’s vertices by constructing each
 * color class one at a time. It does this by identifying a maximal independent
 * set of vertices in the graph (using various heuristics), assigning these to
 * the same color, and then removing these vertices from the graph. These
 * actions are repeated on the remaining subgraph until no vertices remain.
 *
 * If using the heuristic described in (Lewis, R. (2021). A Guide to Graph
 * Colouring: Algorithms and Applications.) RLF produces exact results for
 * bipartite, cycle, and wheel graphs.
 *
 * RLF was shown to produce significantly better vertex colorings than
 * alternative heuristics, such as DSatur, on random graphs.
 *
 * Having a complexity of O(nm), it is slower than the greedy or DSatur
 * heuristics.
 *
 * @author Cristian Frăsinaru
 */
public class RecursiveLargestFirstColoring extends SimpleGraphAlgorithm
        implements ColoringAlgorithm {

    public RecursiveLargestFirstColoring(Graph graph) {
        super(graph);
    }

    @Override
    public Coloring findColoring() {
        return findColoring(graph.numVertices());
    }

    @Override
    public Coloring findColoring(int numColors) {
        int[] colors = new int[graph.numVertices()];
        int currentColor = -1;
        var g = graph.copy();
        while (g.numVertices() > 0) {
            currentColor++;
            if (currentColor >= numColors) {
                return null;
            }
            var set = createMaximalStableSet(g);
            for (int v : set.vertices()) {
                colors[graph.indexOf(v)] = currentColor;
            }
            g.removeVertices(set.vertices());
        }
        var coloring = new Coloring(graph, colors);
        assert coloring.isProper();
        return coloring;
    }

    private StableSet createMaximalStableSet(Graph g) {
        var set = new StableSet(g);
        var cand = new VertexSet(g, g.vertices());
        //The first vertex added to S should be the vertex in G 
        //that has the largest number of neighbors.
        int first = GraphMeasures.maxDegreeVertex(g);
        set.add(first);
        cand.remove(first);
        cand.removeAll(g.neighbors(first));
        //Subsequent vertices added to S should be chosen as those that 
        //(a) are not currently adjacent to any vertex in S, and 
        //(b) have a maximal number of neighbors that are adjacent to vertices in S. 
        //Ties in condition (b) can be broken by selecting the vertex 
        //with the minimum number of neighbors not in S. 
        while (!cand.isEmpty()) {
            int v = chooseVertex(g, cand, set);
            set.add(v);
            cand.remove(v);
            cand.removeAll(g.neighbors(v));
        }
        return set;
    }

    //choose the best candidate, according to (a) and (b)
    private int chooseVertex(Graph g, VertexSet cand, VertexSet set) {
        int bestAdj = -1;
        int bestNot = Integer.MAX_VALUE;
        int bestVertex = -1;
        for (int v : cand.vertices()) {
            int count = countNeighborsAdjTo(g, v, set);
            if (count > bestAdj) {
                bestAdj = count;
                bestNot = countNeighborsNotIn(g, v, set);
                bestVertex = v;
            } else if (count == bestAdj) {
                count = countNeighborsNotIn(g, v, set);
                if (count < bestNot) {
                    bestVertex = v;
                    bestNot = count;
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

    //neighbors of v not in the set
    private int countNeighborsNotIn(Graph g, int v, VertexSet set) {
        int count = 0;
        for (int u : set.vertices()) {
            if (!g.containsEdge(v, u)) {
                count++;
            }
        }
        return count;
    }

}
