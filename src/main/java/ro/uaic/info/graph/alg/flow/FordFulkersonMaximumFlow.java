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

import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.alg.GraphAlgorithm;

/**
 *
 * @author Cristian Frăsinaru
 */
public class FordFulkersonMaximumFlow extends GraphAlgorithm
        implements MaximumFlowAlgorithm {

    double[][] flow;
    //dfs start from s - try to find an augmenting path
    //inspect successors
    //inspect predecessors
    //when the sink is marked augment the flow and restart

    public FordFulkersonMaximumFlow(Graph graph) {
        super(graph);
    }

    @Override
    public double getEdgeValue(int v, int u) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getTotalValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
