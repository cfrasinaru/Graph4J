/*
 * Copyright (C) 2022 Cristian Frăsinaru and contributors
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
package org.graph4j.alg;

import org.graph4j.Digraph;
import org.graph4j.alg.connectivity.StrongConnectivityAlgorithm;

/**
 * Accepts only directed graphs as input.
 *
 * @author Cristian Frăsinaru
 */
public abstract class DirectedGraphAlgorithm {

    protected final Digraph graph;
    protected Boolean stronglyConnected;

    /**
     *
     * @param graph the input digraph
     */
    public DirectedGraphAlgorithm(Digraph graph) {
        this.graph = graph;
    }

    /**
     * Returns {@code true} if the digraph is strongly connected.
     *
     * @return {@code true} if the digraph is strongly connected.
     */
    public boolean isStronglyConnected() {
        if (stronglyConnected == null) {
            stronglyConnected = StrongConnectivityAlgorithm.getInstance(graph)
                    .isStronglyConnected();
        }
        return stronglyConnected;
    }

}
