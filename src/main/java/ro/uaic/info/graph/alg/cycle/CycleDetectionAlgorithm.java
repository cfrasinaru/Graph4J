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
package ro.uaic.info.graph.alg.cycle;

import ro.uaic.info.graph.model.Cycle;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.Multigraph;
import ro.uaic.info.graph.Pseudograph;
import ro.uaic.info.graph.alg.GraphAlgorithm;
import ro.uaic.info.graph.traverse.BFSTraverser;
import ro.uaic.info.graph.traverse.DFSVisitor;
import ro.uaic.info.graph.traverse.DFSTraverser;
import ro.uaic.info.graph.traverse.SearchNode;
import ro.uaic.info.graph.util.CheckArguments;
import ro.uaic.info.graph.traverse.BFSVisitor;

/**
 *
 *
 *
 * @author Cristian Frăsinaru
 */
public class CycleDetectionAlgorithm extends GraphAlgorithm {

    private int target;
    private int parity;
    private boolean shortest;

    public CycleDetectionAlgorithm(Graph graph) {
        super(graph);
    }

    private void reset() {
        this.target = -1;
        this.parity = -1;
        this.shortest = false;
    }

    //cycles of length 1 or 2
    //self-loops in pseudographs
    //symmetrical arcs in digraphs
    //multiple edges in undirected multigraphs
    private Cycle checkSpecialCases() {
        int[] vertices = target < 0 ? graph.vertices() : new int[]{target};
        if (graph instanceof Pseudograph && (parity != 2)) {
            for (int v : vertices) {
                if (graph.containsEdge(v, v)) {
                    return new Cycle(graph, new int[]{v});
                }
            }
        }
        if (directed && (parity != 1)) {
            for (int v : vertices) {
                for (int u : graph.neighbors(v)) {
                    if (graph.containsEdge(u, v)) {
                        return new Cycle(graph, new int[]{v, u});
                    }
                }
            }
        } else {
            if (graph instanceof Multigraph && (parity != 1)) {
                for (int v : vertices) {
                    for (int u : graph.neighbors(v)) {
                        if (((Multigraph) graph).multiplicity(v, u) > 1) {
                            return new Cycle(graph, new int[]{u, v});
                        }
                    }
                }
            }
        }
        return null;
    }

    private Cycle findCycle() {
        if (graph.isEmpty()) {
            return null;
        }
        Cycle cycle = checkSpecialCases();
        if (cycle != null) {
            return cycle;
        }
        if (shortest) {
            return bfs();
        } else {
            return dfs();
        }

    }

    /**
     *
     * @return
     */
    public boolean containsCycle() {
        //return findAnyCycle() != null; //slower, the Cycle may not be required
        var dfs = new DFSTraverser(graph);
        dfs.traverse(new DFSVisitor() {
            @Override
            public void backEdge(SearchNode from, SearchNode to) {
                interrupt();
            }
        });
        return dfs.isInterrupted();        
    }

    /**
     * Uses DFS in order to find a cycle.
     *
     * @return
     */
    public Cycle findAnyCycle() {
        reset();
        return findCycle();
    }

    /**
     *
     * @param target
     * @return
     */
    public Cycle findAnyCycle(int target) {
        CheckArguments.graphContainsVertex(graph, target);
        reset();
        this.target = target;
        return findCycle();
    }

    /**
     * Uses BFS in order to find a cycle.
     *
     * @return
     */
    public Cycle findShortestCycle() {
        reset();
        this.shortest = true;
        return findCycle();
    }

    /**
     *
     * @param target
     * @return
     */
    public Cycle findShortestCycle(int target) {
        CheckArguments.graphContainsVertex(graph, target);
        reset();
        this.target = target;
        this.shortest = true;
        return findCycle();
    }

    /**
     *
     * @return
     */
    public Cycle findOddCycle() {
        reset();
        this.parity = 1;
        return findCycle();
    }

    /**
     *
     * @return
     */
    public Cycle findEvenCycle() {
        reset();
        this.parity = 0;
        return findCycle();
    }

    private Cycle dfs() {
        var visitor = new DFSCycleVisitor();
        new DFSTraverser(graph).traverse(
                target < 0 ? graph.vertexAt(0) : target, visitor);
        return visitor.cycle;
    }

    private Cycle bfs() {
        var visitor = new BFSCycleVisitor();
        new BFSTraverser(graph).traverse(
                target < 0 ? graph.vertexAt(0) : target, visitor);
        return visitor.cycle;
    }

    private Cycle createCycleFromBackEdge(SearchNode from, SearchNode to) {
        var cycle = new Cycle(graph);
        SearchNode firstNode = to;
        SearchNode lastNode = from;
        while (!firstNode.equals(lastNode)) {
            cycle.add(lastNode.vertex());
            lastNode = lastNode.parent();
        }
        cycle.add(firstNode.vertex());
        cycle.reverse();
        return cycle;
    }

    //only for undirected graphs
    //we have to find the closest ancestor of the nodes forming the cross edge
    //to is at the same level with from, or a level below
    private Cycle createCycleFromCrossEdge(SearchNode from, SearchNode to) {
        var cycle = new Cycle(graph);
        cycle.add(from.vertex());
        var parent = from.parent();
        cycle.add(parent.vertex());
        while (!parent.isAncestorOf(to)) {
            parent = parent.parent();
            cycle.add(parent.vertex());
        }
        cycle.reverse();
        while (!to.equals(parent)) {
            cycle.add(to.vertex());
            to = to.parent();
        }
        return cycle;
    }

    //
    private class DFSCycleVisitor implements DFSVisitor {

        Cycle cycle;

        @Override
        public void treeEdge(SearchNode from, SearchNode to) {
            //finding the cycle before backEdge
            if (target >= 0
                    && (directed || from.vertex() != target)
                    && graph.containsEdge(to.vertex(), target)) {
                Cycle temp = createCycleFromBackEdge(to, new SearchNode(target));
                if (parity < 0 || cycle.length() % 2 == parity) {
                    cycle = temp;
                    interrupt();
                }
            }
        }

        @Override
        public void backEdge(SearchNode from, SearchNode to) {
            if (target < 0) {
                Cycle temp = createCycleFromBackEdge(from, to);
                if (parity < 0 || temp.length() % 2 == parity) {
                    cycle = temp;
                    interrupt();
                }
            }
        }
    }

    //
    private class BFSCycleVisitor implements BFSVisitor {

        Cycle cycle;
        private final SearchNode targetNode = new SearchNode(target);
        //the root of the bfs tree is the targetNode

        @Override
        public void backEdge(SearchNode from, SearchNode to) {
            if (target < 0 || to.vertex() == target) {
                analyze(createCycleFromBackEdge(from, to));
            }
        }

        @Override
        public void crossEdge(SearchNode from, SearchNode to) {
            //a cross edge produces a circuit only for undirected graphs
            //the circuit has odd length
            if (!directed
                    && (target < 0 || SearchNode.nearestAncestor(from, to).equals(targetNode))) {
                analyze(createCycleFromCrossEdge(from, to));
            }
        }

        private void analyze(Cycle temp) {
            if (parity >= 0 && temp.length() % 2 != parity) {
                return;
            }
            if (cycle == null || temp.length() < cycle.length()) {
                cycle = temp;
                if (cycle.length() == 3) {
                    interrupt();
                }
            }
        }

    }

}
