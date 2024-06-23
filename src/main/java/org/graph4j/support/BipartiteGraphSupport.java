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
package org.graph4j.support;

import org.graph4j.exceptions.NotBipartiteException;
import org.graph4j.Graph;
import org.graph4j.SimpleGraphAlgorithm;
import org.graph4j.alg.coloring.Coloring;
import org.graph4j.matching.HopcroftKarpMaximumMatching;
import org.graph4j.traversal.BFSTraverser;
import org.graph4j.traversal.BFSVisitor;
import org.graph4j.traversal.SearchNode;
import org.graph4j.util.Cycle;
import org.graph4j.util.Matching;
import org.graph4j.util.StableSet;
import org.graph4j.util.VertexSet;

/**
 * Support class for bipartite graphs. A graph is <em>bipartite</em> if its
 * vertices can be partitioned in two disjoint stable sets.
 *
 * A bipartite graph is 2-colorable.
 *
 * A graph is bipartite if and only if it has no odd-length cycle.
 *
 * @author Cristian Frăsinaru
 */
public class BipartiteGraphSupport extends SimpleGraphAlgorithm {

    private boolean[] color;
    private Boolean bipartite;
    private Cycle oddCycle;
    private StableSet leftSide;
    private StableSet rightSide;
    private Coloring coloring;
    private HopcroftKarpMaximumMatching maximumMatchingAlg;

    /**
     * Creates an instance of the support class.
     *
     * @param graph the input graph.
     */
    public BipartiteGraphSupport(Graph graph) {
        super(graph);
    }

    /**
     *
     * @return {@code true} if the graph is bipartite, {@code false} otherwise.
     */
    public boolean isBipartite() {
        if (bipartite == null) {
            compute();
        }
        return bipartite;
    }

    /**
     * Returns the left side of the bipartition.
     *
     * @return the left side of the graph, if the graph is bipartite.
     * @throws NotBipartiteException if the graph is not bipartite.
     */
    public StableSet getLeftSide() {
        if (leftSide != null) {
            return leftSide;
        }
        if (!isBipartite()) {
            throw new NotBipartiteException();
        }
        leftSide = new StableSet(graph);
        for (int i = 0, n = graph.numVertices(); i < n; i++) {
            if (color[i]) {
                leftSide.add(graph.vertexAt(i));
            }
        }
        return leftSide;
    }

    /**
     * Returns the right side of the bipartition.
     *
     * @return the right side of the graph, if the graph is bipartite.
     * @throws NotBipartiteException if the graph is not bipartite.
     */
    public StableSet getRightSide() {
        if (!isBipartite()) {
            throw new NotBipartiteException();
        }
        rightSide = new StableSet(graph);
        for (int i = 0, n = graph.numVertices(); i < n; i++) {
            if (!color[i]) {
                rightSide.add(graph.vertexAt(i));
            }
        }
        return rightSide;
    }

    /**
     * Determines the stable set of the partition where a specified vertex
     * belongs to.
     *
     * @param v a vertex number
     * @return the partition set {@code v} belongs to, if the graph is
     * bipartite.
     * @throws NotBipartiteException if the graph is not bipartite.
     */
    public StableSet getSide(int v) {
        if (!isBipartite()) {
            throw new NotBipartiteException();
        }
        return color[graph.indexOf(v)] ? leftSide : rightSide;
    }

    /**
     * Determines an odd cycle, if the graph is not bipartite.
     *
     * @return an odd cycle, if the graph is not bipartite, otherwise it returns
     * {@code null}.
     */
    public Cycle findOddCycle() {
        if (bipartite == null) {
            compute();
        }
        return oddCycle;
    }

    /**
     * Created a 2-coloring of the bipartite graph.
     *
     * @return a 2-coloring of the bipartite graph.
     * @throws NotBipartiteException if the graph is not bipartite.
     */
    public Coloring getColoring() {
        if (coloring != null) {
            return coloring;
        }
        if (!isBipartite()) {
            throw new NotBipartiteException();
        }
        this.coloring = new Coloring(graph);
        for (int v : leftSide) {
            coloring.setColor(v, 0);
        }
        for (int v : rightSide) {
            coloring.setColor(v, 1);
        }
        return coloring;
    }

