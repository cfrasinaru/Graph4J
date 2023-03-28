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
 * Support interface for all types of graphs: directed or not, weighted or not,
 * allowing self loops and multiple edges or not.
 *
 * Vertices and edges of all graph types can be labeled using any reference data
 * type.
 *
 * @author Cristian Frăsinaru
 * @param <V> the type of vertex labels in this graph.
 * @param <E> the type of edge labels in this graph.
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
     * @return the name of the graph.
     */
    String getName();

    /**
     * A graph may receive a name, for example "K4", "C5", "Petersen", etc.
     *
     * @param name the name of the graph.
     */
    void setName(String name);

    /**
     * The number of vertices is also called the <i>order</i> of the graph.
     *
     * @return the number of vertices in the graph.
     */
    int numVertices();

    /**
     * The number of edges is also called the <i>dimension</i> of the graph.
     *
     * @return the number of edges in the graph.
     */
    long numEdges();

    /**
     * A simple graph with {@code n} vertices may have at most {@code n*(n-1)/2}
     * edges. A directed graph may have {@code n*(n-1)} edges (also called
     * arcs). For multigraphs and pseudographs, this method returns
     * {@code Long.MAX_VALUE}.
     *
     * @return the maximum number of edges the graph can have.
     */
    long maxEdges();

    /**
     * A simple graph with {@code n} vertices may have at most {@code n*(n-1)/2}
     * edges.
     *
     * @param numVertices a number of vertices.
     * @return the maximum number of edges a graph with {@code numVertices} can
     * have.
     */
    static long maxEdges(int numVertices) {
        return (long) numVertices * (numVertices - 1) / 2;
    }

    /**
     * The vertices of the graph are stored in an array. By default, the
     * vertices have numbers between 0 and {@code numVertices()-1}, however this
     * is not mandatory. Vertices can be removed from the graph, new vertices
     * with any number can be added, etc.
     *
     * For performance reasons, this is the actual array where the vertices are
     * stored, so do not modify it.
     *
     * @return the vertices of the graph.
     */
    int[] vertices();

    /**
     * A graph is considered empty if it has no vertices.
     *
     * @return {@code true} if the number of vertices is 0, otherwise false.
     */
    default boolean isEmpty() {
        return numVertices() == 0;
    }

    /**
     * Vertices, having various numbers, are stored indexed in an array.
     *
     * @param index a value between 0 and {@code numVertices() - 1}.
     * @return the vertex at the specified index (position) in the array.
     */
    int vertexAt(int index);

    /**
     *
     * @return an iterator over the vertices of this graph.
     */
    VertexIterator<V> vertexIterator();

    /**
     * The index of a vertex represents the position where it is stored in the
     * array.
     *
     * @param v a vertex number.
     * @return the index of the specified vertex number.
     */
    int indexOf(int v);

    /**
     * The edge objects are not stored internally in the graph so this method
     * should only be used if all the edges are required to be in the same data
     * structure for a specific purpose. In order to iterate over the edges of
     * the graph it is better to use {@link Graph#edgeIterator()}.
     *
     * Works only if the number of edges is less than {@code Integer.MAX_VALUE}.
     *
     * @return an array of all the edges in the graph.
     */
    Edge[] edges();

    /**
     *
     * @param v a vertex number;
     * @return the edges formed by v with its neighbors (successors).
     */
    Edge[] edgesOf(int v);

    /**
     * Returns an iterator over the edges in this graph. There are no guarantees
     * concerning the order in which the edges are returned, unless the graph is
     * sorted.
     *
     * @return an iterator over the edges in this graph.
     */
    EdgeIterator edgeIterator();

    /**
     * Returns an edge object corresponding to the edge vu, containing all the
     * additional information. If there is no such edge in the graph, it throws
     * {@code InvalidEdgeException}.
     *
     * @param v a vertex number
     * @param u a vertex number
     * @return the edge in the graph corresponding to the vertices v and u.
     * @throws InvalidEdgeException if there is no vu edge in the graph.
     */
    Edge<E> edge(int v, int u);

    /**
     * Checks if two vertices of the graph, v and u, are adjacent. In case of
     * undirected graphs, that means that v is in the adjacency list of u and u
     * is also in the adjacency list of v. In case of directed graphs, the
     * method returns {@code true} only if u is in the adjacency list of v (u is
     * a successor of v).
     *
     * @param u a vertex number
     * @param v a vertex number
     * @return {@code true} if u is in the adjacency list of v.
     */
    boolean containsEdge(int v, int u);

    /**
     * Convenience method for {@code containsEdge(e.source(), e.target()}. See
     * {@link #containsEdge(int, int)}.
     *
     * @param e an edge.
     * @return {@code true} if the edge belongs to the graph.
     */
    default boolean containsEdge(Edge e) {
        return containsEdge(e.source(), e.target());
    }

    /**
     * Verifies if the graph contains a specific vertex number.
     *
     * @param v a vertex number.
     * @return {@code true} if v belongs to the graph.
     */
    default boolean containsVertex(int v) {
        return indexOf(v) >= 0;
    }

    /**
     * The <i>neighbors</i> of a vertex v are the vertices in the adjacency list
     * of v. In case of directed graphs, this method returns the same as
     * {@link  Digraph#successors(int)}.
     *
     * For a more efficient iteration over the neighbors of a vertex, use
     * {@link #neighborIterator(int)}.
     *
     * @param v a vertex number.
     * @return the vertices that are adjacent to v.
     */
    int[] neighbors(int v);

    /**
     * Iterates over the edges incident from v, returning the neighbors of v,
     * along with information regarding their edges.
     *
     * @param v a vertex number.
     * @return an iterator over the neigbors of v.
     */
    default NeighborIterator<E> neighborIterator(int v) {
        return neighborIterator(v, -1);
    }

    /**
     * Iterates over the edges incident from v, returning the neighbors of v,
     * starting from a specified position in the adjacency list of v, along with
     * information regarding their edges.
     *
     * @param v a vertex number.
     * @param pos a position in the adjacency list of v.
     * @return an iterator over the neigbors of v, starting from a specified
     * position in the adjacency list of v.
     */
    NeighborIterator<E> neighborIterator(int v, int pos);

    /**
     *
     * @param v a vertex number.
     * @param u a vertex number;
     * @return the first position of u in the neighbor list of v.
     */
    int adjListPos(int v, int u);

    /**
     * The degree of a vertex is the number of its neighbors, that is vertices
     * that are in its adjacency list. In case of directed graphs, this method
     * returns the same as {@link  Digraph#outdegree(int)}.
     *
     * @param v a vertex number.
     * @return the degree of the vertex v.
     */
    int degree(int v);

    /**
     * An <i>isolated</i> vertex has no neighbors in its adjacency list.
     *
     * @param v a vertex number.
     * @return {@code true} if v has degree 0.
     */
    default boolean isIsolated(int v) {
        return degree(v) == 0;
    }

    /**
     * A <i>pendant</i> vertex has a single neighbor in its adjacency list.
     *
     * @param v a vertex number.
     * @return {@code true} if v has degree 1.
     */
    default boolean isPendant(int v) {
        return degree(v) == 1;
    }

    /**
     * An <i>universal</i> is adjacent to all other vertices in the graph. In
     * case of simple graphs, it has the degree {@code numVertices()-1}. In case
     * of directed graphs it has the outdegree {@code numVertices()-1}.
     *
     * @param v a vertex number.
     * @return {@code true} if v is universal.
     */
    default boolean isUniversal(int v) {
        return degree(v) == numVertices() - 1;
    }

    /**
     * The degree sequence of the graph contains the degrees of all vertices.
     * {@code degrees()[idx]} represents the degree of the vertex with the index
     * idx, that is {@code vertexAt(idx)}.
     *
     * @return the degree sequence of the graph.
     */
    int[] degrees();

    /**
     * Adds a new edge to the graph.The endpoints of the edge are identified
     * using their vertex numbers.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return the position of u in the adjacecny list of v, or {@code -1} if
     * the edge is already in the graph.
     */
    int addEdge(int v, int u);

    /**
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @param weight edge weight.
     * @param label edge label.
     * @return the position of u in the adjacecny list of v, or {@code -1} if
     * the edge is already in the graph.
     */
    int addEdge(int v, int u, double weight, E label);

    /**
     * Adds a new edge to the graph.The {@code Edge}object contains its
     * endpoints and, if necessary, its weight and label. The {@code directed}
     * property of an edge is ignored when it is added to the graph as it will
     * assume the graph type.
     *
     * Note that the {@code Edge} objects are not stored in the graph object,
     * they represent only a a convenient method to add the required
     * information.
     *
     * @param e an edge object.
     * @return the position of the target node in the source node adjacency
     * list, or {@code -1} if the edge is already in the graph.
     */
    int addEdge(Edge<E> e);

    /**
     * Adds a new edge to the graph.The endpoints of the edge are identified
     * using the labels of the vertices, which should be uniquely identifiable.
     *
     * @param vLabel the label of a uniquely identifiable vertex.
     * @param uLabel the label of a uniquely identifiable vertex.
     * @return the position of u in the adjacecny list of v, or {@code -1} if
     * the edge is already in the graph.
     */
    default int addEdge(V vLabel, V uLabel) {
        int v = findVertex(vLabel);
        int u = findVertex(uLabel);
        return Graph.this.addEdge(v, u);
    }

    /**
     * Removes the specified edge from the graph. If the specified edge does not
     * exist, it will throw an exception.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     */
    void removeEdge(int v, int u);

    /**
     * Removes all edges incident with (from in case of digraphs) v.
     *
     * @param v a vertex number.
     */
    void removeAllEdges(int v);

    /**
     * Adds a new vertex to the graph having the number equal to the maximum
     * vertex number plus one. The weight and the label are null.
     *
     * @return the number of the added vertex.
     */
    int addVertex();

    /**
     * Adds a new vertex to the graph having the number. The vertex numbers
     * should not be negative. Adding vertices with unnecessary large numbers
     * may increase the memory occupied by the graph.
     *
     * @param v a vertex number that does not exist in the graph.
     * @return the index of the added vertex.
     */
    int addVertex(int v);

    /**
     * Adds a sequence of vertices to the graph.
     *
     * @param vertices a sequence of vertices.
     */
    default void addVertices(int... vertices) {
        for (int v : vertices) {
            addVertex(v);
        }
    }

    /**
     * Removes the specified vertex from the graph, along with all edges
     * incident to it. If the vertex does not exist, an exception will be
     * thrown.
     *
     * @param v a vertex number.
     */
    void removeVertex(int v);

    /**
     *
     * @param vertices a sequence of vertices.
     */
    default void removeVertices(int... vertices) {
        for (int v : vertices) {
            removeVertex(v);
        }
    }

    /**
     * Creates a new vertex adjacent to all the neigbors of v
     *
     * @param v the vertex to be duplicated.
     * @return the number of the newly created vertex.
     */
    int duplicateVertex(int v);

    /**
     * Creates a new vertex that will replace the given arguments, connected to
     * all their neigbors.
     *
     * @param vertices the vertices which will be contracted.
     * @return the number of the newly created vertex.
     */
    int contractVertices(int... vertices);

    /**
     * Returns the maximum vertex number in the graph. If the default vertex
     * numbering is used, the maximum vertex number is
     * {@code numVertices() - 1}.
     *
     * @return the maximum vertex number in the graph, or {@code -1} if the
     * graph is empty (it contains no vertices).
     */
    int maxVertexNumber();

    /**
     * Creates a new vertex adjacent to v and u, and removes the edge (v,u).
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return the number of the newly created vertex.
     */
    int splitEdge(int v, int u);

    /**
     * Renumbers all the vertices of the graph, adding the specified amount to
     * each of them.
     *
     * @param amount a positive number.
     */
    void renumberAdding(int amount);

    /**
     * Creates and returns an identical copy of the graph.
     *
     * @return an identical copy of the graph.
     */
    Graph<V, E> copy();

    /**
     * Creates and returns a copy of the graph. Vertices are copied by default.
     * All other elements may or may not be copied.
     *
     * @param vertexWeights {@code true} if the vertex weights should be copied.
     * @param vertexLabels {@code true} if the vertex labels should be copied.
     * @param edges {@code true} if the edges should be copied.
     * @param edgeWeights {@code true} if the edge weights should be copied.
     * @param edgeLabels {@code true} if the edge labels should be copied.
     * @return a copy of the graph.
     */
    Graph<V, E> copy(boolean vertexWeights, boolean vertexLabels,
            boolean edges, boolean edgeWeights, boolean edgeLabels);

    /**
     * Creates and returns the subgraph induced by some vertices.
     *
     * @param vertices a sequence of vertices.
     * @return the subgraph induced by the given vertices.
     */
    Graph<V, E> subgraph(int... vertices);

    /**
     * Creates and returns the subgraph induced by some vertices.
     *
     * @param set a set of vertices.
     * @return the subgraph induced by the given vertices.
     */
    default Graph<V, E> subgraph(VertexSet set) {
        return subgraph(set.vertices());
    }

    /**
     *
     * @param set a set of edges.
     * @return the subgraph generated by the given edges.
     */
    Graph<V, E> subgraph(EdgeSet set);

    /**
     * The <i>complement</i> of a graph G has the same vertex set as G, but its
     * edge set consists of the edges not present in G.
     *
     * @return the complement of the graph.
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
     * @return the adjacency matrix.
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
     * @return the cost matrix.
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
     * Works only if the number of edges is less than {@code Integer.MAX_VALUE}.
     *
     *
     * @return the incidence matrix.
     */
    int[][] incidenceMatrix();

    /**
     * Convenience method for testing if this graph is actually a directed
     * graph.
     *
     * @return {@code true}, if this is an instance of {@link Digraph}.
     */
    boolean isDirected();

    /**
     * Convenience method for testing if this graph is actually a multigraph.
     *
     * @return true, if this is an instance of {@link Multigraph}.
     */
    boolean isAllowingMultipleEdges();

    /**
     * Convenience method for testing if this graph is actually a pseudograph.
     *
     * @return true, if this is an instance of {@link Pseudograph}.
     */
    boolean isAllowingSelfLoops();

    /**
     * In safe mode, various checks are performed in order to respect the graph
     * constraints and to prevent illegal method invocations.
     *
     * @param safeMode default is {@code true}.
     */
    void setSafeMode(boolean safeMode);

    /**
     *
     * @return {@code true} if the graph is in safe mode.
     */
    boolean isSafeMode();

}
