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
package org.graph4j.generators;

import java.util.Objects;
import java.util.Random;
import org.graph4j.Graph;
import org.graph4j.GraphBuilder;
import org.graph4j.util.VertexList;

//https://en.wikipedia.org/wiki/Barab%C3%A1si%E2%80%93Albert_model
/**
 * The Barabási–Albert (BA) model generates random scale-free networks using a
 * preferential attachment mechanism.
 *
 * The network begins with an initial graph. New vertices are added to the
 * network, being connected to existing vertices with a probability that is
 * proportional to the number of links that the existing vertices already have.
 *
 * Vertices with higher degrees ("hubs") will quickly accumulate even more
 * neighbors, while vertices with lower degrees are unlikely to be chosen
 * as the destination for a new edge. The new vertices have a "preference" to
 * attach themselves to the already heavily linked vertices.
 *
 * @author Cristian Frăsinaru
 */
public class BarabasiAlbertGenerator {

    private final int numVertices;
    private final int edgesPerVertex;
    private final Graph initialGraph;

    /**
     * The initial graph will be a complete graph.
     *
     * @param initialNumVertices the initial number of vertices.
     * @param edgesPerVertex the number of edges per vertex.
     * @param numVertices the total number of vertices.
     */
    public BarabasiAlbertGenerator(int initialNumVertices, int edgesPerVertex, int numVertices) {
        this.numVertices = numVertices;
        if (initialNumVertices < 1) {
            throw new IllegalArgumentException("The initial number of vertices must be >= 1");
        }
        this.edgesPerVertex = edgesPerVertex;
        this.initialGraph = GraphGenerator.complete(initialNumVertices);
        checkArguments();
    }

    /**
     *
     * @param initialGraph the initial connected graph.
     * @param edgesPerVertex the number of edges per vertex.
     * @param numVertices the total number of vertices.
     */
    public BarabasiAlbertGenerator(Graph initialGraph, int edgesPerVertex, int numVertices) {
        this.initialGraph = Objects.requireNonNull(initialGraph);
        this.numVertices = numVertices;
        this.edgesPerVertex = edgesPerVertex;
        checkArguments();
    }

    private void checkArguments() {
        if (edgesPerVertex <= 0) {
            throw new IllegalArgumentException("The number of edges per vertex must be strictly positive");
        }
        int m0 = initialGraph.numVertices();
        if (edgesPerVertex > m0) {
            throw new IllegalArgumentException("The number of edges per vertex must be <= " + m0);
        }
        if (numVertices < m0) {
            throw new IllegalArgumentException(
                    "The total number of vertices must be >= " + m0);
        }
    }

    public Graph createGraph() {
        int m0 = initialGraph.numVertices();
        var graph = GraphBuilder.numVertices(m0).estimatedNumVertices(numVertices).buildGraph();
        var rand = new Random();

        //start from the initial graph
        for (int v : initialGraph.vertices()) {
            for (var it = initialGraph.neighborIterator(v); it.hasNext();) {
                int u = it.next();
                graph.addEdge(v, u);
            }
        }

        //create the list of vertices multiplied by their degree
        //the probability to select the vertex v from this pool
        //is degree(v) / (sum of all degrees)
        var vertexPool = new VertexList(graph, numVertices * edgesPerVertex);
        for (int v : graph.vertices()) {
            int deg = graph.degree(v);
            if (deg == 0) {
                //address the case when there are isolated vertices
                vertexPool.add(v);
            } else {
                for (int i = 0; i < deg; i++) {
                    vertexPool.add(v);
                }
            }
        }

        //add new vertices to the graph
        int max = initialGraph.maxVertexNumber() + 1;
        for (int i = 0; i < numVertices - m0; i++) {
            int v = max + i;
            graph.addVertex(v);
            var list = new VertexList(graph);
            int edges = 0;
            while (edges < edgesPerVertex) {
                int u = vertexPool.get(rand.nextInt(vertexPool.size()));
                if (!graph.containsEdge(v, u)) {
                    graph.addEdge(v, u);
                    list.add(v);
                    list.add(u);
                    edges++;
                }
            }
            vertexPool.addAll(list.vertices());
        }
        return graph;
    }
}
