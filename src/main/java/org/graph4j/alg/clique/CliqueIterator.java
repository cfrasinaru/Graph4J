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
package org.graph4j.alg.clique;

import java.util.ArrayList;
import java.util.List;
import org.graph4j.Graph;
import org.graph4j.util.Clique;

/**
 * Iterates over all cliques of a graph. Additional constraints, such as the
 * minimum or maximum size of a clique, can be imposed.
 *
 * @author Cristian Frăsinaru
 */
public interface CliqueIterator {

    /**
     *
     * @return {@code true} if the iteration has more elements.
     */
    boolean hasNext();

    /**
     *
     * @return the next element in the iteration.
     */
    Clique next();

    /**
     * Obtains a list of all cliques which would be returned by the iterator.
     * imposed constraints. This list can be exponential in the number of nodes
     * in the graph.
     *
     * @return all cliques of the graph.
     */
    default List<Clique> getAll() {
        List<Clique> all = new ArrayList<>();
        while (hasNext()) {
            all.add(next());
        }
        return all;
    }

    /**
     *
     * @param graph the input graph.
     * @return the default implementation of this interface.
     */
    static CliqueIterator getInstance(Graph graph) {
        return new DFSCliqueIterator(graph);
    }

}
