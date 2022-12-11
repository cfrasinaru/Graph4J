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
package ro.uaic.info.graph.alg;

import java.util.ArrayList;
import java.util.List;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.search.DFSIterator;
import ro.uaic.info.graph.search.DepthFirstSearch;
import ro.uaic.info.graph.search.SearchNode;
import ro.uaic.info.graph.util.Tools;

/**
 *
 * @author Cristian Frăsinaru
 */
public class GraphConnectivity {

    private final Graph graph;
    private List<Graph> components;
    private List<Integer> current;
    private int compIndex;

    /**
     *
     * @param graph
     */
    public GraphConnectivity(Graph graph) {
        this.graph = graph;
    }

    /**
     *
     * @return
     */
    public boolean isConnected() {
        if (components != null) {
            return components.size() == 1;
        }
        var dfs = new DFSIterator(graph);
        while (dfs.hasNext()) {
            if (dfs.next().component() > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return
     */
    public List<Graph> components() {
        if (components != null) {
            return components;
        }
        this.components = new ArrayList<>();
        compIndex = -1;
        current = null;
        new DepthFirstSearch(graph).traverse(this::visit);
        if (current != null && !current.isEmpty()) {
            components.add(graph.subgraph(Tools.listAsArray(current)));
        }
        return components;
    }

    private boolean visit(SearchNode node) {
        if (node.component() > compIndex) {
            if (current != null) {
                components.add(graph.subgraph(Tools.listAsArray(current)));
            }
            current = new ArrayList<>();
            compIndex = node.component();
        }
        current.add(node.vertex());
        return true;
    }
}
