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
package org.graph4j.flow;

import org.graph4j.Network;
import static org.graph4j.Network.FLOW;
import org.graph4j.util.EdgeSet;
import org.graph4j.util.VertexSet;

/**
 *
 * @author Cristian Frăsinaru
 */
public abstract class MaximumFlowBase
        implements MaximumFlowAlgorithm {

    protected final Network graph;
    protected final FlowData initialFlow;
    protected final int numVertices;
    protected int source, sink, sourceIndex, sinkIndex;
    protected boolean computed;
    //
    protected VertexSet sourcePart;
    protected VertexSet sinkPart;
    protected EdgeSet cutEdges;
    //
    protected EdmondsKarpMaximumFlow ekAlg;

    /**
     * Creates an algorithm for computing the maximum flow in a network. If the
     * network edges have flow values the algorithm will take them into account.
     *
     * @param graph the input network.
     */
    public MaximumFlowBase(Network graph) {
        this(graph, null);

    }

    /**
     * Creates an algorithm for computing the maximum flow in a network,
     * initializing the edge flows using the values in the specified in
     * {@code initialFlow}.
     *
     * @param graph the input network.
     * @param initialFlow the initial flow.
     */
    public MaximumFlowBase(Network graph, FlowData initialFlow) {
        this.graph = graph;
        this.numVertices = graph.numVertices();
        this.source = graph.getSource();
        this.sink = graph.getSink();
        this.sourceIndex = graph.indexOf(source);
        this.sinkIndex = graph.indexOf(sink);
        this.initialFlow = initialFlow;
    }

    protected void initFlow() {
        graph.resetEdgeData(FLOW, 0);
        if (initialFlow != null) {
            for (var e : initialFlow.edges()) {
                graph.setEdgeData(FLOW, e.source(), e.target(), initialFlow.get(e));
            }
        }
    }

    @Override
    public double getMaximumFlowValue() {
        if (!computed) {
            computeMaximumFlow();
        }
        double value = 0.0;
        for (var it = graph.successorIterator(source); it.hasNext();) {
            it.next(); //s ->
            value += it.getEdgeData(FLOW);
        }
        for (var it = graph.predecessorIterator(source); it.hasNext();) {
            it.next(); //s <- u
            value -= it.getEdgeData(FLOW);
        }
        return value;

    }

    @Override
    public double getFlowValue(int v, int u) {
        if (!computed) {
            computeMaximumFlow();
        }
        return graph.getEdgeData(FLOW, v, u);
    }

    @Override
    public FlowData getMaximumFlowData() {
        if (!computed) {
            computeMaximumFlow();
        }
        FlowData flowData = new FlowData(graph);
        for (var v : graph.vertices()) {
            for (var it = graph.successorIterator(v); it.hasNext();) {
                it.next();
                flowData.put(it.edge(), it.getEdgeData(FLOW));
            }
        }
        return flowData;
    }

    @Override
    public VertexSet getSourcePart() {
        if (sourcePart != null) {
            return sourcePart;
        }
        if (!computed) {
            computeMaximumFlow();
        }
        if (ekAlg == null) {
            ekAlg = new EdmondsKarpMaximumFlow(graph, getMaximumFlowData());
        }
        sourcePart = ekAlg.getSourcePart();
        return sourcePart;
    }

    @Override
    public VertexSet getSinkPart() {
        if (sinkPart != null) {
            return sinkPart;
        }
        if (!computed) {
            computeMaximumFlow();
        }
        if (ekAlg == null) {
            ekAlg = new EdmondsKarpMaximumFlow(graph, getMaximumFlowData());
        }
        sinkPart = ekAlg.getSinkPart();
        return sinkPart;
    }

    @Override
    public EdgeSet getMinimumCutEdges() {
        if (cutEdges != null) {
            return cutEdges;
        }
        getSourcePart();
        getSinkPart();
        cutEdges = new EdgeSet(graph);
        for (int v : sourcePart) {
            for (var it = graph.successorIterator(v); it.hasNext();) {
                int u = it.next();
                if (sinkPart.contains(u)) {
                    cutEdges.add(v, u);
                }
            }
        }
        return cutEdges;
    }

    public abstract void computeMaximumFlow();

}
