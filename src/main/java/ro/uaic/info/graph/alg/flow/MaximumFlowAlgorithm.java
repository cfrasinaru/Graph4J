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

import ro.uaic.info.graph.alg.mst.*;
import ro.uaic.info.graph.Graph;
import ro.uaic.info.graph.model.EdgeSet;

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
    double getEdgeValue(int v, int u);

    /**
     *
     * @return the maximum value of the flow.
     */
    double getTotalValue();

    /**
     *
     * @param graph the input graph.
     * @return the default implementation of this interface.
     */
    static MaximumFlowAlgorithm getInstance(Graph graph) {
        throw new UnsupportedOperationException();
    }
}
