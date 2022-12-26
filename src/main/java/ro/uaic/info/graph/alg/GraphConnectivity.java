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
import ro.uaic.info.graph.VertexSet;
import ro.uaic.info.graph.search.DFSIterator;
import ro.uaic.info.graph.util.IntArrays;

/**
 *
 * @author Cristian Frăsinaru
 */
public class GraphConnectivity extends SimpleGraphAlgorithm {

    private List<VertexSet> components;

    public GraphConnectivity(Graph graph) {
        super(graph);
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
    public List<VertexSet> components() {
        if (components != null) {
            return components;
        }
        this.components = new ArrayList<>();
        int compIndex = 0;
        var vertexSet = new VertexSet(graph);
        var dfs = new DFSIterator(graph);
        while (dfs.hasNext()) {
            var node = dfs.next();
            if (node.component() > compIndex) {
                //we finished creating a connected component
                components.add(vertexSet);
                vertexSet = new VertexSet(graph);
                compIndex = node.component();
            }
            vertexSet.add(node.vertex());
        }
        if (!vertexSet.isEmpty()) {
            components.add(vertexSet);
        }
        return components;
    }

    @Deprecated
    private void addComponent(VertexSet vertices) {
        //components.add(graph.subgraph(IntArrays.fromList(vertices)));
    }

}
