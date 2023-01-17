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
package ro.uaic.info.graph.alg.connectivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.alg.DirectedGraphAlgorithm;
import ro.uaic.info.graph.GraphBuilder;
import ro.uaic.info.graph.model.VertexSet;
import ro.uaic.info.graph.model.VertexStack;
import ro.uaic.info.graph.search.DFSVisitor;
import ro.uaic.info.graph.search.DepthFirstSearch;
import ro.uaic.info.graph.search.SearchNode;

/**
 * Tarjan's strongly connected components algorithm is an algorithm in graph
 * theory for finding the strongly connected components (SCCs) of a directed
 * graph. It runs in linear time, matching the time bound for alternative
 * methods including Kosaraju's algorithm and the path-based strong component
 * algorithm
 *
 * @author Cristian Frăsinaru
 */
public class TarjanStrongConnectivity
        extends DirectedGraphAlgorithm implements StrongConnectivityAlgorithm {

    private Boolean stronglyConnected;
    private List<VertexSet> compSets;
    private int[] vertexComp; //which component a vertex belongs to

    public TarjanStrongConnectivity(Digraph digraph) {
        super(digraph);
    }

    @Override
    public boolean isStronglyConnected() {
        if (stronglyConnected != null) {
            return stronglyConnected;
        }
        compute(true);
        return stronglyConnected;
    }

    @Override
    public List<VertexSet> getStronglyConnectedSets() {
        if (compSets != null) {
            return compSets;
        }
        compute(false);
        return compSets;
    }

    @Override
    public List<Digraph> getStronglyConnectedComponents() {
        List<Digraph> subgraphs = new ArrayList<>();
        for (var set : compSets) {
            subgraphs.add(digraph.subgraph(set.vertices()));
        }
        return subgraphs;
    }

    //the main method which does the work
    protected void compute(boolean checkOnly) {
        this.compSets = new ArrayList<>();
        this.vertexComp = new int[digraph.numVertices()];
        var dfs = new DepthFirstSearch(digraph);
        dfs.traverse(new Visitor(checkOnly));
        if (stronglyConnected == null) {
            stronglyConnected = true;
        }
        if (dfs.isInterrupted()) {
            compSets = null;
        }
    }

    @Override
    public Digraph<Digraph, Integer> createCondensation() {
        if (compSets == null) {
            compute(false);
        }
        Digraph<Digraph, Integer> condensation = new GraphBuilder<Digraph, Integer>()
                .labeledVertices(getStronglyConnectedComponents()).buildDigraph();
        for (Iterator<Edge> it = digraph.edgeIterator(); it.hasNext();) {
            Edge e = it.next();
            int v = e.source();
            int u = e.target();
            int scv = vertexComp[digraph.indexOf(v)];
            int scu = vertexComp[digraph.indexOf(u)];
            if (scu != scv) {
                if (!condensation.containsEdge(scv, scu)) {
                    condensation.addLabeledEdge(scv, scu, 1);
                } else {
                    condensation.setEdgeLabel(scv, scu, 1 + condensation.getEdgeLabel(scv, scu));
                }
            }
        }
        return condensation;
    }

    private class Visitor implements DFSVisitor {

        private final boolean checkOnly;
        private final int[] low;
        private final VertexStack stack;

        public Visitor(boolean checkOnly) {
            this.checkOnly = checkOnly;
            this.low = new int[digraph.numVertices()];
            this.stack = new VertexStack(digraph);
        }

        @Override
        public void startVertex(SearchNode node) {
            int v = node.vertex();
            low[digraph.indexOf(v)] = node.order();
            stack.push(v);
        }

        @Override
        public void treeEdge(SearchNode from, SearchNode to) {
            int v = from.vertex();
            if (isRoot(from) && !stack.contains(v)) {
                stack.push(v);
            }
        }

        @Override
        public void backEdge(SearchNode from, SearchNode to) {
            int vi = digraph.indexOf(from.vertex());
            low[vi] = Math.min(low[vi], to.order());
        }

        @Override
        public void crossEdge(SearchNode from, SearchNode to) {
            int vi = digraph.indexOf(from.vertex());
            int ui = digraph.indexOf(to.vertex());
            if (stack.contains(ui)) {
                low[vi] = 0; //can reach the root
            }
        }

        @Override
        public void finishVertex(SearchNode node) {
            int v = node.vertex();
            int vi = digraph.indexOf(v);
            if (low[vi] == node.order()) {
                createComponent(v);
            }
        }

        @Override
        public void upward(SearchNode from, SearchNode to) {
            int vi = digraph.indexOf(from.vertex());
            int ui = digraph.indexOf(to.vertex());
            low[ui] = Math.min(low[ui], low[vi]);
        }

        private void createComponent(int u) {
            //u and the vertices on the stack up to u form a component
            if (stronglyConnected == null && compSets.size() > 0) {
                stronglyConnected = false;
                if (checkOnly) {
                    interrupt();
                }
            }
            var component = new VertexSet(digraph);
            int compIndex = compSets.size();
            int w;
            do {
                w = stack.pop();
                component.add(w);
                vertexComp[digraph.indexOf(w)] = compIndex;
            } while (w != u);
            compSets.add(component);
        }
    }
}
