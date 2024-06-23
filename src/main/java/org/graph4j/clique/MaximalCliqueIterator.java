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
package org.graph4j.clique;

import org.graph4j.Graph;

/**
 * Iterates over all <em>maximal</em> cliques of a graph.
 *
 * @author Cristian Frăsinaru
 */
public interface MaximalCliqueIterator extends CliqueIterator {

    /**
     *
     * @param graph the input graph.
     * @return the default implementation of this interface.
     */
    static MaximalCliqueIterator getInstance(Graph graph) {
        return new BronKerboschCliqueIterator(graph);
    }

}
