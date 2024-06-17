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
import java.util.List;
import java.util.stream.IntStream;
import org.graph4j.util.IntArrays;

/**
 * Support class for creating a graph of any type.
 *
 * @author Cristian Frăsinaru
 */
public class GraphBuilder extends GraphBuilderBase {

    protected GraphBuilder() {
    }

    @Override
    GraphImpl newInstance() {
        GraphImpl graph;
        if (allowingMultiEdges) {
            if (allowingSelfLoops) {
                if (directed) {
                    graph = new DirectedPseudographImpl<>(vertices, maxVertices, avgDegree(),
                            directed, allowingMultiEdges, allowingSelfLoops, vertexDataSize, edgeDataSize);
                } else {
                    graph = new PseudographImpl<>(vertices, maxVertices, avgDegree(),
                            directed, allowingMultiEdges, allowingSelfLoops, vertexDataSize, edgeDataSize);
                }
            } else {
                if (directed) {
                    graph = new DirectedMultigraphImpl<>(vertices, maxVertices, avgDegree(),
                            directed, allowingMultiEdges, allowingSelfLoops, vertexDataSize, edgeDataSize);

                } else {
                    graph = new MultigraphImpl<>(vertices, maxVertices, avgDegree(),
                            directed, allowingMultiEdges, allowingSelfLoops, vertexDataSize, edgeDataSize);
                }
            }
        } else {
            if (directed) {
                graph = new DigraphImpl<>(vertices, maxVertices, avgDegree(),
                        directed, allowingMultiEdges, allowingSelfLoops, vertexDataSize, edgeDataSize);
            } else {
                graph = new GraphImpl<>(vertices, maxVertices, avgDegree(),
                        directed, allowingMultiEdges, allowingSelfLoops, vertexDataSize, edgeDataSize);
            }
        }
        return graph;
    }

    /**
     * Creates an empty graph (with no vertices).
     *
     * @return a new a {@link GraphBuilder}.
     */
    public static GraphBuilder empty() {
        var builder = new GraphBuilder();
        builder.vertices = new int[0];
        return builder;
    }

    /**
     * Creates a graph having as vertices the numbers from <code>0</code> to
     * <code>numVertices - 1</code>.
     *
     * @param numVertices the actual number of vertices in the graph.
     * @return a new a {@link GraphBuilder}.
     */
    public static GraphBuilder numVertices(int numVertices) {
        if (numVertices < 0) {
            throw new IllegalArgumentException("Number of vertices must not be negative.");
        }
        var builder = new GraphBuilder();
        builder.vertices = new int[numVertices];
        for (int i = 0; i < numVertices; i++) {
            builder.vertices[i] = i;
        }
        return builder;
    }

    /**
     * Creates a new graph having as vertices the numbers in the specified
     * range.
     *
     * @param firstVertex The number of the first vertex.
     * @param lastVertex The number of the last vertex.
     * @return a new a {@link GraphBuilder}.
     */
    public static GraphBuilder vertexRange(int firstVertex, int lastVertex) {
        var builder = new GraphBuilder();
        builder.vertices = IntStream.rangeClosed(firstVertex, lastVertex).toArray();
        return builder;
    }

    /**
     * Creates a new graph having the specified vertex numbers.
     *
     * @param vertices the vertices of the graph.
     * @return a new a {@link GraphBuilder}.
     */
    public static GraphBuilder vertices(int... vertices) {
        var builder = new GraphBuilder();
        builder.vertices = vertices;
        return builder;
    }

    /**
     * Creates a new graph having the specified vertex labels. The vertex
     * numbers will be assigned in the order of the collection, starting with 0.
     *
     * @param <V> the type of vertex labels.
     * @param vertexObjects a collection of objects, representing the labeled
     * vertices of the graph.
     * @return a new a {@link GraphBuilder}.
     */
    public static <V> GraphBuilder labeledVertices(Collection vertexObjects) {
        var builder = new GraphBuilder();
        int n = vertexObjects.size();
        builder.vertices = IntStream.range(0, n).toArray();
        int v = 0;
        for (var label : vertexObjects) {
            builder.vertexLabelMap.put(label, v++);
        }
        return builder;
    }

    /**
     * Creates a new graph having the specified vertex labels. The vertex
     * numbers will be the indices in the array of labels.
     *
     * @param <V> the type of vertex labels.
     * @param vertexObjects a list of objects, representing the labeled vertices
     * of the graph.
     * @return a new a {@link GraphBuilder}.
     */
    public static <V> GraphBuilder labeledVertices(V... vertexObjects) {
        return labeledVertices(List.of(vertexObjects));
    }

