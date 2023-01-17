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
package ro.uaic.info.graph.alg.connectivity;

import java.util.ArrayList;
import java.util.List;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.alg.SimpleGraphAlgorithm;
import ro.uaic.info.graph.model.VertexSet;
import ro.uaic.info.graph.search.DFSIterator;

/**
 *
 * @author Cristian Frăsinaru
 */
public class GraphConnectivity extends SimpleGraphAlgorithm {

    private Boolean connected;
    private List<VertexSet> components;

    public GraphConnectivity(Graph graph) {
        super(graph);
    }

    /**
     * A graph is connected if there is a path from any vertex to any other
     * vertex in the graph.
     *
     * @return {@code true} if the graph is connected
     */
    public boolean isConnected() {
        if (connected != null) {
            return connected;
        }
        var dfs = new DFSIterator(graph);
        connected = true;
        while (dfs.hasNext()) {
            if (dfs.next().component() > 0) {
                connected = false;
                break;
            }
        }        
        return connected;
    }

    /**
     * Each connected component is represented by its vertices. In order to
     * obtain the subgraph induced by a connected component vertices, you may
     * use {@link Graph#subgraph(int...)}.
     * <pre>
     *   Graph cc = graph.subgraph(component.vertices);
     * </pre>
     *
     * @return the list of connected components
     */
    public List<VertexSet> getComponents() {
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
        connected = components.size() == 1;
        return components;
    }

}
