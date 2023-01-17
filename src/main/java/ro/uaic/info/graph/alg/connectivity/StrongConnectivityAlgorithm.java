/*
 * Copyright (C) 2023 Cristian Fr?sinaru and contributors
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
package ro.uaic.info.graph.alg.connectivity;

import java.util.List;
import ro.uaic.info.graph.Digraph;
import ro.uaic.info.graph.model.VertexSet;

/**
 *
 * @author Cristian Frăsinaru
 */
public interface StrongConnectivityAlgorithm {

    /**
     *
     * @return {@code true} if the digraph is strongly connected.
     */
    boolean isStronglyConnected();

    /**
     *
     * @return a list of vertex sets, each representing the vertices of a
     * strongly connected component.
     */
    List<VertexSet> getStronglyConnectedSets();

    /**
     *
     * @return a list of digraphs, each representing a strongly connected
     * component.
     */
    List<Digraph> getStronglyConnectedComponents();

    /**
     * Each strongly connected component is contracted to a single vertex, the
     * resulting graph is a directed acyclic graph, the <i>condensation</i>.
     *
     * Each vertex of the condensation is labeled with the corresponding
     * strongly connected component. Each edge vu of the condensation is labeled
     * with the number of edges from the component of v to to the component of u
     * in the original digrpah.
     *
     *
     * @return the condensation digraph.
     */
    Digraph<Digraph, Integer> createCondensation();

    /**
     *
     * @param digraph the input digraph.
     * @return the default implementation of this interface.
     */
    static StrongConnectivityAlgorithm getInstance(Digraph digraph) {
        return new TarjanStrongConnectivity(digraph);
    }

}
