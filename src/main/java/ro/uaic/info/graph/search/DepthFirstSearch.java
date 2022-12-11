/*
 * Copyright (C) 2022 Faculty of Computer Science Iasi, Romania
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
package ro.uaic.info.graph.search;

import ro.uaic.info.graph.Graph;

/**
 *
 * @author Cristian Frăsinaru
 */
public class DepthFirstSearch {

    protected final Graph graph;
    protected final int start;
    protected SearchVisitor visitor;
    //
    protected int orderIndex;
    protected int compIndex;
    protected boolean visited[];
    protected boolean cancelled;

    /**
     * 
     * @param graph 
     */
    public DepthFirstSearch(Graph graph) {
        this(graph, graph.isEmpty() ? -1 : graph.vertexAt(0));
    }

    /**
     *
     * @param graph
     * @param start
     */
    public DepthFirstSearch(Graph graph, int start) {
        this.graph = graph;
        this.start = start;
    }

    protected void init() {
        this.visited = new boolean[graph.numVertices()];
        orderIndex = 0;
        compIndex = 0;
        cancelled = false;
    }

    /**
     *
     * @param visitor
     */
    public void traverse(SearchVisitor visitor) {
        if (start < 0) {
            return;
        }
        init();
        this.visitor = visitor;
        visited[graph.indexOf(start)] = true;
        if (visitor == null
                || visitor.visit(new SearchNode(compIndex, start, 0, orderIndex++))) {
            dfs(start, 0);
        }
        for (int v : graph.vertices()) {
            if (cancelled) {
                return;
            }
            int vi = graph.indexOf(v);
            if (!visited[vi]) {
                compIndex++;
                visited[vi] = true;
                if (visitor == null
                        || visitor.visit(new SearchNode(compIndex, v, 0, orderIndex++))) {
                    dfs(v, 0);
                } else {
                    cancelled = true;
                }
            }
        }
    }

    protected void dfs(int v, int level) {
        for (int u : graph.neighbors(v)) {
            if (cancelled) {
                return;
            }
            int ui = graph.indexOf(u);
            if (!visited[ui]) {
                visited[ui] = true;
                if (visitor == null || visitor.visit(new SearchNode(compIndex, u, level + 1, orderIndex++))) {
                    dfs(u, level + 1);
                } else {
                    cancelled = true;
                }
            }
        }
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
