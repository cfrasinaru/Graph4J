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
package ro.uaic.info.graph.alg.flow;

import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.Edge;
import ro.uaic.info.graph.model.EdgeSet;
import ro.uaic.info.graph.model.VertexSet;

/**
 *
 * @author Cristian Frăsinaru
 */
public interface MaximumFlowAlgorithm {

    /**
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return the maximum flow on the vu edge.
     */
    double getValue(int v, int u);

    /**
     *
     * @param e an edge of the network.
     * @return the maximum flow on the given edge.
     */
    default double getValue(Edge e) {
        return getValue(e.source(), e.target());
    }

    /**
     *
     * @return the maximum value of the flow.
     */
    double getValue();

    /**
     *
     * @return the maximum flow for all the edges.
     */
    NetworkFlow getFlow();

    /**
     *
     * @return the partition set of a minimum cut containing the source
     * vertex.
     */
    VertexSet getSourcePartition();

    /**
     *
     * @return the partition set of a minimum cut containing the sink vertex.
     */
    VertexSet getSinkPartition();

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
        return new EdmondsKarpMaximumFlow(graph, source, sink);
    }
}
