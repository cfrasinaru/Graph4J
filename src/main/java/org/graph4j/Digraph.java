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
 * Represents a directed graph.
 *
 * @author Cristian Frăsinaru
 * @param <V> the type of vertex labels in this graph.
 * @param <E> the type of edge labels in this graph.
 */
public interface Digraph<V, E> extends Graph<V, E> {

    /**
     * Returns the maximum number of edges (arcs) in a directed graph with the
     * specified number of vertices.
     *
     * @param numVertices a number of vertices.
     * @return the maximum number of edges (arcs) in a digraph with
     * {@code numVertices} vertices.
     */
    /**
     * Utility method that returns the maximum number of edges a directed graph
     * with a specified number of vertices can have. A digraph with {@code n}
     * vertices, without multiple edges or self-loops, can have at most
     * {@code n*(n-1)} edges.
     *
     * @param numVertices a number of vertices.
     * @return the maximum number of edges a graph of order {@code numVertices}
     * can have.
     */
    static long maxEdges(int numVertices) {
        return (long) numVertices * (numVertices - 1);
    }

    /**
     * Creates the <em>support graph</em> of this digraph. The support graph of
     * a digraph G is an undirected graph containing all the vertices of G and
     * one edge (v,u) for any pair of vertices v and u of the digraph that are
     * connected by an arc, in either direction: from v to u, form u to v or
     * both ways. The resulting graph has no data on its edges, does not contain
     * self loops or multiple edges and the labels are all {@code null}.
     *
     * @return an undirected graph, representing the support graph.
     */
    Graph<V, E> supportGraph();

    /**
     * Creates an identical copy of the digraph.
     *
     * @return an identical copy of the digraph.
     */
    @Override
    Digraph<V, E> copy();

    /**
     * Creates the <em>complement</em> of the digraph. The complement of a
     * directed graph G has the same vertex set as G and its edge set consists
     * of the edges not present in G.
     *
     * @return the complement of the digraph.
     */
    @Override
    Digraph<V, E> complement();

    @Override
    Digraph<V, E> subgraph(VertexSet vertexSet);

    @Override
    default Digraph<V, E> subgraph(int... vertices) {
        return subgraph(new VertexSet(Digraph.this, vertices));
    }

    @Override
    Digraph<V, E> subgraph(Collection<Edge> edges);

    /**
     * Returns the outdegree of a vertex, i.e. the number of its successors.
     *
     * @param v a vertex number.
     * @return the outdegree of v.
     */
    default int outdegree(int v) {
        return degree(v);
    }

    /**
     * Creates the outdegree sequence.
     *
     * @return the array of vertex outdegrees.
     */
    default int[] outdegrees() {
        return degrees();
    }

    /**
     * Returns the indegree of a vertex, i.e. the number of its predecessors.
     *
     * @param v a vertex number.
     * @return the indegree of v.
     */
    default int indegree(int v) {
        int inDegree = 0;
        for (int i = 0, n = numVertices(); i < n; i++) {
            if (containsEdge(vertexAt(i), v)) {
                inDegree++;
            }
        }
        return inDegree;
    }

    /**
     * Creates the indegree sequence.
     *
     * @return the array of vertex indegrees.
     */
    default int[] indegrees() {
        int n = numVertices();
        int[] inDegrees = new int[n];
        for (int i = 0; i < n; i++) {
            inDegrees[i] = indegree(vertexAt(i));
        }
        return inDegrees;
    }

    /**
     * Creates an array containing the vertex numbers of the successors of a
     * specified vertex. If all that is wanted is iterating over the
     * predecessors of v, {@link #predecessorIterator(int)} method is more
     * effective in terms of memory and time required to access information
     * regarding the edges incident to v.
     *
     * @param v a vertex number.
     * @return the successors of v.
     */
    default int[] successors(int v) {
        return neighbors(v);
    }

    /**
     * Creates an iterator over the successors of v. The iterator offers in
     * {@code O(1)} time information regarding the edges incident from v.
     *
     * @param v a vertex number.
     * @return an iterator over the successors of v.
     */
    default SuccessorIterator<E> successorIterator(int v) {
        return successorIterator(v, -1);
    }