    /**
     * Creates a new graph having the same vertex numbers as the specified
     * graph.
     *
     * @param graph a graph of any type.
     * @return a new a {@link GraphBuilder}.
     */
    public static GraphBuilder verticesFrom(Graph graph) {
        var builder = new GraphBuilder();
        builder.vertices = IntArrays.copyOf(graph.vertices());
        if (graph.hasVertexWeights()) {
            for (int v : builder.vertices) {
                builder.vertexWeightMap.put(v, graph.getVertexWeight(v));
            }
        }
        if (graph.hasVertexLabels()) {
            for (int v : builder.vertices) {
                builder.vertexLabelMap.put(graph.getVertexLabel(v), v);
            }
        }
        return builder;
    }

    /**
     * Creates a new graph based on the string representation of its edge set,
     * for example: "1-2, 2-3, 3-1", "a-b, b-c, c-d", etc. "0-1,0-2-0-3" may be
     * written as "0-1,2,3".
     *
     * Useful for creating small graphs for testing purposes.
     *
     * @param edges a text representation of the edges of the graph.
     * @return a new a {@link GraphBuilder}.
     */
    public static GraphBuilder edges(String edges) {
        var builder = new GraphBuilder();
        builder.vertices = new int[0];
        builder.addEdges(edges);
        return builder;
    }

    @Override
    public GraphBuilder named(String name) {
        return (GraphBuilder) super.named(name);
    }

    @Override
    public GraphBuilder estimatedDensity(double density) {
        return (GraphBuilder) super.estimatedDensity(density);
    }

    @Override
    public GraphBuilder estimatedAvgDegree(int avgDegree) {
        return (GraphBuilder) super.estimatedAvgDegree(avgDegree);
    }

    @Override
    public GraphBuilder estimatedNumEdges(long numEdges) {
        return (GraphBuilder) super.estimatedNumEdges(numEdges);
    }

    @Override
    public GraphBuilder estimatedNumVertices(int numVertices) {
        return (GraphBuilder) super.estimatedNumVertices(numVertices);
    }

    @Override
    public GraphBuilder vertexDataSize(int vertexDataSize) {
        return (GraphBuilder) super.vertexDataSize(vertexDataSize);
    }

    @Override
    public GraphBuilder edgeDataSize(int edgeDataSize) {
        return (GraphBuilder) super.edgeDataSize(edgeDataSize);
    }

    @Override
    public GraphBuilder addEdge(Edge e) {
        return (GraphBuilder) super.addEdge(e);
    }

    @Override
    public GraphBuilder addPath(int... path) {
        return (GraphBuilder) super.addPath(path);
    }

    @Override
    public GraphBuilder addCycle(int... cycle) {
        return (GraphBuilder) super.addCycle(cycle);
    }

    @Override
    public GraphBuilder addClique(int... clique) {
        return (GraphBuilder) super.addClique(clique);
    }

    @Override
    public GraphBuilder adjList(int[][] a) {
        return (GraphBuilder) super.adjList(a);
    }

    @Override
    public GraphBuilder addEdges(String edges) {
        return (GraphBuilder) super.addEdges(edges);
    }

    @Override
    public <V> GraphBuilder addEdge(V vLabel, V uLabel) {
        return (GraphBuilder) super.addEdge(vLabel, uLabel);
    }

    @Override
    public GraphBuilder addEdge(int v, int u) {
        return (GraphBuilder) super.addEdge(v, u);
    }

    /**
     * Builds an undirected graph, without multiple edges or self loops.
     *
     * @return a simple undirected graph.
     */
    public Graph buildGraph() {
        return build();
    }

    /**
     * Builds a directed graph, without multiple edges or self loops.
     *
     * @return a simple directed graph.
     */
    public Digraph buildDigraph() {
        directed = true;
        return (Digraph) build();
    }

    /**
     * Builds an undirected graph, without self loops, allowing multiple edges
     * between two vertices.
     *
     * @return an undirected multigraph.
     */
    public Multigraph buildMultigraph() {
        allowingMultiEdges = true;
        return (Multigraph) build();
    }

    /**
     * Builds a directed graph, without self loops, allowing multiple edges
     * between two vertices.
     *
     * @return a directed multigraph.
     */
    public DirectedMultigraph buildDirectedMultigraph() {
        directed = true;
        allowingMultiEdges = true;
        return (DirectedMultigraph) build();
    }

    /**
     * Builds an undirected graph, allowing both multiple edges between two
     * vertices and self loops.
     *
     * @return an undirected pseudograph.
     */
    public Pseudograph buildPseudograph() {
        allowingMultiEdges = true;
        allowingSelfLoops = true;
        return (Pseudograph) build();
    }

    /**
     * Builds a directed graph, allowing both multiple edges between two
     * vertices and self loops.
     *
     * @return a directed pseudograph.
     */
    public DirectedPseudograph buildDirectedPseudograph() {
        directed = true;
        allowingMultiEdges = true;
        allowingSelfLoops = true;
        return (DirectedPseudograph) build();
    }

}