    /**
     * Determines a maximum matching in a bipartite graph, using
     * {@link HopcroftKarpMaximumMatching} algorithm.
     *
     * @see HopcroftKarpMaximumMatching
     * @return a maximum stable set in a bipartite graph.
     * @throws NotBipartiteException if the graph is not bipartite.
     */
    public Matching getMaximumMatching() {
        if (!isBipartite()) {
            throw new NotBipartiteException();
        }
        if (maximumMatchingAlg == null) {
            maximumMatchingAlg = new HopcroftKarpMaximumMatching(graph, getLeftSide(), getRightSide());
        }
        return maximumMatchingAlg.getMatching();
    }

    /**
     * Determines a maximum stable set in a bipartite graph, using
     * {@link HopcroftKarpMaximumMatching} algorithm.
     *
     * @see HopcroftKarpMaximumMatching
     * @return a maximum stable set in a bipartite graph.
     * @throws NotBipartiteException if the graph is not bipartite.
     */
    public StableSet getMaximumStableSet() {
        if (!isBipartite()) {
            throw new NotBipartiteException();
        }
        if (maximumMatchingAlg == null) {
            maximumMatchingAlg = new HopcroftKarpMaximumMatching(graph, getLeftSide(), getRightSide());
        }
        return maximumMatchingAlg.getMaximumStableSet();
    }

    /**
     * Determines a minimum vertex cover in a bipartite graph, using
     * {@link HopcroftKarpMaximumMatching} algorithm.
     *
     * @see HopcroftKarpMaximumMatching
     * @return a maximum stable set in a bipartite graph.
     * @throws NotBipartiteException if the graph is not bipartite.
     */
    public VertexSet getMinimumVertexCover() {
        if (!isBipartite()) {
            throw new NotBipartiteException();
        }
        if (maximumMatchingAlg == null) {
            maximumMatchingAlg = new HopcroftKarpMaximumMatching(graph, getLeftSide(), getRightSide());
        }
        return maximumMatchingAlg.getMinimumVertexCover();
    }

    private void compute() {
        this.color = new boolean[graph.numVertices()];
        color[0] = true;
        new BFSTraverser(graph).traverse(graph.vertexAt(0), new BFSBipartiteVisitor());
        //new DFSTraverser(graph).traverse(graph.vertexAt(0), new DFSBipartiteVisitor());
        bipartite = oddCycle == null;
    }

    //BFS is slightly faster but uses more memory
    private class BFSBipartiteVisitor implements BFSVisitor {

        @Override
        public void treeEdge(SearchNode from, SearchNode to) {
            color[graph.indexOf(to.vertex())] = !color[graph.indexOf(from.vertex())];
        }

        @Override
        public void crossEdge(SearchNode from, SearchNode to) {
            //a cross edge produces a circuit
            //if from and two are on same level, it is an odd circuit
            //otherwise, they are one above the other
            if (from.level() != to.level()) {
                return;
            }
            oddCycle = new Cycle(graph);
            oddCycle.add(from.vertex());
            var parent = from.parent();
            oddCycle.add(parent.vertex());
            while (!parent.isAncestorOf(to)) {
                parent = parent.parent();
                oddCycle.add(parent.vertex());
            }
            oddCycle.reverse();
            while (!to.equals(parent)) {
                oddCycle.add(to.vertex());
                to = to.parent();
            }
            interrupt();
        }
    }

    //DFS uses slightly less memory
    /*
    private class DFSBipartiteVisitor implements DFSVisitor {

        @Override
        public void treeEdge(SearchNode from, SearchNode to) {
            color[graph.indexOf(to.vertex())] = !color[graph.indexOf(from.vertex())];
        }

        @Override
        public void backEdge(SearchNode from, SearchNode to) {
            //a back edge produces a circuit
            //if nodes from and to have different colors, it is an even circuit
            if (color[graph.indexOf(from.vertex())] != color[graph.indexOf(to.vertex())]) {
                return;
            }
            //if from and two have the same color, it is an odd circuit
            oddCycle = new Cycle(graph);
            SearchNode firstNode = to;
            SearchNode lastNode = from;
            while (!firstNode.equals(lastNode)) {
                oddCycle.add(lastNode.vertex());
                lastNode = lastNode.parent();
            }
            oddCycle.add(firstNode.vertex());
            oddCycle.reverse();
            interrupt();
        }
    }*/
}
