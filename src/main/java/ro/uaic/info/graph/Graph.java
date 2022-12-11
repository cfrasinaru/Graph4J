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

import java.util.List;
import java.util.stream.IntStream;

/**
 * Support interface for all types of graphs: directed or not, weighted or not,
 * allowing self loops and multiple edges or not.
 *
 * Vertices and edges of all graph types can be labelled using any reference
 * data type.
 *
 * @author Cristian Frăsinaru
 * @param <V>
 * @param <E>
 */
public interface Graph<V, E> extends Weighted, Labeled<V, E> {

    /**
     *
     * @return the number of vertices in the graph
     */
    int numVertices();

    /**
     *
     * @return the number of edges in the graph
     */
    int numEdges();

    /**
     *
     * @return the vertices of the graph
     */
    int[] vertices();

    /**
     *
     * @return true if the number of vertices is 0, otherwise false
     */
    default boolean isEmpty() {
        return numVertices() == 0;
    }

    /**
     *
     * @param index between 0 and vertexCount() - 1
     * @return the vertex with the specified index
     */
    int vertexAt(int index);

    /**
     * The vertices of the graph are maintained sorted in an array. The index of
     * a vertex represents the position where it is stored in the array.
     *
     * @param v a vertex number
     * @return the index of the specified vertex number
     */
    int indexOf(int v);

    /**
     *
     * @return
     */
    int[][] edges();

    /**
     * A convenience method that returns all the edges in the graph.
     *
     * @return a list of all edges in the graph
     */
    List<Edge> edgeList();

    /**
     *
     * @param u
     * @param v
     * @return {@code true} if u is in the adjacency list of v
     */
    boolean containsEdge(int v, int u);

    /**
     *
     * @param v a vertex number
     * @return {@code true} if v belongs to the graph
     */
    default boolean containsVertex(int v) {
        return indexOf(v) >= 0;
    }

    /**
     * The <i>neighbors</i> of a vertex v are the vertices in the adjacency list
     * of v. In case of directed graphs, this method returns the same as
     * <code>successors</code>.
     *
     * @param v a vertex number
     * @return the vertices that are adjacent to v
     */
    int[] neighbors(int v);

    /**
     * The degree of a vertex is the number of its neighbors, that is vertices
     * that are in its adjacency list. In case of directed graphs, this method
     * returns the same as <code>outDegree</code>.
     *
     * @param v vertex number
     * @return the degree of the vertex v
     */
    int degree(int v);

    /**
     *
     * @param v
     * @param u
     */
    void addEdge(int v, int u);

    /**
     *
     * @param v
     * @param u
     */
    void removeEdge(int v, int u);

    /**
     * Adds a new vertex to the graph having the number equal to the maximum
     * vertex number plus one.
     *
     * @return the number of the added vertex
     */
    default int addVertex() {
        int v = 1 + IntStream.of(vertices()).max().orElse(1);
        return addVertex(v);
    }

    /**
     *
     * @param v a vertex number that does not exist in the graph
     * @return the index of the added vertex
     */
    int addVertex(int v);

    /**
     *
     * @param vertices
     */
    default void addVertices(int... vertices) {
        for (int v : vertices) {
            addVertex(v);
        }
    }

    /**
     *
     * @param v
     */
    void removeVertex(int v);

    /**
     *
     * @param vertices
     */
    default void removeVertices(int... vertices) {
        for (int v : vertices) {
            removeVertex(v);
        }
    }

    /**
     *
     * @return true, if the adjacency lists of the graph are maintained sorted
     */
    boolean isSorted();

    /**
     * Creates a new vertex adjacent to all the neigbors of v
     *
     * @param v the vertex to be duplicated
     * @return the number of the newly created vertex
     */
    int duplicateVertex(int v);

    /**
     * Creates a new vertex that will replace the given arguments, connected to
     * all their neigbors.
     *
     * @param vertices
     * @return the number of the newly created vertex
     */
    int contractVertices(int... vertices);

    /**
     * Creates a new vertex adjacent to v and u, and removes the edge (v,u).
     *
     * @param v
     * @param u
     * @return the number of the newly created vertex
     */
    int splitEdge(int v, int u);

    /**
     *
     * @param amount
     */
    void renumberAdding(int amount);

    /**
     *
     * @return
     */
    Graph<V, E> copy();

    /**
     *
     * @param vertices
     * @return the subgraph induced by the given vertices
     */
    Graph<V, E> subgraph(int... vertices);

    /**
     * The <i>complement</i> of a graph G has the same vertex set as G, but its
     * edge set consists of the edges not present in G.
     *
     * @return the complement of the graph
     */
    Graph<V, E> complement();

}
