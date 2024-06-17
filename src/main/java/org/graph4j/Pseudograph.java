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
package org.graph4j;

import java.util.Collection;
import org.graph4j.util.VertexSet;

/**
 * Self loops and multiple (parallel) edges are allowed.
 *
 * @author Cristian Frăsinaru
 * @param <V> the type of vertex labels in this graph.
 * @param <E> the type of edge labels in this graph.
 */
public interface Pseudograph<V, E> extends Multigraph<V, E> {

    /**
     *
     * @param v a vertex number.
     * @return the number of self loops of v.
     */
    int selfLoops(int v);

    /**
     *
     * @return an identical copy of the pseudograph.
     */
    @Override
    Pseudograph<V, E> copy();

    @Override
    Pseudograph<V, E> subgraph(VertexSet vertexSet);

    @Override
    Pseudograph<V, E> subgraph(Collection<Edge> edges);
    
    

}
