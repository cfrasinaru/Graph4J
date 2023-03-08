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

import org.graph4j.util.VertexSet;

/**
 * A directed graph.
 *
 * @author Cristian Frăsinaru
 * @param <V> the type of vertex labels in this graph.
 * @param <E> the type of edge labels in this graph.
 */
public interface Digraph<V, E> extends Graph<V, E> {

    /**
     *
     * @param numVertices
     * @return the maximum number of edges (arcs) in a digraph with
     * {@code numVertices}
     */
    static long maxEdges(int numVertices) {
        return (long) numVertices * (numVertices - 1);
    }

    /**
     * The <i>support graph</i> of a digraph G is an undirected graph containing
     * all the vertices of G and one edge vu for any pair of vertices v and u of
     * the digraph that are connected by an arc, in either direction: from v to
     * u, form u to v or both ways. The resulting graph is unweighted, does not
     * contain self loops or multiple edges and the labels are all null.
     *
     * @return a new graph, representing the support graph
     */
    Graph<V, E> supportGraph();

    /**
     *
     * @return an identical copy of the digraph
     */
    @Override
    Digraph<V, E> copy();

    /**
     * The <i>complement</i> of a digraph G has the same vertex set as G, but
     * its edge set consists of the edges not present in G.
     *
     * @return the complement of the digraph.
     */
    @Override
    Digraph<V, E> complement();

    /**
     *
     * @param vertices an array of vertices.
     * @return the subgraph induced by the given vertices.
     */
    @Override
    Digraph<V, E> subgraph(int... vertices);

    @Override
    default Digraph<V, E> subgraph(VertexSet set) {
        return subgraph(set.vertices());
    }

    /**
     *
     * @param v a vertex number.
     * @return the outdegree of v.
     */
    default int outdegree(int v) {
        return degree(v);
    }

    /**
     *
     * @return the array of vertex outdegrees.
     */
    default int[] outdegrees() {
        return degrees();
    }

    /**
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
     *
     * @param v a vertex number.
     * @return the successors of v.
     */
    default int[] successors(int v) {
        return neighbors(v);
    }

    /**
     *
     * @param v a vertex number.
     * @return an iterator over the successors of v.
     */
    default SuccessorIterator successorIterator(int v) {
        return successorIterator(v, -1);
    }

    /**
     * Iterates over the edges incident from v, returning the successors of v,
     * starting from a specified position in the successors adjacency list of v,
     * along with information regarding their edges.
     *
     * @param v a vertex number.
     * @param pos a position in the successors adjacency list of v.
     * @return an iterator over the successors of v, starting from a specified
     * position in the adjacency list of v.
     */
    SuccessorIterator successorIterator(int v, int pos);

    /**
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
     *
     * @param v a vertex number.
     * @return an iterator over the predecessors of v.
     */
    default PredecessorIterator predecessorIterator(int v) {
        return predecesorIterator(v, -1);
    }

    /**
     * Iterates over the edges incident to v, returning the predecessors of v,
     * starting from a specified position in the predecessors adjacency list of
     * v, along with information regarding their edges.
     *
     * @param v a vertex number.
     * @param pos a position in the predecessors adjacency list of v.
     * @return an iterator over the successors of v, starting from a specified
     * position in the predecessors adjacency list of v.
     */
    PredecessorIterator predecesorIterator(int v, int pos);

    /**
     *
     * @param v a vertex number.
     * @return outgoing edges from v.
     */
    default Edge[] outgoingEdgesFrom(int v) {
        return edgesOf(v);
    }

    /**
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
     * A <i>symmetrical</i> digraph has only pairs of symmetrical edges: if the
     * edge vu belongs to the digraph, so does uv.
     *
     * @return {@code true} if the digraph is symmetrical.
     */
    default boolean isSymmetrical() {
        for (int v : vertices()) {
            for (int u : neighbors(v)) {
                if (!containsEdge(u, v)) {
                    return false;
                }
            }
        }
        return true;
    }
}
