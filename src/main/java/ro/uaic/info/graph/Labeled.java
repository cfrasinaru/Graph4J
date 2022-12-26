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
package ro.uaic.info.graph;

/**
 *
 * @author Cristian Frăsinaru
 */
interface Labeled<V, E> {
    
    /**
     *
     * @param v a vertex number
     * @param label
     */
    int addLabeledVertex(int v, V label);

    /**
     *
     * @param v a vertex number
     * @param label
     */
    int addLabeledVertex(V label);
    
    /**
     *
     * @param v a vertex number
     * @param u a vertex number
     * @param label
     */
    void addLabeledEdge(int v, int u, E label);

    /**
     *
     * @param v a vertex number
     * @param label
     */
    void setVertexLabel(int v, V label);

    /**
     *
     * @param v a vertex number
     * @return
     */
    V getVertexLabel(int v);

    /**
     *
     * @param v a vertex number
     * @param u a vertex number
     * @param label
     */
    void setEdgeLabel(int v, int u, E label);

    /**
     *
     * @param v a vertex number
     * @param u a vertex number
     * @return
     */
    E getEdgeLabel(int v, int u);
    
}
