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

import org.graph4j.util.EdgeSet;
import org.graph4j.util.VertexSet;

/**
 *
 * @author Cristian Frăsinaru
 */
interface Labeled<V, E> {

    /**
     * Adds a new vertex to the graph having the specified number and label.
     *
     * After the execution of this method, the graph is considered
     * <em>vertex labeled</em>.
     *
     * @param v a vertex number.
     * @param label a vertex label.
     * @return the index of the added vertex.
     * @throws InvalidVertexException if {@code v} is negative or it is already
     * in the graph.
     */
    int addLabeledVertex(int v, V label);

    /**
     * Adds a new vertex to the graph having the number equal to the maximum
     * vertex number plus one, and the specified label.
     *
     * After the execution of this method, the graph is considered
     * <em>vertex labeled</em>.
     *
     * @param label a vertex label.
     * @return the number of the added vertex.
     */
    int addLabeledVertex(V label);

    /**
     * Adds a new weighted and labeled edge to the graph. The endpoints of the
     * edge are identified using their vertex numbers. See also
     * {@link Graph#addEdge(int, int)}.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @param weight edge weight.
     * @param label edge label.
     * @return the position of {@code u} in the adjacency list of {@code v} if
     * {@code (v,u)} edge was added, or {@code -1} if the edge was not added.
     * @throws InvalidVertexException if either of the two vertices is not in
     * the graph.
     */
    int addLabeledEdge(int v, int u, E label, double weight);
    
    /**
     * Adds a new labeled edge to the graph. The endpoints of the edge are
     * identified using their vertex numbers.
     *
     * After a successful execution of this method, the graph is considered
     * <em>edge labeled</em>. See also {@link Graph#addEdge(int, int)}.
     *
     * @param v a vertex number
     * @param u a vertex number
     * @param label an edge label.
     * @return the position of {@code u} in the adjacency list of {@code v} if
     * {@code (v,u)} edge was added, or {@code -1} if the edge was not added.
     * @throws InvalidVertexException if either of the two vertices is not in
     * the graph.
     */
    int addLabeledEdge(int v, int u, E label);

    /**
     * Adds a new labeled edge to the graph. The endpoints of the edge are
     * identified using their uniquely identifiable vertex labels.
     *
     * After a successful execution of this method, the graph is considered
     * <em>edge labeled</em>. See also {@link Graph#addEdge(int, int)}.
     *
     * @param vLabel a vertex label.
     * @param uLabel a vertex label.
     * @param edgeLabel an edge label.
     * @return the position of {@code u} in the adjacency list of {@code v} if
     * {@code (v,u)} edge was added, or {@code -1} if the edge was not added.
     * @throws InvalidVertexException if either of the two vertices is not in
     * the graph.
     */
    default int addLabeledEdge(V vLabel, V uLabel, E edgeLabel) {
        int v = findVertex(vLabel);
        int u = findVertex(uLabel);
        return Labeled.this.addLabeledEdge(v, u, edgeLabel);
    }

    /**
     * Sets the label for the specified vertex. If at least one vertex has been
     * assigned a label, the graph is considered <em>vertex labeled</em>.
     *
     * The time complexity of this method is {@code O(1)}.
     *
     * @param v a vertex number.
     * @param label a vertex label.
     * @throws InvalidVertexException if {@code v} is not in the graph.
     *
     */
    void setVertexLabel(int v, V label);

    /**
     * Returns the label of the specified vertex or {@code null} if no label has
     * been set.
     *
     * The time complexity of this method is {@code O(1)}.
     *
     * @param v a vertex number.
     * @return the label of the specified vertex.
     * @throws InvalidVertexException if {@code v} is not in the graph.
     */
    V getVertexLabel(int v);

    /**
     * Sets the label of a specified edge.
     *
     * The time complexity of this method is {@code O(degree(v))}. It is more
     * efficient to set the labels of the edges while iterating over them, see
     * {@link Graph#neighborIterator(int)} or {@link Graph#edgeIterator()}.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @param label an edge label.
     * @throws InvalidEdgeException if the graph does not contain the edge
     * {@code (v,u)}.
     */
    void setEdgeLabel(int v, int u, E label);

    /**
     * Returns the label of a specified edge.
     *
     * The time complexity of this method is {@code O(degree(v))}. It is more
     * efficient to get the weights of the edges while iterating over them, see
     * {@link Graph#neighborIterator(int)} or {@link Graph#edgeIterator()}.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return the label of the edge {@code (v,u)}.
     * @throws InvalidEdgeException if the graph does not contain the edge
     * {@code (v,u)}.
     */
    E getEdgeLabel(int v, int u);

    /**
     * Checks if the graph has at least one edge that has been assigned a label
     * (is edge labeled).
     *
     * @return {@code true} if labels have been set on edges, {@code false}
     * otherwise.
     */
    boolean hasEdgeLabels();

    /**
     * Checks if the graph has at least one vertex that has been assigned a
     * label (is vertex labeled).
     *
     * @return {@code true} if labels have been set on vertices, {@code false}
     * otherwise.
     */
    boolean hasVertexLabels();

    /**
     * Returns the number of the vertex having the specified label. If there are
     * more vertices having the specified label, it returns the last one that
     * has been assigned that label. If no vertex has the specified label, it
     * returns {@code -1}.
     *
     * @param label a vertex label.
     * @return the number of the vertex which has the specified label, or
     * {@code -1} if no such vertex exists.
     */
    int findVertex(V label);

    /**
     * Returns a set containing all the vertex numbers having the specified
     * label.
     *
     * @param label a vertex label.
     * @return all the vertices of the graph which have been assigned the
     * specified label.
     */
    VertexSet findAllVertices(V label);

    /**
     * Returns the edge having the specified label. If there are more edges with
     * the specified label, it returns the last one added in the graph.
     *
     * @param label an edge label.
     * @return the edge with the specified label.
     */
    Edge findEdge(E label);

    /**
     * Returns a set containing all the edges having the specified label.
     *
     * @param label an edge label.
     * @return all the edges with the specified label.
     */
    EdgeSet findAllEdges(E label);
}
