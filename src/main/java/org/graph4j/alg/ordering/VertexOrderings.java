/*
 * Copyright (C) 2023 Cristian Frăsinaru and contributors
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
package org.graph4j.alg.ordering;

import java.util.stream.IntStream;
import org.graph4j.Digraph;
import org.graph4j.Graph;
import org.graph4j.traverse.BFSIterator;
import org.graph4j.traverse.DFSIterator;

/**
 * Contains static methods that produce various vertex orderings.
 *
 * @author Cristian Frăsinaru
 */
public class VertexOrderings {

    private VertexOrderings() {
    }

    /**
     * Creates an array containing the vertices of the graph in the order
     * produced by a DFS (Depth First Search) traversal.
     *
     * @param graph the input graph.
     * @param startVertex the start vertex for the DFS traversal.
     * @return the ordering produced by a DFS traversal, starting from the
     * specified vertex.
     */
    public static int[] depthFirst(Graph graph, int startVertex) {
        int[] vertices = new int[graph.numVertices()];
        var it = new DFSIterator(graph, startVertex);
        int i = 0;
        while (it.hasNext()) {
            vertices[i++] = it.next().vertex();
        }
        return vertices;
    }

    /**
     * Creates an array containing the vertices of the graph in the order
     * produced by a BFS (Breadth First Search) traversal.
     *
     * @param graph the input graph.
     * @param startVertex the start vertex for the BFS traversal.
     * @return the ordering produced by a BFS traversal, starting from the
     * specified vertex.
     */
    public static int[] breadthFirst(Graph graph, int startVertex) {
        int[] vertices = new int[graph.numVertices()];
        var it = new BFSIterator(graph, startVertex);
        int i = 0;
        while (it.hasNext()) {
            vertices[i++] = it.next().vertex();
        }
        return vertices;
    }

    /**
     * Creates an array containing the vertices of the graph in decreasing order
     * by their degree. In case of ties, they are ordered by their indices in
     * the graph. If the input argument is a directed graph, the ordering is
     * created by the outdegree of the vertices.
     *
     * @param graph the input graph.
     * @return the vertices of the graph in decreasing order by their degree.
     */
    public static int[] largestDegreeFirst(Graph graph) {
        int[] deg = graph.degrees();
        int n = deg.length;
        return IntStream.of(graph.vertices())
                .boxed()
                .sorted((v, u) -> n * (deg[graph.indexOf(u)] - deg[graph.indexOf(v)]) + (v - u))
                .mapToInt(Integer::intValue)
                .toArray();
    }

    /**
     * Creates an array containing the vertices of the graph in increasing order
     * by their degree. In case of ties, they are ordered by their indices in
     * the graph. If the input argument is a directed graph, the ordering is
     * created by the outdegree of the vertices.
     *
     * @param graph the input graph.
     * @return the vertices of the graph in increasing order by their degree.
     */
    public static int[] smallestDegreeFirst(Graph graph) {
        int[] deg = graph.degrees();
        int n = deg.length;
        return IntStream.of(graph.vertices())
                .boxed()
                .sorted((v, u) -> n * (deg[graph.indexOf(v)] - deg[graph.indexOf(u)]) + (v - u))
                .mapToInt(Integer::intValue)
                .toArray();
    }

    /**
     * Computes the vertex ordering from the end to the begining: at each step,
     * the node of minimum degree in the current graph is selected and then it
     * is removed from the graph.
     *
     * @see SmallestDegreeLastOrdering
     * @param graph the input graph.
     * @return the vertex ordering according to the smallest-degree-last
     * strategy.
     */
    public static int[] smallestDegreeLast(Graph graph) {
        return new SmallestDegreeLastOrdering(graph).compute();
    }

    /**
     * Computes a vertex ordering of a directed graph such that for every
     * directed edge uv from vertex u to vertex v, u comes before v in the
     * ordering.
     *
     * @param graph the input directed graph.
     * @return the topological order, or {@code null} if the digraph is not
     * acyclic.
     * @see TopologicalSorting
     */
    public static int[] topological(Digraph graph) {
        return new TopologicalSorting(graph).compute();
    }

}
