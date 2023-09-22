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
import org.graph4j.Edge;
import org.graph4j.util.EdgeSet;
import org.graph4j.util.VertexSet;

/**
 * Contract for algorithms that compute a maximum flow in a transportation
 * network.
 *
 * @author Cristian Frăsinaru
 */
public interface MaximumFlowAlgorithm {

    /**
     * Returns the flow on the specified edge.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return the maximum flow on the vu edge.
     */
    double getValue(int v, int u);

    /**
     * Returns the flow on the specified edge.
     *
     * @param e an edge of the network.
     * @return the maximum flow on the given edge.
     */
    default double getValue(Edge e) {
        return getValue(e.source(), e.target());
    }

    /**
     * Returns the maximum flow of the network.
     *
     * @return the maximum value of the flow.
     */
    double getValue();

    /**
     * Creates a data structure storing the flow value for all edges.
     *
     * @return a data structure storing the flow value for all edges.
     */
    NetworkFlow getFlow();

    /**
     *
     * @return the partition set of a minimum cut containing the source vertex.
     */
    VertexSet getSourcePart();

    /**
     *
     * @return the partition set of a minimum cut containing the sink vertex.
     */
    VertexSet getSinkPart();

    /**
     *
     * @return the edges of a minimum cut set.
     */
    EdgeSet getCutEdges();

    /**
     *
     * @param graph the input network.
     * @param source the source vertex number.
     * @param sink the sink vertex number.
     * @return the default implementation of this interface.
     */
    static MaximumFlowAlgorithm getInstance(Digraph graph, int source, int sink) {
        //return new PushRelabelMaximumFlow(graph, source, sink);
        return new EdmondsKarpMaximumFlow(graph, source, sink);
    }
}
