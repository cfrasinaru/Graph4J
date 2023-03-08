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
package org.graph4j.alg.flow;

import org.graph4j.Digraph;
import org.graph4j.alg.DirectedGraphAlgorithm;
import org.graph4j.util.EdgeSet;
import org.graph4j.util.VertexSet;

/**
 *
 * @author Cristian Frăsinaru
 */
public abstract class MaximumFlowBase extends DirectedGraphAlgorithm
        implements MaximumFlowAlgorithm {

    protected int source, sink, sourceIndex, sinkIndex;
    protected double[][] flowData;
    //
    protected VertexSet sourcePart;
    protected VertexSet sinkPart;
    protected EdgeSet cutEdges;

    /**
     *
     * @param graph the input network.
     * @param source the source vertex.
     * @param sink the sink vertex.
     */
    public MaximumFlowBase(Digraph graph, int source, int sink) {
        this(graph, source, sink, null);

    }

    /**
     *
     * @param graph the input network.
     * @param source the source vertex.
     * @param sink the sink vertex.
     * @param flow the initial flow.
     */
    public MaximumFlowBase(Digraph graph, int source, int sink, NetworkFlow flow) {
        super(graph);
        if (source == sink) {
            throw new IllegalArgumentException("Source and sink must be different.");
        }
        if (graph.isAllowingMultipleEdges() || graph.isAllowingSelfLoops()) {
            throw new IllegalArgumentException("Multigraphs and pseudographs are not supported.");
        }
        this.source = source;
        this.sink = sink;
        this.sourceIndex = graph.indexOf(source);
        this.sinkIndex = graph.indexOf(sink);
        if (flow != null) {
            initFlowData();
            for (var e : flow.edges()) {
                int v = e.source();
                int u = e.target();
                int pos = graph.adjListPos(v, u);
                flowData[graph.indexOf(v)][pos] = flow.get(e);
            }
        }
    }

    protected final void initFlowData() {
        flowData = new double[graph.numVertices()][];
        for (int v : graph.vertices()) {
            flowData[graph.indexOf(v)] = new double[graph.degree(v)];
        }
    }

    @Override
    public double getValue() {
        if (flowData == null) {
            compute();
        }
        double value = 0.0;
        for (var it = graph.successorIterator(source); it.hasNext();) {
            int u = it.next(); //s -> u
            value += flowData[sourceIndex][it.adjListPos()];
        }
        for (var it = graph.predecessorIterator(source); it.hasNext();) {
            int u = it.next(); //s <- u
            int ui = graph.indexOf(u);
            value -= flowData[ui][it.adjListPos()];
        }
        return value;

    }

    @Override
    public double getValue(int v, int u) {
        if (flowData == null) {
            compute();
        }
        int pos = graph.adjListPos(v, u);
        if (pos < 0) {
            return 0;
        }
        return flowData[graph.indexOf(v)][pos];
    }

    @Override
    public NetworkFlow getFlow() {
        NetworkFlow flow = new NetworkFlow(graph, source, sink);
        for (var v : graph.vertices()) {
            int vi = graph.indexOf(v);
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                it.next();
                flow.put(it.edge(), flowData[vi][it.adjListPos()]);
            }
        }
        return flow;
    }

    @Override
    public abstract VertexSet getSourcePart();

    @Override
    public abstract VertexSet getSinkPart();

    @Override
    public EdgeSet getCutEdges() {
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

    protected abstract void compute();

}
