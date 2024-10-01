/*
 * Copyright (C) 2024 Cristian Frăsinaru and contributors
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
package org.graph4j.connectivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.graph4j.Graph;
import org.graph4j.GraphTests;
import org.graph4j.SimpleGraphAlgorithm;
import org.graph4j.generators.EdgeWeightsGenerator;
import org.graph4j.measures.GraphMeasures;
import org.graph4j.util.IntHashMap;
import org.graph4j.util.VertexHeap;
import org.graph4j.util.VertexList;

/**
 * Provides a method to find the minimum weighted cut of an undirected graph
 * using the Stoer-Wagner algorithm. An <em>edge cut</em> is a set of edges
 * that, if removed, would disconnect the graph.
 *
 * This implementation uses a binary heap and has time complexity
 * {@code O(|V||E| log|E|)}.
 *
 * See: <a href="https://dl.acm.org/doi/10.1145/263867.263872">Stoer and Wagner
 * minimum cut algorithm</a>.
 *
 * @author Cristian Frăsinaru
 */
public class StoerWagnerMinimumCut1 extends SimpleGraphAlgorithm {

    private boolean ignoreWeights;
    private Graph workGraph;
    private VertexHeap maxHeap;
    private boolean[] processed;
    private double[] weight;

    private int[] ordering;
    private IntHashMap orderingIndex;
    private Map<Integer, VertexList> vertexMap;
    private Double minWeight;
    private Integer minCutVertex;
    private EdgeCut minCut;
    private Integer newVertex;

    /**
     * Creates an algorithm for computing the minimum weighted cut. If the input
     * graph has no weights on its edges, the algorithm will assume the default
     * value of 1 for each edge.
     *
     * @param graph the input graph.
     */
    public StoerWagnerMinimumCut1(Graph graph) {
        this(graph, !graph.hasEdgeWeights());
    }

    /**
     * Creates an algorithm for computing the minimum weighted/cardinality cut.
     *
     * @param graph the input graph.
     * @param ignoreWeights if {@code true}
     */
    public StoerWagnerMinimumCut1(Graph graph, boolean ignoreWeights) {
        super(graph);
        this.ignoreWeights = ignoreWeights;
        //support for multigraphs?
    }

    /**
     * Returns the minimum cut.
     *
     * @return the minimum cut.
     * @throws IllegalArgumentException if the graph contains edges with
     * negative weights.
     */
    public EdgeCut getMinimumCut() {
        if (minCut != null) {
            return minCut;
        }
        if (minWeight == null) {
            compute();
        }
        minCut = new EdgeCut(graph, vertexMap.get(minCutVertex).vertices(), minWeight);
        assert minCut.isValid();
        return minCut;
    }

    /**
     * Returns the weight of the minimum cut, that is the sum of the weights of
     * the edges in the cut.
     *
     * @return the weight of the minimum cut.
     */
    public double getMinimumCutWeight() {
        if (minWeight == null) {
            compute();
        }
        return minWeight;
    }

    private void compute() {
        if (!ignoreWeights) {
            checkForNegativeEdges();
        }
        if (graph.isEmpty() || !GraphTests.isConnected(graph)) {
            minCut = new EdgeCut(graph);
            minWeight = 0.0;
            return;
        }
        this.workGraph = graph.copy();
        if (ignoreWeights) {
            EdgeWeightsGenerator.fill(workGraph, Graph.DEFAULT_EDGE_WEIGHT);
        }
        int n = workGraph.numVertices();
        this.vertexMap = new HashMap<>(2 * n);
        for (int v : graph.vertices()) {
            vertexMap.put(v, new VertexList(workGraph, new int[]{v}));
        }
        this.weight = new double[2 * n];
        this.minWeight = Double.POSITIVE_INFINITY;
        while (workGraph.numVertices() > 1) {
            if (!minCutPhase()) {
                break;
            }
        }
    }

