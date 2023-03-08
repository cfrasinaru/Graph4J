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
package org.graph4j.alg.connectivity;

import java.util.List;
import org.graph4j.Graph;
import org.graph4j.util.VertexSet;

/**
 *
 * @author Cristian Frăsinaru
 */
public interface BiconnectivityAlgorithm {

    /**
     *
     * @return {@code true} if the graph is 2-connected.
     */
    boolean isBiconnected();

    /**
     *
     * @return the blocks of the graph.
     */
    List<VertexSet> getBlocks();

    /**
     * A <i>cut vertex</i> (cut point, articulation point, separating point) is
     * any vertex whose removal increases the number of connected components.
     *
     * @return the set of cut vertices.
     */
    VertexSet getCutVertices();

    /**
     *
     * @param graph the input graph.
     * @return the default implementation of this interface.
     */
    static BiconnectivityAlgorithm getInstance(Graph graph) {
        return new TarjanBiconnectivity(graph);
    }

}
