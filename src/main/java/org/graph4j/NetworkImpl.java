/*
 * Copyright (C) 2024 Cristian Frăsinaru and contributors
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
package org.graph4j;

import java.util.Collection;
import org.graph4j.flow.InvalidFlowException;
import org.graph4j.util.Validator;
import org.graph4j.util.VertexSet;

/**
 *
 * @author Cristian Frăsinaru
 */
class NetworkImpl<V, E> extends DigraphImpl<V, E> implements Network<V, E> {

    protected int source;
    protected int sink;

    protected NetworkImpl() {
    }

    protected NetworkImpl(int[] vertices, int maxVertices, int avgDegree,
            boolean directed, boolean allowingMultipleEdges, boolean allowingSelfLoops,
            int vertexDataSize, int edgeDataSize) {
        super(vertices, maxVertices, avgDegree, directed, allowingMultipleEdges, allowingSelfLoops,
                vertexDataSize, edgeDataSize);
    }

    @Override
    protected NetworkImpl newInstance() {
        return new NetworkImpl();
    }

    @Override
    protected NetworkImpl newInstance(int[] vertices, int maxVertices, int avgDegree,
            boolean directed, boolean allowingMultipleEdges, boolean allowingSelfLoops,
            int vertexDataSize, int edgeDataSize) {
        return new NetworkImpl(vertices, maxVertices, avgDegree, directed,
                allowingMultipleEdges, allowingSelfLoops, vertexDataSize, edgeDataSize);
    }

    @Override
    public Network<V, E> copy() {
        return copy(true, true, true, true, true);
    }

    @Override
    public Network<V, E> copy(boolean vertexWeights, boolean vertexLabels, boolean edges, boolean edgeWeights, boolean edgeLabels) {
        var copy = (NetworkImpl<V, E>) super.copy(vertexWeights, vertexLabels, edges, edgeWeights, edgeLabels);
        copy.source = source;
        copy.sink = sink;
        return copy;
    }

    @Override
    protected void initEdgeData() {
        //WEIGHT(0), CAPACITY(1), COST(2), FLOW(3)
        this.edgeData = new double[4][][];

    }

    @Override
    public int addEdge(int v, int u, double capacity) {
        int pos = addEdge(v, u);
        if (pos < 0) {
            return pos;
        }
        setEdgeDataAt(CAPACITY, indexOf(v), pos, capacity);
        return pos;
    }

    @Override
    public int addLabeledEdge(int v, int u, E label, double capacity) {
        int pos = addLabeledEdge(v, u, label);
        if (pos < 0) {
            return pos;
        }
        setEdgeDataAt(CAPACITY, indexOf(v), pos, capacity);
        return pos;
    }

    @Override
    public int addEdge(int v, int u, double capacity, double cost) {
        int pos = addEdge(v, u);
        if (pos < 0) {
            return pos;
        }
        int vi = indexOf(v);
        setEdgeDataAt(CAPACITY, vi, pos, capacity);
        setEdgeDataAt(COST, vi, pos, cost);
        return pos;
    }

    @Override
    public int addLabeledEdge(int v, int u, E label, double capacity, double cost) {
        int pos = addLabeledEdge(v, u, label);
        if (pos < 0) {
            return pos;
        }
        int vi = indexOf(v);
        setEdgeDataAt(CAPACITY, vi, pos, capacity);
        setEdgeDataAt(COST, vi, pos, cost);
        return pos;
    }

    @Override
    protected void setEdgeDataAt(int dataType, int vi, int pos, double value) {
        if (dataType == CAPACITY) {
            if (value < 0) {
                throw new IllegalArgumentException("Capacity must be non-negative: " + value);
            }
            double flow = getEdgeDataAt(FLOW, vi, pos, 0);
            if (value < flow) {
                throw new IllegalArgumentException("Capacity must not be smaller than flow: " + value + " < " + flow);
            }
        } else if (dataType == FLOW) {
            if (value < 0) {
                throw new IllegalArgumentException("Flow must be non-negative: " + value);
            }
            double capacity = getEdgeDataAt(CAPACITY, vi, pos, 0);
            if (value > capacity) {
                throw new IllegalArgumentException("Flow must not be larger than capacity: " + value + " > " + capacity);
            }
        }
        super.setEdgeDataAt(dataType, vi, pos, value);
    }

    @Override
    public void checkFlow() {
        checkFlowOrPreflow(false);
    }

    @Override
    public void checkPreflow() {
        checkFlowOrPreflow(true);
    }

    private void checkFlowOrPreflow(boolean preflow) {
        double[] in = new double[numVertices];
        double[] out = new double[numVertices];
        for (var it = edgeIterator(); it.hasNext();) {
            var e = it.next();
            int vi = indexOf(e.source());
            int ui = indexOf(e.target());
            double flow = it.getData(FLOW);
            if (flow < 0) {
                throw new InvalidFlowException("Edge " + e + " has a negative flow value: " + flow);
            }
            double capacity = it.getData(CAPACITY); //capacity
            if (flow > capacity) {
                throw new InvalidFlowException(
                        "Edge " + e + " has the flow greater than capacity: " + flow + " > " + capacity);
            }
            out[vi] += flow;
            in[ui] += flow;
        }
        int si = indexOf(source);
        int ti = indexOf(sink);
        for (int i = 0; i < numVertices; i++) {
            if (i == si || i == ti) {
                continue;
            }
            if (preflow) {
                if (in[i] < out[i]) {
                    throw new InvalidFlowException(
                            "Vertex " + vertexAt(i) + " violates the preflow constraint: "
                            + "in=" + in[i] + " < out=" + out[i]);
                }
            } else {
                if (in[i] != out[i]) {
                    throw new InvalidFlowException(
                            "Vertex " + vertexAt(i) + " violates the flow conservation constraint: "
                            + "in=" + in[i] + " != out=" + out[i]);
                }
            }
        }
    }

    @Override
    public void removeVertex(int v) {
        if (v == source) {
            throw new IllegalArgumentException("Source vertex cannot be removed: " + v);
        }
        if (v == sink) {
            throw new IllegalArgumentException("Sink vertex cannot be removed: " + v);
        }
        super.removeVertex(v);
    }

    @Override
    public Network<V, E> subgraph(VertexSet vertexSet) {
        return (Network<V, E>) super.subgraph(vertexSet);
    }

    @Override
    public Network<V, E> subgraph(Collection<Edge> edges) {
        return (Network<V, E>) super.subgraph(edges);
    }

    @Override
    public Network<V, E> complement() {
        return (Network<V, E>) super.complement();
    }

    @Override
    public int getSource() {
        return source;
    }

    @Override
    public void setSource(int source) {
        Validator.containsVertex(this, source);
        this.source = source;
    }

    @Override
    public int getSink() {
        return sink;
    }

    @Override
    public void setSink(int sink) {
        Validator.containsVertex(this, sink);
        this.sink = sink;
    }

}
