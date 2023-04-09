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

import org.graph4j.Graph;
import org.graph4j.util.Clique;

/**
 * Iterates over all maximal cliques of a graph.
 *
 * @author Cristian Frăsinaru
 */
public interface MaximalCliqueIterator {

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
     *
     * @param graph the input graph.
     * @return the default implementation of this interface.
     */
    static MaximalCliqueIterator getInstance(Graph graph) {
        return new BronKerboschCliqueIterator(graph);
    }

}