    /**
     * Creates an iterator over the edges incident from v, returning the
     * successors of v, starting from a specified position in the successors
     * adjacency list of v, along with information regarding their edges.
     *
     * @param v a vertex number.
     * @param pos a position in the successors adjacency list of v.
     * @return an iterator over the successors of v, starting from a specified
     * position in the adjacency list of v.
     */
    SuccessorIterator successorIterator(int v, int pos);

    /**
     * Creates an array holding the predecessors of a specified vertex. If all
     * that is wanted is iterating over the predecessors of v,
     * {@link #predecessorIterator(int)} method is more effective in terms of
     * memory and time required to access information regarding the edges
     * incident to v.
     *
     * @param v a vertex number.
     * @return the predecessors of v.
     */
    default int[] predecessors(int v) {
        int[] pred = new int[indegree(v)];
        int k = 0;
        for (int i = 0, n = numVertices(); i < n; i++) {
            int u = vertexAt(i);
            if (containsEdge(u, v)) {
                pred[k++] = u;
            }
        }
        return pred;
    }

    /**
     * Creates an iterator over the predecessors of a specified vertex. The
     * iterator offers in {@code O(1)} time information regarding the edges
     * incident to v.
     *
     * @param v a vertex number.
     * @return an iterator over the predecessors of v.
     */
    default PredecessorIterator predecessorIterator(int v) {
        return predecessorIterator(v, -1);
    }

    /**
     * Creates an iterator over the predecessors of a vertex v, starting from a
     * specified position in the predecessors adjacency list of v. The iterator
     * offers in @{code O(1)} time information regarding the edges incident to
     * v.
     *
     * @param v a vertex number.
     * @param pos a position in the predecessors adjacency list of v.
     * @return an iterator over the successors of v, starting from a specified
     * position in the predecessors adjacency list of v.
     */
    PredecessorIterator predecessorIterator(int v, int pos);

    NeighborIterator neighborIterator(int v, boolean allEdges);

    /**
     * Creates an array holding the edges outgoing from a vertex. If creating
     * the {@code Edge} objects is not required, a more effective method is
     * {@link #successorIterator(int)}.
     *
     * @param v a vertex number.
     * @return outgoing edges from v.
     */
    default Edge[] outgoingEdgesFrom(int v) {
        return edgesOf(v);
    }

    /**
     * Creates an array holding the edges incoming to a vertex. If creating the
     * {@code Edge} objects is not required, a more effective method is
     * {@link #predecessorIterator(int)}.
     *
     * @param v a vertex number.
     * @return incoming edges to v.
     */
    default Edge[] incomingEdgesTo(int v) {
        Edge[] edges = new Edge[indegree(v)];
        int k = 0;
        for (int i = 0, n = numVertices(); i < n; i++) {
            int u = vertexAt(i);
            if (containsEdge(u, v)) {
                edges[k++] = edge(u, v);
            }
        }
        return edges;
    }

    /**
     * Checks if the digraph is <em>symmetrical</em>. A symmetrical digraph has
     * only pairs of symmetrical edges, i.e. if the edge (v,u) belongs to the
     * digraph, so does (u,v).
     *
     * @return {@code true} if the digraph is symmetrical.
     */
    default boolean isSymmetrical() {
        for (int v : vertices()) {
            for (var it = neighborIterator(v); it.hasNext();) {
                int u = it.next();
                if (!containsEdge(u, v)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks if a vertex is a <em>source</em>, meaning that it has no
     * predecessors.
     *
     * @param v a vertex number.
     * @return {@code true} if {@code v} has indegree 0, {@code false}
     * otherwise.
     */
    default boolean isSource(int v) {
        return indegree(v) == 0;
    }

    /**
     * Checks if a vertex is a <em>sink</em>, meaning that it has no successors.
     *
     * @param v a vertex number.
     * @return {@code true} if {@code v} has outdegree 0, {@code false}
     * otherwise.
     */
    default boolean isSink(int v) {
        return outdegree(v) == 0;
    }
}
