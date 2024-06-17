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

import org.graph4j.Edge;
import org.graph4j.Network;
import org.graph4j.util.EdgeSet;
import org.graph4j.util.VertexSet;

/**
 * Contract for algorithms that computeMaximumFlow a maximum flow in a transportation
 network.
 *
 * @author Cristian Frăsinaru
 */
public interface MaximumFlowAlgorithm {

    /**
     * Returns the value of the maximum flow on the specified edge.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return the maximum flow on (v,u) edge.
     */
    double getFlowValue(int v, int u);

    /**
     * Returns the value of the maximum flow on the specified edge.
     *
     * @param e an edge of the network.
     * @return the maximum flow on {@code e}.
     */
    default double getFlowValue(Edge e) {
        return MaximumFlowAlgorithm.this.getFlowValue(e.source(), e.target());
    }

    /**
     * Returns the value of the maximum flow of the network.
     *
     * @return the maximum value of the flow.
     */
    double getMaximumFlowValue();

    /**
     * Creates a data structure storing the flow value for all edges.
     *
     * @return a data structure storing the flow value for all edges.
     */
    FlowData getMaximumFlowData();

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
    EdgeSet getMinimumCutEdges();

    /**
     *
     * @param graph the input network.
     * @return the default implementation of this interface.
     */
    static MaximumFlowAlgorithm getInstance(Network graph) {
        //return new PushRelabelMaximumFlow(graph);
        //return new EdmondsKarpMaximumFlow(graph);
        return new DinicMaximumFlow(graph);
    }
}
