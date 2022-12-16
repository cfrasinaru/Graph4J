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

import java.util.ArrayList;
import java.util.List;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.search.DFSIterator;
import ro.uaic.info.graph.util.IntArrays;

/**
 *
 * @author Cristian Frăsinaru
 */
public class GraphConnectivity {

    private final Graph graph;
    private List<Graph> components;

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
        int compIndex = 0;
        List<Integer> vertices = new ArrayList<>();
        var dfs = new DFSIterator(graph);
        while (dfs.hasNext()) {
            var node = dfs.next();
            if (node.component() > compIndex) {
                //we finished creating a connected component
                addComponent(vertices);
                vertices = new ArrayList<>();
                compIndex = node.component();
            }
            vertices.add(node.vertex());
        }
        if (!vertices.isEmpty()) {
            addComponent(vertices);
        }
        return components;
    }

    private void addComponent(List<Integer> vertices) {
        components.add(graph.subgraph(IntArrays.fromList(vertices)));
    }

}
