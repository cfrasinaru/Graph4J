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
package org.graph4j.connectivity;

import java.util.ArrayList;
import java.util.List;
import org.graph4j.Graph;
import org.graph4j.GraphUtils;
import org.graph4j.GraphAlgorithm;
import org.graph4j.GraphTests;
import org.graph4j.Network;
import static org.graph4j.Network.CAPACITY;
import static org.graph4j.Network.FLOW;
import org.graph4j.flow.MaximumFlowAlgorithm;
import org.graph4j.generators.EdgeDataGenerator;
import org.graph4j.measures.GraphMeasures;
import org.graph4j.util.Path;
import org.graph4j.util.Validator;
import org.graph4j.util.VertexQueue;

/**
 * Determines a maximum size set of edge disjoint paths between two vertices, a
 * minimum size set of edges whose removal disconnects two vertices, the minimum
 * cardinality edge cut and the edge connectivity number.
 *
 * @see StoerWagnerMinimumCut
 * @author Cristian Frăsinaru
 */
public class EdgeConnectivityAlgorithm extends GraphAlgorithm {

    private Network network;
    private EdgeCut globalMinCut;
    private Integer connectivityNumber;

    /**
     * Creates an algorithm for determining the edge connectivity of a graph.
     *
     * @param graph the input graph.
     */
    public EdgeConnectivityAlgorithm(Graph graph) {
        super(graph);
    }

    private void createNetwork() {
        network = GraphUtils.toNetwork(graph);
        //capacity of each arc is 1
        new EdgeDataGenerator(network, CAPACITY).fill(Graph.DEFAULT_EDGE_WEIGHT);
    }

    /**
     * Determines the maximum size of a set of edge disjoint paths between the
     * source and the target without creating the paths. The number is computed
     * by solving the corresponding maximum flow problem.
     *
     * @param source the source vertex number.
     * @param target the target vertex number.
     * @return the maximum size of a set of edge disjoint paths between the
     * source and the target.
     */
    public int countMaximumDisjointPaths(int source, int target) {
        Validator.containsVertex(graph, source);
        Validator.containsVertex(graph, target);
        if (source == target) {
            return 0;
        }
        if (network == null) {
            createNetwork();
        }
        network.setSource(source);
        network.setSink(target);
        network.resetEdgeData(FLOW, 0);
        return (int) MaximumFlowAlgorithm.getInstance(network).getMaximumFlowValue();
    }

    /**
     * Computes a minimum cardinality edge cut, that is a set of edges of
     * minimum size whose removal disconnects the source and the target. The cut
     * is created by solving the corresponding maximum flow problem.
     *
     * @param source the source vertex number.
     * @param target the target vertex number.
     * @return the minimum cut whose removal disconnects the source and the
     * target.
     */
    public EdgeCut getMinimumCut(int source, int target) {
        Validator.containsVertex(graph, source);
        Validator.containsVertex(graph, target);
        if (source == target) {
            throw new IllegalArgumentException(
                    "The source and target vertices must be different.");
        }
        if (network == null) {
            createNetwork();
        }
        network.setSource(source);
        network.setSink(target);
        network.resetEdgeData(FLOW, 0);
        var alg = MaximumFlowAlgorithm.getInstance(network);
        return new EdgeCut(graph, alg.getMinimumCutEdges(), alg.getMaximumFlowValue());
    }

    /**
     * Computes a maximum size set of edge disjoint paths between the source and
     * the target.
     *
     * @param source the source vertex number.
     * @param target the target vertex number.
     * @return a maximum size set of edge disjoint paths between the source and
     * the target.
     */
    public List<Path> getMaximumDisjointPaths(int source, int target) {
        Validator.containsVertex(graph, source);
        Validator.containsVertex(graph, target);
        List<Path> allPaths = new ArrayList<>();
        if (source == target) {
            return allPaths;
        }
        if (network == null) {
            createNetwork();
        }
        network.setSource(source);
        network.setSink(target);
        network.resetEdgeData(FLOW, 0);

        //solve the maximum flow problem
        var maxFlowAlg = MaximumFlowAlgorithm.getInstance(network);
        int flowValue = (int) maxFlowAlg.getMaximumFlowValue();

        //the maximum flow can be expressed as a sum of flows of value 1
        //each of these flows corresponds to a path from source to target
        //for each v, store the outgoing edges that have flow=1
        int n = network.numVertices();
        VertexQueue[] sat = new VertexQueue[n]; //saturated edges
        for (int i = 0; i < n; i++) {
            sat[i] = new VertexQueue(network);
            int v = network.vertexAt(i);
            for (var it = network.successorIterator(v); it.hasNext();) {
                int u = it.next();
                if (maxFlowAlg.getFlowValue(v, u) == 1) {
                    sat[i].add(u);
                }
            }
        }
        //create the paths
        for (int i = 0; i < flowValue; i++) {
            Path path = new Path(graph);
            path.add(source);
            int v = source;
            while (v != target) {
                v = sat[network.indexOf(v)].poll();
                path.add(v);
            }
            allPaths.add(path);
        }
        return allPaths;
    }

    /**
     * Computes a set of edges of minimum size whose removal disconnects the
     * graph.
     *
     * @return a set of edges of minimum size whose removal disconnects the
     * graph.
     */
    public EdgeCut getMinimumCut() {
        if (globalMinCut == null) {
            globalMinCut = new StoerWagnerMinimumCut(graph, true).getMinimumCut();
        }
        connectivityNumber = globalMinCut.size();
        return globalMinCut;
    }

    /*
    //SLOW
    public EdgeSet getMinimumCut() {
        int s = 0;
        EdgeSet minCut = null;
        for (int i = 1, n = graph.numVertices(); i < n; i++) {
            int t = graph.vertexAt(i);
            EdgeSet cut = getMinimumCut(s, t);
            if (minCut == null || cut.size() < minCut.size()) {
                minCut = cut;
            }
        }
        return minCut;
    }*/
    /**
     * Computes the edge connectivity number, that is the minimum size of a set
     * of edges whose removal disconnects the graph.
     *
     * @return the edge connectivity number.
     */
    public int getConnectivityNumber() {
        if (connectivityNumber != null) {
            return connectivityNumber;
        }
        if (graph.isComplete()) {
            connectivityNumber = graph.numVertices() - 1;
        } else if (graph.isEmpty() || !GraphTests.isConnected(graph)) {
            connectivityNumber = 0;
        } else if (GraphMeasures.minDegree(graph) == 1 || !GraphTests.isBridgeless(graph)) {
            connectivityNumber = 1;
        } else {
            connectivityNumber = getMinimumCut().size();
        }
        return connectivityNumber;
    }

}
