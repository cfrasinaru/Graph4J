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
import org.graph4j.GraphAlgorithm;
import org.graph4j.GraphTests;
import org.graph4j.Network;
import static org.graph4j.Network.FLOW;
import org.graph4j.NetworkBuilder;
import org.graph4j.flow.MaximumFlowAlgorithm;
import org.graph4j.measures.GraphMeasures;
import org.graph4j.util.Validator;
import org.graph4j.util.EdgeSet;
import org.graph4j.util.Path;
import org.graph4j.util.VertexQueue;
import org.graph4j.util.VertexSet;

/**
 * Determines a maximum size set of vertex disjoint paths between two vertices,
 * a minimum size set of vertices whose removal disconnects two vertices, the
 * vertex connectivity number.
 *
 * @author Cristian Frăsinaru
 */
public class VertexConnectivityAlgorithm extends GraphAlgorithm {

    private Network network;
    private VertexSet globalMinCut;
    private Integer connectivityNumber;
    private boolean computed;

    /**
     * Creates an algorithm for determining the vertex connectivity of a graph.
     *
     * @param graph the input graph.
     */
    public VertexConnectivityAlgorithm(Graph graph) {
        super(graph);
        createNetwork();
    }

    private void createNetwork() {
        int n = graph.numVertices();
        network = NetworkBuilder.numVertices(2 * n).buildNetwork();
        network.setSafeMode(false);
        //0..n-1 are a vertices
        //n..2n-1 are b vertices
        //for each edge vw in the graph, add (b_v,a_w), (b_w,a_v) in the target
        for (int v : graph.vertices()) {
            int vi = graph.indexOf(v);
            int av = vi;
            int bv = n + vi;
            network.addEdge(av, bv, Graph.DEFAULT_VERTEX_WEIGHT);
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                int wi = graph.indexOf(it.next());
                if (vi > wi) {
                    continue;
                }
                int aw = wi;
                int bw = n + wi;
                network.addEdge(bv, aw, Double.POSITIVE_INFINITY);
                network.addEdge(bw, av, Double.POSITIVE_INFINITY);
            }
        }
    }

    /**
     * Determines the maximum size of a set of vertex disjoint paths between the
     * source and the target, without creating the paths. The source and the
     * target must not form an edge. The number is computed by solving the
     * corresponding maximum flow problem.
     *
     * @param source the source vertex number.
     * @param target the target vertex number.
     * @return the maximum size of a set of vertex disjoint paths between the
     * source and the target.
     */
    public int countMaximumDisjointPaths(int source, int target) {
        Validator.containsVertex(graph, source);
        Validator.containsVertex(graph, target);
        if (source == target) {
            return 0;
        }
        int bs = graph.indexOf(source) + graph.numVertices();
        int at = graph.indexOf(target);
        network.setSource(bs);
        network.setSink(at);
        network.resetEdgeData(FLOW, 0);
        return (int) MaximumFlowAlgorithm.getInstance(network).getMaximumFlowValue();
    }

    /**
     * For each target vertex, different from the source, determines the maximum
     * size of a set of vertex disjoint paths between the source and target,
     * without creating the paths - among these, it is returned <em>the
     * smallest</em> value. The number is computed by solving the corresponding
     * maximum flow problems.
     *
     * @param source the source vertex number.
     * @return the maximum size of a set of vertex disjoint paths between the
     * source and some other vertex of the graph.
     */
    public int countMaximumDisjointPaths(int source) {
        Validator.containsVertex(graph, source);
        int minSize = Integer.MAX_VALUE;
        for (int t : graph.vertices()) {
            if (source == t || graph.containsEdge(source, t)) {
                continue;
            }
            int size = countMaximumDisjointPaths(source, t);
            if (size < minSize) {
                minSize = size;
            }
        }
        return minSize;
    }

    /**
     * Computes a maximum size set of vertex disjoint paths between the source
     * and the target. The source and the target must not form an edge.
     *
     * @param source the source vertex number.
     * @param target the target vertex number.
     * @return a maximum size set of vertex disjoint paths between the source
     * and the target.
     */
    public List<Path> getMaximumDisjointPaths(int source, int target) {
        Validator.containsVertex(graph, source);
        Validator.containsVertex(graph, target);
        List<Path> allPaths = new ArrayList<>();
        if (source == target) {
            return allPaths;
        }
        //solve the maximum flow problem
        int n = graph.numVertices();
        int bs = graph.indexOf(source) + n;
        int at = graph.indexOf(target);
        network.setSource(bs);
        network.setSink(at);
        network.resetEdgeData(FLOW, 0);
        var maxFlowAlg = MaximumFlowAlgorithm.getInstance(network);
        int flowValue = (int) maxFlowAlg.getMaximumFlowValue();

        //the maximum flow can be expressed as a sum of flows of value 1
        //each of these flows corresponds to a path from source to target
        //for each v, store the outgoing edges that have flow=1
        int p = network.numVertices();
        VertexQueue[] sat = new VertexQueue[p]; //saturated edges
        for (int vi = 0; vi < p; vi++) {
            sat[vi] = new VertexQueue(network);
            for (var it = network.successorIterator(vi); it.hasNext();) {
                int ui = it.next();
                if (maxFlowAlg.getFlowValue(vi, ui) == 1) {
                    sat[vi].add(ui);
                }
            }
        }
        //create the paths        
        for (int k = 0; k < flowValue; k++) {
            Path path = new Path(graph);
            path.add(source);
            int vi = bs;
            while (vi != at) {
                vi = sat[vi].poll(); //a or b
                if (vi < n) {
                    path.add(graph.vertexAt(vi));
                }
            }
            allPaths.add(path);
        }
        return allPaths;
    }

    /**
     * Computes the set of vertices of minimum size whose removal disconnects
     * the source and the target. The source and the target must not form an
     * edge. The cutset is created by solving the corresponding maximum flow
     * problem.
     *
     * @param source the source vertex number.
     * @param target the target vertex number.
     * @return a set of vertices of minimum size whose removal disconnects the
     * source and the target.
     */
    public VertexSet getMinimumCut(int source, int target) {
        Validator.containsVertex(graph, source);
        Validator.containsVertex(graph, target);
        if (source == target || graph.containsEdge(source, target)) {
            return null;
        }
        int bs = graph.indexOf(source) + graph.numVertices();
        int at = graph.indexOf(target);
        network.setSource(bs);
        network.setSink(at);
        network.resetEdgeData(FLOW, 0);
        var alg = MaximumFlowAlgorithm.getInstance(network);
        EdgeSet edgeCut = alg.getMinimumCutEdges();
        VertexSet vertexCut = new VertexSet(graph, edgeCut.size());
        for (var e : edgeCut) {
            vertexCut.add(Math.min(e.source(), e.target()));
        }
        return vertexCut;
    }

    /**
     * For each target vertex, different from the source, determines the vertex
     * set of minimum size whose removal disconnects source and target - among
     * these, we return the cutset with minimum size.
     *
     * @param source the source vertex number.
     * @return a set of vertices of minimum size whose removal disconnects the
     * source from some part of the graph or {@code null} if no such set exists.
     */
    public VertexSet getMinimumCut(int source) {
        Validator.containsVertex(graph, source);
        VertexSet minCut = null;
        for (int t : graph.vertices()) {
            if (source == t || graph.containsEdge(source, t)) {
                continue;
            }
            VertexSet cut = getMinimumCut(source, t);
            if (minCut == null || cut.size() < minCut.size()) {
                minCut = cut;
            }
        }
        return minCut;
    }

    /**
     * Computes a minimum vertex cut, that is a set of vertices of minimum size
     * whose removal disconnects the graph. Complete graphs have no vertex cuts.
     *
     * @return a set of vertices of minimum size whose removal disconnects the
     * graph or {@code null} if no such set exists.
     */
    //SLOW
    public VertexSet getMinimumCut() {
        if (computed) {
            return globalMinCut;
        }
        int n = graph.numVertices();
        long m = graph.numEdges();
        int k = (int) Math.ceil(2 * m / n) + 1;
        this.globalMinCut = null;
        for (int i = 0; i < k; i++) {
            int s = graph.vertexAt(i);
            for (int j = 0; j < n; j++) {
                int t = graph.vertexAt(j);
                if (s == t || graph.containsEdge(s, t)) {
                    continue;
                }
                VertexSet cut = getMinimumCut(s, t);
                if (globalMinCut == null || cut.size() < globalMinCut.size()) {
                    globalMinCut = cut;
                }
            }
        }
        computed = true;
        connectivityNumber = globalMinCut.size();
        return globalMinCut;
    }

    /**
     * Computes a minimum vertex cut, that is a set of vertices of minimum size
     * whose removal disconnects the graph. Complete graphs have no vertex cuts.
     *
     * @return a set of vertices of minimum size whose removal disconnects the
     * graph or {@code null} if no such set exists.
     */
    /*
    public VertexSet getMinimumCut() {
        if (computed) {
            //return globalMinCut;
        }
        var temp = GraphBuilder.numVertices(2 * graph.numVertices()).buildGraph();
        temp.setSafeMode(false);
        //HOW?
        EdgeCut edgeCut = new StoerWagnerMinimumCut(temp).getMinimumCut();
        globalMinCut = new VertexSet(graph);
        for (var e : edgeCut.edges()) {
            globalMinCut.add(Math.min(e.source(), e.target()));
        }
        return globalMinCut;
    }*/
    /**
     * Computes the edge connectivity number, that is the minimum size of a set
     * of edges whose removal disconnects the graph. If the graph is complete,
     * it returns {@code n-1}, where {@code n} is the number of vertices in the
     * graph.
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
        } else if (GraphMeasures.minDegree(graph) == 1 || !GraphTests.isBiconnected(graph)) {
            connectivityNumber = 1;
        } else {
            connectivityNumber = getMinimumCut().size();
        }
        return connectivityNumber;
    }

}