    private boolean minCutPhase() {
        int n = workGraph.numVertices();
        //long m = workGraph.numEdges();
        //double d = GraphMeasures.avgDegree(workGraph);
        this.processed = new boolean[n];
        //        
        int beforeLast = -1, last = -1, pos = 0;
        boolean freshStart = false;
        if (ordering == null) {
            freshStart = true;
        } else {
            pos = findFirstNeighborPos(newVertex);
            assert pos >= 0 && pos < n;
            if (pos <= n / 2) {
                //if ((n - pos) * (d + 1) <= m) {
                freshStart = true;
            } else {
                for (int j = 0; j < pos; j++) {
                    assert ordering[j] >= 0;
                    processed[workGraph.indexOf(ordering[j])] = true;
                    //weight remains the same
                }
                beforeLast = pos >= 2 ? ordering[pos - 2] : -1;
                last = pos >= 1 ? ordering[pos - 1] : -1;

                //System.out.println("Reused ordering up to: " + pos + "/" + n);
                this.maxHeap = new VertexHeap(workGraph, false,
                        (i, j) -> (int) Math.signum(weight[j] - weight[i]));
                for (int i = pos; i < n; i++) {
                    int v = ordering[i];
                    int vi = workGraph.indexOf(v);
                    weight[vi] = sumProcessedNeighbors(v);
                    maxHeap.add(vi);
                }
            }
        }
        if (freshStart) {
            pos = 0;
            this.ordering = new int[n];
            this.orderingIndex = new IntHashMap(2 * n);
            this.weight = new double[2 * n];
            this.maxHeap = new VertexHeap(workGraph, true,
                    (i, j) -> (int) Math.signum(weight[j] - weight[i]));
        }
        int[] vertices = workGraph.vertices();
        while (!maxHeap.isEmpty()) {
            int vi = maxHeap.poll();
            processed[vi] = true;
            beforeLast = last;
            last = vertices[vi];

            ordering[pos] = last;
            orderingIndex.put(last, pos++);

            for (var it = workGraph.neighborIterator(last); it.hasNext();) {
                int ui = workGraph.indexOf(it.next());
                if (processed[ui]) {
                    continue;
                }
                weight[ui] += it.getEdgeWeight();
                maxHeap.update(ui);
            }
        }
        //System.out.println("ordering: " + Arrays.toString(ordering));

        //update minimum
        double cutWeight = weight[workGraph.indexOf(last)]; //cut-of-the-phase
        if (minCutVertex == null || cutWeight < minWeight) {
            minWeight = cutWeight;
            minCutVertex = last;
        }

        //shrink the work graph by merging the last two vertices
        //contracting the vertices cumulates the weights of the edges
        assert last >= 0;
        assert beforeLast >= 0;
        newVertex = workGraph.contractVertices(last, beforeLast);
        vertexMap.put(newVertex, vertexMap.get(last).union(vertexMap.get(beforeLast)));

        ordering[n - 2] = newVertex;
        orderingIndex.put(newVertex, n - 2);
        return !ignoreWeights || (minWeight > 1);
    }

    private int findFirstNeighborPos(int v) {
        int n = workGraph.numVertices();
        int minPos = Integer.MAX_VALUE;
        for (var it = workGraph.neighborIterator(v); it.hasNext();) {
            int u = it.next();
            int pos = orderingIndex.get(u);
            if (pos < minPos) {
                minPos = pos;
                if (minPos <= n / 2) {
                    break;
                }
            }
        }
        return minPos;
    }

    private double sumProcessedNeighbors(int v) {
        double sum = 0;
        for (var it = workGraph.neighborIterator(v); it.hasNext();) {
            int ui = workGraph.indexOf(it.next());
            if (processed[ui]) {
                sum += it.getEdgeWeight();
            }
        }
        return sum;
    }

    private void checkForNegativeEdges() {
        for (int v : graph.vertices()) {
            for (var it = graph.neighborIterator(v); it.hasNext();) {
                it.next();
                if (it.getEdgeWeight() < 0) {
                    throw new IllegalArgumentException(
                            "The graph contains edges with negative weight: " + it.edge());
                }
            }
        }
    }

}
