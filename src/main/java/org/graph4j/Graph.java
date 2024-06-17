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
import java.util.Objects;
import org.graph4j.util.VertexSet;

/**
 * The base data type for all graphs: directed or not, weighted or not, labeled
 * or not, allowing self loops and multiple edges or not.
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
     * 0
     */
    static final int WEIGHT = 0;
    /**
     * 1
     */
    static final double DEFAULT_EDGE_WEIGHT = 1; //for distance algorithms in unweighted graphs
    /**
     * 1
     */
    static final double DEFAULT_VERTEX_WEIGHT = 1;

    /**
     * Returns the name of the graph, or {@code null} if the graph has no name.
     *
     * @return the name of the graph.
     */
    String getName();

    /**
     * Sets the name of the graph, for example "K4", "C5", "Petersen", etc.
     *
     * @param name the name of the graph.
     */
    void setName(String name);

    /**
     * Returns the number of vertices in the graph. The number of vertices is
     * also called the <em>order</em> of the graph.
     *
     * @return the number of vertices in the graph.
     */
    int numVertices();

    /**
     * Returns the number of edges in the graph. The number of edges is also
     * called the <em>dimension</em> of the graph.
     *
     * @return the number of edges in the graph.
     */
    long numEdges();

    /**
     * Returns the maximum number of edges the graph can have. A simple graph
     * with {@code n} vertices can have at most {@code n*(n-1)/2} edges. A
     * directed graph may have {@code n*(n-1)} edges (also called arcs). For
     * multigraphs and pseudographs, this method returns {@code Long.MAX_VALUE}.
     *
     * @return the maximum number of edges the graph can have.
     */
    long maxEdges();

    /**
     * Utility method that returns the maximum number of edges a graph with a
     * specified number of vertices can have. A simple graph with {@code n}
     * vertices can have at most {@code n*(n-1)/2} edges.
     *
     * @param numVertices a number of vertices.
     * @return the maximum number of edges a graph of order {@code numVertices}
     * can have.
     */
    static long maxEdges(int numVertices) {
        return (long) numVertices * (numVertices - 1) / 2;
    }

    /**
     * Returns the array in which the vertices of the graph are stored. By
     * default, the vertices have numbers between 0 and {@code numVertices()-1},
     * however this is not mandatory. Vertices can be removed from the graph,
     * new vertices with any number can be added, etc.
     *
     * For performance reasons, this is the actual array where the vertices are
     * stored, so <b>do not modify it</b>.
     *
     * @return the vertices of the graph.
     */
    int[] vertices();

    /**
     * Checks if the graph has no vertices. An empty graph is also called the
     * <em>null</em> graph.
     *
     * @return {@code true} if the number of vertices is {@code 0},
     * {@code false} otherwise.
     */
    default boolean isEmpty() {
        return numVertices() == 0;
    }

    /**
     * Checks if the graph has no edges.
     *
     * @return {@code true} if the number of edges is {@code 0}, {@code false}
     * otherwise.
     */
    default boolean isEdgeless() {
        return numEdges() == 0;
    }

    /**
     * Checks if the graph is complete. In case of undirected graphs, that means
     * that there is an edge between every two vertices. In case of directed
     * graphs, for every two vertices {@code v} and {@code u}, both {@code vu}
     * and {@code uv} edges must exist. In case of multigraphs and pseudographs,
     * the presence of self-loops or multiple edges between two vertices does
     * not affect this property.
     *
     * @return {@code true} if the graph is complete, {@code false} otherwise.
     */
    boolean isComplete();

    /**
     * Returns the number of the vertex with the specified index. The vertices
     * of a graph are indexed in the array returned by the method
     * {@link #vertices()}.
     *
     * @param index a value between {@code 0} and {@code numVertices() - 1}.
     * @return the vertex at the specified index (position) in the array.
     */
    int vertexAt(int index);

    /**
     * Creates an iterator over the vertices of the graph.
     *
     * @return an iterator over the vertices of the graph.
     */
    VertexIterator<V> vertexIterator();

    /**
     * Returns the index corresponding to a vertex number. The index of a vertex
     * represents the position where it is stored in the array returned by the
     * method {@link #vertices()}.
     *
     * @param v a vertex number.
     * @return the index of the specified vertex number.
     */
    int indexOf(int v);

    /**
     * Creates and returns an array holding all the edges of the graph. This
     * method has a high memory overhead, since the edge objects are not stored
     * internally in the graph, so it should only be used if all the edges are
     * required to be in the same data structure for a specific purpose. In
     * order to iterate efficiently over the edges of the graph it is better to
     * use {@link Graph#edgeIterator()} or {@link Graph#neighborIterator(int)}.
     *
     * The method works only if the number of edges is less than
     * {@code Integer.MAX_VALUE}.
     *
     * @return an array of all the edges in the graph.
     */
    Edge[] edges();

    /**
     * Creates and returns an array holding the edges incident to a specified
     * vertex.
     *
     * @param v a vertex number.
     * @return the edges formed by {@code v} with its neighbors (successors).
     */
    Edge[] edgesOf(int v);

    /**
     * Returns an iterator over the edges in this graph. There are no guarantees
     * concerning the order in which the edges are returned.
     *
     * @return an iterator over the edges in this graph.
     */
    EdgeIterator<E> edgeIterator();

    /**
     * Returns an {@link Edge} object corresponding to the specified vertices
     * (its endpoints). If there is no such edge in the graph, it throws
     * {@code InvalidEdgeException}.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return the edge in the graph with the endpoints {@code v} and {@code u}.
     * @throws InvalidEdgeException if there is no {@code vu} edge in the graph.
     */
    Edge<E> edge(int v, int u);

    /**
     * Checks if two vertices of the graph are adjacent. In case of undirected
     * graphs, that means that v is in the adjacency list of u and u is also in
     * the adjacency list of v. In case of directed graphs, the method returns
     * {@code true} only if u is in the adjacency list of v (u is a successor of
     * v).
     *
     * @param u a vertex number
     * @param v a vertex number
     * @return {@code true} if u is in the adjacency list of v, {@code false}
     * otherwise.
     */
    boolean containsEdge(int v, int u);

    /**
     * Checks if an edge belongs to the graph. See also
     * {@link #containsEdge(int, int)}.
     *
     * @param e an edge.
     * @return {@code true} if the edge belongs to the graph, {@code false}
     * otherwise.
     */
    default boolean containsEdge(Edge e) {
        return containsEdge(e.source(), e.target());
    }

    /**
     * Checks if the graph contains a specific vertex number.
     *
     * @param v a vertex number.
     * @return {@code true} if v belongs to the graph.
     */
    default boolean containsVertex(int v) {
        return indexOf(v) >= 0;
    }

    /**
     * Returns the  <em>neighbors</em> of a vertex. The neighbors of a vertex v
     * are the vertices in the adjacency list of v. For performance reasons,
     * this method returns the actual array used for storing the adjacency list,
     * so <b>do not modify it</b>.
     *
     * In case of directed graphs, this method returns the same as
     * {@link  Digraph#successors(int)}.
     *
     * For a more efficient iteration over the neighbors of a vertex, use
     * {@link #neighborIterator(int)}.
     *
     * @param v a vertex number.
     * @return the vertices that are adjacent to {@code v}.
     */
    int[] neighbors(int v);

    /**
     * Creates an iterator over the neighbors of a vertex. The iterator returns
     * the neighbors of a vertex v, along with information regarding the edges
     * connecting v to them. Using this method is the most efficient way to
     * explore the neighborhood of a vertex.
     *
     * @param v a vertex number.
     * @return an iterator over the neighbors of {@code v}.
     */
    default NeighborIterator<E> neighborIterator(int v) {
        return neighborIterator(v, -1);
    }

    /**
     * Creates an iterator over the neighbors of a vertex, starting from a
     * specified position in the adjacency list.
     *
     * @param v a vertex number.
     * @param pos a position in the adjacency list of {@code v}.
     * @return an iterator over the neighbors of {@code v}, starting from the
     * position {@code pos} in the adjacency list of {@code v}.
     */
    NeighborIterator<E> neighborIterator(int v, int pos);

    /**
     * Returns the first position of u in the neighbor list of v.
     *
     * @param v a vertex number.
     * @param u a vertex number;
     * @return the first position of {@code u} in the neighbor list of
     * {@code v}.
     */
    int adjListPos(int v, int u);

    /**
     * The degree of a vertex is the number of its neighbors, that is vertices
     * that are in its adjacency list. In case of directed graphs, this method
     * returns the number of successors, same as {@link  Digraph#outdegree(int)}.
     *
     * @param v a vertex number.
     * @return the degree of the vertex {@code v}.
     */
    int degree(int v);

    /**
     * Checks if a vertex is <em>isolated</em>, meaning that it has no
     * neighbors. In case of directed graphs, it checks if the vertex has no
     * successors, same as {@link Digraph#isSink(int)}.
     *
     * @param v a vertex number.
     * @return {@code true} if {@code v} has degree 0.
     */
    default boolean isIsolated(int v) {
        return degree(v) == 0;
    }

    /**
     * Checks if a vertex is <em>pendant</em>, meaning that it has a single
     * neighbor. In case of directed graphs, it checks if the vertex has
     * outdegree {@code 1}.
     *
     * @param v a vertex number.
     * @return {@code true} if {@code v} has degree 1.
     */
    default boolean isPendant(int v) {
        return degree(v) == 1;
    }

    /**
     * Checks if a vertex is <em>universal</em>, meaning that it is adjacent to
     * all other vertices in the graph. In case of simple graphs, it has the
     * degree {@code numVertices()-1}. In case of directed graphs it has the
     * outdegree {@code numVertices()-1}.
     *
     * @param v a vertex number.
     * @return {@code true} if v is universal.
     */
    default boolean isUniversal(int v) {
        return degree(v) == numVertices() - 1;
    }

    /**
     * Creates and returns an array representing the degree sequence of the
     * graph. The value {@code degrees()[index]} represents the degree of the
     * vertex with the specified index, that is {@code vertexAt(index)}.
     *
     * @return the degree sequence of the graph.
     */
    int[] degrees();

    /**
     * Adds a new edge to the graph. The endpoints of the edge are identified
     * using their vertex numbers.
     *
     * The edge is not added if it already exists and the graph does not allow
     * multiple edges, or if the endpoints are equal and the graph does not
     * allow self loops. If the edge is not added, the method returns
     * {@code -1}.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return the position of {@code u} in the adjacency list of {@code v} if
     * {@code (v,u)} edge was added, or {@code -1} if the edge was not added.
     * @throws InvalidVertexException if either of the two vertices is not in
     * the graph.
     */
    int addEdge(int v, int u);

    /**
     * Adds a new edge to the graph. The {@link Edge} object contains the
     * endpoints and, if necessary, its weight and label. The {@code directed}
     * property of an edge is ignored when it is added to the graph as it will
     * assume the graph type.
     *
     * Note that the {@code Edge} objects are not actually stored in the graph.
     *
     * @param e an edge object.
     * @return the position of the target node in the source node adjacency list
     * if the edge was added, or {@code -1} if the edge was not added.
     * @throws InvalidVertexException if the source or the target of the edge
     * are not in the graph.
     */
    int addEdge(Edge<E> e);

    /**
     * Adds a new edge to the graph. The endpoints of the edge are specified
     * using the labels of the vertices, which should be uniquely identifiable.
     *
     * @param vLabel the label of a uniquely identifiable vertex.
     * @param uLabel the label of a uniquely identifiable vertex.
     * @return the position of u in the adjacency list of v, or {@code -1} if
     * the edge is already in the graph or vLabel and uLabel are the same.
     */
    default int addEdge(V vLabel, V uLabel) {
        Objects.requireNonNull(vLabel);
        Objects.requireNonNull(uLabel);
        if (!hasVertexLabels()) {
            if (vLabel instanceof Integer && uLabel instanceof Integer) {
                int v = (Integer) vLabel;
                int u = (Integer) uLabel;
                return addEdge(v, u);
            }
            throw new IllegalArgumentException("The graph has no labeled vertices.");
        }
        int v = findVertex(vLabel);
        if (v == -1) {
            throw new InvalidVertexException(vLabel);
        }
        int u = findVertex(uLabel);
        if (u == -1) {
            throw new InvalidVertexException(vLabel);
        }
        return Graph.this.addEdge(v, u);
    }

    /**
     * Removes the specified edge from the graph.
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @throws IllegalArgumentException if the edge does not exist.
     */
    void removeEdge(int v, int u);

    /**
     * Removes the specified edge from the graph.
     *
     * @param e an edge of the graph.
     * @throws IllegalArgumentException if the edge does not exist.
     */
    default void removeEdge(Edge e) {
        removeEdge(e.source(), e.target());
    }

    /**
     * Removes all edges incident with a vertex. In case of digraphs, it removes
     * all the edges incident from the vertex (having as source the specified
     * vertex).
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
     * Adds a new vertex to the graph having a specified number. The vertex
     * number should not be negative and must not exist already in the graph.
     * Adding vertices with unnecessary large numbers may increase the memory
     * occupied by the graph.
     *
     * @param v a vertex number that does not exist in the graph.
     * @return the index of the added vertex.
     * @throws InvalidVertexException if {@code v} is negative or it is already
     * in the graph.
     */
    int addVertex(int v);

    /**
     * Convenience method for adding multiple vertices to the graph. It invokes
     * the {@link #addVertex(int)} method for each vertex.
     *
     *
     * @param vertices an array of vertex numbers.
     * @throws InvalidVertexException if there are negative numbers or any of
     * them already exists in the graph.
     */
    default void addVertices(int... vertices) {
        for (int v : vertices) {
            addVertex(v);
        }
    }

    /**
     * Removes the specified vertex from the graph, together with all the edges
     * incident to or from it.
     *
     * @param v a vertex number.
     * @throws InvalidVertexException if the vertex is not in the graph.
     */
    void removeVertex(int v);

    /**
     * Convenience method for removing multiple vertices from the graph. It
     * invokes the {@link #removeVertex(int)} method for each vertex.
     *
     * @param vertices an array of vertex numbers.
     * @throws InvalidVertexException if any of the vertices is not in the
     * graph.
     */
    default void removeVertices(int... vertices) {
        for (int v : vertices) {
            removeVertex(v);
        }
    }

    /**
     * Creates a new vertex adjacent to all the neighbors of a specified vertex.
     *
     * @param v the vertex to be duplicated.
     * @return the number of the newly created vertex.
     */
    int duplicateVertex(int v);

    /**
     * Creates a new vertex that will replace the specified vertices, being
     * connected to all their neighbors.
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
     * Checks if the vertex numbers are the default ones, in the range
     * {@code 0..numVertices-1}.
     *
     * @return {@code true} if the vertex numbers are in the range
     * {@code 0..numVertices-1}, {@code false} otherwise.
     */
    boolean isDefaultVertexNumbering();

    /**
     * Creates a new vertex adjacent to v and u, and removes the edge (v,u).
     *
     * @param v a vertex number.
     * @param u a vertex number.
     * @return the number of the newly created vertex.
     * @throws InvalidEdgeException if the edge {@code vu} does not exist.
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
     * Adds the vertices and edges of another graph. The vertex sets of the two
     * graphs must be disjoint.
     *
     * @param graph the graph to be added.
     */
    default void addGraph(Graph<V, E> graph) {
        for (int v : graph.vertices()) {
            addVertex(v);
        }
        for (var it = graph.edgeIterator(); it.hasNext();) {
            addEdge(it.next());
        }
    }

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
     * Creates and returns the subgraph induced by an array of vertices.
     *
     * @param vertices an array of vertices.
     * @return the subgraph induced by the specified vertices.
     */
    default Graph<V, E> subgraph(int... vertices) {
        return subgraph(new VertexSet(Graph.this, vertices));
    }

    /**
     * Creates and returns the subgraph induced by a set of vertices.
     *
     * @param vertexSet a set of vertices.
     * @return the subgraph induced by the specified vertices.
     */
    Graph<V, E> subgraph(VertexSet vertexSet);

    /**
     * Creates and returns the subgraph generated by a set of edges.
     *
     * @param set a set of edges.
     * @return the subgraph generated by the given edges.
     */
    //Graph<V, E> subgraph(EdgeSet set);
    /**
     * Creates and returns the subgraph generated by a collection of edges.
     *
     * @param edges a collection of edges.
     * @return the subgraph generated by the given edges.
     */
    Graph<V, E> subgraph(Collection<Edge> edges);

    /**
     * Creates the <em>complement</em> of the graph. The complement of a graph G
     * has the same vertex set as G and its edge set consists of the pairs of
     * vertices that do not form an edge in G.
     *
     * @return the complement of the graph.
     */
    Graph<V, E> complement();

    /**
     * Creates and return the <em>adjacency matrix</em>. The adjacency matrix of
     * a graph shows the relationship between vertices. The matrix is square,
     * the number of lines and columns being equal to the number of vertices,
     * and it contains non-negative integers. An element at row i and column j
     * has a positive value if <code>u = vertexAt(j)</code> appears in the
     * adjacency list of <code>v = vertexAt(i)</code>, where(v,u) represents an
     * edge, and the value denotes the multiplicity of the edge (v,u), otherwise
     * it is 0. The elements on the main diagonal denote the number of
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
     * Creates and returns the <em>weight matrix</em>. The weight matrix is
     * created according to the same principles as the adjacency matrix. An
     * element at row i and column j corresponding to an edge (v,u), where
     * <code>v = vertexAt(i)</code> and <code>u = vertexAt(j)</code>, has as
     * value the weight of that edge: <code>getEdgeWeight(v,u)</code>. The
     * values on the main diagonal are all 0 otherwise, if the cell does not
     * correspond to an edge, it has the value
     * <code>Graph.NO_EDGE_WEIGHT</code>.
     *
     * The weight-matrix is not defined for multigraphs and pseudographs.
     *
     * @return the weight matrix.
     */
    double[][] weightMatrix();

    /**
     * Creates and returns the <em>incidence matrix</em>. The incidence matrix
     * shows the relationship between vertices and edges. Lines are associated
     * with the vertices and columns with the edges. A vertex
     * <code>v = vertexAt(i)</code> has the line number i, while an edge between
     * <code>v = vertexAt(i)</code> and <code>u = vertexAt(j)</code> has the
     * column number equal to its index, that is the position it appears in the
     * array returned by the <code>edges()</code> method.
     *
     * In case of undirected graphs, an element at row i and column k has the
     * value 1 if the edge having the index k is incident with the vertex
     * <code>v = vertexAt(i)</code>, and 0 otherwise.
     *
     * In case of directed graphs, an element at row i and column k has the
     * value 1 if the edge having the index k is incident <em>from</em> the
     * vertex <code>v = vertexAt(i)</code>, -1 if it is incident <em>to</em> v,
     * and 0 otherwise.
     *
     * Works only if the number of edges is less than {@code Integer.MAX_VALUE}.
     *
     * @return the incidence matrix.
     */
    int[][] incidenceMatrix();

    /**
     * Convenience method for testing if the graph is a directed graph.
     *
     * @return {@code true} if this is an instance of
     * {@link Digraph}, {@code false} otherwise.
     */
    boolean isDirected();

    /**
     * Convenience method for testing if the graph is a multigraph.
     *
     * @return {@code true} if this is an instance of
     * {@link Multigraph}, {@code false} otherwise.
     */
    boolean isAllowingMultipleEdges();

    /**
     * Convenience method for testing if the graph is a pseudograph.
     *
     * @return {@code true} if this is an instance of
     * {@link Pseudograph}, {@code false} otherwise.
     */
    boolean isAllowingSelfLoops();

    /**
     * Convenience method for testing if the graph does not contain multiple
     * edges or self loops.
     *
     * @return {@code true} if this is an instance of {@link Pseudograph}.
     */
    default boolean isSimple() {
        return !isAllowingMultipleEdges() && !isAllowingSelfLoops();
    }

    /**
     * Sets the flag indicating whether the graph is in safe mode or not. In
     * safe mode, various checks are performed in order to respect the graph
     * constraints and to prevent illegal method invocations. Setting the safe
     * mode to {@code false} is useful when generating various type of graphs
     * using algorithms that are guaranteed to respect the graph constraints.
     *
     * By default, all graphs are in safe mode.
     *
     * @param safeMode {@code true} if the safe mode is enabled (default),
     * {@code false} otherwise.
     */
    void setSafeMode(boolean safeMode);

    /**
     * Checks if the graph is in safe mode.
     *
     * @return {@code true} if the graph is in safe mode, {@code false}
     * otherwise.
     */
    boolean isSafeMode();

    /**
     * Sets the maximum number of numerical values that can be stored on edges.
     * Each such value must have an index corresponding to a number between 0
     * and {@code edgeDataSize - 1}.
     *
     * @param edgeDataSize how many numeric values are stored on the edges.
     */
    void setEdgeDataSize(int edgeDataSize);

    /**
     * Returns the maximum number of values that can be stored on edges.
     *
     * By default, the method returns 1, since all graphs allow setting the
     * weight of an edge. The weight has the predefined index
     * {@link Graph#WEIGHT}, equal to 0.
     *
     * In case of {@code Networks}, the method returns 3, corresponding to
     * {@link Network#CAPACITY}, {@link Network#COST} and {@link Network#FLOW}.
     *
     * @return the maximum number of values that can be stored on edges.
     */
    int getEdgeDataSize();
}
