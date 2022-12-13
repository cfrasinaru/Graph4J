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
package ro.uaic.info.graph.alg;

import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.Multigraph;
import ro.uaic.info.graph.search.BFSVisitor;
import ro.uaic.info.graph.search.BreadthFirstSearch;
import ro.uaic.info.graph.search.DFSVisitor;
import ro.uaic.info.graph.search.DepthFirstSearch;
import ro.uaic.info.graph.search.SearchNode;
import ro.uaic.info.graph.util.IntArrayList;

/**
 *
 *
 * @author Cristian Frăsinaru
 */
public class CycleDetector {

    private final Graph graph;
    private final boolean directed;
    private int[] cycle;

    public CycleDetector(Graph graph) {
        this.graph = graph;
        this.directed = (graph instanceof Multigraph);
    }

    public boolean containsCycle() {
        return findAnyCycle() != null;
    }

    /**
     * Uses DFS in order to find a cycle.
     *
     * @return
     */
    public int[] findAnyCycle() {
        cycle = null;
        new DepthFirstSearch(graph).traverse(new DFSCycleVisitor());
        return cycle;
    }

    /**
     * Uses DFS in order to find a cycle.
     *
     * @return
     */
    public int[] findShortestCycle() {
        cycle = null;
        new BreadthFirstSearch(graph).traverse(new BFSCycleVisitor());
        return cycle;
    }

    private void createCycleFromBackEdge(SearchNode from, SearchNode to) {
        var list = new IntArrayList();
        SearchNode firstNode = to;
        SearchNode lastNode = from;
        while (!firstNode.equals(lastNode)) {
            list.add(lastNode.vertex());
            lastNode = lastNode.parent();
        }
        list.add(firstNode.vertex());
        list.reverse();
        cycle = list.values();
    }

    private void createCycleFromCrossEdge(SearchNode from, SearchNode to) {
        var list = new IntArrayList();
        list.add(from.parent().vertex());
        list.add(from.vertex());
        list.add(to.vertex());
        cycle = list.values();
    }

    //
    private class DFSCycleVisitor implements DFSVisitor {

        @Override
        public void backEdge(SearchNode from, SearchNode to) {
            createCycleFromBackEdge(from, to);
            interrupt();
        }
    }

    //
    private class BFSCycleVisitor implements BFSVisitor {

        @Override
        public void backEdge(SearchNode from, SearchNode to) {
            createCycleFromBackEdge(from, to);
            interrupt();
        }

        @Override
        public void crossEdge(SearchNode from, SearchNode to) {
            if (!directed) {
                createCycleFromCrossEdge(from, to);
                interrupt();
            }
        }
    }

}
