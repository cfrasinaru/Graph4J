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
package org.graph4j.connectivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.graph4j.Graph;
import org.graph4j.SimpleGraphAlgorithm;
import org.graph4j.util.VertexSet;
import org.graph4j.traversal.DFSIterator;
import org.graph4j.util.Validator;

/**
 * Determines the connected components of a graph. In case of directed graph,
 * the algorithm is executed on its support graph, analyzing the
 * weak-connectivity of the digraph.
 *
 * @author Cristian Frăsinaru
 */
public class ConnectivityAlgorithm extends SimpleGraphAlgorithm {

    private Boolean connected;
    private List<VertexSet> connectedSets;
    private final Map<Integer, VertexSet> vertexSetMap = new HashMap<>();
    private List<Graph> components;
    private final Map<Integer, Graph> componentMap = new HashMap<>();

    /**
     * Creates an algorithm for analyzing the connectivity of a graph.
     *
     * @param graph the input graph.
     */
    public ConnectivityAlgorithm(Graph graph) {
        super(graph);
    }

    /**
     * A graph is connected if there is a path from any vertex to any other
     * vertex in the graph.
     *
     * @return {@code true} if the graph is connected.
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
     * Determines the number of connected components, without creating the
     * actual components.
     *
     * @return the number of connected components.
     */
    public int countConnectedComponents() {
        if (connectedSets != null) {
            return connectedSets.size();
        }
        var dfs = new DFSIterator(graph);
        int comp = 0;
        while (dfs.hasNext()) {
            comp = dfs.next().component();
        }
        return comp + 1;
    }

    /**
     * Determines if there is a path from v to u in the graph.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return {@code true} if v and u are connected, {@code false} otherwise.
     */
    public boolean hasPath(int v, int u) {
        if (connected) {
            return true;
        }
        if (connectedSets != null) {
            return getConnectedSet(v).contains(u);
        }
        var dfs = new DFSIterator(graph, v);
        while (dfs.hasNext()) {
            var node = dfs.next();
            if (node.component() > 0) {
                break;
            }
            if (node.vertex() == u) {
                return true;
            }
        }
        return false;
    }

    /**
     * Each connected component is represented by its vertices. In order to
     * obtain the subgraph induced by the vertices of a connected component, you
     * may use {@link Graph#subgraph(VertexSet) }.
     * <pre>
     *   Graph cc = graph.subgraph(set.vertices());
     * </pre>
     *
     * @return the list of the connected sets.
     */
    public List<VertexSet> getConnectedSets() {
        if (connectedSets == null) {
            createConnectedSets();
        }
        return connectedSets;
    }

    /**
     * Returns the vertex set of the connected component the specified vertex
     * belongs to.
     *
     * @param v a vertex number.
     * @return the connected set containing v.
     */
    public VertexSet getConnectedSet(int v) {
        Validator.containsVertex(graph, v);
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

    /**
     * Returns the connected components of the graph. If only the vertices of
     * the connected components are needed, the {@link #getConnectedSets()}
     * method should be used.
     *
     *
     * @return the connected components.
     */
    public List<Graph> getConnectedComponents() {
        if (components != null) {
            return components;
        }
        if (connectedSets == null) {
            createConnectedSets();
        }
        components = new ArrayList<>(connectedSets.size());
        for (var set : connectedSets) {
            components.add(graph.subgraph(set));
        }
        return components;
    }

    /**
     * Returns the connected component the specified vertex belongs to. If only
     * the vertices of the connected component are needed, the
     * {@link #getConnectedSet(int)} method should be used.
     *
     * @param v a vertex number.
     * @return the connected component containing v.
     */
    public Graph getConnectedComponent(int v) {
        Validator.containsVertex(graph, v);
        var vcomp = componentMap.get(v);
        if (vcomp != null) {
            return vcomp;
        }
        var vset = getConnectedSet(v);
        vcomp = graph.subgraph(vset);
        componentMap.put(v, vcomp);
        return vcomp;
    }
}
