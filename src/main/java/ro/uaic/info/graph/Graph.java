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

import java.util.Iterator;
import java.util.stream.IntStream;

/**
 * Support interface for all types of graphs: directed or not, weighted or not,
 * allowing self loops and multiple edges or not.
 *
 * Vertices and edges of all graph types can be labelled using any reference
 * data type.
 *
 * @author Cristian Frăsinaru
 * @param <V> the type of vertex labels in this graphs
 * @param <E> the type of edge labels in this graphs
 */
public interface Graph<V, E> extends Weighted, Labeled<V, E> {

    /**
     * 1
     */
    static final double DEFAULT_EDGE_WEIGHT = 1; //for distance algorithms in unweighted graphs
    /**
     * 1
     */
    static final double DEFAULT_VERTEX_WEIGHT = 1; //for what?

    /**
     * Returns the name of the graph, or {@code null} if the graph has no name.
     *
     * @return the name of the graph
     */
    String getName();

    /**
     * A graph may receive a name, for example "K4", "C5", "Petersen", etc.
     *
     * @param name the name of the graph
     */
    void setName(String name);

    /**
     * The number of vertices is also called the <i>order</i> of the graph.
     *
     * @return the number of vertices in the graph
     */
    int numVertices();

    /**
     * The number of edges is also called the <i>dimension</i> of the graph.
     *
     * @return the number of edges in the graph
     */
    int numEdges();

    /**
     * A simple graph with {@code n} vertices may have at most {@code n*(n-1)/2}
     * edges. A directed graph may have {@code n*(n-1)} edges (also called
     * arcs). For multigraphs and pseudographs, this method returns
     * {@code Long.MAX_VALUE}.
     *
     * @return the maximum number of edges the graph can have
     */
    long maxEdges();

    /**
     * A simple graph with {@code n} vertices may have at most {@code n*(n-1)/2}
     * edges.
     *
     * @param numVertices
     * @return the maximum number of edges a graph with {@code numVertices} can
     * have
     */
    static long maxEdges(int numVertices) {
        return (long) numVertices * (numVertices - 1) / 2;
    }

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
     * The array has {@code numEdges()} rows and two columns, so
     * {@code e = edges()[i]} represents the edge {@code (v=e[0],u=e[1])}, where
     * v and u are vertex numbers.
     *
     * @return an array of all the edges in the graph
     */
    int[][] edges();

    /**
     * Creates an iterator through all edges in the graph.
     *
     * @return an iterator through all edges in the graph
     */
    Iterator<Edge> edgeIterator();

    /**
     * Returns an edge object corresponding to the edge vu, containing all the
     * additional information. The edge may or may be not be part of the graph.
     *
     * @param v a vertex number
     * @param u a vertex number
     * @return the edge corresponding to the vertices v and u
     */
    Edge<E> edge(int v, int u);

    /**
     * Check if two vertices of the graph, v and u, are adjacent. In case of
     * undirected graphs, that means that v is in the adjacency list of u and u
     * is also in the adjacency list of v. In case of directed graphs, the
     * method returns {@code true} only if u is in the adjacency list of v (u is
     * a succesor of v).
     *
     * @param u a vertex number
     * @param v a vertex number
     * @return {@code true} if u is in the adjacency list of v
     */
    boolean containsEdge(int v, int u);

    /**
     * Convenience method for {@code containsEdge(e.source(), e.target()}. See
     * {@link #containsEdge(int, int)}.
     *
     * @param e an edge
     * @return {@code true} if the edge belongs to the graph
     */
    default boolean containsEdge(Edge e) {
        return containsEdge(e.source(), e.target());
    }

    /**
     * Verifies if the graph contains a specific vertex number.
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
     * {@link  Digraph#succesors(int)}.
     *
     * @param v a vertex number
     * @return the vertices that are adjacent to v
     */
    int[] neighbors(int v);

    /**
     *
     * @param v
     * @return
     */
    NeighborIterator<E> neighborIterator(int v);

    /**
     * The degree of a vertex is the number of its neighbors, that is vertices
     * that are in its adjacency list. In case of directed graphs, this method
     * returns the same as {@link  Digraph#outDegree(int)}.
     *
     * @param v a vertex number
     * @return the degree of the vertex v
     */
    int degree(int v);

    /**
     * An <i>isolated</i> vertex has no neighbors in its adjacency list.
     *
     * @param v a vertex number
     * @return {@code true} if v has degree 0
     */
    default boolean isIsolated(int v) {
        return degree(v) == 0;
    }

