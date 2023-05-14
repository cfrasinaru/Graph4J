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
package org.graph4j.alg;

import org.graph4j.Graph;
import org.graph4j.Multigraph;
import org.graph4j.util.VertexList;

/**
 * Counts the number of triangles in an undirected graph. A triangle is formed
 * by three distinct vertices all connected with each other.
 *
 * Complexity <code>O(|E|^{3/2})</code>.
 *
 * See Ullman, Jeffrey: "Mining of Massive Datasets", Chapter 10
 *
 * @author Cristian Frăsinaru
 * @author Gabriel Ignat
 */
public class TriangleCounter extends UndirectedGraphAlgorithm {

    public TriangleCounter(Graph graph) {
        super(graph);
    }

    //naive count using 3 fors
    private long naiveCount(VertexList vertices) {
        if (graph.isAllowingMultipleEdges()) {
            return naiveCountMulti(vertices);
        }
        return naiveCountSimple(vertices);
    }

    //naive count for simple graphs
    private long naiveCountSimple(VertexList vertices) {
        long count = 0;
        for (int i = 0, size = vertices.size(); i < size - 2; i++) {
            int v = vertices.get(i);
            for (int j = i + 1; j < size - 1; j++) {
                int u = vertices.get(j);
                if (!graph.containsEdge(v, u)) {
                    continue;
                }
                for (int k = j + 1; k < size; k++) {
                    int w = vertices.get(k);
                    if (graph.containsEdge(v, w) && graph.containsEdge(u, w)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    //naive count for multigraphs
    private long naiveCountMulti(VertexList vertices) {
        Multigraph g = (Multigraph) graph;
        long count = 0;
        for (int i = 0, size = vertices.size(); i < size - 2; i++) {
            int v = vertices.get(i);
            for (int j = i + 1; j < size - 1; j++) {
                int u = vertices.get(j);
                int vuEdgeCount = g.multiplicity(v, u);
                if (vuEdgeCount == 0) {
                    continue;
                }
                for (int k = j + 1; k < size; k++) {
                    int w = vertices.get(k);
                    int vwEdgeCount = g.multiplicity(v, w);
                    if (vwEdgeCount == 0) {
                        continue;
                    }
                    int uwEdgeCount = g.multiplicity(u, w);
                    count += vuEdgeCount * vwEdgeCount * uwEdgeCount;
                }
            }
        }
        return count;
    }

    /**
     *
     * @return the number of triangles in the graph.
     */
    public long count() {
        int n = graph.numVertices();
        final int sqrtNumVertices = (int) Math.sqrt(n);

        VertexList heavyHitters = new VertexList(graph, n);
        for (int v : graph.vertices()) {
            if (graph.degree(v) >= sqrtNumVertices) {
                heavyHitters.add(v);
            }
        }
        long numberOfTriangles = naiveCount(heavyHitters);

        //for each vertex of the graph
        for (int v : graph.vertices()) {
            int vi = graph.indexOf(v);
            int vDeg = graph.degree(v);
            //for each neighbor of v
            for (var vNeighbors = graph.neighborIterator(v); vNeighbors.hasNext();) {
                int u = vNeighbors.next();
                int ui = graph.indexOf(u);
                if (vi >= ui) {
                    continue;
                }
                int uDeg = graph.degree(u);
                //for each neighbor w of u (and v)
                for (var uNeighbors = graph.neighborIterator(u); uNeighbors.hasNext();) {
                    int w = uNeighbors.next();
                    int wi = graph.indexOf(w);
                    int wDeg = graph.degree(w);
                    if (ui >= wi || (vDeg >= sqrtNumVertices && uDeg >= sqrtNumVertices && wDeg >= sqrtNumVertices)) {
                        continue;
                    }
                    if (graph.containsEdge(w, v)) {
                        //vuw is a triangle
                        numberOfTriangles++;
                    }
                }
            }
        }
        return numberOfTriangles;
    }
}
