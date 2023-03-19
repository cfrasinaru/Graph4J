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
package org.graph4j.alg.connectivity;

import org.graph4j.Graph;
import org.graph4j.alg.SimpleGraphAlgorithm;
import org.graph4j.util.VertexStack;
import org.graph4j.traverse.DFSVisitor;
import org.graph4j.traverse.DFSTraverser;
import org.graph4j.traverse.SearchNode;
import org.graph4j.util.EdgeSet;

/**
 * A bridge in an undirected graph is defined as an edge which, when removed,
 * increases the number of connected components in the graph).
 *
 * The algorithm runs in linea time, and is based on depth-first search.
 *
 *
 * @author Cristian Frăsinaru
 */
public class BridgeDetectionAlgorithm extends SimpleGraphAlgorithm {

    private Boolean bridgeless;
    private EdgeSet bridges;

    /**
     *
     * @param graph the input graph.
     */
    public BridgeDetectionAlgorithm(Graph graph) {
        super(graph);
    }

    /**
     *
     * @return {@code true} if the graph contains no bridge.
     */
    public boolean isBridgeless() {
        if (bridgeless != null) {
            return bridgeless;
        }
        if (graph.numVertices() < 2) {
            return false;
        }
        compute(true);
        return bridgeless;
    }

    /**
     *
     * @return the bridges of the graph.
     */
    public EdgeSet getBridges() {
        if (bridges == null) {
            compute(false);
        }
        return bridges;
    }

    private void compute(boolean checkOnly) {
        this.bridges = new EdgeSet(graph);
        var dfs = new DFSTraverser(graph);
        dfs.traverse(new Visitor(checkOnly));
        if (bridgeless == null) {
            bridgeless = true;
        }
        if (dfs.isInterrupted()) {
            bridges = null;
        }
    }

    private class Visitor implements DFSVisitor {

        private final boolean checkOnly;
        private final int[] low;
        private final VertexStack stack;

        public Visitor(boolean checkOnly) {
            this.checkOnly = checkOnly;
            this.low = new int[graph.numVertices()];
            this.stack = new VertexStack(graph);
        }

        @Override
        public void startVertex(SearchNode node) {
            int v = node.vertex();
            //by default, the lowpoint is the dfs visit time (order)
            low[graph.indexOf(v)] = node.order();
            stack.push(v);
        }

        @Override
        public void backEdge(SearchNode from, SearchNode to) {
            //change the lowpoint of v=from.vertex
            int vi = graph.indexOf(from.vertex());
            low[vi] = Math.min(low[vi], to.order());
        }

        @Override
        public void upward(SearchNode from, SearchNode to) {
            int v = from.vertex();
            int u = to.vertex();
            int vi = graph.indexOf(v);
            int ui = graph.indexOf(u);
            //going up v -> u
            //the lowpoint of u is atleast the lowpoint of v 
            low[ui] = Math.min(low[ui], low[vi]);
            if (low[vi] > to.order()) {
                bridgeless = false;
                if (checkOnly) {
                    interrupt();
                }
                bridges.add(u, v);
            }
        }

    }
}