    /**
     * A <i>pendant</i> vertex has a single neighbor in its adjacency list.
     *
     * @param v a vertex number
     * @return {@code true} if v has degree 1
     */
    default boolean isPendant(int v) {
        return degree(v) == 1;
    }

    /**
     * An <i>universal</i> is adjacent to all other vertices in the graph. In
     * case of simple graphs, it has the degree {@code numVertices()-1}. In case
     * of directed graphs it has the outdegree {@code numVertices()-1}.
     *
     * @param v a vertex number
     * @return {@code true} if v is universal
     */
    default boolean isUniversal(int v) {
        return degree(v) == numVertices() - 1;
    }

    /**
     * The degree sequence of the graph contains the degrees of all vertices.
     * {@code degrees()[idx]} represents the degree of the vertex with the index
     * idx, that is {@code vertexAt(idx)}.
     *
     * @return the degree sequence of the graph
     */
    int[] degrees();

    /**
     *
     * @param v a vertex number
     * @param u a vertex number
     */
    void addEdge(int v, int u);

    /**
     *
     * @param e an edge
     */
    void addEdge(Edge<E> e);

    /**
     *
     * @param v a vertex number
     * @param u a vertex number
     */
    void removeEdge(int v, int u);

    /**
     * Adds a new vertex to the graph having the number equal to the maximum
     * vertex number plus one. The weight and the label are null.
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

    /**
     * The <i>adjacency</i> matrix shows the relationship between vertices. The
     * matrix is square, the number of lines and columns being equal to the
     * number of vertices, and it contains non-negative integers. An element at
     * row i and column j has a positive value if <code>u = vertexAt(j)</code>
     * appears in the adjacency list of <code>v = vertexAt(i)</code> (vu
     * represent an edge) and the value denotes the multiplicity of the edge vu,
     * otherwise it is 0. The elements on the main diagonal denote the number of
     * self-loops.
     *
     * <p>
     * In case of simple graphs, the matrix contains only values of 0 and 1. In
     * case of undirected graphs, the matrix is symmetrical. If a graph
     * implementation does not allow self-loops, the elements on the main
     * diagonal are all 0.
     *
     * @return the adjacency matrix
     */
    int[][] adjacencyMatrix();

    /**
     * The <i>cost</i> matrix is created according to the same principles as the
     * adjacency matrix. An element at row i and column j corresponding to an
     * edge vu, where <code>v = vertexAt(i)</code> and
     * <code>u = vertexAt(j)</code>, has as value the weight of that edge:
     * <code>getEdgeWeight(v,u)</code>. The values on the main diagonal are all
     * 0 otherwise, if the cell does not correspond to an edge, it has the value
     * <code>Graph.NO_EDGE_WEIGHT</code>.
     *
     * The cost-matrix is not defined for multigraphs and pseudographs.
     *
     * @return the cost matrix
     */
    double[][] costMatrix();

    /**
     * The <i>incidence</i> matrix shows the relationship between vertices and
     * edges. Lines are associated with the vertices and columns with the edges.
     * A vertex <code>v = vertexAt(i)</code> has the line number i, while an
     * edge between <code>v = vertexAt(i)</code> and
     * <code>u = vertexAt(j)</code> has the column number equal to its index,
     * that is the positon it appears in the array returned by the
     * <code>edges()</code> method.
     *
     * In case of undirected graphs, an element at row i and column k has the
     * value 1 if the edge having the index k is incident with the vertex
     * <code>v = vertexAt(i)</code>, and 0 otherwise.
     *
     * In case of directed graphs, an element at row i and column k has the
     * value 1 if the edge having the index k is incident <i>from</i> the vertex
     * <code>v = vertexAt(i)</code>, -1 if it is incident <i>to</i> v, and 0
     * otherwise.
     *
     * The incidence-matrix is not defined for multigraphs and pseudographs.
     *
     *
     * @return
     */
    int[][] incidenceMatrix();

    /**
     *
     * @return true, if the adjacency lists of the graph are maintained sorted
     */
    boolean isSorted();

    /**
     * Convenience method for testing if this graph is actually a directed
     * graph.
     *
     * @see Digraph
     * @return true, if this is an instance of <code>Digraph</code>
     */
    boolean isDirected();

    /**
     * Convenience method for testing if this graph is actually a multigraph.
     *
     * @return true, if this is an instance of <code>Multigraph</code>
     */
    boolean isAllowingMultipleEdges();

    /**
     * Convenience method for testing if this graph is actually a pseudograph.
     *
     * @return true, if this is an instance of <code>Pseudograph</code>
     */
    boolean isAllowingSelfLoops();

}
