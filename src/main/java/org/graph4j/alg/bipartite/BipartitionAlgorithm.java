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
package org.graph4j.alg.bipartite;

import org.graph4j.Graph;
import org.graph4j.util.Cycle;
import org.graph4j.util.StableSet;

/**
 *
 * An algorithm specification for determining whether an undirected graph is
 * <em>bipartite</em>.
 *
 * A graph is bipartite if its vertices can be partitioned in two disjoint
 * stable sets.
 *
 * A graph is bipartite if and only if it has no odd-length cycle.
 *
 * A graph is bipartite if and only if it is 2-colorable.
 *
 * @author Cristian Frăsinaru
 */
public interface BipartitionAlgorithm {

    /**
     *
     * @return {@code true} if the graph is bipartite.
     */
    boolean isBipartite();

    /**
     *
     * @return the left side of the graph, if the graph is bipartite, otherwise
     * it throws an exception.
     */
    StableSet getLeftSide();

    /**
     *
     * @return the right side of the graph, if the graph is bipartite,
     * otherwise it throws an exception.
     */
    StableSet getRightSide();

    /**
     *
     * @param v a vertex number
     * @return the partition set v belongs to, if the graph is bipartite,
     * otherwise it throws an exception.
     */
    StableSet getSide(int v);

    /**
     *
     * @return an odd cycle, if the graph is not bipartite, otherwise it returns
     * null.
     */
    Cycle findOddCycle();

    /**
     *
     * @param graph the input graph.
     * @return the default implementation of this interface.
     */
    static BipartitionAlgorithm getInstance(Graph graph) {
        return new DFSBipartitionAlgorithm(graph);
    }
}
