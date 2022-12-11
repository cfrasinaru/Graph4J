/*
 * Copyright (C) 2022 Faculty of Computer Science Iasi, Romania
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
package ro.uaic.info.graph;

/**
 * Self loops and multiple (parallel) edges are allowed.
 *
 * @author Cristian Frăsinaru
 * @param <V>
 * @param <E>
 */
public interface Pseudograph<V, E> extends Multigraph<V, E> {

    /**
     *
     * @param v
     * @return
     */
    int selfLoops(int v);

    /**
     *
     * @return an identical copy of the pseudograph
     */
    @Override
    Pseudograph<V, E> copy();

    /**
     *
     * @param vertices
     * @return the subgraph induced by the given vertices
     */
    @Override
    Pseudograph<V, E> subgraph(int... vertices);
}
