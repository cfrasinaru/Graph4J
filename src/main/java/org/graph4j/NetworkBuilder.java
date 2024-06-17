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
 * Support class for creating transportation (flow) networks.
 *
 * @author Cristian Frăsinaru
 */
public class NetworkBuilder extends GraphBuilderBase {

    private int source = -1;
    private int sink = -1;

    protected NetworkBuilder() {
        edgeDataSize = 3;
    }

    @Override
    NetworkImpl newInstance() {
        NetworkImpl graph = new NetworkImpl(vertices, maxVertices, avgDegree(),
                true, false, false, vertexDataSize, edgeDataSize);
        return graph;
    }

    /**
     * Creates an empty network (with no vertices).
     *
     * @return a new a {@link NetworkBuilder}.
     */
    public static NetworkBuilder empty() {
        var builder = new NetworkBuilder();
        builder.vertices = new int[0];
        return builder;
    }

    /**
     * Creates a network having as vertices the numbers from <code>0</code> to
     * <code>numVertices - 1</code>.
     *
     * @param numVertices the actual number of vertices in the graph.
     * @return a new a {@link NetworkBuilder}.
     */
    public static NetworkBuilder numVertices(int numVertices) {
        if (numVertices < 0) {
            throw new IllegalArgumentException("Number of vertices must not be negative.");
        }
        var builder = new NetworkBuilder();
        builder.vertices = new int[numVertices];
        for (int i = 0; i < numVertices; i++) {
            builder.vertices[i] = i;
        }
        return builder;
    }

    /**
     * Creates a new network having as vertices the numbers in the specified
     * range.
     *
     * @param firstVertex The number of the first vertex.
     * @param lastVertex The number of the last vertex.
     * @return a new a {@link NetworkBuilder}.
     */
    public static NetworkBuilder vertexRange(int firstVertex, int lastVertex) {
        var builder = new NetworkBuilder();
        builder.vertices = IntStream.rangeClosed(firstVertex, lastVertex).toArray();
        return builder;
    }

    /**
     * Creates a new network having the specified vertex numbers.
     *
     * @param vertices the vertices of the graph.
     * @return a new a {@link NetworkBuilder}.
     */
    public static NetworkBuilder vertices(int... vertices) {
        var builder = new NetworkBuilder();
        builder.vertices = vertices;
        return builder;
    }

    /**
     * Creates a new network having the specified vertex labels. The vertex
     * numbers will be assigned in the order of the collection, starting with 0.
     *
     * @param <V> the type of vertex labels.
     * @param vertexObjects a collection of objects, representing the labeled
     * vertices of the graph.
     * @return a new a {@link NetworkBuilder}.
     */
    public static <V> NetworkBuilder labeledVertices(Collection vertexObjects) {
        var builder = new NetworkBuilder();
        int n = vertexObjects.size();
        builder.vertices = IntStream.range(0, n).toArray();
        int v = 0;
        for (var label : vertexObjects) {
            builder.vertexLabelMap.put(label, v++);
        }
        return builder;
    }

    /**
     * Creates a new network having the specified vertex labels. The vertex
     * numbers will be the indices in the array of labels.
     *
     * @param <V> the type of vertex labels.
     * @param vertexObjects a list of objects, representing the labeled vertices
     * of the graph.
     * @return a new a {@link NetworkBuilder}.
     */
    public static <V> NetworkBuilder labeledVertices(V... vertexObjects) {
        return labeledVertices(List.of(vertexObjects));
    }

    /**
     * Creates a new network having the same vertex numbers as the specified
     * graph.
     *
     * @param graph a graph of any type.
     * @return a new a {@link NetworkBuilder}.
     */
    public static NetworkBuilder verticesFrom(Graph graph) {
        var builder = new NetworkBuilder();
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
     * Creates a new network based on the string representation of its edge set,
     * for example: "1-2, 2-3, 3-1", "a-b, b-c, c-d", etc. "0-1,0-2-0-3" may be
     * written as "0-1,2,3".
     *
     * Useful for creating small graphs for testing purposes.
     *
     * @param edges a text representation of the edges of the graph.
     * @return a new a {@link NetworkBuilder}.
     */
    public static NetworkBuilder edges(String edges) {
        var builder = new NetworkBuilder();
        builder.vertices = new int[0];
        builder.addEdges(edges);
        return builder;
    }

    @Override
    public NetworkBuilder named(String name) {
        return (NetworkBuilder) super.named(name);
    }

    @Override
    public NetworkBuilder estimatedDensity(double density) {
        return (NetworkBuilder) super.estimatedDensity(density);
    }

    @Override
    public NetworkBuilder estimatedAvgDegree(int avgDegree) {
        return (NetworkBuilder) super.estimatedAvgDegree(avgDegree);
    }

    @Override
    public NetworkBuilder estimatedNumEdges(long numEdges) {
        return (NetworkBuilder) super.estimatedNumEdges(numEdges);
    }

    @Override
    public NetworkBuilder estimatedNumVertices(int numVertices) {
        return (NetworkBuilder) super.estimatedNumVertices(numVertices);
    }

    @Override
    public NetworkBuilder vertexDataSize(int vertexDataSize) {
        return (NetworkBuilder) super.vertexDataSize(vertexDataSize);
    }

    @Override
    public NetworkBuilder edgeDataSize(int edgeDataSize) {
        return (NetworkBuilder) super.edgeDataSize(edgeDataSize);
    }

    @Override
    public NetworkBuilder addEdge(Edge e) {
        return (NetworkBuilder) super.addEdge(e);
    }

    public NetworkBuilder source(int source) {
        this.source = source;
        return this;
    }

    public NetworkBuilder sink(int sink) {
        this.sink = sink;
        return this;
    }

    @Override
    NetworkImpl build() {
        var net = (NetworkImpl) super.build();
        if (vertices.length < 2) {
            throw new IllegalArgumentException("Networks must have at least two vertices.");
        }
        if (source == -1) {
            source = vertices[0];
        }
        if (sink == -1) {
            sink = vertices[vertices.length - 1];
        }
        if (source == sink) {
            throw new IllegalArgumentException("Source and sink must be different.");
        }
        net.setSource(source);
        net.setSink(sink);
        return net;
    }

    /**
     * Builds a transportation (flow) network.
     *
     * @return a network.
     */
    public Network buildNetwork() {
        return (Network) build();
    }

}
