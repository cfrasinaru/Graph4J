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
package org.graph4j.alg.connectivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.graph4j.Graph;
import org.graph4j.alg.SimpleGraphAlgorithm;
import org.graph4j.util.VertexSet;
import org.graph4j.traverse.DFSIterator;
import org.graph4j.util.CheckArguments;

/**
 *
 * @author Cristian Frăsinaru
 */
public class ConnectivityAlgorithm extends SimpleGraphAlgorithm {

    private Boolean connected;
    private List<VertexSet> connectedSets;
    private final Map<Integer, VertexSet> vertexSetMap = new HashMap<>();

    public ConnectivityAlgorithm(Graph graph) {
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
     * use {@link Graph#subgraph(VertexSet)  }.
     * <pre>
     *   Graph cc = graph.subgraph(set.vertices());
     * </pre>
     *
     * @return the list of connected sets.
     */
    public List<VertexSet> getConnectedSets() {
        if (connectedSets == null) {
            createConnectedSets();
        }
        return connectedSets;
    }

    /**
     * Returns the connected component the specified vertex belongs to.
     *
     * @param v a vertex number.
     * @return The connected set of v.
     */
    public VertexSet getConnectedSet(int v) {
        CheckArguments.graphContainsVertex(graph, v);
        var vset = vertexSetMap.get(v);
        if (vset != null) {
            return vset;
        }
        if (connectedSets != null) {
            for (VertexSet set : connectedSets) {
                if (set.contains(v)) {
                    vertexSetMap.put(v, set);
                    return set;
                }
            }
            throw new IllegalStateException();
        }
        vset = new VertexSet(graph);
        var dfs = new DFSIterator(graph, v);
        while (dfs.hasNext()) {
            var node = dfs.next();
            if (node.component() > 0) {
                //we finished creating a connected component
                break;
            }
            vset.add(node.vertex());
        }
        vertexSetMap.put(v, vset);
        return vset;
    }

    @Deprecated
    private List<Graph> getConnectedComponents() {
        if (connectedSets == null) {
            createConnectedSets();
        }
        List<Graph> components = new ArrayList<>(connectedSets.size());
        for (var set : connectedSets) {
            components.add(graph.subgraph(set));
        }
        return components;
    }

    //all of them
    private void createConnectedSets() {
        this.connectedSets = new ArrayList<>();
        int compIndex = 0;
        var vertexSet = new VertexSet(graph);
        var dfs = new DFSIterator(graph);
        while (dfs.hasNext()) {
            var node = dfs.next();
            if (node.component() > compIndex) {
                //we finished creating a connected component
                connectedSets.add(vertexSet);
                vertexSet = new VertexSet(graph);
                compIndex = node.component();
            }
            vertexSet.add(node.vertex());
        }
        if (!vertexSet.isEmpty()) {
            connectedSets.add(vertexSet);
        }
        connected = connectedSets.size() == 1;
    }

}
