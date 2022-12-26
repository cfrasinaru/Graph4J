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
interface Weighted {

    /**
     *
     * @param v a vertex number
     * @param weight
     */
    int addWeightedVertex(int v, double weight);

    /**
     *
     * @param weight
     * @return
     */
    int addWeightedVertex(double weight);

    /**
     *
     * @param v a vertex number
     * @param u a vertex number
     * @param weight the weigth to be set for the edge vu
     */
    void addWeightedEdge(int v, int u, double weight);

    /**
     *
     * @param v a vertex number
     * @param weight the weight to be set for vertex v
     */
    void setVertexWeight(int v, double weight);

    /**
     * The default weight of a vertex is 0.
     *
     * @param v a vertex number
     * @return the weight of the vertex, 0 if the graph is unweighted
     */
    double getVertexWeight(int v);

    /**
     *
     * @param v a vertex number
     * @param u a vertex number
     * @param weight the weigth to be set for the edge vu
     */
    void setEdgeWeight(int v, int u, double weight);

    /**
     * The default weight of an edge is 1.
     *
     * @param v a vertex number
     * @param u a vertex number
     * @return the weight of the edge vu, 1 if the graph is unweighted, and
     * <code>Graph.NO_EDGE_WEIGHT</code> if vu is not an edge of the graph.
     */
    double getEdgeWeight(int v, int u);

    /**
     *
     * @return true, if weights have been set on edges
     */
    boolean isEdgeWeighted();

    /**
     *
     * @return true, if weights have been set on vertices
     */
    boolean isVertexWeighted();
}
